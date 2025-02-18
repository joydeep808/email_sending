package com.interview.interview.config;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.interview.interview.dto.EnvEntry;

@Configuration
@Component
public class EnvReader {

  List<EnvEntry> envEntries = new ArrayList<>();

  public EnvReader() {
    try {
      String userDir = System.getProperty("user.dir");
      List<String> allLines = Files.readAllLines(Paths.get(userDir + "/.env"));

      for (String line : allLines) {
        // Trim leading and trailing whitespace
        String trimmedLine = line.trim();

        // Skip empty lines
        if (trimmedLine.isEmpty()) {
          continue;
        }

        // Split by the first '=' and ensure there are exactly two parts (key and value)
        String[] parts = trimmedLine.split("=", 2);
        if (parts.length == 2) {
          String key = parts[0].trim();
          String value = parts[1].trim();
          envEntries.add(new EnvEntry(key, value));
        } else {
          // Handle malformed lines (without '=')
          System.err.println("Skipping malformed line: " + trimmedLine);
        }
      }
    } catch (Exception e) {
      throw new RuntimeException("Error reading .env file", e);
    }
  }

  public List<EnvEntry> getEnvEntries() {
    return envEntries;
  }

  @SuppressWarnings("unchecked")
  public <T> T getEnvEntry(String key) {
    EnvEntry entry = envEntries.stream().filter(e -> e.getKey().equals(key)).findFirst().orElse(null);
    if (entry == null) {
      return null;
    }
    return (T) entry.getValue().trim();
  }

}
