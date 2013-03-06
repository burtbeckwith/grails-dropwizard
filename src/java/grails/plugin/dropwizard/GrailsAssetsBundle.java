package grails.plugin.dropwizard;

import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Environment;

public class GrailsAssetsBundle extends AssetsBundle {

   private static final String DEFAULT_INDEX_FILE = "index.htm";

	private String resourcePath;
	private String uriPath;
	private String indexFile;
	private String contextPath;

	public GrailsAssetsBundle(String contextPath, String path) {
		this(contextPath, path, path, DEFAULT_INDEX_FILE);
	}

	public GrailsAssetsBundle(String contextPath, String resourcePath, String uriPath) {
      this(contextPath, resourcePath, uriPath, DEFAULT_INDEX_FILE);
	}

	public GrailsAssetsBundle(String contextPath, String resourcePath, String uriPath, String indexFile) {
		super(resourcePath, uriPath, indexFile);
      this.resourcePath = resourcePath.endsWith("/") ? resourcePath : (resourcePath + '/');
      this.uriPath = uriPath.endsWith("/") ? uriPath : (uriPath + '/');
      this.indexFile = indexFile;
      this.contextPath = contextPath;
	}

	@Override
	public void run(Environment environment) {
		environment.addServlet(new GrailsAssetServlet(contextPath, resourcePath, uriPath, indexFile), uriPath + '*');
	}
}
