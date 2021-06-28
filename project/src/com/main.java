package com;

import java.io.File;
import java.io.IOException;

public class main
{
    static String defaultFile = "files/data.csv";
    static String defaultFilePath = defaultFile.substring(0, defaultFile.lastIndexOf("/") + 1);
    public static void main(String[] args)
    {
        File file = new File(defaultFilePath);
        if (file.exists() && !file.isDirectory()) {
            ReadCSV csv = new ReadCSV(defaultFilePath);
            ProjectFrame.main(new String[0], csv, 1024, 600, defaultFilePath);
        }
        else {
            ProjectFrame.main(new String[0], 1024, 600, defaultFilePath);
        }
    }
}
