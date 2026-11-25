/*
 * Copyright (C) 2009-2025 by the geOrchestra PSC
 *
 * This file is part of geOrchestra.
 *
 * geOrchestra is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * geOrchestra is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra. If not, see <http://www.gnu.org/licenses/>.
 */

package org.georchestra.geowebcache;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.geowebcache.util.URLMangler;
import org.springframework.web.context.ServletContextAware;

import jakarta.servlet.ServletContext;

/**
 * A simple URLMangler that overrides values provided by the servlet engine,
 * only keeping the most significant parts of the URL (after the hostname and
 * the webapp name).
 * <p>
 * Since GeoWebCache 2.0, manglers receive the base URL and a path buffer which
 * already contains the servlet context path. The context path the webapp is
 * actually deployed under is obtained from the {@link ServletContext} and
 * replaced by the configured public one.
 *
 * @author pmauduit
 */
public class GeorchestraURLMangler implements URLMangler, ServletContextAware {

    private final String baseURL;
    private final String contextPath;

    /** context path the webapp is deployed under, without leading/trailing slashes */
    private String deployedContextPath = "";

    public GeorchestraURLMangler(String baseUrl, String contextPath) {
        this.baseURL = baseUrl;
        this.contextPath = contextPath;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.deployedContextPath = StringUtils.strip(servletContext.getContextPath(), "/");
    }

    @Override
    public void mangleURL(StringBuilder baseURL, StringBuilder path, Map<String, String> kvp, URLType type) {
        String remaining = path.toString();
        if (StringUtils.isNotEmpty(deployedContextPath)) {
            if (remaining.equals(deployedContextPath)) {
                remaining = "";
            } else if (remaining.startsWith(deployedContextPath + "/")) {
                remaining = remaining.substring(deployedContextPath.length() + 1);
            }
        }
        baseURL.setLength(0);
        baseURL.append(StringUtils.strip(this.baseURL, "/"));

        path.setLength(0);
        String ctx = StringUtils.strip(this.contextPath, "/");
        if (StringUtils.isNotEmpty(ctx)) {
            path.append(ctx);
        }
        remaining = StringUtils.stripStart(remaining, "/");
        if (StringUtils.isNotEmpty(remaining)) {
            if (!path.isEmpty()) {
                path.append('/');
            }
            path.append(remaining);
        }
    }
}
