package se.bjurr.violations.git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import se.bjurr.violations.git.data.DiffsPerFile;
import se.bjurr.violations.lib.model.Violation;

public class ViolationsGit {
  private final List<Violation> violations;

  public ViolationsGit(final List<Violation> violations) {
    this.violations = violations;
  }

  public List<Violation> getViolationsInChangeset(
      final File file, final String from, final String to) throws Exception {
    final DiffsPerFile diffs = ViolationsGitRepo.diff(file, from, to);
    final List<Violation> filtered = new ArrayList<>();
    for (final Violation candidate : violations) {
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
