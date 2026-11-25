package org.georchestra.geowebcache;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.geowebcache.util.URLMangler;
import org.geowebcache.util.URLManglerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;

public class GeorchestraURLManglerTest {

    private static GeorchestraURLMangler mangler(String baseUrl, String ctx, String deployedCtx) {
        GeorchestraURLMangler gum = new GeorchestraURLMangler(baseUrl, ctx);
        MockServletContext sc = new MockServletContext();
        sc.setContextPath(deployedCtx);
        gum.setServletContext(sc);
        return gum;
    }

    private static String build(URLMangler m, String base, String ctx, String path) {
        return URLManglerUtils.buildURL(base, ctx, path, null, m, URLMangler.URLType.SERVICE);
    }

    @Test
    public void testGeorchestraURLMangler() {
        URLMangler gum = mangler("http://sdi.georchestra.org", "geowebcache", "/geowebcache-private");
        assertEquals("http://sdi.georchestra.org/geowebcache/service/wmts",
                build(gum, "http://localhost:8080", "/geowebcache-private", "/service/wmts"));

        // Tests stripping of the parameters
        gum = mangler("////https://sdi.georchestra.org:8443////", "////geo/web/cache/////", "/geowebcache-private");
        assertEquals("https://sdi.georchestra.org:8443/geo/web/cache/service/wmts///",
                build(gum, "http://localhost:8080////", "/geowebcache-private/", "///service/wmts///"));

        // empty public context path
        gum = mangler("https://sdi.georchestra.org/", "", "/geowebcache");
        assertEquals("https://sdi.georchestra.org/service/wmts",
                build(gum, "http://localhost:8080", "/geowebcache", "/service/wmts"));

        // webapp deployed as ROOT
        gum = mangler("https://sdi.georchestra.org", "/geowebcache", "");
        assertEquals("https://sdi.georchestra.org/geowebcache/service/wmts",
                build(gum, "http://localhost:8080", "", "/service/wmts"));
    }

    @Test
    public void testQueryParametersArePreserved() {
        URLMangler gum = mangler("https://sdi.georchestra.org", "/geowebcache", "/geowebcache");
        assertEquals("https://sdi.georchestra.org/geowebcache/service/wms?SERVICE=WMS&",
                build(gum, "http://localhost:8080", "/geowebcache", "/service/wms?SERVICE=WMS&"));
    }
}
