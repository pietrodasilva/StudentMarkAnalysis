package com;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class CSVFilter extends FileFilter {
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        String extension = "";
        int i = file.getName().lastIndexOf(".");
        if (i != -1) {
            extension = file.getName().substring(i + 1);
        }
        if (!extension.equals("")) {
            if (extension.equals("csv")) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }
    public String getDescription() {
        return "Spreadsheet Files (*.csv)";
    }
}
