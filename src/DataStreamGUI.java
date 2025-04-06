import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class DataStreamGUI extends JFrame {

    private JTextArea originalTextArea = new JTextArea(20, 30);
    private JEditorPane filteredTextArea = new JEditorPane("text/html", "");
    private JTextField searchField = new JTextField(20);
    private JButton loadButton = new JButton("Load File");
    private JButton searchButton = new JButton("Search");
    private JButton quitButton = new JButton("Quit");
    private Path loadedFile;

    public DataStreamGUI() {
        setTitle("Data Stream Search Tool");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loadPanel = new JPanel();
        loadPanel.add(loadButton);
        loadPanel.add(quitButton);
        add(loadPanel, BorderLayout.NORTH);

        originalTextArea.setEditable(false);
        filteredTextArea.setEditable(false);
        JScrollPane scroll1 = new JScrollPane(originalTextArea);
        JScrollPane scroll2 = new JScrollPane(filteredTextArea);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2));
        centerPanel.add(scroll1);
        centerPanel.add(scroll2);
        add(centerPanel, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        quitButton.addActionListener(e -> System.exit(0));

        originalTextArea.setText("No file loaded.");
        searchButton.setEnabled(false);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadFile() {
        JFileChooser chooser = new JFileChooser("data");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            String fileName = chooser.getSelectedFile().getName();
            loadedFile = Path.of("data", fileName);
            List<String> content = DataStreamLoader.loadFile(loadedFile);
            if (content != null) {
                originalTextArea.setText(String.join("\n", content));
                originalTextArea.setCaretPosition(0);
                filteredTextArea.setText("");
                searchButton.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load file.");
                originalTextArea.setText("No file loaded.");
                searchButton.setEnabled(false);
            }
        } else {
            originalTextArea.setText("No file loaded.");
            searchButton.setEnabled(false);
        }
    }

    private void searchFile() {
        if (loadedFile == null) {
            JOptionPane.showMessageDialog(this, "Please load a file first.");
            return;
        }

        String keyword = searchField.getText();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a search string.");
            return;
        }

        List<String> results = DataStreamFilter.filterFile(loadedFile, keyword);
        if (results != null) {
            String highlightedHtml = highlightKeyword(results, keyword);
            filteredTextArea.setText(highlightedHtml);
            filteredTextArea.setCaretPosition(0);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No matches found for \"" + keyword + "\".");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error searching the file.");
        }
    }

    private String highlightKeyword(List<String> lines, String keyword) {
        StringBuilder html = new StringBuilder("<html><body style='font-family:monospace;'>");

        for (String line : lines) {
            String escapedLine = line
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
            String highlighted = escapedLine.replaceAll("(?i)(" + Pattern.quote(keyword) + ")",
                    "<span style='background:yellow;'>$1</span>");
            html.append(highlighted).append("<br>");
        }

        html.append("</body></html>");
        return html.toString();
    }
}
