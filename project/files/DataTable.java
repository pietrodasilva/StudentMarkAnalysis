package com;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @name DataTable
 * @description This class is an extension of the regular JTable, meant to ease the strain of initialisation and
 * setup that a regular JTable has. The attributes of the table dynamically set based on the data entered, and it can
 * be wiped or replaced with new data sets.
 * @author ck18334
 * @version 1.0
 * @since 2019-11-8
 */

public class DataTable extends JTable {
    int columnWidth;                /** @param columnWidth holds the width of all columns in the table. */
    DefaultTableModel model;        /** @param model controls the functionality of the table. */
    String[] headers;               /** @param headers holds the header names. */
    String[][] data;                /** @param data holds the values in each row of the table. */
    int w;                          /** @param w holds the width of the table. */
    int h;                          /** @param h holds the height of the table. */

    /** @name DataTable (constructor, 3 derivatives)
     * @param headers
     * @param data
     * @param columnWidth (optional)
     * @param w (optional)
     * @param h (optional)
     */
    public DataTable(String[] headers, String[][] data) {
        this.headers = headers;
        this.data = data;
        updateModel();
    }
    public DataTable(String[] headers, String[][] data, int columnWidth) {
        this(headers, data);
        setColumnWidth(columnWidth);
    }
    public DataTable(String[] headers, String[][] data, int w, int h) {
        this(headers, data);
        setSize(w, h);
    }
    public DataTable(String[] headers, String[][] data, int columnWidth, int w, int h) {
        this(headers, data, columnWidth);
        setSize(w, h);
    }

    /** @name updateModel
     * Recreates the table model to refactor the table's attributes. */
    public void updateModel() {
        model = new DefaultTableModel() {
            @Override
            public String getColumnName(int index) {
                String header = headers[index];
                if (!header.equals("0")) {
                    return header;
                }
                else {
                    return "";
                }
            }
            @Override
            public Object getValueAt(int row, int col) {
                String value = data[row][col];
                if (!value.equals("0")) {
                    return value;
                }
                else {
                    return "";
                }
            }
            @Override
            public int getColumnCount() {
                return headers.length;
            }
            @Override
            public int getRowCount() {
                return data.length;
            }
        };
        setModel(model);
    }

    /** @name updateTable
     * Updates the table by refactoring its model and resetting its row and column counts. */
    public void updateTable() {
        updateModel();
        System.out.println(Arrays.toString(headers));
    }

    /** @name setSize
     * Sets the size of the table on the screen. */
    public void setSize(int w, int h) {
        setPreferredScrollableViewportSize(new Dimension(w, h));
        setPreferredSize(new Dimension(w, h));
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);
        this.w = w;
        this.h = h;
    }

    /** @name setColumnWidth
     * Runs through each column of the table, setting their widths. */
    public void setColumnWidth(int width) {
        columnWidth = width;
        for (int i = 0; i < getColumnCount(); i += 1) {
            getColumnModel().getColumn(i).setPreferredWidth(columnWidth);
        }
    }

    /** @name replaceTable
     * Recreates the table to allow for new data to be shown.
     * @param headers the new headers to be introduced to the table.
     * @param data holds the data for each individual cell in the table.
     */
    public void replaceTable(String[] headers, String[][] data) {
        this.headers = headers;
        this.data = data;
        System.out.println(getColumnCount() + ", " + getRowCount());
        updateTable();
        System.out.println(getColumnCount() + ", " + getRowCount());
    }

    /** @name insert
     * Edits a cell with new content.
     * @param value the value to be entered into the cell.
     * @param x the x-coordinates of the cell.
     * @param y the y-coordinates of the cell.
     */
    public void insert(String value, int x, int y) {
        if (x < headers.length && y < data.length) {
            data[y][x] = value;
        }
    }

    /** @name getScrollPane
     * Returns the table's native scrollbar.
     * @param tree the tree that the table is contained within.
     * @return JScrollPane
     */
    public JScrollPane getScrollPane(ElementMap tree) {
        HashSet<JScrollPane> mapSet = new HashSet<>(tree.getBranch("gui", "scroll").values());
        for (JScrollPane scrollbar : mapSet) {
            if (java.util.Arrays.asList(scrollbar.getComponents()).contains(this)) {
                return scrollbar;
            }
        }
        return null;
    }

    /** @name display
     * Prints the table to the terminal. */
    public void display() {
        System.out.println();
        System.out.println(": DISPLAY TABLE :");
        for (int i = 0; i < getRowCount(); i += 1) {
            System.out.print(getValueAt(i, 0));
            for (int j = 1; j < getColumnCount(); j += 1) {
                System.out.print(", " + getValueAt(i, j));
            }
            System.out.println();
        }
    }
}
