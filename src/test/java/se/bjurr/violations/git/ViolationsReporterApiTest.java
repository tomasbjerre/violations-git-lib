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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import se.bjurr.violations.lib.model.Violation;

public class ViolationsReporterApiTest {

  private static Logger LOG = Logger.getLogger(ViolationsReporterApiTest.class.getName());

  private List<Violation> findbugsViolations;
  private List<Violation> pmdViolations;
  private final List<Violation> accumulatedViolations = new ArrayList<>();
  private List<Violation> perlCriticViolations;

  public static String getRootFolder() {
    return new File(
            ViolationsReporterApiTest.class.getClassLoader().getResource("root.txt").getFile())
        .getParent();
  }

  @Rule public TestName name = new TestName();

  @Before
  public void before() {
    final String rootFolder = getRootFolder();

    findbugsViolations =
        violationsApi() //
            .withPattern(".*/findbugs/main\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(FINDBUGS) //
            .violations();
    accumulatedViolations.addAll(findbugsViolations);

    pmdViolations =
        violationsApi() //
            .withPattern(".*/pmd/main\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(PMD) //
            .violations();

    pmdViolations =
        violationsApi() //
            .withPattern(".*/pmd/main\\.xml$") //
            .inFolder(rootFolder) //
            .findAll(PMD) //
            .violations();
    accumulatedViolations.addAll(pmdViolations);

    perlCriticViolations =
        violationsApi() //
            .withPattern(".*/perlcritic/.*\\.txt$") //
            .inFolder(rootFolder) //
            .findAll(PERLCRITIC) //
            .violations();
    accumulatedViolations.addAll(perlCriticViolations);
    assertThat(accumulatedViolations).isNotEmpty();
    LOG.info("\n\n\n " + this.name.getMethodName() + " \n\n\n");
  }

  @Test
  public void testCompact() {
    final String report =
        violationsReporterApi() //
            .withViolations(accumulatedViolations) //
            .getReport(COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testPerFileCompact() {
    final String report =
        violationsReporterApi() //
            .withViolations(accumulatedViolations) //
            .getReport(PER_FILE_COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testVerbose() {
    final String report =
        violationsReporterApi() //
            .withViolations(accumulatedViolations) //
            .getReport(VERBOSE);

    LOG.info("\n" + report);
  }

  @Test
  public void testVerboseLimitations() {
    final String report =
        violationsReporterApi() //
            .withViolations(accumulatedViolations) //
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
            .withViolations(new ArrayList<Violation>()) //
            .getReport(COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testPerFileCompactWithZeroViolations() {
    final String report =
        violationsReporterApi() //
            .withViolations(new ArrayList<Violation>()) //
            .getReport(PER_FILE_COMPACT);

    LOG.info("\n" + report);
  }

  @Test
  public void testVerboseWithZeroViolations() {
    final String report =
        violationsReporterApi() //
            .withViolations(new ArrayList<Violation>()) //
            .getReport(VERBOSE);

    LOG.info("\n" + report);
  }
}
