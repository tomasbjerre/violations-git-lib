package se.bjurr.violations.table;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciitable.CWC_LongestWordMax;
import de.vandermeer.asciithemes.a7.A7_Grids;

public class ViolationsTable {

  public static String create(
      final String[] headers, final String[][] data, final int... columnWidths) {
    final AsciiTable at = new AsciiTable();
    at.getContext().setGrid(A7_Grids.minusBarPlus());
    at.addRow(headers);
    at.addRule();
    for (final String[] dataRow : data) {
      at.addRow(dataRow);
      at.addRule();
    }
    if (columnWidths.length > 0) {
      final CWC_LongestLine longestLinesMinMax = new CWC_LongestLine();
      for (final int columnWidth : columnWidths) {
        longestLinesMinMax.add(0, columnWidth);
      }
      at.getRenderer().setCWC(longestLinesMinMax);
    } else {
      at.getRenderer().setCWC(new CWC_LongestWordMax(Integer.MAX_VALUE));
    }
    at.setPadding(1);
    return at.render();
  }
}
