import grails.util.GrailsUtil

includeTargets << grailsScript('_GrailsBootstrap')

target(runDropwizard: 'The description of the script goes here!') {
	depends checkVersion, configureProxy, bootstrap, loadApp

	try {
		def conf = config.grails.plugin.dropwizard

		String yamlPath = conf.yamlPath ?: 'classpath:dropwizard.yml'
		def resource = appCtx.getResource(yamlPath)
		if (!resource.exists()) {
			println "ERROR: YAML config file '$yamlPath' not found"
			return
		}

		String serviceClassName = conf.serviceClassName ?: 'grails.plugin.dropwizard.GrailsService'

		try {
			def service = classLoader.loadClass(serviceClassName).newInstance(appCtx, resource.file)
			service.start()
		}
		catch (ex) {
			println "Unable to instantiate the specified service class: $ex.message"
			GrailsUtil.deepSanitize(ex).printStackTrace(System.out)
		}
	}
	catch (e) {
		GrailsUtil.deepSanitize(e).printStackTrace(System.out)
	}
}

setDefaultTarget 'runDropwizard'
