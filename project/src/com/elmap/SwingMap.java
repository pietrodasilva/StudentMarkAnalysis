package com.elmap;

import com.Methods;

import javax.swing.*;

public class SwingMap extends ElementMap {
    public SwingMap(JFrame frame) {
        super("gui");
        extend("panel", false, "gui");
        extend("label", false, "gui");
        extend("button", false, "gui");
        extend("combo", false, "gui");
        extend("list", false, "gui");
        extend("checkbox", false, "gui");
        extend("list", false, "gui", "checkbox");
        extend("group", false, "gui", "checkbox");
        extend("table", false, "gui");
        extend("field", false, "gui");
        extend("scroll", false, "gui");
        extend("controller", false, "gui");
        extend("tab", false, "gui");
        extend("icon", false, "gui");
        extend("frame", false, "gui");
        put("main", frame, "gui", "frame");
    }

    public void addIcons(int size, String ... iconNames) {
        for (String name : iconNames) {
            put(name, new ImageIcon(Methods.getScaledImage(Methods.getImage("icons/" + name), size, size)), "gui", "icon");
        }
    }
}
