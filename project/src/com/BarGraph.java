package com;

import javax.swing.*;
import javax.xml.bind.Element;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;

import com.elmap.ElementMap;


public class BarGraph extends DataGraph {

    ElementMap tree;
    String[] headers = new String[3];
    int[][] data = new int [modules.size()][3];
    double[] barValues;
    int barWidth;
    String title;

    BarGraph(ArrayList<String> modules, int w, int h, ElementMap tree) {
        this.modules = modules;
        this.tree = tree;
        tree.generateTree();
        changeModules(modules);
        setSize(new Dimension(w, h));
        System.out.println("[BAR]: " + Methods.tuple(w, h));
        updateDimensions();
    }

    protected void changeModules(ArrayList<String> modules) {
        this.modules = modules;
        headers = new String[modules.size()];
        for (int i = 0; i < headers.length; i += 1) {
            headers[i] = modules.get(i);
        }
        barWidth = 0;
        if (headers.length > 0) {
            barWidth = (int) ((w / headers.length) * 0.8);
        }
        System.out.println("[BAR]: " + Arrays.toString(headers));
        feedData(
                modules,
                (String[][]) tree.get("data", "data", "table", "moduleAverages")
        );
    }

    protected void feedData(ArrayList<String> modules, String[][] data) {
        this.data = new int[modules.size()][2];
        for (int i = 0; i < modules.size(); i += 1) {
            for (int j = 0; j < data.length; j += 1) {
                if (data[j][0].equals(modules.get(i))) {
                    this.data[i][0] = Integer.parseInt(data[j][5]);
                    this.data[i][1] = Integer.parseInt(data[j][4]);
                }
            }
        }
    }

    protected void paintPoints(Graphics2D gObj, Color[] colours, int x, int i, int j) {
        int h = (int) ((this.h - (GraphPad * 2)) * ((double) data[i][j] / 100));
        int y = (this.h - GraphPad) - h;
        gObj.setPaint(colours[j]);
        gObj.fillRect(x - (barWidth / 2), y, barWidth, h);
        System.out.println(Methods.tuple(x, y));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gObj = paintAxes(g, "Bar Graph Comparing the Number of Students Excelling and Failing in Various Modules");
        //Graphics2D gObj = (Graphics2D) g;

        /*
        if (barValues == null || barValues.length == 0) { return; }

        double minValue = 0;
        double maxValue = 100;

        for (int i = 0; i < barValues.length; i++) {
            if (minValue > barValues[i]) { minValue = barValues[i]; }
            if (maxValue < barValues[i]) { maxValue = barValues[i]; }
        }

        Font titleFont = new Font("Calibri", Font.BOLD, 15);
        FontMetrics titleFontMetrics = g.getFontMetrics(titleFont);
        Font labelFont = new Font("Calibri", Font.PLAIN, 10);
        FontMetrics labelFontMetric = g.getFontMetrics(labelFont);

        int titleWidth = titleFontMetrics.stringWidth(title);
        int q = titleFontMetrics.getAscent();
        int p = (w - titleWidth) / 2;
        g.setFont(titleFont);
        g.drawString(title, p, q);

        int top = titleFontMetrics.getHeight();
        int bottom = labelFontMetric.getHeight();

        if (maxValue == minValue) { return; }

        double barScale = (h - top - bottom) / (maxValue - minValue);
        q = h - labelFontMetric.getDescent();
        g.setFont(labelFont);

        for (int j = 0; j < barValues.length; j++) {
            int valueP = j * barWidth + 1;
            int valueQ = top;
            int height = (int) (barValues[j] * barScale);
            if (barValues[j] >= 0) {
                valueQ += (int) ((maxValue - barValues[j]) * barScale);
            } else {
                valueQ += (int) (maxValue * barScale);
                height = -height;
            }
            g.setColor(Color.blue);
            g.fillRect(valueP, valueQ, barWidth - 2, height);
            g.setColor(Color.black);
            g.drawRect(valueP, valueQ, barWidth - 2, height);

            int labelWidth = labelFontMetric.stringWidth(headers[j]);
            p = j * barWidth + (barWidth - labelWidth) / 2;
            g.drawString(headers[j], p, q);
        }
        */
        // graph origin position.
        int x0 = GraphPad;
        Color[] colours = {Color.GREEN, Color.RED};
        System.out.println("[BAR]: " + data.length);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                int bx = x0 + (int) ((i + 0.5) * ((w - (GraphPad * 2)) / headers.length));
                if (j == 0) {
                    gObj.setColor(Color.BLACK);
                    gObj.fillRect(bx - 3, h - GraphPad, 6, 6);
                    gObj.drawString(headers[i], bx, h - 10);
                }
                paintPoints(gObj, colours, bx, i, j);
            }
        }
        System.out.println();
    }
}
