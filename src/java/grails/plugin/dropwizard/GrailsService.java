package grails.plugin.dropwizard;

import grails.plugin.dropwizard.artifact.DropwizardHealthCheckArtefactHandler;
import grails.plugin.dropwizard.artifact.DropwizardHealthCheckArtefactHandler.DropwizardHealthCheckGrailsClass;
import grails.plugin.dropwizard.artifact.DropwizardResourceArtefactHandler;
import grails.plugin.dropwizard.artifact.DropwizardResourceArtefactHandler.DropwizardResourceGrailsClass;
import grails.plugin.dropwizard.artifact.DropwizardTaskArtefactHandler;
import grails.plugin.dropwizard.artifact.DropwizardTaskArtefactHandler.DropwizardTaskGrailsClass;
import grails.plugin.dropwizard.config.GrailsConfiguration;
import grails.plugin.dropwizard.config.GrailsServiceConfiguration;
import grails.plugin.dropwizard.util.DropwizardUtils;
import grails.util.BuildSettings;
import grails.util.BuildSettingsHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.codehaus.groovy.grails.commons.GrailsClass;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.ReflectionUtils;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.cli.ServerCommand;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.ConfigurationFactory;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.LoggingFactory;
import com.yammer.dropwizard.config.ServerFactory;
import com.yammer.dropwizard.jetty.BiDiGzipHandler;
import com.yammer.dropwizard.lifecycle.Managed;
import com.yammer.dropwizard.lifecycle.ServerLifecycleListener;
import com.yammer.dropwizard.tasks.Task;
import com.yammer.dropwizard.validation.Validator;
import com.yammer.dropwizard.views.ViewBundle;
import com.yammer.metrics.core.HealthCheck;
import com.yammer.metrics.jetty.InstrumentedHandler;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class GrailsService extends Service<GrailsConfiguration> {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected final ApplicationContext ctx;
	protected final File yamlFile;
	protected final GrailsApplication grailsApplication;

	public GrailsService(ApplicationContext ctx, File yamlFile) {
		this.yamlFile = yamlFile;
		this.ctx = ctx;
		grailsApplication = ctx.getBean("grailsApplication", GrailsApplication.class);
	}

	public void start() throws Exception {
		final Bootstrap<GrailsConfiguration> bootstrap = new Bootstrap<GrailsConfiguration>(this);
		bootstrap.addCommand(new ServerCommand<GrailsConfiguration>(this));
		initialize(bootstrap);

		Validator validator = new Validator();
		final ConfigurationFactory<GrailsConfiguration> configurationFactory =
            ConfigurationFactory.forClass(GrailsConfiguration.class, validator,
            		bootstrap.getObjectMapperFactory().copy());
		final GrailsConfiguration configuration;

		if (yamlFile == null) {
			configuration = configurationFactory.build();
		}
		else {
			if (!yamlFile.exists()) {
				throw new FileNotFoundException("YAML file " + yamlFile + " not found");
			}
			configuration = configurationFactory.build(yamlFile);
		}

		new LoggingFactory(configuration.getLoggingConfiguration(), bootstrap.getName()).configure();

		final Environment environment = new Environment(bootstrap.getName(), configuration,
				bootstrap.getObjectMapperFactory().copy(), validator);

		if (grails.util.Environment.isDevelopmentMode()) {
			environment.setBaseResource(Resource.newResource(new File(BuildSettingsHolder.getSettings().getBaseDir(), "web-app"))); // TODO prod
		}

		bootstrap.runWithBundles(configuration, environment);
		run(configuration, environment);

		final Logger logger = LoggerFactory.getLogger(ServerCommand.class);
		displayBanner(logger, environment);

		final Server server = new ServerFactory(configuration.getHttpConfiguration(), environment.getName()).buildServer(environment);
		configureGrailsContext(server);
		try {
			server.start();
			for (ServerLifecycleListener listener : environment.getServerListeners()) {
				listener.serverStarted(server);
			}
			server.join();
		}
		catch (Exception e) {
			logger.error("Unable to start server, shutting down", e);
			server.stop();
		}
	}

	protected void displayBanner(Logger logger, Environment environment) {
		String banner = null;
		try {
			banner = Resources.toString(Resources.getResource("banner.txt"), Charsets.UTF_8);
		}
		catch (IllegalArgumentException ignored) {}
		catch (IOException ignored) {}

		if (banner == null) {
			CharSequence configBanner = getConfigValue("banner", null, CharSequence.class);
			if (configBanner != null) {
				banner = configBanner.toString();
			}
		}

		if (banner == null) {
			logger.info("Starting {}", environment.getName());
		}
		else {
			logger.info("Starting {}\n{}", environment.getName(), banner);
		}
	}

	protected void configureGrailsContext(Server server) throws IOException {

		BuildSettings settings = BuildSettingsHolder.getSettings();
		String webAppRoot = new File(settings.getBaseDir(), "web-app").getPath(); // TODO prod

		String contextPath = DropwizardUtils.findContextPath(grailsApplication);
		final WebAppContext context = new WebAppContext(webAppRoot, contextPath);

		Class<?>[] configClasses = {
				WebInfConfiguration.class,
				WebXmlConfiguration.class,
				MetaInfConfiguration.class,
				FragmentConfiguration.class,
				EnvConfiguration.class,
				PlusConfiguration.class,
				JettyWebXmlConfiguration.class
		};

		List<Configuration> configurations = new ArrayList<Configuration>();
		for (Class<?> c : configClasses) {
			try {
				configurations.add((Configuration) c.newInstance());
			}
			catch (InstantiationException e) {
				ReflectionUtils.handleReflectionException(e);
			}
			catch (IllegalAccessException e) {
				ReflectionUtils.handleReflectionException(e);
			}
		}

		Object grailsJndi = grailsApplication.getFlatConfig().get("grails.development.jetty.env");
		if (grailsJndi != null) {
			FileSystemResource res = new FileSystemResource(grailsJndi.toString());
			if (res.exists()) {
				EnvConfiguration jndiConfig = (EnvConfiguration) configurations.get(3);
				jndiConfig.setJettyEnvXml(res.getURL());
			}
		}

		context.setClassLoader(Thread.currentThread().getContextClassLoader());
		context.setConfigurations(configurations.toArray(new Configuration[configurations.size()]));
		context.setDescriptor(settings.getWebXmlLocation().getAbsolutePath());

		String dropwizardContext = getConfigValue("dropwizardContext", "dropwizard", CharSequence.class).toString();
		if (!dropwizardContext.startsWith("/")) {
			dropwizardContext = '/' + dropwizardContext;
		}

		HandlerCollection handlers = (HandlerCollection) server.getHandler();
		((ServletContextHandler) handlers.getHandlers()[0]).setContextPath(dropwizardContext);
		BiDiGzipHandler handler2 = (BiDiGzipHandler) handlers.getHandlers()[1];
		((ServletContextHandler)((InstrumentedHandler) handler2.getHandler()).getHandler()).setContextPath(dropwizardContext);

		ContextHandlerCollection newHandlers = new ContextHandlerCollection();
		newHandlers.setHandlers(new Handler[] { context, handlers });
		server.setHandler(newHandlers);
	}

	@Override
	public void initialize(Bootstrap<GrailsConfiguration> bootstrap) {
		bootstrap.setName("dropwizard-grails");
		bootstrap.addBundle(new ViewBundle());
	}

	@Override
	public void run(GrailsConfiguration conf, final Environment environment) {

		final GrailsServiceConfiguration configuration = conf.getGrailsServiceConfiguration();

		environment.addLifeCycleListener(new LifeCycle.Listener() {
			public void lifeCycleStarting(LifeCycle event) {
				debug("LifeCycle Starting");
			}
			public void lifeCycleStarted(LifeCycle event) {
				initHolder(configuration, environment);
				debug("LifeCycle Started");
			}
			public void lifeCycleStopping(LifeCycle event) {
				debug("LifeCycle Stopping");
			}
			public void lifeCycleStopped(LifeCycle event) {
				debug("LifeCycle Stopped");
			}
			public void lifeCycleFailure(LifeCycle event, Throwable cause) {
				log.error("LifeCycle Failure: " + cause.getMessage(), cause);
			}
		});

		registerResources(configuration.getResources(), environment);
		registerHealthChecks(configuration.getHealthChecks(), environment);
		registerManagedBeans(configuration.getManaged(), environment);
		registerLifeCycleBeans(configuration.getLifeCycles(), environment);
		registerJerseyProviders(configuration.getJerseyProviders(), environment);
		registerTasks(configuration.getTasks(), environment);

		enableJerseyFeatures(configuration.getEnabledJerseyFeatures(), environment);
		disableJerseyFeatures(configuration.getDisabledJerseyFeatures(), environment);
	}

	protected void initHolder(GrailsServiceConfiguration configuration, Environment environment) {
		DropwizardHolder dropwizard = ctx.getBean("dropwizard", DropwizardHolder.class);
		dropwizard.setConfiguration(configuration);
		dropwizard.setEnvironment(environment);
		dropwizard.setService(this);
	}

	protected void registerResources(List<String> extraResourceNames, Environment environment) {

		boolean autoRegisterResources = isTrue("autoRegisterResources", false);
		if (autoRegisterResources) {
			for (Map.Entry<String, Object> e : ctx.getBeansWithAnnotation(Path.class).entrySet()) {
				environment.addResource(e.getValue());
				debug("added discovered annotated resource {}", e.getKey());
			}
		}

		for (GrailsClass grailsClass : grailsApplication.getArtefacts(DropwizardResourceArtefactHandler.TYPE)) {
			DropwizardResourceGrailsClass gc = (DropwizardResourceGrailsClass) grailsClass;
			environment.addResource(gc.newInstance()); // TODO register as bean? support scope? tx?
			debug("added resource of class {}", gc.getClazz().getName());
		}

		for (String beanName : extraResourceNames) {
			environment.addResource(ctx.getBean(beanName));
			debug("added resource for bean {}", beanName);
		}
	}

	protected void registerHealthChecks(List<String> extraHealthCheckNames, Environment environment) {

		boolean autoRegisterHealthChecks = isTrue("autoRegisterHealthChecks", false);
		if (autoRegisterHealthChecks) {
			for (Map.Entry<String, HealthCheck> e : ctx.getBeansOfType(HealthCheck.class).entrySet()) {
				environment.addHealthCheck(e.getValue());
				debug("added discovered health check {}", e.getKey());
			}
		}

		for (GrailsClass grailsClass : grailsApplication.getArtefacts(DropwizardHealthCheckArtefactHandler.TYPE)) {
			DropwizardHealthCheckGrailsClass gc = (DropwizardHealthCheckGrailsClass) grailsClass;
			environment.addHealthCheck((HealthCheck)gc.newInstance()); // TODO register as bean? support scope? tx?
			debug("added health check of class {}", gc.getClazz().getName());
		}

		for (String beanName : extraHealthCheckNames) {
			environment.addHealthCheck(ctx.getBean(beanName, HealthCheck.class));
			debug("added health check for bean {}", beanName);
		}
	}

	protected void registerManagedBeans(List<String> managedBeanNames, Environment environment) {

		boolean autoRegisterManaged = isTrue("autoRegisterManaged", false);
		if (autoRegisterManaged) {
			for (Map.Entry<String, Managed> e : ctx.getBeansOfType(Managed.class).entrySet()) {
				environment.manage(e.getValue());
				debug("added discovered Managed bean {}", e.getKey());
			}
		}

		for (String beanName : managedBeanNames) {
			environment.manage(ctx.getBean(beanName, Managed.class));
			debug("added Managed bean {}", beanName);
		}
	}

	protected void registerLifeCycleBeans(List<String> lifeCycleBeanNames, Environment environment) {

		boolean autoRegisterLifeCycle = isTrue("autoRegisterLifeCycle", false);
		if (autoRegisterLifeCycle) {
			for (Map.Entry<String, LifeCycle> e : ctx.getBeansOfType(LifeCycle.class).entrySet()) {
				environment.manage(e.getValue());
				debug("added discovered LifeCycle bean {}", e.getKey());
			}
		}

		for (String lifeCycle : lifeCycleBeanNames) {
			environment.manage(ctx.getBean(lifeCycle, LifeCycle.class));
		}
	}

	protected void registerJerseyProviders(List<String> providerBeanNames, Environment environment) {

		boolean autoRegisterAnnotatedProviders = isTrue("autoRegisterAnnotatedProviders", false);
		if (autoRegisterAnnotatedProviders) {
			for (Map.Entry<String, Object> e : ctx.getBeansWithAnnotation(Provider.class).entrySet()) {
				environment.addProvider(e.getValue());
				debug("added annotated Provider bean {}", e.getKey());
			}
		}

		boolean autoRegisterInjectableProviders = isTrue("autoRegisterInjectableProviders", false);
		if (autoRegisterInjectableProviders) {
			for (@SuppressWarnings("rawtypes") Map.Entry<String, InjectableProvider> e : ctx.getBeansOfType(InjectableProvider.class).entrySet()) {
				environment.addProvider(e.getValue());
				debug("added discovered InjectableProvider bean {}", e.getKey());
			}
		}

		for (String beanName : providerBeanNames) {
			environment.addProvider(ctx.getBean(beanName));
		}
	}

	protected void registerTasks(List<String> extraTaskNames, Environment environment) {

		boolean autoRegisterTasks = isTrue("autoRegisterTasks", false);
		if (autoRegisterTasks) {
			for (Map.Entry<String, Task> e : ctx.getBeansOfType(Task.class).entrySet()) {
				environment.addTask(e.getValue());
				debug("added discovered Task {}", e.getKey());
			}
		}

		for (GrailsClass grailsClass : grailsApplication.getArtefacts(DropwizardTaskArtefactHandler.TYPE)) {
			DropwizardTaskGrailsClass gc = (DropwizardTaskGrailsClass) grailsClass;
			environment.addTask((Task)gc.newInstance()); // TODO register as bean? support scope? tx?
			debug("added Task of class {}", gc.getClazz().getName());
		}

		for (String task : extraTaskNames) {
			environment.addTask(ctx.getBean(task, Task.class));
		}
	}

	protected void enableJerseyFeatures(List<String> features, Environment environment) {
		for (String feature : features) {
			environment.enableJerseyFeature(feature);
		}
	}

	protected void disableJerseyFeatures(List<String> features, Environment environment) {
		for (String feature : features) {
			environment.disableJerseyFeature(feature);
		}
	}

	protected boolean isTrue(String name, boolean defaultIfNotSet) {
		return getConfigValue(name, defaultIfNotSet, Boolean.class);
	}

	@SuppressWarnings("unchecked")
	protected <T> T getConfigValue(String name, Object defaultIfNotSet, Class<? extends T> expectedType) {
		Object value = grailsApplication.getFlatConfig().get("grails.plugin.dropwizard." + name);
		if (value == null || !expectedType.isAssignableFrom(value.getClass())) {
			return (T) defaultIfNotSet;
		}
		return (T) value;
	}

	protected void debug(String message, Object... vars) {
		log.debug(message, vars);
	}
}
