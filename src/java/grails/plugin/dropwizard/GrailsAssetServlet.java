package grails.plugin.dropwizard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.yammer.dropwizard.assets.AssetServlet;

public class GrailsAssetServlet extends AssetServlet {

	private static final long serialVersionUID = 1;

	protected String contextPath;

	public GrailsAssetServlet(String contextPath, String resourcePath, String uriPath, String indexFile) {
		super(resourcePath, uriPath, indexFile);
		this.contextPath = contextPath;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doGet(new HttpServletRequestWrapper(request) {
			@Override
			public String getRequestURI() {
				String uri = super.getRequestURI();
				if (uri.startsWith(contextPath)) {
					uri = uri.substring(contextPath.length());
				}
				return uri;
			}
		}, response);
	}
}
