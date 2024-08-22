package haushaltskasse;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class HaushaltskasseGUI extends JFrame {
    private JTextField datumField;
    private JTextField betragField;
    private JTextField beschreibungField;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel sumLabel;
    private TransactionDAO transactionDAO;

    public HaushaltskasseGUI() {
        transactionDAO = new TransactionDAO();

        setTitle("Haushaltskasse");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Datum (YYYY-MM-DD):"));
        datumField = new JTextField();
        inputPanel.add(datumField);

        inputPanel.add(new JLabel("Betrag:"));
        betragField = new JTextField();
        inputPanel.add(betragField);

        inputPanel.add(new JLabel("Beschreibung:"));
        beschreibungField = new JTextField();
        inputPanel.add(beschreibungField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Hinzufügen");
        JButton deleteButton = new JButton("Löschen");
        JButton filterButton = new JButton("Filtern");
        JButton overviewButton = new JButton("Übersicht");
        JButton exportButton = new JButton("Exportieren");
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(filterButton);
        buttonPanel.add(overviewButton);
        buttonPanel.add(exportButton);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Datum", "Betrag", "Kategorie", "Beschreibung"}, 0);
        table = new JTable(tableModel);
        loadTableData();

        sumLabel = new JLabel("Gesamtsumme: 0.00 €");
        updateSumLabel();

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        southPanel.add(sumLabel, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEinnahme();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEinnahme();
            }
        });

        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterEinnahmen();
            }
        });

        overviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadTableData();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });
    }

    private void addEinnahme() {
        String datum = datumField.getText();
        double betrag;
        try {
            betrag = Double.parseDouble(betragField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie einen gültigen Betrag ein.");
            return;
        }
        String kategorie = betrag < 0 ? "Ausgabe" : "Einnahme";
        String beschreibung = beschreibungField.getText();

        Transaction transaction = new Transaction(0, datum, betrag, kategorie, beschreibung);
        transactionDAO.addTransaction(transaction);
        JOptionPane.showMessageDialog(this, "Transaktion hinzugefügt!");
        loadTableData();
        updateSumLabel();
    }

    private void deleteEinnahme() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) tableModel.getValueAt(selectedRow, 0);
            transactionDAO.deleteTransaction(id);
            JOptionPane.showMessageDialog(this, "Transaktion gelöscht!");
            loadTableData();
            updateSumLabel();
        } else {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie eine Zeile zum Löschen aus.");
        }
    }

    private void filterEinnahmen() {
        String kategorie = JOptionPane.showInputDialog(this, "Geben Sie die Kategorie ein:");
        if (kategorie != null && !kategorie.isEmpty()) {
            List<Transaction> transactions = transactionDAO.filterTransactionsByCategory(kategorie);
            tableModel.setRowCount(0);
            for (Transaction t : transactions) {
                Vector<Object> row = new Vector<>();
                row.add(t.getId());
                row.add(t.getDatum());
                row.add(t.getBetrag());
                row.add(t.getKategorie());
                row.add(t.getBeschreibung());
                tableModel.addRow(row);
            }
        }
    }

    private void loadTableData() {
        List<Transaction> transactions = transactionDAO.getAllTransactions();
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            Vector<Object> row = new Vector<>();
            row.add(t.getId());
            row.add(t.getDatum());
            row.add(t.getBetrag());
            row.add(t.getKategorie());
            row.add(t.getBeschreibung());
            tableModel.addRow(row);
        }
    }

    private void updateSumLabel() {
        double sum = transactionDAO.getTotalSum();
        sumLabel.setText(String.format("Gesamtsumme: %.2f €", sum));
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Speichern Sie die Tabelle als CSV-Datei");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try (FileWriter csvWriter = new FileWriter(fileChooser.getSelectedFile() + ".csv")) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    csvWriter.append(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) {
                        csvWriter.append(";");
                    }
                }
                csvWriter.append("\n");

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        csvWriter.append(String.valueOf(tableModel.getValueAt(i, j)));
                        if (j < tableModel.getColumnCount() - 1) {
                            csvWriter.append(";");
                        }
                    }
                    csvWriter.append("\n");
                }

                JOptionPane.showMessageDialog(this, "Daten erfolgreich exportiert.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Fehler beim Export: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new HaushaltskasseGUI().setVisible(true);
            }
        });
    }
}
