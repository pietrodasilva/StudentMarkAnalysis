package com;

import com.elmap.ElementMap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ComboBoxListener implements ActionListener {
    ElementMap tree;
    JComboBox<String> box;
    int action;

    //Static variables for all actions a combo box could perform.
    //Saves the trouble of having to remember exact strings or indexes.
    static int NULL = -1;
    static int SWITCH_GRAPH_TYPE = 0;

    public ComboBoxListener(ElementMap tree, JComboBox<String> box, int action) {
        this.tree = tree;
        this.box = box;
        this.action = action;
    }

    public void actionPerformed(ActionEvent e) {
        if (action == SWITCH_GRAPH_TYPE) {
            JPanel panel = (JPanel) tree.get("button", "gui", "panel");
            JPanel linePanel = (JPanel) tree.get("lineGraphInfo", "gui", "panel");
            JPanel barPanel = (JPanel) tree.get("barGraphInfo", "gui", "panel");
            if (box.getSelectedItem().equals("Line Graph")) {
                panel.remove(barPanel);
                panel.add(linePanel);
                tree.put("graphType", "line", "data", "string");
            } else {
                panel.remove(linePanel);
                panel.add(barPanel);
                tree.put("graphType", "bar", "data", "string");
            }
            panel.revalidate();
            panel.repaint();
        }
    }
}
