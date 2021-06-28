package com;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.ArrayList;

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
    int columnWidth;                    /** @param columnWidth holds the width of all columns in the table. */
    DefaultTableModel model;            /** @param model controls the functionality of the table. */
    public String[] headers;            /** @param headers holds the header names. */
    public String[][] data;             /** @param data holds the values in each row of the table. */
    int maxColumnView = 8;              /** @param minColumnView the maximum number of columns visible in the viewport. */
    int w;
    int h;

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
        updateTable();
        feedData(headers, data);
    }
    public DataTable(String[] headers, String[][] data, int columnWidth) {
        this(headers, data);
        setColumnWidth(columnWidth);
    }
    public DataTable(String[] headers, String[][] data, int w, int h) {
        this(headers, data);
        setSize(w, h, true);
    }
    public DataTable(String[] headers, String[][] data, int columnWidth, int w, int h) {
        this(headers, data, columnWidth);
        setSize(w, h, false);
    }

    public void setSize(int w, int h, boolean dynamicView) {
        this.w = w;
        this.h = h;
        setPreferredScrollableViewportSize(new Dimension(this.w, this.h));
        if (dynamicView) {
            if (getColumnCount() >= maxColumnView) {
                setColumnWidth(w / maxColumnView);
            }
            else {
                setAutoResizeMode(AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            }
        }
    }

    /** @name updateModel
     * Recreates the table model to refactor the table's attributes. */
    public void updateTable() {
        this.model = new DefaultTableModel() {
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
            public int getColumnCount() {
                return headers.length;
            }
            @Override
            public int getRowCount() {
                return data.length;
            }
        };
        setModel(this.model);
    }

    /** @name setColumnWidth
     * Runs through each column of the table, setting their widths. */
    public void setColumnWidth(int width) {
        setAutoResizeMode(AUTO_RESIZE_OFF);
        columnWidth = width;
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(columnWidth);
        }
    }

    /** @name feedData
     * Feeds data directly into the cells of the table.
     * @param headers the new headers to be introduced to the table.
     * @param data holds the data for each individual cell in the table.
     */
    public void feedData(String[] headers, String[][] data) {
        this.headers = headers;
        this.data = data;
        for (int i = 0; i < getRowCount(); i += 1) {
            for (int j = 0; j < getColumnCount(); j += 1) {
                if (data[i][j] != null) {
                    if (!(data[i][j].equals("0"))) {
                        setValueAt(data[i][j], i, j);
                    }
                }
            }
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
        setSize(w, h, true);
        System.out.println(getColumnCount() + ", " + getRowCount());
        feedData(this.headers, this.data);
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
