import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class Table {
  private static String nextColumnName = "D";
  private static JButton addColumnButton = new JButton();
  private static JButton addRowButton = new JButton();
  private static JLabel errorLabel = new JLabel();
  private static JTable table = new JTable();
  private static JFrame frame = new JFrame("JetBrains Test Table");
  private static JScrollPane scrollPanel = new JScrollPane(table);

  public static void main(String[] a) {
    SwingUtilities.invokeLater(new Table.RunnableImpl());
  }

  private static class RunnableImpl implements Runnable {

    public void run() {
      configureFrame();
    }
  }

  private static void configureFrame() {
    configureButtons();
    configureTable();
    configureErrorLabel();
    configureScroller();
    JPanel tablePanel = new JPanel();
    tablePanel.add(scrollPanel);
    tablePanel.setBounds(0, 0, 600, 400);
    JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));
    buttonsPanel.add(addColumnButton);
    buttonsPanel.add(addRowButton);
    buttonsPanel.setBounds(650, 0, 300, 200);
    frame.setLayout(null);
    frame.add(buttonsPanel);
    frame.add(tablePanel);
    frame.add(errorLabel);
    frame.pack();
    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setVisible(true);
  }

  private static void configureScroller() {
    scrollPanel.setPreferredSize(new Dimension(600, 400));
  }

  private static void configureErrorLabel() {
    errorLabel.setBounds(650, 250, 200, 200);
  }

  private static void configureTable() {
    TableModel tableModel = new DefaultTableModel(new String[] {"A", "B", "C"}, 3);
    table.setModel(tableModel);
    table.setSize(600, 400);
    table.setCellSelectionEnabled(true);
    ListSelectionModel select = table.getSelectionModel();
    select.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    select.addListSelectionListener(event -> {
      errorLabel.setText("");
      int row = table.getSelectedRow();
      int column = table.getSelectedColumn();
      if (row >= 0 && column >= 0 && table.getValueAt(row, column) != null &&
          table.getValueAt(row, column).toString().startsWith("=")) {
        String formula = table.getValueAt(row, column).toString().replaceFirst("=", "");
        try {
          formula = parseTableLinks(formula);
          double calculatedFormula = ExpressionParser.calculateFormula(formula);
          table.setValueAt(String.valueOf(calculatedFormula), row, column);
        } catch (Exception exception) {
          errorLabel.setText("<html>Error: <font color='red'>Invalid input</font></html>");
        }
      }
      //todo update the ui on value change
    });
  }

  private static void configureButtons() {
    addColumnButton.setText("Add column");
    addColumnButton.addActionListener(event -> {
      DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
      tableModel.addColumn(nextColumnName);
      updateNextColumnName();
    });
    addRowButton.setText("Add row");
    addRowButton.addActionListener(event -> {
      DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
      Object[] rows = new Object[0];
      tableModel.addRow(rows);
    });
  }

  private static void updateNextColumnName() {
    if (nextColumnName.endsWith("Z")) {
      nextColumnName = nextColumnName.length() > 1 ? nextColumnName.substring(0, nextColumnName.length() - 1) : "";
      nextColumnName += "AA";
    } else {
      nextColumnName = nextColumnName.substring(0, nextColumnName.length() - 1) +
          ((char) (nextColumnName.charAt(nextColumnName.length() - 1) + 1));
    }
  }

  private static String parseTableLinks(String formula) {
    int index = 0;
    while (index < formula.length()) {
      if ('A' <= formula.charAt(index) && formula.charAt(index) <= 'Z') {
        char element = formula.charAt(index);
        StringBuilder replaceWithValue = new StringBuilder();
        int rowIndex = 0;
        int columnIndex = 0;
        while (index < formula.length() && (('A' <= element && element <= 'Z') || ('0' <= element && element <= '9'))) {
          if ('A' <= element && element <= 'Z') {
            columnIndex = columnIndex * 10 + (Character.getNumericValue(element) - Character.getNumericValue('A'));
          }
          if ('0' <= element && element <= '9') {
            rowIndex = rowIndex * 10 + Character.getNumericValue(element);
          }
          replaceWithValue.append(element);
          index++;
          if (index < formula.length()) {
            element = formula.charAt(index);
          }
        }
        String valueToPutInFormula = Table.table.getValueAt(rowIndex - 1, columnIndex) == null ? "0.0" :
            (String) Table.table.getValueAt(rowIndex - 1, columnIndex);
        if (!Utils.isDouble(valueToPutInFormula)) {
          throw new ClassCastException();
        }
        formula = formula.replaceFirst(replaceWithValue.toString(), (valueToPutInFormula));
        index = 0;
      } else {
        index++;
      }
    }
    return formula;
  }

}