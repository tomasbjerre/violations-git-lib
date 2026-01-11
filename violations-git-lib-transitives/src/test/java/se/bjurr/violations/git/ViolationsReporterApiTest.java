package se.bjurr.violations.git;

import static org.assertj.core.api.Assertions.assertThat;
import static se.bjurr.violations.git.ViolationsReporterApi.violationsReporterApi;
import static se.bjurr.violations.git.ViolationsReporterDetailLevel.COMPACT;
import static se.bjurr.violations.git.ViolationsReporterDetailLevel.PER_FILE_COMPACT;
import static se.bjurr.violations.git.ViolationsReporterDetailLevel.VERBOSE;
import static se.bjurr.violations.lib.ViolationsApi.violationsApi;
import static se.bjurr.violations.lib.reports.Parser.FINDBUGS;
import static se.bjurr.violations.lib.reports.Parser.PERLCRITIC;
import static se.bjurr.violations.lib.reports.Parser.PMD;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import se.bjurr.violations.lib.model.Violation;

public class ViolationsReporterApiTest {

  private static Logger LOG = Logger.getLogger(ViolationsReporterApiTest.class.getName());

  private Set<Violation> findbugsViolations;
  private Set<Violation> pmdViolations;
  private final Set<Violation> accumulatedViolations = new TreeSet<>();
  private Set<Violation> perlCriticViolations;

  public static String getRootFolder() {
    return new File(
            ViolationsReporterApiTest.class.getClassLoader().getResource("root.txt").getFile())
        .getParent();
  }

  private TestInfo testInfo;

  @BeforeEach
  public void before(TestInfo testInfo) {
    this.testInfo = testInfo;
    final String rootFolder = getRootFolder();

    this.findbugsViolations =
        violationsApi() //
            .withPattern(".*/findbugs/main\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(FINDBUGS) //
            .violations();
    this.accumulatedViolations.addAll(this.findbugsViolations);

    this.pmdViolations =
        violationsApi() //
            .withPattern(".*/pmd/main\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(PMD) //
            .violations();

    this.pmdViolations =
        violationsApi() //
            .withPattern(".*/pmd/main\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(PMD) //
            .violations();
    this.accumulatedViolations.addAll(this.pmdViolations);

    this.perlCriticViolations =
        violationsApi() //
            .withPattern(".*/perlcritic/.*\\.txt$") //
            .inFolder(rootFolder) //
            .findAll(PERLCRITIC) //
            .violations();
    this.accumulatedViolations.addAll(this.perlCriticViolations);
    assertThat(this.accumulatedViolations).isNotEmpty();
    LOG.info("\n\n\n " + this.testInfo.getDisplayName() + " \n\n\n");
  }

  @Test
  public void testCompact() {
    final String report =
        violationsReporterApi() //
            .withViolations(this.accumulatedViolations) //
            .getReport(COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testPerFileCompact() {
    final String report =
        violationsReporterApi() //
            .withViolations(this.accumulatedViolations) //
            .getReport(PER_FILE_COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testVerbose() {
    final String report =
        violationsReporterApi() //
            .withViolations(this.accumulatedViolations) //
            .getReport(VERBOSE);

    LOG.info("\n" + report);
  }

  @Test
  public void testVerboseLimitations() {
    final String report =
        violationsReporterApi() //
            .withViolations(this.accumulatedViolations) //
            .withMaxReporterColumnWidth(20) //
            .withMaxRuleColumnWidth(50) //
            .withMaxSeverityColumnWidth(20) //
            .withMaxLineColumnWidth(10) //
            .withMaxMessageColumnWidth(50) //
            .getReport(VERBOSE);

    LOG.info("\n" + report);
  }

  @Test
  public void testCompactWithZeroViolations() {
    final String report =
        violationsReporterApi() //
            .withViolations(new TreeSet<Violation>()) //
            .getReport(COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testPerFileCompactWithZeroViolations() {
    final String report =
        violationsReporterApi() //
            .withViolations(new TreeSet<Violation>()) //
            .getReport(PER_FILE_COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testVerboseWithZeroViolations() {
    final String report =
        violationsReporterApi() //
            .withViolations(new TreeSet<Violation>()) //
            .getReport(VERBOSE);

    LOG.info("\n" + report);
  }
}
