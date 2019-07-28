package com.bpodgursky.jbool_expressions.rules;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RuleList<K> {

  private final List<Rule<?, K>> rules;
  private final String key;

  public RuleList(List<Rule<?, K>> rules) {
    this.rules = rules;

    List<String> names = new ArrayList<>();

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      for (Rule<?, K> rule : rules) {
        names.add(rule.getClass().getName());
      }

      byte[] hash = digest.digest(String.join(",", names).getBytes());

      this.key = new String(hash);

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

  }

  public List<Rule<?, K>> getRules() {
    return rules;
  }

  public String getKey() {
    return key;
  }
}
