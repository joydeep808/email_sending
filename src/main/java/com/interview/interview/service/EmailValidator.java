package com.interview.interview.service;

import org.xbill.DNS.*;
import org.xbill.DNS.Record;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmailValidator {

  /**
   * Validates an email address by checking:
   * 1. Basic format validation
   * 2. DNS MX record lookup for the domain
   * 
   * @param email The email address to validate
   * @return true if email is valid and domain has MX records, false otherwise
   */
  public boolean isEmailValid(String email) {
    if (!isValidEmailFormat(email)) {
      return false;
    }

    String domain = getDomainFromEmail(email);
    return hasMXRecord(domain);
  }

  /**
   * Checks basic email format using regex
   */
  private boolean isValidEmailFormat(String email) {
    String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    return email != null && email.matches(regex);
  }

  /**
   * Extracts domain from email address
   */
  private String getDomainFromEmail(String email) {
    return email.substring(email.indexOf('@') + 1);
  }

  /**
   * Looks up MX records for the given domain
   */
  public boolean hasMXRecord(String domain) {
    try {
      Record[] records = new Lookup(domain, Type.MX).run();
      return records != null && records.length > 0;
    } catch (TextParseException e) {
      return false;
    }
  }

  /**
   * Gets all MX records for a domain with their priorities
   */
  public List<MXRecord> getMXRecords(String domain) {
    List<MXRecord> mxRecords = new ArrayList<>();
    try {
      Record[] records = new Lookup(domain, Type.MX).run();
      if (records != null) {
        for (Record record : records) {
          if (record instanceof MXRecord) {
            mxRecords.add((MXRecord) record);
          }
        }
      }
    } catch (TextParseException e) {
      // Handle exception
    }
    return mxRecords;
  }
}