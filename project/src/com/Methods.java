package com;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.*;

public class Methods {
    public int start = -1;

    //Print multiples of variables inside brackets.
    public static String tuple(Object ... str) {
        String tuple = "(" + str[0];
        for (int i = 1; i < str.length; i += 1) {
            tuple += ", " + str[i];
        }
        tuple += ")";
        return tuple;
    }

    //Obtain an image from a path.
    public static BufferedImage getImage(String imagePath) {
        File file = new File(imagePath + ".png");
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }
    }

    //Code sourced from stack overflow users Suken Shah and Mr. Polywhirl - https://stackoverflow.com/questions/6714045/how-to-resize-jlabel-imageicon
    public static Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

    //Shalini - added a method to generate an arrayList of the module names
    public ArrayList<String> ModuleList(DataTable table) {
        return ModuleList(table, false);
    }
    public ArrayList<String> ModuleList(DataTable table, boolean findStart) {
        System.out.println(": LIST MODULES :");
        ArrayList<String> moduleNames = new ArrayList<>();
        Integer start = -1;
        System.out.println("[DBG]: Vector initialized.");
        for (int i = 0; i < table.getColumnCount(); i += 1) {
            System.out.print("[DBG]: Searching index " + i + "... ");
            if (table.headers[i].startsWith("CE")) {
                if (start == -1 && findStart) {
                    start = i;
                }
                moduleNames.add(table.headers[i]);
                System.out.print("Added " + table.headers[i] + ".");
            }
            System.out.println();
        }
        if (findStart) {
            moduleNames.add(0, start.toString());
        }
        return moduleNames;
    }

    public ArrayList<Integer> MarksList(String modName, String[] headers, String[][] data) {
        System.out.println(": LIST MARKS :");
        System.out.println(modName);
        int start = -1;
        ArrayList<String> StrModMarks = new ArrayList<>();
        for (int i = 0; i < data.length; i += 1) {
            for (int j = 0; j < data[i].length; j += 1) {
                if (modName.equals(headers[j])) {
                    StrModMarks.add(data[i][j]);
                }
            }
        }
        ArrayList<Integer> modMarks = new ArrayList<>();
        for (String StrMark : StrModMarks) {
            modMarks.add(Integer.parseInt(StrMark));
        }
        System.out.println(modMarks);
        return modMarks;
    }

    public Integer MaxMark(ArrayList<Integer> modMarks) {
        System.out.println(": MAX MARKS :");
        Integer max = modMarks.get(0);
        for (Integer mark : modMarks) {
            if(mark > max) {
                max = mark;
            }
        }
        return max;
    }

    public Integer MinMark(ArrayList<Integer> modMarks) {
        System.out.println(": MIN MARKS :");
        Integer min = 101;
        for (Integer mark : modMarks) {
            //discount the null value which have been set to 0 when calculating the minimum mark.
            if (mark < min) {
                if (mark != 0) {
                    min = mark;
                }
            }
            else {
                continue;
            }
        }
        if (min == 101) {
            min = 0;
        }
        return min;
    }

    public Integer AvgMark(ArrayList<Integer> modMarks) {
        System.out.println(": AVERAGE MARKS :");
        Integer average = 0;
        Integer sum = 0;
        Integer counter = modMarks.size();
        for (Integer mark : modMarks) {
            sum += mark;
            if (mark == 0) {
                counter -= 1;
            }
        }
        if (counter > 0) {
            average = sum / counter;
        }
        return average;
    }

    //this method is used to sort the list in the ascending order
    public static List<List<Integer>> studentAvgMark(String[][] studentMarksArray, boolean top) {
        Arrays.sort(studentMarksArray, (x1, x2) -> {
            if(Integer.parseInt(x1[19]) >= Integer.parseInt(x2[19])) return -1;
            else return 1;
        });

        List<List<Integer>> quartile = new LinkedList<>();
        /*System.out.println("Default list");
        Arrays.stream(studentMarksArray).forEach(x -> System.out.println(x[19]));
        System.out.println("end list");*/

        int quartileLen = (int) Math.floor(studentMarksArray.length / 4);
        int startIndex = top ? 0 : quartileLen * 3;
        int endIndex = top ? quartileLen : studentMarksArray.length;

        for (int i = startIndex; i < endIndex; i++) {
            Integer id = Integer.parseInt(studentMarksArray[i][0]);
            Integer avg = Integer.parseInt(studentMarksArray[i][19]);
            quartile.add(Arrays.asList(id));//, avg));
        }

        //quartile.forEach(System.out::println);
        return quartile;
    }


    /*
     * Passed this.data into my studentQuartiles method
     * the method filters students marks
     * return a map containing student and marks
     * Second parameter : boolean returns top students if set true
     * Third parameter : module you want to find the score of (optional if you want to find a module rather than the overall average)
     * Fourth parameter : the list of modules
     * */
    public String[][] studentPerformance(String[][] studentMarksArray, boolean top) {
        return studentPerformance(studentMarksArray, top, null, null);
    }
    public String[][] studentPerformance(String[][] students, boolean top, String module, Vector<String> moduleList) {
        ArrayList<String[]> studentList = new ArrayList<>();
        if (students.length > 0) {
            int column = students[0].length - 1;
            System.out.println(tuple(column, start));
            if (module != null) {
                column = moduleList.indexOf(module);
            }
            if (column != -1) {
                for (String[] student : students) {
                    String studentId = student[0];
                    String avg = student[Math.min(students[0].length - 1, column + start)];
                    int avgInt = Integer.parseInt(avg);
                    if (((top && avgInt >= 70) || (!top && avgInt < 40)) && avgInt > 0) {
                        studentList.add(new String[] {studentId, avg});
                    }
                }
            }
            else {
                return null;
            }
        }
        else {
            return null;
        }
        String[][] studentArray = new String[studentList.size()][2];
        for (int i = 0; i < studentArray.length; i += 1) {
            studentArray[i] = studentList.get(i);
        }
        if (top) {
            System.out.println(": " + studentArray.length + " STUDENTS GETTING FIRSTS :");
        } else {
            System.out.println(": " + studentArray.length + " STUDENTS FAILING :");
        }
        return studentArray;
    }
    public int averagePerformance(String[][] students) {
        int average = 0;
        for (String[] student : students) {
            average += Integer.parseInt(student[1]);
            System.out.println(tuple(student));
        }
        if (students.length > 0) {
            return average / students.length;
        }
        return 0;
    }
}
