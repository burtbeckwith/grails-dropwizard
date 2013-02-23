package grails.plugin.dropwizard.artifact;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.codehaus.groovy.grails.commons.InjectableGrailsClass;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class DropwizardTaskArtefactHandler extends ArtefactHandlerAdapter {

	/** The artefact type. */
	public static final String TYPE = "DropwizardTask";

	public DropwizardTaskArtefactHandler() {
		super(TYPE, DropwizardTaskGrailsClass.class, DefaultDropwizardTaskGrailsClass.class, TYPE);
	}

	public static interface DropwizardTaskGrailsClass extends InjectableGrailsClass {}

	public static class DefaultDropwizardTaskGrailsClass extends AbstractDropwizardGrailsClass implements DropwizardTaskGrailsClass {
		public DefaultDropwizardTaskGrailsClass(Class<?> wrappedClass) {
			super(wrappedClass, DropwizardTaskArtefactHandler.TYPE);
		}
	}
}
