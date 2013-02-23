package grails.plugin.dropwizard.config;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

/**
 * Populated from dropwizard.yml.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class GrailsServiceConfiguration extends Configuration {

	@JsonProperty
	private List<String> resources = Collections.emptyList();

	@JsonProperty
	private List<String> healthChecks = Collections.emptyList();

	@JsonProperty
	private List<String> jerseyProviders = Collections.emptyList();

	@JsonProperty
	private List<String> managed = Collections.emptyList();

	@JsonProperty
	private List<String> lifeCycles = Collections.emptyList();

	@JsonProperty
	private List<String> tasks = Collections.emptyList();

	@JsonProperty
	private List<String> disabledJerseyFeatures = Collections.emptyList();

	@JsonProperty
	private List<String> enabledJerseyFeatures = Collections.emptyList();

	public List<String> getResources() {
		return resources;
	}

	public List<String> getHealthChecks() {
		return healthChecks;
	}

	public List<String> getJerseyProviders() {
		return jerseyProviders;
	}

	public List<String> getManaged() {
		return managed;
	}

	public List<String> getLifeCycles() {
		return lifeCycles;
	}

	public List<String> getDisabledJerseyFeatures() {
		return disabledJerseyFeatures;
	}

	public List<String> getEnabledJerseyFeatures() {
		return enabledJerseyFeatures;
	}

	public List<String> getTasks() {
		return tasks;
	}
}
