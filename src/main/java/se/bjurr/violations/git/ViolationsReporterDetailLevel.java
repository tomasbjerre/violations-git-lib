package se.bjurr.violations.git;

public enum ViolationsReporterDetailLevel {
  /** Show detailed violations per file. */
  VERBOSE,
  /** Show number of violations. Per reporter and in total. */
  COMPACT,
  /** Like compact but per file. */
  PER_FILE_COMPACT
}
