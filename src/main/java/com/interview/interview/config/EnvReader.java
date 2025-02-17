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
        String[] parts = line.split("=");
        envEntries.add(new EnvEntry(parts[0].trim(), parts[1].trim()));
      }
    } catch (Exception e) {
      throw new RuntimeException("Error reading env file");
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
