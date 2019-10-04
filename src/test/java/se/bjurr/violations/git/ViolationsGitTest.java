package se.bjurr.violations.git;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;
import static se.bjurr.violations.lib.model.Violation.violationBuilder;
import static se.bjurr.violations.lib.reports.Parser.ANDROIDLINT;
import static se.bjurr.violations.lib.reports.Parser.CPD;

import java.io.File;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.bjurr.violations.lib.model.Violation;

public class ViolationsGitTest {
  private static Logger LOG = LoggerFactory.getLogger(ViolationsGitTest.class);

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
    final List<Violation> violations =
        newArrayList( //
            violation1, //
            violation2 //
            );
    final ViolationsGit violationsGit = new ViolationsGit(violations);

    final List<Violation> filteredViolations =
        violationsGit.getViolationsInChangeset(repo, from, to);

    assertThat(filteredViolations) //
        .containsOnly(violation2);
  }
}
