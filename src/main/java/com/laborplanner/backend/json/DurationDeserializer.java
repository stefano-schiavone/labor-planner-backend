package com.laborplanner.backend.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.time.Duration;

/**
 * Flexible deserializer for java.time.Duration:
 * - If JSON is a NUMBER -> treat as minutes (fits frontend constraint)
 * - If JSON is a STRING -> parse ISO-8601 (Duration.parse)
 * - If JSON is an OBJECT -> try to extract numeric "seconds" and "nano"/"nanos"
 * fields
 *
 * Register this module AFTER JavaTimeModule so it overrides the default jsr310
 * Duration deserializer.
 */
public class DurationDeserializer extends JsonDeserializer<Duration> {

   @Override
   public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken token = p.currentToken();

      if (token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT) {
         // Treat numeric values as minutes (preferred from UI)
         long minutes = p.getLongValue();
         return Duration.ofMinutes(minutes);
      }

      if (token == JsonToken.VALUE_STRING) {
         String text = p.getText().trim();
         if (text.isEmpty()) {
            return null;
         }
         // ISO-8601 text e.g. "PT1H30M"
         return Duration.parse(text);
      }

      if (token == JsonToken.START_OBJECT) {
         ObjectNode node = p.getCodec().readTree(p);

         // Common protobuf-like or custom shapes may expose seconds/nano fields
         if (node.has("seconds") || node.has("nano") || node.has("nanos")) {
            long seconds = 0L;
            int nanos = 0;

            if (node.has("seconds") && node.get("seconds").isNumber()) {
               seconds = node.get("seconds").asLong(0L);
            } else if (node.has("seconds") && node.get("seconds").isTextual()) {
               // sometimes numbers are stringified
               try {
                  seconds = Long.parseLong(node.get("seconds").asText());
               } catch (NumberFormatException ignored) {
               }
            }

            if (node.has("nano") && node.get("nano").isNumber()) {
               nanos = node.get("nano").asInt(0);
            } else if (node.has("nanos") && node.get("nanos").isNumber()) {
               nanos = node.get("nanos").asInt(0);
            } else if (node.has("nano") && node.get("nano").isTextual()) {
               try {
                  nanos = Integer.parseInt(node.get("nano").asText());
               } catch (NumberFormatException ignored) {
               }
            }

            return Duration.ofSeconds(seconds, nanos);
         }

         // Fallback: if object contains an ISO string under a field named "duration"
         if (node.has("duration") && node.get("duration").isTextual()) {
            String text = node.get("duration").asText();
            return Duration.parse(text);
         }

         // If we can't extract a duration, throw an informative error so caller can see
         // payload shape
         throw JsonMappingException.from(p,
               "Unrecognized Duration object shape. Expected numeric 'seconds' and optional 'nano' fields or an ISO string; found: "
                     + node.toString());
      }

      // unexpected token -> delegate to default handling (will produce a helpful
      // exception)
      return (Duration) ctxt.handleUnexpectedToken(Duration.class, p);
   }
}
