package com;
import com.elmap.ElementMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ButtonListener implements ActionListener{
    //Controller object holds methods for actually doing stuff with data
    //Action variable indicates what exactly the instance of button is meant to do
    private ListController controller;
    int action;
    private List<OperationCompleteListener> listeners = new ArrayList<>();
    private ElementMap tree;
    private PDFGenerator pdfGenerator;
    private Map<String, JPanel> panels;
    private String tableKey;
    private Methods methods = new Methods();

    //Static variables for all actions buttons could perform.
    //Saves the trouble of having to remember exact strings or indexes.
    static int NULL = -1;
    static int IMPORT_TABLE = 0;
    static int CHANGE_TABLE = 1;
    static int ADD_MODULE = 2;
    static int REMOVE_MODULE = 3;
    static int CHANGE_QUARTILE = 4;
    static int SELECT_LINE_MODULES = 5;
    static int SUBMIT_LINE_MODULES = 6;
    static int TOGGLE_TABLE_GRAPH = 7;
    static int QUARTILES = 8;
    static int GENERATE_PDF = 9;

    ButtonListener(ElementMap tree, ListController controllerRef) {
        this.tree = tree;
        panels = (Map) tree.get("panel",  "gui");
        controller = controllerRef;
    }
    ButtonListener(ElementMap tree, String tableKey, ListController controllerRef) {
        this(tree, controllerRef);
        this.tableKey = tableKey;
        action = CHANGE_TABLE;
    }
    ButtonListener(ElementMap tree, int action, ListController controllerRef) {
        this(tree, controllerRef);
        this.action = action;
    }
    ButtonListener(ElementMap tree, int action, ListController controllerRef, PDFGenerator pdfGenerator) {
        this(tree, action, controllerRef);
        this.pdfGenerator = pdfGenerator;
    }

    void addCompleteListener(OperationCompleteListener listener) {
        listeners.add(listener);
    }

    void fireCompleteListeners() {
        for (OperationCompleteListener listener : listeners) {
            listener.operationComplete();
        }
    }

    public int createOptionPane(JScrollPane scrollpane, JButton submitButton) {
        return JOptionPane.showOptionDialog(null, scrollpane, "Select 2 modules", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null); //new JButton[] { submitButton }, submitButton);
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        System.out.println();
        System.out.println(": BUTTON PRESSED :");
        if (action == IMPORT_TABLE) {
            ApplicationFrame frame = (ApplicationFrame) tree.get("main", "gui", "frame");
            frame.createFileChooser(frame.defaultFilePath);
        }
        else if (action == CHANGE_TABLE) {
            DataTable table = (DataTable) tree.get("importedTable","gui", "table", "table");
            if (tree.branchExists("data", "table", tableKey) || tableKey.equals("quartiles")) {
                if (tableKey.equals("moduleData")) {
                    String[] headers = (String[]) tree.get("headers", "data", "table", "importedData");
                    String[][] data = (String[][]) tree.get("data", "data", "table", "importedData");
                    String[][] moduleData = new String[1][4];
                    tree.generateTree(2);
                    for (int i = 0; i < moduleData.length; i += 1) {
                        ArrayList<Integer> listMarks = methods.MarksList((String) ((JComboBox) tree.get("moduleList", "gui", "combo", "table")).getSelectedItem(),headers, data);
                        moduleData[i][0] = (String) ((JComboBox) tree.get("moduleList", "gui", "combo", "table")).getSelectedItem();
                        moduleData[i][1] = Integer.toString(methods.MinMark(listMarks));
                        moduleData[i][2] = Integer.toString(methods.MaxMark(listMarks));
                        moduleData[i][3] = Integer.toString(methods.AvgMark(listMarks));
                    }
                    System.out.println(methods.tuple(moduleData[0]));
                    tree.update("data", moduleData, "data", "table", tableKey);
                }
                String quartile = "";
                if (tableKey.equals("quartiles")) {
                    quartile = (String) tree.get("selectedQuartile", "data", "string");
                    if (quartile.equals("upper_quartile")) {
                        tableKey = "upperQuartile";
                    }
                    else {
                        tableKey = "lowerQuartile";
                    }
                }
                String[] headers = (String[]) tree.get("headers", "data", "table", tableKey);
                String[][] data = (String[][]) tree.get("data", "data", "table", tableKey);
                System.out.println(Arrays.deepToString(data));
                System.out.println(tableKey + " " + headers + " " + data);
                table.replaceTable(headers, data);
                table.display();
                System.out.println("[KEY]: " + methods.tuple(tableKey, quartile));
                if (tableKey.equals("upperQuartile") || tableKey.equals("lowerQuartile")) {
                    tableKey = "quartiles";
                }
            }
        }
        else if (action == CHANGE_QUARTILE) {
            String quartile;
            if (tree.get("selectedQuartile", "data", "string").equals("upper_quartile")) {
                quartile = "lower_quartile";
            }
            else {
                quartile = "upper_quartile";
            }
            tree.put("selectedQuartile", quartile,"data", "string");
            ((JButton) tree.get("quartileToggle", "gui", "button", "search")).setIcon((Icon) tree.get(quartile, "gui", "icon"));
        }
        else if (action == SELECT_LINE_MODULES) {
            Object[] moduleList = ((ArrayList<String>) tree.get("moduleList", "data", "list")).toArray();
            String[] boxNames = {"module_1", "module_2"};
            String[] modules = new String[boxNames.length];
            CheckboxGroup[] checkGroups = new CheckboxGroup[boxNames.length];
            Box box = Box.createVerticalBox();
            for (int i = 0; i < boxNames.length; i += 1) {
                checkGroups[i] = new CheckboxGroup();
                ArrayList<Checkbox> checkboxList = new ArrayList<>();
                box.add(new JLabel("Pick module " + (i + 1) + ": "));
                for (int j = 0; j < moduleList.length; j += 1) {
                    checkboxList.add(new Checkbox((String) moduleList[j], checkGroups[i], false));
                    box.add(checkboxList.get(j));
                }
            }
            JScrollPane scrollpane = new JScrollPane(box);
            scrollpane.setPreferredSize(new Dimension(200,400));

            JButton submitButton = new JButton((Icon) tree.get("tick", "gui", "icon"));
            submitButton.addActionListener(new ButtonListener(tree, ButtonListener.SUBMIT_LINE_MODULES, (ListController) tree.get("buttonControl", "gui", "controller", "button", "lineGraphInfo")));
            submitButton.setPreferredSize(new Dimension(scrollpane.getWidth(), 30));

            System.out.println(Methods.tuple(checkGroups));
            boolean done = false;
            while (!done) {
                int okPress = createOptionPane(scrollpane, submitButton);
                for (int i = 0; i < modules.length; i += 1) {
                    modules[i] = checkGroups[i].getSelectedCheckbox().getLabel();
                }
                if (okPress == JOptionPane.OK_OPTION) {
                    if (modules[0].equals(modules[1])) {
                        JOptionPane.showMessageDialog(null, "SAME MODULE SELECTED TWICE, Please select 2 different modules.", "same module error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        tree.put("lineModules", modules, "data", "list");
                        ((JTextField) tree.get("xAxis", "gui", "search", "button", "lineGraphInfo")).setText(modules[0]);
                        ((JTextField) tree.get("yAxis", "gui", "search", "button", "lineGraphInfo")).setText(modules[1]);
                        ((LineGraph) tree.get("line", "gui", "graph")).changeModules(modules[0], modules[1]);
                        tree.generateTree();
                        done = true;
                    }
                } else {
                    done = true;
                }
            }
        }
        else if (action == SUBMIT_LINE_MODULES) {
            JOptionPane pane = (JOptionPane) actionEvent.getSource();
            // set the value of the option pane
            pane.setValue(JOptionPane.OK_OPTION);
        }
        else if(action == QUARTILES)
        {
            String[] header = (String[]) tree.get("headers","data", "table", "importedData");
            String[][] data = (String[][]) tree.get("data", "data", "table", "importedData");
        }
        if (action == TOGGLE_TABLE_GRAPH) {
            String graphType = (String) tree.get("graphType", "data", "string");
            DataGraph graph = (DataGraph) tree.get(graphType, "gui", "graph");
            JFrame frame = new JFrame();
            frame.setSize(new Dimension(graph.w + 1, graph.h + (graph.GraphPad * 2)));
            frame.add(graph);
            frame.setVisible(true);
            BufferedImage lineIMG = graph.createImage(frame);
            System.out.println("[IMG]: " + Arrays.toString(graph.modules.toArray()));
            File outputFile = new File("files/graphs/" + graphType + "Graph.jpg");
            try {
                ImageIO.write(lineIMG, "jpg", outputFile);
            }
            catch (IOException e) {
                System.out.println("[ERR]: " + e);
            }
            System.out.println("[FRAME]: " + methods.tuple(frame.getWidth(), frame.getHeight(), graph.w, graph.h));
        }
        if (action == GENERATE_PDF) {
            pdfGenerator.generateReport();
        }
        else {
            JList moduleField = (JList) tree.get("moduleList", "gui", "list", "button");
            JComboBox moduleList = (JComboBox) tree.get("moduleList", "gui", "combo", "button");
            ArrayList<String> graphModules = (ArrayList<String>) tree.get("graphModules", "data", "list");
            String module = (String) moduleList.getSelectedItem();
            if (action == ButtonListener.ADD_MODULE) {
                if (moduleField.getModel().getSize() > 0) {
                    if (!(moduleField.getNextMatch(module, 0, Position.Bias.Forward) > -1)) {
                        graphModules.add(module);
                    }
                }
                else {
                    graphModules.add(module);
                }
            }
            if (action == ButtonListener.REMOVE_MODULE) {
                if (moduleField.getModel().getSize() > 0) {
                    int[] selected = moduleField.getSelectedIndices();
                    if (selected.length > 0) {
                        for (int i = 0; i < selected.length; i += 1) {
                            graphModules.remove(selected[i] - i);
                        }
                    } else if (moduleField.getNextMatch(module, 0, Position.Bias.Forward) > -1) {
                        graphModules.remove(module);
                    }
                }
            }
            moduleField.setListData(graphModules.toArray());
            tree.put("graphModules", graphModules, "data", "list");
            ((BarGraph) tree.get("bar", "gui", "graph")).changeModules(graphModules);
        }
        panels.get("table").revalidate();
        panels.get("table").repaint();
    }
}
