package components;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class TableFilterDemo extends JPanel{
    public Boolean DEBUG = false;
    private JTable table;
    private JTextField filterText;
    private JTextField statusText;
    private TableRowSorter<DefaultTableModel> sorter;

    public TableFilterDemo() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Create a table with a sorter.
        MyTableModel n = new MyTableModel(this);
        DefaultTableModel model = new DefaultTableModel(n.data, n.columnNames);
        sorter = new TableRowSorter<DefaultTableModel>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //task 2
        //add(initMenu());
        Box b1 = Box.createHorizontalBox();
        b1.add(initMenu());
        b1.add(Box.createHorizontalGlue());
        add(b1);

        //For the purposes of this example, better to have a single
        //selection.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //When selection changes, provide user with row numbers for
        //both view and model.
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = table.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                            statusText.setText("");
                        } else {
                            int modelRow =
                                    table.convertRowIndexToModel(viewRow);
                            statusText.setText(
                                    String.format("Selected Row in view: %d. " +
                                                    "Selected Row in model: %d.",
                                            viewRow, modelRow));
                        }
                    }
                }
        );


        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);

        //Create a separate form for filterText and statusText
        JPanel form = new JPanel(new SpringLayout());
        JLabel l1 = new JLabel("Filter Text:", SwingConstants.TRAILING);
        form.add(l1);
        filterText = new JTextField();
        //Whenever filterText changes, invoke newFilter.
        filterText.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        newFilter();
                    }
                });
        l1.setLabelFor(filterText);
        form.add(filterText);
        JLabel l2 = new JLabel("Status:", SwingConstants.TRAILING);
        form.add(l2);
        statusText = new JTextField();
        l2.setLabelFor(statusText);
        form.add(statusText);
        //task 1
        JButton button = new JButton("Remove");
        button.addActionListener(new RemoveLineActionLister());

        add(button);
        SpringUtilities.makeCompactGrid(form, 2, 2, 6, 6, 6, 6);
        add(form);

    }

    /**
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    private void newFilter() {
        RowFilter<DefaultTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filterText.getText(), 0, 1, 2);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }


    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("TableFilterDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        TableFilterDemo newContentPane = new TableFilterDemo();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private JMenuBar initMenu() {
       JMenuBar menuBar;
       JMenu menu;
       JMenuItem header, menuItem;
       JMenuItem menuCSV = new  JMenuItem("Load CSV");

       menuBar = new JMenuBar();
       menu = new JMenu("Menu");
       menuBar.add(menu);

       header = new JMenuItem("COMMANDS:");
       header.setEnabled(false);
       menu.add(header);

       menuItem = new JMenuItem("Remove");
       menuItem.addActionListener(null);
       menu.add(menuItem);
       menu.addSeparator();
       //minitask2
        menu.addMenuListener(new MenuListener() {
            public void menuSelected(MenuEvent e) {
                int viewRow = table.getSelectedRow();
                if (viewRow < 0) {
                    menu.setEnabled(false);
                }else{
                    menu.setEnabled(true);
                }
            }

            @Override
            public void menuDeselected(MenuEvent e) {}

            @Override
            public void menuCanceled(MenuEvent e) {}
        });

       menuItem.addActionListener(new RemoveLineActionLister());

       menu.add(menuCSV);
       menuCSV.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               try(BufferedReader br = new BufferedReader(
                       new FileReader(new File(this.getClass().getResource("/data.csv").getFile()))
               )){
                   String line;
                   while((line = br.readLine()) != null){
                       System.out.println(line);
                   }
               }catch(FileNotFoundException ex){
                   JOptionPane.showMessageDialog(null, "Issue with loading file: " + ex.getMessage());
               }catch(IOException ex){
                   JOptionPane.showMessageDialog(null, "Issue with loading file: " + ex.getMessage());
                   ex.printStackTrace();
               }
           }
       });

        return menuBar;
    }

    private final class RemoveLineActionLister implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e){
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(null, "No row selected!");
            }
            else{
                int modelRow = table.convertRowIndexToModel(selectedRow);
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int answer = JOptionPane.showConfirmDialog(null, "Do you want to remove " +
                        model.getValueAt(modelRow, 0) + " "+ model.getValueAt(modelRow, 1)+"?", "Warning",
                        JOptionPane.YES_NO_OPTION);
                if (answer == 0){
                    model.removeRow(modelRow);
                }
            }
        }
    }
}
