package com;

import java.io.*;
import java.util.Arrays;

public class ReadCSV
{
    String[][] table;
    public ReadCSV(String file) {
        try {
            table = readCSV(file);
        }
        catch (IOException e) {
            table = new String[][] {{"0"}};
        }
    }
    public String[][] readCSV(String file) throws IOException
    {
        //opens file with BufferedReader
        BufferedReader csvRead = null;
        try {
            csvRead = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("[ERR]: Please check your file path.");
        }

        //counts number of lines in file and closes & instantiates file Reader to read from 1st line again
        int lines = 0;
        int line = 0;
        while (csvRead.readLine() != null) {
            lines += 1;
        }
        csvRead.close();

        try {
            csvRead = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("[ERR]: Please check your file path.");
        }

        //outputs split lines to 2d array, then puts array into 2d array
        String[][] fileRead = new String[lines][];
        String test;
        while ((test = csvRead.readLine()) != null) {
            fileRead[line] = test.split(",");
            line += 1;
        }
        csvRead.close();

        for (int i = 0; i < fileRead.length; i += 1) {
            for (int j = 0; j < fileRead[i].length; j += 1) {
                if (fileRead[i][j].equals("")) {
                    fileRead[i][j] = "0";
                }
            }
        }
        this.table = fileRead;
        return this.table;
    }
}
