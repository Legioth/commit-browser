/*
 * Copyright 2000-2014 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.demo.commitbrowser;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides various helper methods for connectors. Meant for internal use.
 *
 * @author Vaadin Ltd
 */
// @WebFilter(urlPatterns = "/*")
public class MyCORSFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    /**
     * Override to handle the CORS requests.
     */

    @Override
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            // Origin is needed for all CORS requests
            String origin = request.getHeader("Origin");
            if (origin != null && isAllowedRequestOrigin(origin)) {

                // Handle a preflight "option" requests
                if ("options".equalsIgnoreCase(request.getMethod())) {
                    response.addHeader("Access-Control-Allow-Origin", origin);
                    response.setHeader("Allow",
                            "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");

                    // allow the requested method
                    String method = request
                            .getHeader("Access-Control-Request-Method");
                    response.addHeader("Access-Control-Allow-Methods", method);

                    // allow the requested headers
                    String headers = request
                            .getHeader("Access-Control-Request-Headers");
                    response.addHeader("Access-Control-Allow-Headers", headers);

                    response.addHeader("Access-Control-Allow-Credentials",
                            "true");
                    response.setContentType("text/plain");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().flush();
                    return;
                } // Handle UIDL post requests
                else if ("post".equalsIgnoreCase(request.getMethod())) {
                    response.addHeader("Access-Control-Allow-Origin", origin);
                    response.addHeader("Access-Control-Allow-Credentials",
                            "true");
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            // All the other requests nothing to do with CORS
            filterChain.doFilter(request, response);

        }
    }

    /**
     * Check that the page Origin header is allowed.
     */

    private boolean isAllowedRequestOrigin(String origin) {
        // TODO: Remember to limit the origins.
        return origin.matches(".*");
    }
}