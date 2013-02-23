package grails.plugin.dropwizard.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

/**
 * Populated from dropwizard.yml.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class GrailsConfiguration extends Configuration {

	@JsonProperty
	private GrailsServiceConfiguration grails = new GrailsServiceConfiguration();

	public GrailsServiceConfiguration getGrailsServiceConfiguration() {
		return grails;
	}
}
