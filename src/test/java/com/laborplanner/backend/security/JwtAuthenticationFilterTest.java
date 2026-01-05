package com.laborplanner.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtAuthenticationFilterTest {

   private JwtService jwtService;
   private UserDetailsService userDetailsService;

   private JwtAuthenticationFilter filter;

   private HttpServletRequest request;
   private HttpServletResponse response;
   private FilterChain chain;

   @BeforeEach
   void setup() {
      MockitoAnnotations.openMocks(this);
      SecurityContextHolder.clearContext();

      jwtService = mock(JwtService.class);
      userDetailsService = mock(UserDetailsService.class);

      filter = new JwtAuthenticationFilter(jwtService, userDetailsService);

      request = mock(HttpServletRequest.class);
      response = mock(HttpServletResponse.class);
      chain = mock(FilterChain.class);
   }

   @AfterEach
   void tearDown() {
      SecurityContextHolder.clearContext();
   }

   @Test
   void doFilterInternal_whenNoAuthorizationHeader_continuesWithoutAuth() throws Exception {
      when(request.getHeader("Authorization")).thenReturn(null);

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      assertNull(SecurityContextHolder.getContext().getAuthentication());
      verifyNoInteractions(jwtService, userDetailsService);
   }

   @Test
   void doFilterInternal_whenHeaderNotBearer_continuesWithoutAuth() throws Exception {
      when(request.getHeader("Authorization")).thenReturn("Basic abc");

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      assertNull(SecurityContextHolder.getContext().getAuthentication());
      verifyNoInteractions(jwtService, userDetailsService);
   }

   @Test
   void doFilterInternal_whenBearerAndValidToken_setsAuthentication() throws Exception {
      when(request.getHeader("Authorization")).thenReturn("Bearer token123");
      when(jwtService.extractUsername("token123")).thenReturn("user@mail.com");

      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@mail.com");
      when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

      when(userDetailsService.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
      when(jwtService.isTokenValid("token123", userDetails)).thenReturn(true);

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);

      assertNotNull(SecurityContextHolder.getContext().getAuthentication());
      assertTrue(SecurityContextHolder.getContext().getAuthentication() instanceof UsernamePasswordAuthenticationToken);

      UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
            .getContext().getAuthentication();
      assertEquals(userDetails, auth.getPrincipal());
      assertNotNull(auth.getDetails(), "WebAuthenticationDetails should be set");
   }

   @Test
   void doFilterInternal_whenBearerButInvalidToken_doesNotSetAuthentication() throws Exception {
      when(request.getHeader("Authorization")).thenReturn("Bearer token123");
      when(jwtService.extractUsername("token123")).thenReturn("user@mail.com");

      UserDetails userDetails = mock(UserDetails.class);
       when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

       when(userDetailsService.loadUserByUsername("user@mail.com")).thenReturn(userDetails);
      when(jwtService.isTokenValid("token123", userDetails)).thenReturn(false);

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      assertNull(SecurityContextHolder.getContext().getAuthentication());
   }

   @Test
   void doFilterInternal_whenAuthenticationAlreadyPresent_skipsLoadingUserAndValidation() throws Exception {
      UsernamePasswordAuthenticationToken existingAuth = new UsernamePasswordAuthenticationToken("existing", null,
            List.of());
      SecurityContextHolder.getContext().setAuthentication(existingAuth);

      when(request.getHeader("Authorization")).thenReturn("Bearer token123");
      when(jwtService.extractUsername("token123")).thenReturn("user@mail.com");

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      // Should not call userDetailsService/jwt validation when auth already set
      verify(userDetailsService, never()).loadUserByUsername(anyString());
      verify(jwtService, never()).isTokenValid(anyString(), any());
      assertEquals(existingAuth, SecurityContextHolder.getContext().getAuthentication());
   }

   @Test
   void doFilterInternal_whenExtractedUsernameNull_doesNotAuthenticate() throws Exception {
      when(request.getHeader("Authorization")).thenReturn("Bearer token123");
      when(jwtService.extractUsername("token123")).thenReturn(null);

      filter.doFilter(request, response, chain);

      verify(chain).doFilter(request, response);
      verifyNoInteractions(userDetailsService);
      assertNull(SecurityContextHolder.getContext().getAuthentication());
   }
}
