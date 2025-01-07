package itmo.app.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

public class RequestCacherFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		if (request instanceof HttpServletRequest httpServletRequest) {
			ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);
			chain.doFilter(wrappedRequest, response);
		} else {
			chain.doFilter(request, response);
		}
	}
}
