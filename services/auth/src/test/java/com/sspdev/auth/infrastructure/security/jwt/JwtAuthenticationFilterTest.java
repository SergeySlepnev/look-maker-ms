package com.sspdev.auth.infrastructure.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider tokenProvider;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    private JwtAuthenticationFilter authenticationFilter;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_shouldSetAuthentication_whenTokenIsValid() throws Exception {
        var header = "Bearer validJwtToken";
        var token = "validJwtToken";
        when(request.getHeader("Authorization")).thenReturn(header);
        var userId = UUID.randomUUID();
        var roles = List.of("ADMIN", "USER");

        when(tokenProvider.validateToken(token)).thenReturn(true);
        when(tokenProvider.getUserId(token)).thenReturn(userId);
        when(tokenProvider.getRoles(token)).thenReturn(roles);

        authenticationFilter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userId);

        var authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities).containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_USER");
    }

    @Test
    void doFilterInternal_shouldProceedWithoutAuthentication_whenHeaderAbsent() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_shouldProceedWithoutAuthentication_whenTokenInvalid() throws Exception {
        var header = "Bearer invalidToken";
        var invalidToken = "invalidToken";
        when(request.getHeader("Authorization")).thenReturn(header);

        when(tokenProvider.validateToken(invalidToken)).thenReturn(false);

        authenticationFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }
}