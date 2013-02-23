package grails.plugin.dropwizard

import grails.plugin.dropwizard.util.DropwizardUtils
import grails.plugin.dropwizard.util.EndpointData

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
class DropwizardService {

	static transactional = false

	def dropwizard
	def grailsApplication

	Collection<EndpointData> findEndpoints() {
		DropwizardUtils.findEndpoints dropwizard.environment, dropwizard.configuration, grailsApplication
	}
}
