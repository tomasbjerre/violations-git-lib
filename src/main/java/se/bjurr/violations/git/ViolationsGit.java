package se.bjurr.violations.git;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import se.bjurr.violations.git.data.DiffsPerFile;
import se.bjurr.violations.lib.model.Violation;

public class ViolationsGit {
  private final Set<Violation> violations;

  public ViolationsGit(final Set<Violation> violations) {
    this.violations = violations;
  }

  public Set<Violation> getViolationsInChangeset(
      final File file, final String from, final String to) throws Exception {
    final DiffsPerFile diffs = ViolationsGitRepo.diff(file, from, to);
    final Set<Violation> filtered = new TreeSet<>();
    for (final Violation candidate : this.violations) {
      final Optional<String> patchStringOpt = diffs.findPatchString(candidate.getFile());
      if (patchStringOpt.isPresent()) {
        final String patchString = patchStringOpt.get();
        final Integer violatedLine = candidate.getStartLine();
        final Optional<Integer> lineOpt = PatchParser.findLineInDiff(patchString, violatedLine);
        if (lineOpt.isPresent()) {
          filtered.add(candidate);
        }
      }
    }
    return filtered;
  }
}
