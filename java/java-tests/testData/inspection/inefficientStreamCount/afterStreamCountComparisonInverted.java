// "Replace with 'stream.anyMatch()'" "true-preview"

import java.util.Arrays;

class Test {
  boolean anyMatch() {
    return Arrays.asList("ds", "e", "fe").stream().anyMatch(s -> s.length() > 1);
  }
}