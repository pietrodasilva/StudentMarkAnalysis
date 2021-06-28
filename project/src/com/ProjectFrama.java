package com;

public class ProjectFrame {  // com.main class to run the application
    public static void main(String[] args, ReadCSV csv, int w, int h, String file) {
        ApplicationFrame frame = new ApplicationFrame(w, h, csv, file); // creates new frame object to display
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setTitle("CE291 Team 14 Project"); //added title here
    }
    public static void main(String[] args, int w, int h, String file) {
        ApplicationFrame frame = new ApplicationFrame(w, h, file); // creates new frame object to display
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setTitle("CE291 Team 14 Project"); //added title here
    }
}
