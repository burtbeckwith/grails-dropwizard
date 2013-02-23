includeTargets << grailsScript('_GrailsBootstrap')

target(runDropwizard: 'The description of the script goes here!') {
	depends checkVersion, configureProxy, bootstrap, loadApp

	String yamlPath = config.grails.plugin.dropwizard.yamlPath ?: 'classpath:dropwizard.yml'
	def resource = appCtx.getResource(yamlPath)
	if (!resource.exists()) {
		println "ERROR: YAML config file '$yamlPath' not found"
		return
	}

	def service = classLoader.loadClass('grails.plugin.dropwizard.GrailsService').newInstance(appCtx, resource.file)
	service.start()
}

setDefaultTarget 'runDropwizard'
