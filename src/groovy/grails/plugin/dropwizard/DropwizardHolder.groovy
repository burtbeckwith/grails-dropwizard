package grails.plugin.dropwizard

import com.yammer.dropwizard.config.Configuration
import com.yammer.dropwizard.config.Environment

/**
 * Registered as the "dropwizard" Spring bean.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
@Singleton
class DropwizardHolder {
	Configuration configuration
	Environment environment
	GrailsService service
}
