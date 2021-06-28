package com;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class DataGraph extends JPanel {
    protected int GraphPad = 30;
    protected int x = getX();
    protected int y = getY();
    protected int w = getWidth();
    protected int h = getHeight();
    ArrayList<String> modules = new ArrayList<>();
    int[] yScaleIntervals = { 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };

    public void updateDimensions() {
        x = getX();
        y = getY();
        w = getWidth();
        h = getHeight();
    }
    protected void paintComponent(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }

    protected Graphics2D paintAxes(Graphics g, String title) {
        Graphics2D gObj = (Graphics2D) g;
        gObj.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //adding title of graph
        gObj.drawString(title,100,20);

        for (int i = 0; i < yScaleIntervals.length; i++){
            int hy = (h - GraphPad) - (int) ((h - (GraphPad * 2)) * ((double) yScaleIntervals[i] / 100));
            gObj.setColor(Color.LIGHT_GRAY);
            gObj.drawLine(GraphPad, hy, w - GraphPad, hy);
            gObj.setColor(Color.BLACK);
            gObj.fillRect(GraphPad - 5, hy - 3, 6, 6);
            gObj.drawString(String.valueOf(yScaleIntervals[i]), 0, hy);
        }

        //y-axis
        gObj.drawLine(GraphPad, GraphPad, GraphPad, h - GraphPad);
        //x-axis
        gObj.drawLine(GraphPad, h - GraphPad, w - GraphPad, h - GraphPad);

        return gObj;
    }

    public BufferedImage createImage(JFrame frame) {
        int w = frame.getWidth();
        int h = frame.getHeight();
        BufferedImage graph = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = graph.createGraphics();
        frame.paint(g);
        g.dispose();
        return graph;
    }

}
