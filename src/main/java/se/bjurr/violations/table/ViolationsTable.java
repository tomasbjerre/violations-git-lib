package se.bjurr.violations.table;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestWordMax;
import de.vandermeer.asciithemes.a7.A7_Grids;

public class ViolationsTable {

  public static String of(final String[] headers, final String[][] data, final int[] columnWidths) {
    final AsciiTable at = new AsciiTable();
    at.getContext().setGrid(A7_Grids.minusBarPlus());
    at.addRow(headers);
    at.addRule();
    for (final String[] dataRow : data) {
      at.addRow(dataRow);
      at.addRule();
    }
    if (columnWidths.length > 0) {
      at.getRenderer().setCWC(new CWC_LongestWordMax(columnWidths));
    } else {
      at.getRenderer().setCWC(new CWC_LongestWordMax(Integer.MAX_VALUE));
    }
    at.setPadding(1);
    return at.render();
  }
}
