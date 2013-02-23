package grails.plugin.dropwizard.util;

/**
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class EndpointData {
	public final String verb;
	public final String path;
	public final String classname;
	public final String contextPath;

	public EndpointData(final String verb, final String path, final String classname, final String contextPath) {
		this.verb = verb;
		this.path = path;
		this.classname = classname;
		this.contextPath = contextPath;
	}

	@Override
	public String toString() {
		return verb + " " + (contextPath + path).replaceAll("//", "/") + " (" + classname + ")";
	}
}
