package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetModulesFromQuartiles {
    public String[][] getModuleAverages(String[] quartileNames, String[][] table, String[] header)//, ArrayList<String> moduleList, int start)
    {
        /*
        goes through the input string array of arrays and compares it to the list of all students
        if there is a match, it gets the module score (if it's higher than 0), and adds it
        to the moduleAverages String[][], and divides by the amount when it has gone
        through all students in the quartile - Arthur
        */
        String[][] moduleAverages = new String[15/*moduleList.size()*/][2];
        for (int module = 3/*start*/; module < 18 /*start + moduleList.size()*/; module++) {
            int count = 0;
            moduleAverages[module - 3][0] = header[module];
            moduleAverages[module - 3][1] = "0";
            for (int i = 0; i < quartileNames.length; i++) {
                for (int j = 1; j < table.length; j++) {
                    if (quartileNames[i].equals(table[j][0])) {
                        if (!(table[j][module].equals(null))) {
                            moduleAverages[module - 3][1] = Integer.toString((Integer.parseInt(moduleAverages[module - 3][1]) + Integer.parseInt(table[j][module])));
                            count += 1;
                        }
                    }
                }
            }
            moduleAverages[module - 3][1] = Integer.toString(Integer.parseInt(moduleAverages[module - 3][1]) / count);
        }
        return moduleAverages;
    }

    public String[][] getGoodModules(String[][] quartile)//, ArrayList<String> moduleList) {
    {
        /*
        Takes module averages from upper / lower quartile and takes the 'good' and 'bad'
        modules for each quartile
        */
        int count = 0;
        for (int i = 0; i < quartile.length; i++)
        {
            if(Integer.parseInt(quartile[i][1]) >= 70)
            {
                count++;
            }
        }

        int prevIndex = -1;
        String[][] QuartileGoodModules = new String[count][2];
        for (int goodModules = 0; goodModules < QuartileGoodModules.length; goodModules++) {
            for (int i = 0; i < quartile.length; i++) {
                if (i > prevIndex && Integer.parseInt(quartile[i][1]) >= 70) {
                    QuartileGoodModules[goodModules] = quartile[i];
                    prevIndex=i;
                    i=quartile.length;
                }
            }
        }
        return QuartileGoodModules;
    }

    public String[][] getBadModules(String[][] quartile)//, ArrayList<String> moduleList) {
    {
        /*
        Takes module averages from upper / lower quartile and takes the 'good' and 'bad'
        modules for each quartile
        */
        int count = 0;
        for (int i = 0; i < quartile.length; i++)
        {
            if(Integer.parseInt(quartile[i][1]) <= 40)
            {
                count++;
            }
        }

        int prevIndex = -1;
        String[][] QuartileBadModules = new String[count][2];
        for (int badModules = 0; badModules < QuartileBadModules.length; badModules++)
        {
            for (int i = 0; i < quartile.length; i++) {
                if (i > prevIndex && Integer.parseInt(quartile[i][1]) <= 40) {
                    QuartileBadModules[badModules] = quartile[i];
                    prevIndex=i;
                    i=quartile.length;
                }
            }
        }
        return QuartileBadModules;
    }

    public String[][][] getModulesFromQuartiles(String[] headers, String[][] data) {
        System.out.println(Arrays.toString(headers));
        System.out.println(Arrays.deepToString(data));
        String[] convertTop = new String[Methods.studentAvgMark(data, true).size()];
        List<List<Integer>> avgMarkTop = Methods.studentAvgMark(data, true);
        for(int i = 0; i < avgMarkTop.size(); i++)
        {
            String newS = (avgMarkTop.get(i).toString()).replace("[", "");
            convertTop[i] = newS.replace("]","");
        }
        System.out.println("TEST1 " + Arrays.toString(convertTop));
        System.out.println();

        String[] convertBottom = new String[Methods.studentAvgMark(data, false).size()];
        List<List<Integer>> avgMarkBottom = Methods.studentAvgMark(data, false);
        for(int i = 0; i < avgMarkBottom.size(); i++)
        {
            String newS = (avgMarkBottom.get(i).toString()).replace("[", "");
            convertBottom[i] = newS.replace("]","");
        }
        System.out.println("TEST2" + Arrays.toString(convertBottom));

        GetModulesFromQuartiles getModulesFromQuartiles = new GetModulesFromQuartiles();

        String[][] TopQuartileModuleAverages = getModulesFromQuartiles.getModuleAverages(convertTop, data, headers);
        String[][] BottomQuartileModuleAverages = getModulesFromQuartiles.getModuleAverages(convertBottom, data, headers);

        System.out.println("TQMA" + Arrays.deepToString(TopQuartileModuleAverages));
        System.out.println("BQMA" + Arrays.deepToString(BottomQuartileModuleAverages));
        System.out.println();

        String[][] goodModulesTopQuartile = getModulesFromQuartiles.getGoodModules(getModulesFromQuartiles.getModuleAverages(convertTop, data, headers));
        String[][] badModulesTopQuartile = getModulesFromQuartiles.getBadModules(getModulesFromQuartiles.getModuleAverages(convertTop, data, headers));

        String[][] goodModulesBottomQuartile = getModulesFromQuartiles.getGoodModules(getModulesFromQuartiles.getModuleAverages(convertBottom, data, headers));
        String[][] badModulesBottomQuartile = getModulesFromQuartiles.getBadModules(getModulesFromQuartiles.getModuleAverages(convertBottom, data, headers));

        System.out.println("GMTQ" + Arrays.deepToString(goodModulesTopQuartile));
        System.out.println("BMTQ" + Arrays.deepToString(badModulesTopQuartile));
        System.out.println("GMBQ" + Arrays.deepToString(goodModulesBottomQuartile));
        System.out.println("BMBQ" + Arrays.deepToString(badModulesBottomQuartile));
        System.out.println();

        String[][][] columns = {badModulesBottomQuartile, goodModulesBottomQuartile, badModulesTopQuartile, goodModulesTopQuartile};
        return columns;
    }
}
