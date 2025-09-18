package se.bjurr.violations.git;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.ANDROIDLINT;
import static se.bjurr.violations.lib.reports.Parser.CPD;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.violations.lib.ViolationsLogger;
import se.bjurr.violations.lib.model.Violation;

public class ViolationsGitTest {
  private static Logger LOG = LoggerFactory.getLogger(ViolationsGitTest.class);
  private final ViolationsLogger logger =
      new ViolationsLogger() {

        @Override
        public void log(final Level level, final String string, final Throwable t) {
          LOG.info(level + " " + string + " ", t);
        }

        @Override
        public void log(final Level level, final String string) {
          LOG.info(level + " " + string);
        }
      };

  // @Test //Failing in Travis
  public void testThatPathchStringCanBeConstructed() throws Exception {
    final File repo = new File(".");
    final String from = "525ad7f";
    final String to = "e98d8da";

    final Violation violation1 =
        violationBuilder() //
            .setFile("build.gradle") //
            .setParser(ANDROIDLINT) //
            .setSeverity(ERROR) //
            .setStartLine(2) //
            .setMessage("asd") //
            .build();
    final Violation violation2 =
        violationBuilder() //
            .setFile("build.gradle") //
            .setParser(CPD) //
            .setSeverity(INFO) //
            .setStartLine(23) //
            .setMessage("asd") //
            .build();
    final Set<Violation> violations = new TreeSet<>();
    violations.add(violation1);
    violations.add(violation2);
    final ViolationsGit violationsGit = new ViolationsGit(this.logger, violations);

    final Set<Violation> filteredViolations =
        violationsGit.getViolationsInChangeset(repo, from, to);

    assertThat(filteredViolations) //
        .containsOnly(violation2);
  }
}
