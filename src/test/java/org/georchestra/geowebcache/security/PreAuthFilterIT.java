package org.georchestra.geowebcache.security;

import static org.georchestra.commons.security.SecurityHeaders.SEC_ROLES;
import static org.georchestra.commons.security.SecurityHeaders.SEC_USERNAME;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** @author Jesse on 4/24/2014. */
public class PreAuthFilterIT {

    private final PreAuthFilter preAuthFilter = new PreAuthFilter();

    @Test
    public void testDoFilter() throws Exception {
        SecurityContextHolder.clearContext();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        ServletResponse response = Mockito.mock(ServletResponse.class);

        FilterChain chain = Mockito.mock(FilterChain.class);
        preAuthFilter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        final String username = "username";
        Mockito.when(request.getHeader(SEC_USERNAME)).thenReturn(username);
        final String roleAdmin = "ROLE_ADMINISTRATOR";
        final String roleOther = "ROLE_OTHER";

        Mockito.when(request.getHeader(SEC_ROLES)).thenReturn(roleAdmin + ";" + roleOther);

        chain = Mockito.mock(FilterChain.class);
        preAuthFilter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(auth instanceof PreAuthToken);
        PreAuthToken preAuthToken = (PreAuthToken) auth;

        assertEquals(username, preAuthToken.getPrincipal());
        assertEquals(2, preAuthToken.getAuthorities().size());
        Set<String> authorities = preAuthToken.getAuthorities().stream().map(a -> a.getAuthority()).collect(Collectors.toSet());
        assertTrue(authorities.contains(roleAdmin));
        assertTrue(authorities.contains(roleOther));
    }
}
