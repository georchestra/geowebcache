package org.geowebcache.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/** @author Jesse on 4/24/2014. */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
    "file*:/webapp/WEB-INF/web.xml",
    "file*:/webapp/WEB-INF/geowebcache-servlet.xml"
})
public class PreAuthFilterTest {

    @Autowired private PreAuthFilter preAuthFilter;

    @BeforeClass
    public static void before() {}

    @Test
    public void testDoFilter() throws Exception {
        SecurityContextHolder.clearContext();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = Mockito.mock(FilterChain.class);
        preAuthFilter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        final String username = "username";
        request.addHeader("sec-username", username);
        final String roleAdmin = "ROLE_ADMINISTRATOR";
        final String roleOther = "ROLE_OTHER";

        request.addHeader("sec-roles", roleAdmin + ";" + roleOther);

        chain = Mockito.mock(FilterChain.class);
        preAuthFilter.doFilter(request, response, chain);
        Mockito.verify(chain, Mockito.times(1)).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertTrue(auth instanceof PreAuthToken);
        PreAuthToken preAuthToken = (PreAuthToken) auth;

        assertEquals(username, preAuthToken.getPrincipal());
        assertEquals(2, preAuthToken.getAuthorities().size());
        List<GrantedAuthority> authorities =
                preAuthToken.getAuthorities().stream().collect(Collectors.toList());
        assertEquals(roleAdmin, authorities.get(0).getAuthority());
        assertEquals(roleOther, authorities.get(1).getAuthority());
    }
}
