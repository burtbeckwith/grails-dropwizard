package grails.plugin.dropwizard.artifact;

import grails.plugin.dropwizard.ConfigValue;

import java.lang.reflect.Field;

import org.codehaus.groovy.grails.commons.AbstractInjectableGrailsClass;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ReflectionUtils;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public abstract class AbstractDropwizardGrailsClass extends AbstractInjectableGrailsClass {

	public AbstractDropwizardGrailsClass(Class<?> clazz, String trailingName) {
		super(clazz, trailingName);
	}

	@Override
	public Object newInstance() {
		return autowireBeanProperties(super.newInstance());
	}

	protected Object autowireBeanProperties(Object instance) {
		ConfigurableApplicationContext ctx = (ConfigurableApplicationContext)grailsApplication.getMainContext();
		ctx.getBeanFactory().autowireBeanProperties(instance, AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
		wireConfigValues(instance);
		return instance;
	}

	protected void wireConfigValues(Object instance) {
		for (Field field : instance.getClass().getDeclaredFields()) {
			ConfigValue ann = field.getAnnotation(ConfigValue.class);
			if (ann != null) {
				Object value = grailsApplication.getFlatConfig().get(ann.value());
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, instance, value);
			}
		}
	}
}
