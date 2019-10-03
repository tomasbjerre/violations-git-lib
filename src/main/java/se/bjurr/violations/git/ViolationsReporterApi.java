package se.bjurr.violations.git;

import static se.bjurr.violations.git.ViolationsReporterDetailLevel.COMPACT;
import static se.bjurr.violations.git.ViolationsReporterDetailLevel.PER_FILE_COMPACT;
import static se.bjurr.violations.git.ViolationsReporterDetailLevel.VERBOSE;
import static se.bjurr.violations.lib.model.SEVERITY.ERROR;
import static se.bjurr.violations.lib.model.SEVERITY.INFO;
import static se.bjurr.violations.lib.model.SEVERITY.WARN;
import static se.bjurr.violations.lib.util.Utils.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import se.bjurr.violations.lib.model.SEVERITY;
import se.bjurr.violations.lib.model.Violation;
import se.bjurr.violations.table.ViolationsTable;

public class ViolationsReporterApi {

  private Iterable<Violation> violations;
  private int maxReporterColumnWidth;
  private int maxRuleColumnWidth = 10;
  private int maxSeverityColumnWidth;
  private int maxLineColumnWidth;
  private int maxMessageColumnWidth = 50;

  private ViolationsReporterApi() {}

  public static ViolationsReporterApi violationsReporterApi() {
    return new ViolationsReporterApi();
  }

  public String getReport(final ViolationsReporterDetailLevel level) {
    checkNotNull(violations, "violations");

    final StringBuilder sb = new StringBuilder();

    if (level == COMPACT) {
      sb.append(toCompact(violations, "Summary"));
    } else if (level == PER_FILE_COMPACT) {
      sb.append(toPerFile(violations));
      sb.append(toCompact(violations, "Summary"));
    } else if (level == VERBOSE) {
      sb.append(toVerbose(violations));
      sb.append(toCompact(violations, "Summary"));
    }

    return sb.toString();
  }

  private StringBuilder toVerbose(final Iterable<Violation> violations) {
    final StringBuilder sb = new StringBuilder();
    final Map<String, Set<Violation>> perFile = getViolationsPerFile(violations);
    for (final Entry<String, Set<Violation>> perFileEntry : perFile.entrySet()) {
      final String file = perFileEntry.getKey();
      final Set<Violation> fileViolations = perFile.get(file);
      sb.append(file + "\n");
      sb.append(toDetailed(fileViolations, "Summary of " + file));
      sb.append("\n");
    }
    return sb;
  }

  private StringBuilder toPerFile(final Iterable<Violation> violations) {
    final StringBuilder sb = new StringBuilder();
    final Map<String, Set<Violation>> perFile = getViolationsPerFile(violations);
    for (final Entry<String, Set<Violation>> fileEntry : perFile.entrySet()) {
      final Set<Violation> fileViolations = fileEntry.getValue();
      final String fileName = fileEntry.getKey();
      sb.append(toCompact(fileViolations, "Summary of " + fileName));
      sb.append("\n");
    }
    return sb;
  }

  public ViolationsReporterApi withViolations(final List<Violation> violations) {
    this.violations = violations;
    return this;
  }

  /**
   * Avoid wider column. Will add new lines if wider. A value of 0 or less will disable the feature.
   */
  public ViolationsReporterApi withMaxMessageColumnWidth(final int maxMessageColumnWidth) {
    this.maxMessageColumnWidth = maxMessageColumnWidth;
    return this;
  }

  /**
   * Avoid wider column. Will add new lines if wider. A value of 0 or less will disable the feature.
   */
  public ViolationsReporterApi withMaxLineColumnWidth(final int maxLineColumnWidth) {
    this.maxLineColumnWidth = maxLineColumnWidth;
    return this;
  }

  /**
   * Avoid wider column. Will add new lines if wider. A value of 0 or less will disable the feature.
   */
  public ViolationsReporterApi withMaxReporterColumnWidth(final int maxReporterColumnWidth) {
    this.maxReporterColumnWidth = maxReporterColumnWidth;
    return this;
  }

  /**
   * Avoid wider column. Will add new lines if wider. A value of 0 or less will disable the feature.
   */
  public ViolationsReporterApi withMaxRuleColumnWidth(final int maxRuleColumnWidth) {
    this.maxRuleColumnWidth = maxRuleColumnWidth;
    return this;
  }

  /** Avoid wider column. Will add new lines if wider. */
  public ViolationsReporterApi withMaxSeverityColumnWidth(final int maxSeverityColumnWidth) {
    this.maxSeverityColumnWidth = maxSeverityColumnWidth;
    return this;
  }

  private StringBuilder toDetailed(
      final Iterable<Violation> violations, final String summarySubject) {
    final StringBuilder sb = new StringBuilder();
    final List<String[]> rows = new ArrayList<>();
    for (final Violation violation : violations) {
      final String message = nullToEmpty(violation.getMessage());
      final String line = nullToEmpty(violation.getStartLine().toString());
      final String severity = nullToEmpty(violation.getSeverity().name());
      final String rule = nullToEmpty(violation.getRule());
      final String reporter = nullToEmpty(violation.getReporter());
      final String[] row = {reporter, rule, severity, line, message};
      rows.add(row);
    }

    final String[] headers = {"Reporter", "Rule", "Severity", "Line", "Message"};

    final String[][] data = rows.toArray(new String[][] {});
    final int[] columnWidths = {
      zeroToMax(maxReporterColumnWidth),
      zeroToMax(maxRuleColumnWidth),
      zeroToMax(maxSeverityColumnWidth),
      zeroToMax(maxLineColumnWidth),
      zeroToMax(maxMessageColumnWidth)
    };
    sb.append(ViolationsTable.of(headers, data, columnWidths));
    sb.append("\n");
    sb.append(toCompact(violations, summarySubject));
    sb.append("\n");
    return sb;
  }

  private int zeroToMax(final int i) {
    return i == 0 ? Integer.MAX_VALUE : i;
  }

  private String nullToEmpty(final String message) {
    if (message == null) {
      return "";
    }
    return message.trim();
  }

  private StringBuilder toCompact(final Iterable<Violation> violations, final String subject) {
    final StringBuilder sb = new StringBuilder();
    final Map<String, Set<Violation>> perReporter = getViolationsPerReporter(violations);
    final List<String[]> rows = new ArrayList<>();

    Integer totNumInfo = 0;
    Integer totNumWarn = 0;
    Integer totNumError = 0;
    Integer totNumTot = 0;

    for (final Entry<String, Set<Violation>> reporterEntry : perReporter.entrySet()) {
      final String reporter = reporterEntry.getKey();
      final Set<Violation> reporterViolations = reporterEntry.getValue();
      final Map<SEVERITY, Set<Violation>> perSeverity =
          getViolationsPerSeverity(reporterViolations);
      final Integer numInfo = perSeverity.get(INFO).size();
      final Integer numWarn = perSeverity.get(WARN).size();
      final Integer numError = perSeverity.get(ERROR).size();
      final Integer numTot = numInfo + numWarn + numError;
      final String[] row = {
        reporter, numInfo.toString(), numWarn.toString(), numError.toString(), numTot.toString()
      };
      rows.add(row);

      totNumInfo += numInfo;
      totNumWarn += numWarn;
      totNumError += numError;
      totNumTot += numTot;
    }
    final String[] row = {
      "", totNumInfo.toString(), totNumWarn.toString(), totNumError.toString(), totNumTot.toString()
    };
    rows.add(row);

    final String[] headers = {"Reporter", INFO.name(), WARN.name(), ERROR.name(), "Total"};

    final String[][] data = rows.toArray(new String[][] {});
    sb.append(subject + "\n");
    final int[] columnWidths = {};
    sb.append(ViolationsTable.of(headers, data, columnWidths));
    sb.append("\n");
    return sb;
  }

  private Map<SEVERITY, Set<Violation>> getViolationsPerSeverity(final Set<Violation> violations) {
    final Map<SEVERITY, Set<Violation>> violationsPerSeverity = new TreeMap<>();
    for (final SEVERITY severity : SEVERITY.values()) {
      violationsPerSeverity.put(severity, new TreeSet<Violation>());
    }

    for (final Violation violation : violations) {
      final Set<Violation> perReporter =
          getOrCreate(violationsPerSeverity, violation.getSeverity());
      perReporter.add(violation);
    }
    return violationsPerSeverity;
  }

  private Map<String, Set<Violation>> getViolationsPerFile(final Iterable<Violation> violations) {
    final Map<String, Set<Violation>> violationsPerFile = new TreeMap<>();
    for (final Violation violation : violations) {
      final Set<Violation> perReporter = getOrCreate(violationsPerFile, violation.getFile());
      perReporter.add(violation);
    }
    return violationsPerFile;
  }

  private Map<String, Set<Violation>> getViolationsPerReporter(
      final Iterable<Violation> violations) {
    final Map<String, Set<Violation>> violationsPerReporter = new TreeMap<>();
    for (final Violation violation : violations) {
      final Set<Violation> perReporter =
          getOrCreate(violationsPerReporter, violation.getReporter());
      perReporter.add(violation);
    }
    return violationsPerReporter;
  }

  private <T, K> Set<T> getOrCreate(final Map<K, Set<T>> container, final K key) {
    if (container.containsKey(key)) {
      return container.get(key);
    } else {
      final Set<T> violationList = new TreeSet<>();
      container.put(key, violationList);
      return violationList;
    }
  }
}
