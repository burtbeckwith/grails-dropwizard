package grails.plugin.dropwizard.artifact;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.codehaus.groovy.grails.commons.InjectableGrailsClass;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class DropwizardResourceArtefactHandler extends ArtefactHandlerAdapter {

	/** The artefact type. */
	public static final String TYPE = "DropwizardResource";

	public DropwizardResourceArtefactHandler() {
		super(TYPE, DropwizardResourceGrailsClass.class, DefaultDropwizardResourceGrailsClass.class, TYPE);
	}

	public static interface DropwizardResourceGrailsClass extends InjectableGrailsClass {}

	public static class DefaultDropwizardResourceGrailsClass extends AbstractDropwizardGrailsClass implements DropwizardResourceGrailsClass {
		public DefaultDropwizardResourceGrailsClass(Class<?> wrappedClass) {
			super(wrappedClass, DropwizardResourceArtefactHandler.TYPE);
		}
	}
}
