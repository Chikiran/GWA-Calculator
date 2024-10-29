import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GWACalculator extends JFrame {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JButton addButton;
    private final JButton deleteButton;
    private final JButton calculateButton;
    private final JButton resetButton;
    private final JLabel resultLabel;

    public GWACalculator() {
        setTitle("GWA Calculator");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        tableModel = new DefaultTableModel(new Object[]{"Units", "Grade"}, 3);
        table = new JTable(tableModel);

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(70);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
        table.setFont(new Font("Arial", Font.PLAIN, 16));
        table.setRowHeight(30);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        addButton = new JButton("Add Row");
        deleteButton = new JButton("Delete Row");
        calculateButton = new JButton("Calculate GWA");
        resetButton = new JButton("Reset");
        resultLabel = new JLabel("GWA: ");
        resultLabel.setFont(new Font("Arial", Font.BOLD, 16));

        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        addButton.setFont(buttonFont);
        deleteButton.setFont(buttonFont);
        calculateButton.setFont(buttonFont);
        resetButton.setFont(buttonFont);

        addButton.setFocusPainted(false);
        deleteButton.setFocusPainted(false);
        calculateButton.setFocusPainted(false);
        resetButton.setFocusPainted(false);

        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(calculateButton);
        panel.add(resetButton);
        panel.add(resultLabel);

        add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(new AddRowAction());
        deleteButton.addActionListener(new DeleteRowAction());
        calculateButton.addActionListener(new CalculateAction());
        resetButton.addActionListener(new ResetAction());
    }

    // removes the selected thingie that interrupts other processes
    private void stopEditing() {
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }
    }

    // Adds row
    private class AddRowAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            tableModel.addRow(new Object[]{"", ""});
        }
    }

    // Delete row
    private class DeleteRowAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            stopEditing();

            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(GWACalculator.this,
                        "Please select a row to delete.", "Selection Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Calculate GWA
    private class CalculateAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            stopEditing();

            double totalWeightedGrades = 0;
            int totalUnits = 0;
            boolean validData = true;

            for (int row = 0; row < tableModel.getRowCount(); row++) {
                String unitsStr = tableModel.getValueAt(row, 0) != null ? tableModel.getValueAt(row, 0).toString().trim() : "";
                String gradeStr = tableModel.getValueAt(row, 1) != null ? tableModel.getValueAt(row, 1).toString().trim() : "";

                if (unitsStr.isEmpty() && gradeStr.isEmpty()) {
                    continue;
                }

                try {
                    int units = Integer.parseInt(unitsStr);
                    double grade = Double.parseDouble(gradeStr);

                    totalWeightedGrades += units * grade;
                    totalUnits += units;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GWACalculator.this,
                            "Please enter valid numbers for all units and grades.", "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    validData = false;
                    break;
                }
            }

            if (validData && totalUnits > 0) {
                double gwa = totalWeightedGrades / totalUnits;
                resultLabel.setText("GWA: " + String.format("%.2f", gwa));
            } else if (totalUnits == 0) {
                JOptionPane.showMessageDialog(GWACalculator.this,
                        "Please ensure at least one subject with valid data is entered.", "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Resets the table and GWA label thingie
    private class ResetAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            stopEditing();

            tableModel.setRowCount(3);
            for (int row = 0; row < 3; row++) {
                tableModel.setValueAt("", row, 0);
                tableModel.setValueAt("", row, 1);
            }
            resultLabel.setText("GWA: ");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GWACalculator frame = new GWACalculator();
            frame.setVisible(true);
        });
    }
}
