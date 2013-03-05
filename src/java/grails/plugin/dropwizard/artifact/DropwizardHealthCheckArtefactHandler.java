package grails.plugin.dropwizard.artifact;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.codehaus.groovy.grails.commons.InjectableGrailsClass;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class DropwizardHealthCheckArtefactHandler extends ArtefactHandlerAdapter {

	/** The artefact type. */
	public static final String TYPE = "HealthCheck";

	public DropwizardHealthCheckArtefactHandler() {
		super(TYPE, DropwizardHealthCheckGrailsClass.class, DefaultDropwizardHealthCheckGrailsClass.class, TYPE);
	}

	public static interface DropwizardHealthCheckGrailsClass extends InjectableGrailsClass {}

	public static class DefaultDropwizardHealthCheckGrailsClass extends AbstractDropwizardGrailsClass implements DropwizardHealthCheckGrailsClass {
		public DefaultDropwizardHealthCheckGrailsClass(Class<?> wrappedClass) {
			super(wrappedClass, DropwizardHealthCheckArtefactHandler.TYPE);
		}
	}
}
