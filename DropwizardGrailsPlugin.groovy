import grails.plugin.dropwizard.DropwizardHolder
import grails.plugin.dropwizard.artifact.DropwizardHealthCheckArtefactHandler
import grails.plugin.dropwizard.artifact.DropwizardResourceArtefactHandler
import grails.plugin.dropwizard.artifact.DropwizardTaskArtefactHandler

class DropwizardGrailsPlugin {
	String version = '0.1'
	String grailsVersion = '2.0 > *'
	String author = 'Burt Beckwith'
	String authorEmail = 'beckwithb@vmware.com'
	String title = 'Dropwizard Plugin'
	String description = 'Adds support for Dropwizard'
	String documentation = 'http://grails.org/plugin/dropwizard'
	List pluginExcludes = [
		'docs/**',
		'src/docs/**'
	]
	List artefacts = [DropwizardHealthCheckArtefactHandler, DropwizardResourceArtefactHandler, DropwizardTaskArtefactHandler]
	List watchedResources = [
		'file:./grails-app/dropwizard/**/*HealthCheck.groovy',
		'file:./grails-app/dropwizard/**/*DropwizardResource.groovy',
		'file:./grails-app/dropwizard/**/*DropwizardTask.groovy']

	String license = 'APACHE'
	def issueManagement = [system: 'Github', url: 'https://github.com/burtbeckwith/grails-dropwizard/issues']
	def scm = [url: 'https://github.com/burtbeckwith/grails-dropwizard']

	def doWithSpring = {
		// fields will be set at startup
		dropwizard(DropwizardHolder) { bean ->
			bean.factoryMethod = 'getInstance'
		}
	}

	def onChange = { event ->
		if (application.isHealthCheckClass(event.source)) {
			// TODO
		}
		else if (application.isDropwizardResourceClass(event.source)) {
			// TODO
		}
		else if (application.isDropwizardTaskClass(event.source)) {
			// TODO
		}
	}

	def onConfigChange = { event ->
		// TODO
	}
}
