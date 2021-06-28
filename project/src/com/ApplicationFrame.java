package com;

import com.elmap.SwingMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Vector;

class ApplicationFrame extends JFrame {
    protected int w;
    protected int h;
    protected int tw;
    protected int th;
    protected String file = "";
    protected String[] headers;
    protected String[][] data;
    protected DataTable averages;
    protected DataTable table;
    protected LineGraph lineGraph;
    protected BarGraph barGraph;
    protected Methods methods = new Methods();
    protected SwingMap tree = new SwingMap(this);
    protected String defaultFilePath;
    protected String defaultFileFolder;
    protected Map<String, Object> elements = tree.getBranch("gui");
    protected Map<String, JPanel> panels = tree.getBranch("gui", "panel");

    ApplicationFrame(int w, int h) {
        this.w = w;
        this.h = h;
        this.tw = 800;
        this.th = 400;
        tree.addIcons(24, "tick", "cross", "add", "remove");
        tree.addIcons(16, "upper_quartile", "lower_quartile");
    }
    ApplicationFrame(int w, int h, ReadCSV csv, String file) {
        this(w, h);
        createFileChooser(file);
        importTable(csv);
        createInterface();
    }
    ApplicationFrame(int w, int h, String file) {
        this(w, h);
        createFileChooser(file);
        createInterface();
    }

    public void createFileChooser(String file) {
        defaultFilePath = file;
        defaultFileFolder = Paths.get("files/").toAbsolutePath().toString();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new CSVFilter());
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(defaultFileFolder));
        add(fileChooser);
        System.out.println(defaultFileFolder);

        //Show it.
        int returnVal = fileChooser.showDialog(this, "Open");
        //Process the results.
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.file = fileChooser.getSelectedFile().getPath();
            importTable(new ReadCSV(this.file));
            int lastSeparator = this.file.lastIndexOf("\\");
            if (this.file.substring(0, lastSeparator).equals(defaultFileFolder)) {
                this.file = this.file.substring(lastSeparator + 1);
            }
            if (tree.exists("file", "gui", "search", "button")) {
                ((JTextField) tree.get("file", "gui", "search", "button")).setText(this.file);
            }
        } else {
            createTable();
        }

    }

    public void importTable(ReadCSV csv) {
        importTable(csv, true);
    }
    public void importTable(ReadCSV csv, boolean importTables) {
        this.headers = (String[]) tree.put("headers", csv.table[0], "data", "table", "importedData");
        String[][] tempData = new String[csv.table.length - 1][headers.length]; // 2d array with all student records ordered by csv file row
        for (int i = 0; i < csv.table.length - 1; i += 1) {
            tempData[i] = csv.table[i + 1];
        }
        this.data = (String[][]) tree.put("data", tempData, "data", "table", "importedData");

        if (importTables) {
            DataTable oldTable = (DataTable) tree.get("importedTable", "gui", "table", "table");
            table = (DataTable) tree.put("importedTable", new DataTable(headers, data, this.tw - 32, this.th), "gui", "table", "table");
            ArrayList<String> moduleList = (ArrayList<String>) tree.put("moduleList", methods.ModuleList(table, true), "data", "list");
            methods.start = Integer.parseInt(moduleList.get(0));
            moduleList.remove(0);
            if (oldTable != null) {
                JScrollPane scrollpane = (JScrollPane) tree.get("table", "gui", "scroll", "table");
                if (scrollpane != null) {
                    scrollpane.setViewportView(table);
                }
                ArrayList<String> graphModuleList = (ArrayList) tree.put("graphModules", new ArrayList<String>(), "data", "list");
                ((JList) tree.get("moduleList", "gui", "list", "button")).setListData(graphModuleList.toArray());
                JComboBox combo = (JComboBox) tree.get("moduleList", "gui", "combo", "table");
                JComboBox graphModules = (JComboBox) tree.get("moduleList", "gui", "combo", "button");
                Vector<String> vector = new Vector<>(moduleList);
                replaceTables(vector, "");
                if (combo != null) {
                    combo.removeAllItems();
                    graphModules.removeAllItems();
                    for (String module : vector) {
                        combo.addItem(module);
                        graphModules.addItem(module);
                    }
                }
                else {
                    tree.put("moduleList", new JComboBox<>(vector), "gui", "combo", "table");
                }
            }
            System.out.println(table);
            System.out.println("Imported table.");
            if (panels.get("table") != null) {
                panels.get("filter").repaint();
                panels.get("filter").revalidate();
                panels.get("table").repaint();
                panels.get("table").revalidate();
            }
        }
    }

    public void replaceTables(Vector<String> moduleList, String singleModule) {
        String[][] moduleAverages = new String[moduleList.size()][6];
        for (int i = 0; i < moduleAverages.length; i += 1) {
            ArrayList<Integer> listMarks = methods.MarksList(moduleList.get(i), this.headers, this.data);
            moduleAverages[i][0] = moduleList.get(i);
            moduleAverages[i][1] = Integer.toString(methods.MinMark(listMarks));
            moduleAverages[i][2] = Integer.toString(methods.MaxMark(listMarks));
            moduleAverages[i][3] = Integer.toString(methods.AvgMark(listMarks));
            moduleAverages[i][4] = Integer.toString(methods.averagePerformance(methods.studentPerformance(this.data, false, moduleAverages[i][0], moduleList)));
            moduleAverages[i][5] = Integer.toString(methods.averagePerformance(methods.studentPerformance(this.data, true, moduleAverages[i][0], moduleList)));
        }
        averages = new DataTable(new String[] {"moduleCode", "min", "max", "average", "poorStudentAverages", "goodStudentAverages"}, moduleAverages);
        System.out.println(tree.getBranch("data", "table", "moduleAverages"));
        System.out.println(tree.getBranch("data", "table", "moduleData"));
        if (tree.getBranch("data", "table", "moduleAverages") != null) {
            System.out.println("[DBG]: Found module averages");
            tree.update("data", moduleAverages, "data", "table", "moduleAverages");
        }
        else {
            System.out.println("[DBG]: Created module averages");
            tree.put("headers", new String[] {"moduleCode", "min", "max", "average", "poorStudentAverages", "goodStudentAverages"}, "data", "table", "moduleAverages");
            tree.put("data", moduleAverages, "data", "table", "moduleAverages");
        }
        String[][] moduleData = new String[1][6];
        String modName = singleModule;

        System.out.println(methods.tuple(this.headers));
        for (int i = 0; i < moduleData.length; i += 1) {
            ArrayList<Integer> listMarks = methods.MarksList(modName, this.headers, this.data);
            System.out.println(methods.tuple(listMarks));
            moduleData[i][0] = modName;
            if (listMarks.size() > 0) {
                moduleData[i][1] = Integer.toString(methods.MinMark(listMarks));
                moduleData[i][2] = Integer.toString(methods.MaxMark(listMarks));
                moduleData[i][3] = Integer.toString(methods.AvgMark(listMarks));
            }
            else {
                moduleData[i][1] = "-1";
                moduleData[i][2] = "-1";
                moduleData[i][3] = "-1";
            }
        }
        if (tree.getBranch("data", "table", "moduleData") != null) {
            System.out.println("[DBG]: Found module data");
            tree.update("data", moduleData, "data", "table", "moduleData");
        }
        else {
            System.out.println("[DBG]: Created module data");
            tree.put("headers", new String[] {"moduleCode", "min", "max", "average"}, "data", "table", "moduleData");
            tree.put("data", moduleData, "data", "table", "moduleData");
        }

        String[] quartileHeaders = (String[]) tree.put("headers", new String[] {"Student RegNo", "Average Mark"}, "data", "table", "goodStudents");
        String[][] upperQuartile = (String[][]) tree.put("data", methods.studentPerformance(this.data, true), "data", "table", "goodStudents");
        System.out.println(upperQuartile);
        tree.put("headers", quartileHeaders, "data", "table", "badStudents");
        String[][] lowerQuartile = (String[][]) tree.put("data", methods.studentPerformance(this.data, false), "data", "table", "badStudents");
        System.out.println(lowerQuartile);

        String[] headers = (String[]) tree.get("headers","data", "table", "importedData");
        String[][] data = (String[][]) tree.get("data", "data", "table", "importedData");
        GetModulesFromQuartiles quartileChecker = new GetModulesFromQuartiles();
        String[][][] quartileInfo = quartileChecker.getModulesFromQuartiles(headers, data);

        String[][] keys = {{"lowerQuartile", "LQ"}, {"upperQuartile", "UQ"}};
        for (int i = 0; i < 2; i += 1) {
            String[][] quartileData = new String[moduleList.size()][3];
            String[][][] goodAndBad = {quartileInfo[i * 2], quartileInfo[(i * 2) + 1]};
            for (int j = 0; j < quartileData.length; j += 1) {
                quartileData[j][0] = moduleList.get(j);
                for (int k = 0; k < 2; k += 1) {
                    boolean done = false;
                    for (int l = 0; l < goodAndBad[k].length; l += 1) {
                        if (goodAndBad[k][l][0].equals(quartileData[j][0])) {
                            quartileData[j][k + 1] = goodAndBad[k][l][1];
                            done = true;
                        } else if (!done) {
                            quartileData[j][k + 1] = "0";
                        }
                    }
                }
                System.out.println("[QUARTILES/" + i + "]:" + Arrays.toString(quartileData[j]));
            }
            System.out.println(Arrays.deepToString(quartileData));
            System.out.println();
            if (tree.getBranch("data", "table", keys[i][0]) != null) {
                System.out.println("[DBG]: Found quartile data");
                tree.update("data", quartileData, "data", "table", keys[i][0]);
            }
            else {
                System.out.println("[DBG]: Created quartile data");
                tree.put("headers", new String[] {"moduleCode", "badStudents (" + keys[i][1] + ")", "goodStudents (" + keys[i][1] + ")"}, "data", "table", keys[i][0]);
                tree.put("data", quartileData, "data", "table", keys[i][0]);
            }
        }

        tree.generateTree(2);
        lineGraph = (LineGraph) tree.put("line", new LineGraph(null, null, table.w, table.h, tree), "gui", "graph");
        barGraph = (BarGraph) tree.put("bar", new BarGraph(new ArrayList<>(), table.w, table.h, tree), "gui", "graph");

    }

    public void createTable() {
        this.headers = new String[] {""};
        this.data = new String[][] {{"0"}};
    }

    public void createInterface() {
        tree.put("button", new JPanel(), "gui", "panel");
        tree.put("filter", new JPanel(), "gui", "panel");
        tree.put("table",  new JPanel(), "gui", "panel");
        tree.put("search", new JPanel(), "gui", "panel");
        tree.put("lineGraphInfo", new JPanel(), "gui", "panel");
        tree.put("barGraphInfo", new JPanel(), "gui", "panel");

        table = (DataTable) tree.put("importedTable", new DataTable(headers, data, this.tw - 32, this.th), "gui", "table", "table");
        //lineGraph = (LineGraph) tree.put("lineGraph", new LineGraph(table, this.tw - 32, this.th), "gui", "graph", "table");
        //barGraph = (BarGraph) tree.put("barGraph", new BarGraph(table, this.tw - 32, this.th), "gui", "graph", "table");
        panels.get("table").add(table);
        table.setVisible(true);

        JScrollPane scrollbar = (JScrollPane) tree.put("table", new JScrollPane(table), "gui", "scroll", "table");
        scrollbar.setVisible(true);
        panels.get("table").add(scrollbar);

        buttonPanel();
        filterPanel();
        bottomPanel();

        tree.generateTree(2);
        System.out.println("Initialized table.");
        System.out.println(table);

        add(panels.get("button"), BorderLayout.WEST);
        add(panels.get("filter"), BorderLayout.NORTH);
        add(panels.get("table"), BorderLayout.CENTER);
        add(panels.get("search"), BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(w, h); // used to change the size accordingly

        //generateReportGraph();
    }

    public void buttonPanel() {
        int pw = this.w - this.tw - 8;
        int ph = (int) panels.get("button").getPreferredSize().getHeight();

        JTextField fileField = (JTextField) tree.put("file", new JTextField(file), "gui", "search", "button");
        fileField.setPreferredSize(new Dimension(pw, 20));
        fileField.setEditable(false);
        panels.get("button").add(fileField);

        String[] buttonNames = {"Load New Table", "Show Graph"};
        int[] buttonActions = {ButtonListener.IMPORT_TABLE, ButtonListener.TOGGLE_TABLE_GRAPH};
        boolean[] active = {true, true};
        for (int i = 0; i < buttonNames.length; i += 1) {
            JButton button = (JButton) tree.put("button_" + i, new JButton(buttonNames[i]), "gui", "button", "button");
            button.addActionListener(new ButtonListener(tree, buttonActions[i], (ListController) tree.get("buttonControl", "gui", "controller", "button")));
            button.setPreferredSize(new Dimension(pw, 30));
            button.setEnabled(active[i]);
            panels.get("button").add(button);
        }

        DataTable table = (DataTable) tree.get("importedTable", "gui", "table", "table");
        tree.put("graphType", "line", "data", "string");
        String[] graphList = new String[] {"Line Graph", "Bar Graph"};
        JComboBox<String> selectGraph = (JComboBox<String>) tree.put("graphList", new JComboBox<>(graphList), "gui", "combo", "table");
        selectGraph.addActionListener(new ComboBoxListener(tree, selectGraph, ComboBoxListener.SWITCH_GRAPH_TYPE));
        selectGraph.setPreferredSize(new Dimension(pw, 30));
        selectGraph.setToolTipText("The graph you'd like to display the data in.");
        panels.get("button").add(selectGraph);

        lineGraphPanel(pw, 400, 110);
        panels.get("button").add(panels.get("lineGraphInfo"));
        barGraphPanel(pw, 400, 110);

        //Formatting
        panels.get("button").setBackground(new Color(110, 110, 110));
        panels.get("button").setPreferredSize(new Dimension(pw + 8, ph));
    }

    public void setGraphPanel(String panelName, int pw, int ph, int shade) {
        panels.get(panelName).setBackground(new Color(shade, shade, shade));
        panels.get(panelName).setPreferredSize(new Dimension(pw, ph));
    }
    public void lineGraphPanel(int pw, int ph, int shade) {
        String panelName = "lineGraphInfo";
        setGraphPanel(panelName, pw, ph, shade);
        JButton submitBTN = new JButton("Compare Modules");
        submitBTN.setPreferredSize(new Dimension(pw - 8, 30));
        submitBTN.addActionListener(new ButtonListener(tree, ButtonListener.SELECT_LINE_MODULES, null));
        panels.get(panelName).add(submitBTN);
        String[] axes = {"xAxis", "yAxis"};
        String[] labels = {"X-Axis", "Y-Axis"};
        for (int i = 0; i < axes.length; i += 1) {
            JLabel label = (JLabel) tree.put(axes[i], new JLabel(labels[i]), "gui", "search", "button", "lineGraphInfo");
            panels.get(panelName).add(label);
            JTextField field = (JTextField) tree.put(axes[i], new JTextField(), "gui", "search", "button", "lineGraphInfo");
            field.setPreferredSize(new Dimension(pw - 8, 30));
            field.setEditable(false);
            panels.get(panelName).add(field);
        }

    }

    public void barGraphPanel(int pw, int ph, int shade) {
        String panelName = "barGraphInfo";
        setGraphPanel(panelName, pw, ph, shade);

        ArrayList<String> graphModules = (ArrayList) tree.put("graphModules", new ArrayList<>(), "data", "list");
        JList moduleField = (JList) tree.put("moduleList", new JList(graphModules.toArray()), "gui", "list", "button");
        moduleField.setDragEnabled(true);
        moduleField.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        moduleField.setFixedCellWidth(pw - 20);
        moduleField.setFixedCellHeight(30);
        moduleField.setSize(new Dimension(moduleField.getFixedCellWidth(), (int) (30 * 6.5)));
        moduleField.setToolTipText("All the modules you have added to the graph. (Note: You can select modules in this list to delete them as well as using the combo box.)");
        panels.get(panelName).add(moduleField);

        JScrollPane scrollbar = (JScrollPane) tree.put("moduleList", new JScrollPane(moduleField), "gui", "scroll", "button");
        scrollbar.createVerticalScrollBar();
        scrollbar.setVisible(true);
        panels.get(panelName).add(scrollbar);

        Object[] moduleList = ((ArrayList<String>) tree.get("moduleList", "data", "list")).toArray();
        JComboBox<String> selectModule = (JComboBox<String>) tree.put("moduleList", new JComboBox<>(moduleList), "gui", "combo", "button");
        selectModule.setPreferredSize(new Dimension(pw - 12,30));
        selectModule.setToolTipText("The module you wish to add to or remove from your graph.");
        panels.get(panelName).add(selectModule);

        String[] buttonNames = new String[] {"add", "remove"};
        int [] buttonActions = new int[] {ButtonListener.ADD_MODULE, ButtonListener.REMOVE_MODULE};
        String[] buttonTooltips = new String[] {"Add a module", "Remove a (number of) module(s)"};
        boolean[] active = new boolean[] {true, true};
        for (int i = 0; i < buttonNames.length; i += 1) {
            JButton button = (JButton) tree.put("button_" + i, new JButton((Icon) tree.get(buttonNames[i], "gui", "icon")), "gui", "button", "button", "moduleList");
            button.addActionListener(new ButtonListener(tree, buttonActions[i], (ListController) tree.get("buttonControl", "gui", "controller", "button", "moduleList")));
            button.setPreferredSize(new Dimension((pw / 2) - 8, 30));
            button.setToolTipText(buttonTooltips[i]);
            button.setEnabled(active[i]);
            panels.get(panelName).add(button);
        }
    }

    public void filterPanel() {
        //shalini - Added a drop down list containing all the modules for the user to select.
        //pietro - added a combo box menu for the different graphs to visualise the data in.
        try {
            System.out.println();
            Vector<String> moduleList = new Vector<>(methods.ModuleList(table));
            JComboBox<String> selectModule = (JComboBox<String>) tree.put("moduleList", new JComboBox<>(moduleList), "gui", "combo", "table");
            selectModule.setPreferredSize(new Dimension(400,30));
            selectModule.setToolTipText("The module you wish to find.");

            panels.get("filter").add(selectModule);
            replaceTables(moduleList, (String) selectModule.getSelectedItem());
        }
        catch (NullPointerException e) {
            System.out.println("[ERR]: The module list is null.");
        }
        String[] buttonNames = {"Show Table", "Show Module Averages", "Show Single Module"};
        String[] tableKeys = {"importedData", "moduleAverages", "moduleData"};
        boolean[] active = {true, true, true};
        JButton[] buttons = new JButton[buttonNames.length];
        tree.put("buttonControl", new ListController(new String[2][2]), "gui", "controller", "filter");
        for (int i = 0; i < buttonNames.length; i += 1) {
            buttons[i] = (JButton) tree.put("button_" + i, new JButton(buttonNames[i]), "gui", "button", "filter");
            buttons[i].addActionListener(new ButtonListener(tree, tableKeys[i], (ListController) tree.get("buttonControl", "gui", "controller", "filter")));
            buttons[i].setPreferredSize(new Dimension(192, 30));
            buttons[i].setEnabled(active[i]);
            panels.get("filter").add(buttons[i]);
        }
        //Formatting
        panels.get("filter").setBackground(Color.LIGHT_GRAY);
    }

    public void bottomPanel() {
        //Add buttons
        String[] buttonNames = {"Generate Report", "Show Quartiles"};
        boolean[] active = {true, true};
        int[] buttonActions = {ButtonListener.GENERATE_PDF, ButtonListener.CHANGE_TABLE};
        JButton[] buttons = new JButton[buttonNames.length];
        tree.put("buttonControl", new ListController(new String[2][2]), "gui", "controller", "search");
        for (int i = 0; i < buttonNames.length; i += 1) {
            buttons[i] = (JButton) tree.put("button_" + i, new JButton(buttonNames[i]), "gui", "button", "search");
            buttons[i].setPreferredSize(new Dimension(180, 30));
            if (buttonActions[i] == ButtonListener.CHANGE_TABLE) {
                buttons[i].addActionListener(new ButtonListener(tree, "quartiles", null));
            }
            else {
                buttons[i].addActionListener(new ButtonListener(tree, buttonActions[i], null, new PDFGenerator(this)));
            }
            buttons[i].setEnabled(active[i]);
            panels.get("search").add(buttons[i]);
        }

        String quartile = (String) tree.put("selectedQuartile", "upper_quartile", "data", "string");
        JButton quartileToggle = (JButton) tree.put("quartileToggle", new JButton((Icon) tree.get(quartile, "gui", "icon")), "gui", "button", "search");
        quartileToggle.addActionListener(new ButtonListener(tree, ButtonListener.CHANGE_QUARTILE, (ListController) tree.get("buttonControl", "gui", "controller", "button")));
        quartileToggle.setPreferredSize(new Dimension(16, 16));
        quartileToggle.setToolTipText("Toggle the quartile.");
        quartileToggle.setEnabled(true);
        panels.get("search").add(quartileToggle);
        //Formatting
        panels.get("search").setBackground(Color.LIGHT_GRAY);
    }

    private void generateReportGraph () {
        String graphType = "bar";
        String [] allModulesArray = Arrays.copyOfRange(headers, 3, headers.length-2);
        ArrayList allModulesList = new ArrayList(Arrays.asList(allModulesArray));
        DataGraph graph = new BarGraph(allModulesList, 700, 500, tree);
        JFrame frame = new JFrame();
        frame.setSize(new Dimension(graph.w + 1, graph.h + (graph.GraphPad * 2)));
        frame.add(graph);
        frame.setVisible(true);
        BufferedImage lineIMG = graph.createImage(frame);
        System.out.println("[IMG]: " + Arrays.toString(graph.modules.toArray()));
        File outputFile = new File("files/graphs/" + graphType + "Graph " + Arrays.toString(graph.modules.toArray()) + ".jpg");
        try {
            ImageIO.write(lineIMG, "jpg", outputFile);
        }
        catch (IOException e) {
            System.out.println("[ERR]: " + e);
        }
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }
}

