package components;

import javax.swing.table.AbstractTableModel;

class MyTableModel extends AbstractTableModel {
    private final TableFilterDemo tableFilterDemo;
    public String[] columnNames = {"CarType",
            "Make",
            "Model",
            "Year",
            "MinimumVersion",
            "EngineType",
            "Power",
            "EngineVersion"};
    public Object[][] data = {
            {"RegularCar","Honda","Civic",2020,"v2.0","RegularCarEngine",20,"v3.0"},
            {"RegularCar","Toyota","Camry",2020,"v3.0","RegularCarEngine",30,"v4.0"},
            {"RegularCar","Ford","Mustang",2010,"v3.0","RegularCarEngine",20,"v3.0"},
            {"LuxurayCar","Ferrari","296GTS",2023,"v4.0","LuxuryCarEngine",40,"v5.0"},
            {"LuxurayCar","Mercedes-Benz","Maybach S 580",2015,"v4.0","LuxuryCarEngine",40,"v5.0"},
            {"RegularCar","Honda","Accord",2020,"v3.0","RegularCarEngine",30,"v3.0"}
    };

    public MyTableModel(TableFilterDemo tableFilterDemo) {
        this.tableFilterDemo = tableFilterDemo;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        if (tableFilterDemo.DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                    + " to " + value
                    + " (an instance of "
                    + value.getClass() + ")");
        }

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        if (tableFilterDemo.DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------------");
    }
}