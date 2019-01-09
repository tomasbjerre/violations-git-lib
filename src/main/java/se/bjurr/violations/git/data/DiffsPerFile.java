package se.bjurr.violations.git.data;

import static java.lang.Integer.MAX_VALUE;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class DiffsPerFile {

  private final Map<String, String> patchPerFile;

  public DiffsPerFile(final Map<String, String> patchPerFile) {
    this.patchPerFile = patchPerFile;
  }

  public Optional<String> findPatchString(final String file) {
    Integer pathLength = MAX_VALUE;
    String patchString = null;
    for (final Entry<String, String> candidate : patchPerFile.entrySet()) {
      final String candidateFile = candidate.getKey();
      if (candidateFile.endsWith(file) || file.endsWith(candidateFile)) {
        final Integer candidatePathLength = candidateFile.length();
        if (candidatePathLength < pathLength) {
          patchString = candidate.getValue();
          pathLength = candidatePathLength;
        }
      }
    }
    return Optional.ofNullable(patchString);
  }
}
