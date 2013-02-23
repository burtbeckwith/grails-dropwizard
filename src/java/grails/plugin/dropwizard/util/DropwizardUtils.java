package grails.plugin.dropwizard.util;

import grails.util.Metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import org.codehaus.groovy.grails.commons.GrailsApplication;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.config.Environment;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class DropwizardUtils {

	private DropwizardUtils() {
		// static only
	}

	public static Collection<EndpointData> findEndpoints(Environment environment, Configuration configuration,
			GrailsApplication grailsApplication) {

		ResourceConfig config = environment.getJerseyResourceConfig();

		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Object o : config.getSingletons()) {
			if (o.getClass().isAnnotationPresent(Path.class)) {
				classes.add(o.getClass());
			}
		}
		for (Class<?> klass : config.getClasses()) {
			if (klass.isAnnotationPresent(Path.class)) {
				classes.add(klass);
			}
		}

		List<EndpointData> endpoints = new ArrayList<EndpointData>();

		String contextPath = findContextPath(grailsApplication);
		for (Class<?> clazz : classes) {
			final String path = clazz.getAnnotation(Path.class).value();
			for (AnnotatedMethod method : new MethodList(clazz, true).hasMetaAnnotation(HttpMethod.class)) {
				String rootPath = configuration.getHttpConfiguration().getRootPath();
				if (rootPath.endsWith("/*")) {
					rootPath = rootPath.substring(0, rootPath.length() - 2);
				}
				final StringBuilder pathBuilder = new StringBuilder().append(rootPath).append(path);
				if (method.isAnnotationPresent(Path.class)) {
					final String methodPath = method.getAnnotation(Path.class).value();
					if (!methodPath.startsWith("/") && !path.endsWith("/")) {
						pathBuilder.append('/');
					}
					pathBuilder.append(methodPath);
				}
				for (HttpMethod verb : method.getMetaMethodAnnotations(HttpMethod.class)) {
					endpoints.add(new EndpointData(verb.value(), pathBuilder.toString(), clazz.getCanonicalName(), contextPath));
				}
			}
		}

		return endpoints;
	}

	public static String findContextPath(GrailsApplication grailsApplication) {
		String path = System.getProperty("app.context");
		if (path == null) {
			path = (String) Metadata.getCurrent().get("app.context");
			if (path == null) {
				Object c = grailsApplication.getFlatConfig().get("grails.app.context");
				if (c instanceof CharSequence) {
					path = c.toString();
				}
				if (path == null) {
					path = Metadata.getCurrent().getApplicationName();
				}
			}
		}

		if (!path.startsWith("/")) {
			path = '/' + path;
      }

      return path;
	}
}
