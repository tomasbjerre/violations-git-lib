package se.bjurr.violations.git;

import java.io.File;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import se.bjurr.violations.git.data.DiffsPerFile;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.lib.util.PatchParserUtil;

public class ViolationsGit {
  private final Set<Violation> violations;
  private final ViolationsLogger logger;

  public ViolationsGit(final ViolationsLogger logger, final Set<Violation> violations) {
    this.violations = violations;
    this.logger = logger;
  }

  public Set<Violation> getViolationsInChangeset(
      final File file, final String from, final String to) throws Exception {
    final DiffsPerFile diffs = ViolationsGitRepo.diff(file, from, to);
    final Set<Violation> filtered = new TreeSet<>();
    for (final Violation candidate : this.violations) {
      final Optional<String> patchStringOpt = diffs.findPatchString(candidate.getFile());
      this.logger.log(Level.FINE, "Checking if candidate " + candidate.getFile() + " is in diff");
      if (patchStringOpt.isPresent()) {
        final String patchString = patchStringOpt.get();
        final Integer violatedLine = candidate.getStartLine();
        if (new PatchParserUtil(patchString).isLineInDiff(violatedLine)) {
          filtered.add(candidate);
        } else {
          this.logger.log(
              Level.FINE,
              "Violated line ("
                  + violatedLine
                  + ") in "
                  + candidate.getFile()
                  + " not found in patch: "
                  + patchString);
        }
      } else {
        this.logger.log(Level.FINE, "Candidate not found in diff: " + candidate.getFile());
      }
    }
    return filtered;
  }
}
