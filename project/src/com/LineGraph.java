package com;

import com.elmap.ElementMap;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

//SHALINI added class to create a line graph to compare 2 modules statistics
//CHARLES altered the code to loop less often and allowed the graph to compare modules the user selects
public class LineGraph extends DataGraph {
    //test values for 2 modules
    ElementMap tree;
    String[] modules = new String[2];
    String[] headers = new String[3];
    int[][] data = new int[modules.length][headers.length];
    //padding value used when drawing the graph axis
    LineGraph(String module1, String module2, int w, int h, ElementMap tree) {
        this.tree = tree;
        tree.generateTree();
        changeModules(module1, module2);
        setSize(new Dimension(w, h));
        System.out.println("[LINE]: " + Methods.tuple(w, h));
        updateDimensions();
    }

    public void changeModules(String module1, String module2) {
        modules[0] = module1;
        modules[1] = module2;
        System.out.println("[LINE]:" + Methods.tuple(module1, module2));
        feedData(
                (String[]) tree.get("headers", "data", "table", "moduleAverages"),
                (String[][]) tree.get("data", "data", "table", "moduleAverages")
        );
    }
    public void feedData(String[] headers, String[][] data) {
        int[] h = {4, 3, 5};
        for (int i = 0; i < data.length; i += 1) {
            for (int j = 0; j < this.headers.length; j += 1) {
                this.headers[j] = headers[h[j]];
            }
        }
        for (int i = 0; i < modules.length; i += 1) {
            this.data[i] = new int[this.headers.length];
            for (int j = 0; j < data.length; j += 1) {
                if (data[j][0].equals(modules[i])) {
                    for (int k = 0; k < this.data[i].length; k += 1) {
                        this.data[i][k] = Integer.parseInt(data[j][h[k]]);
                    }
                }
            }
        }
    }

    protected void paintPoints(Graphics2D gObj, Color[] colours, int x1, int x2, int i, int j) {
        int y1 = (h - GraphPad) - (int) ((h - (GraphPad * 2)) * ((double) data[i][j] / 100));
        int y2 = (h - GraphPad) - (int) ((h - (GraphPad * 2)) * ((double) data[i][Math.min(data[i].length - 1, j + 1)] / 100));
        gObj.setPaint(colours[i]);
        gObj.drawLine(x1, y1, x2, y2);
        gObj.fillOval(x1 - 2, y1 - 2, 4, 4);
        System.out.println(Methods.tuple(x1, x2, y1, y2));
        if (j == 0) {
            gObj.drawString(modules[i], x1, y1);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gObj = paintAxes(g, "Line Graph Comparing 2 Modules");

        // graph origin position.
        int x0 = GraphPad;
        Color[] colours = {Color.RED, Color.BLUE};
        System.out.println("[LINE]: " + data.length);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                int x1 = x0 + (int) ((j + 0.5) * ((w - (GraphPad * 2)) / headers.length));
                int x2 = x0 + (int) ((Math.min(data[i].length - 1, j + 1) + 0.5) * ((w - (GraphPad * 2)) / headers.length));
                if (i == 0) {
                    gObj.setColor(Color.BLACK);
                    gObj.fillRect(x1 - 3, h - GraphPad, 6, 6);
                    gObj.drawString(headers[j], x1, h - 10);
                }
                if (modules[i] != null) {
                    paintPoints(gObj, colours, x1, x2, i, j);
                }
            }
        }
        System.out.println();
    }
//    for testing purposes:
//    public static void main(String[] args) {
//        JFrame f = new JFrame();
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        f.getContentPane().add(new LineGraph("CE202","CE212",500,500));
//        f.setSize(600,600);
//        f.setLocation(200,200);
//        f.setVisible(true);
//    }
}
