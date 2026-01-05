package com.laborplanner.backend.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.laborplanner.backend.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {

   private JwtService jwtService;

   private static final String TEST_SECRET_BASE64 = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
   private static final long ONE_HOUR_MS = 60 * 60 * 1000L;

   @BeforeEach
   void setup() throws Exception {
      jwtService = new JwtService();
      setField(jwtService, "secret", TEST_SECRET_BASE64);
      setField(jwtService, "jwtExpiration", ONE_HOUR_MS);
   }

   @Test
   void generateToken_and_extractUsername_roundTrip() {
      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@example.com");

      String token = jwtService.generateToken(userDetails);

      assertNotNull(token);
      assertEquals("user@example.com", jwtService.extractUsername(token));
   }

   @Test
   void generateToken_withExtraClaims_includesClaims() {
      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@example.com");

      String token = jwtService.generateToken(Map.of("role", "ADMIN"), userDetails);

      String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
      assertEquals("ADMIN", role);
   }

   @Test
   void isTokenValid_whenUsernameMatches_andNotExpired_true() {
      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@example.com");

      String token = jwtService.generateToken(userDetails);

      assertTrue(jwtService.isTokenValid(token, userDetails));
   }

   @Test
   void isTokenValid_whenUsernameDoesNotMatch_false() {
      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@example.com");

      String token = jwtService.generateToken(userDetails);

      UserDetails other = mock(UserDetails.class);
      when(other.getUsername()).thenReturn("other@example.com");

      assertFalse(jwtService.isTokenValid(token, other));
   }

   @Test
   void isTokenValid_whenExpired_throwsExpiredJwtException() throws Exception {
      // Make token already expired.
      setField(jwtService, "jwtExpiration", -1L);

      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@example.com");

      String token = jwtService.generateToken(userDetails);

      assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, userDetails));
   }

   @Test
   void extractClaim_canReadExpiration() {
      UserDetails userDetails = mock(UserDetails.class);
      when(userDetails.getUsername()).thenReturn("user@example.com");

      String token = jwtService.generateToken(userDetails);

      Date exp = jwtService.extractClaim(token, claims -> claims.getExpiration());
      assertNotNull(exp);
      assertTrue(exp.after(new Date(System.currentTimeMillis() - 1000)));
   }

   private static void setField(Object target, String fieldName, Object value) throws Exception {
      Field f = target.getClass().getDeclaredField(fieldName);
      f.setAccessible(true);
      f.set(target, value);
   }
}
