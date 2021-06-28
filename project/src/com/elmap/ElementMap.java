package com.elmap;

import com.DataTable;
import com.ListController;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * @name elmap
 * @description The elmap system allows for simple organisation of components, values and other instances,
 * making structuring and retrieval easy and functional. The system uses a tree-like structure, allowing for new
 * branches to be created quickly and easily.
 *      The entire tree can be displayed using the generateTree() command, allowing for multiple levels of specificity
 * in regards to the detail shown about each element. Objects can easily be stored, altered and queried using the key
 * path system, allowing the user to specify an exact route to the object they are looking for.
 * @author ck18334
 * @version 1.0
 * @since 2019-11-22
 */

public class ElementMap extends HashMap {
    public String name;
    public ElementMap() {

    }
    public ElementMap(String name) {
        this.name = name;
        createBaseNode(name, true);
    }

    /** @name getBranch (1 derivative, 7 inclusions)
     * Determines whether a particular branch exists and returns its reference.
     * @param keyPath the route to the inspected branch.
     * @param extendable (optional) whether a new branch will be created if the route is cut off.
     * @return Map
     */
    public Map getBranch(boolean extendable, String ... keyPath) {
        Map<String, Object> node = this;
        String key;
        for (int i = 0; i < keyPath.length; i += 1) {
            key = keyPath[i];
            Object selection = node.get(key);
            if (selection != null) {
                if (selection instanceof Map) {
                    node = (Map) selection;
                }
            } else if (extendable) {
                String[] cutPath = new String[i + 1];
                System.arraycopy(keyPath, 0, cutPath, 0, i);
                node = (Map) extend(key, false, cutPath);
            }
            else {
                return null;
            }
        }
        return node;
    }
    public Map getBranch(String ... keyPath) {
        return getBranch(false, keyPath);
    }

    /** @name get
     * Retrieves a specified object from the tree.
     * @param key the object's string-based key signature.
     * @param keyPath the route to the required object.
     * @return Object (has to be cast into a specific type once returned)
     */
    public Object get(String key, String ... keyPath) {
        System.out.println(": GET :");
        Map branch = getBranch(keyPath);
        if (branch != null) {
            return branch.get(key);
        }
        return null;
    }

    /** @name put
     * Creates a new object at a specified branch and returns the reference. (Note: new branches will be created if
     * they weren't already extant before the operation.)
     * @param key the object's string-based key signature.
     * @param value the object's value.
     * @param keyPath the route to the required object.
     * @return Object (has to be cast into a specific type once returned)
     */
    public Object put(String key, Object value, String ... keyPath) {
        System.out.println(": PUT :");
        Map node = getBranch(true, keyPath);
        node.put(key, value);
        return node.get(key);
    }

    /** @name remove
     * Removes a specified object from the tree.
     * @param key the object's string-based key signature.
     * @param keyPath the route to the required object.
     * @return boolean
     */
    public boolean remove(String key, String ... keyPath) {
        System.out.println(": REMOVE :");
        Map node = getBranch(false, keyPath);
        if (get(key, keyPath) != null) {
            node.remove(key);
            return true;
        }
        return false;
    }

    /** @name exists
     * Checks if a specific object is extant within the tree.
     * @param key the object's string-based key signature.
     * @param keyPath the route to the queried object.
     * @return boolean
     */
    public boolean exists(String key, String ... keyPath) {
        System.out.println(": EXISTS :");
        if (get(key, keyPath) != null) {
            return true;
        }
        return false;
    }

    /** @name branchExists
     * Checks if a specific branch acts as a route within the tree.
     * @param keyPath the route to the queried branch.
     * @return boolean
     */
    public boolean branchExists(String ... keyPath) {
        System.out.println(": EXISTS :");
        Map node = getBranch(false, keyPath);
        if (node != null) {
            return true;
        }
        return false;
    }

    /** @name update
     * Updates the key-value pairing of a specific object within the tree, returning its reference.
     * @param key the object's string-based key signature.
     * @param keyPath the route to the object to be updated.
     * @return Object (has to be cast into a specific type once returned)
     */
    public Object update(String key, Object value, String ... keyPath) {
        System.out.println(": UPDATE :");
        Map node = getBranch(false, keyPath);
        if (exists(key, keyPath)) {
            node.replace(key, value);
            return node;
        }
        return null;
    }

    /** @name extend (1 inclusion)
     * Extends a currently extant branch and returns its reference, allowing it to split off.
     * @param key the key for this branch.
     * @param isEndNode whether this branch can sprout new branches.
     * @param keyPath the route to the branch nub.
     * @return Map
     */
    public Map extend(String key, boolean isEndNode, String ... keyPath) {
        System.out.println(": EXTEND :");
        Map<String, Object> node = this;
        String currentKey;
        for (int i = 0; i < keyPath.length; i += 1) {
            currentKey = keyPath[i];
            Object selection = node.get(currentKey);
            if (selection instanceof Map) {
                node = (Map) selection;
            }
        }
        Map childNode;
        if (isEndNode) {
            childNode = new HashMap<String, Object>();
        }
        else {
            childNode = new ElementMap();
        }
        node.put(key, childNode);
        return childNode;
    }

    /** @name createBaseNode
     * Creates a fork in the trunk of the tree. Only use if creating a new section that its entirely disjoint from the
     * rest of the tree.
     * @param childKey the key signature of the fork to be created.
     * @param isEndNode whether this branch can sprout new branches.
     * @return Map
     */
    public Map createBaseNode(String childKey, boolean isEndNode) {
        return extend(childKey, isEndNode);
    }

    /** @name parentPanel (1 inclusion)
     * Determines the parent panel of a component in the tree and returns its reference. In this case, a parent panel
     * refers to the panel used to display the component on-screen. (Note: this function only iterates through the
     * panels in the tree segment 'gui/panels'. If your panel is not in there, it will not be registered.)
     * @param component the component to be checked for.
     * @return JPanel
     */
    public JPanel parentPanel(Object component) {
        if (component instanceof Component) {
            HashSet<JPanel> mapSet = new HashSet<>(getBranch("gui", "panel").values());
            for (JPanel panel : mapSet) {
                if (java.util.Arrays.asList(panel.getComponents()).contains(component)) {
                    return panel;
                }
            }
            return null;
        }
        return null;
    }

    /** @name isActive (1 inclusion)
     * Returns whether a component is assigned to any panels. (Note: this function only iterates through the panels in
     * the tree segment 'gui/panels'. If your panel is not in there, it will not be registered.)
     * @param component the component to be checked for.
     * @return boolean
     */
    public boolean isActive(Object component) {
        if (parentPanel(component) == null) {
            return false;
        }
        return true;
    }

    /** @name generateTree (1 derivative)
     * Displays the tree in a (sub)directory relationship, delineating between subdivisions from right to left. Folders are
     * displayed with a '+' next to their names.
     * @param attributeLevel (optional) the level of detail shown about each object.
     *                       0: only key-names.
     *                       1: data-types, as well as whether the object is active.
     *                       2: basic information, such as the name of a button or the number of elements in a list.
     *                       3: detailed information, such as the contents of a combo box or the headers in a table.
     *                       4: the full object reference, including all pieces of attribute data.
     */
    public void generateTree(int attributeLevel) {
        System.out.println();
        System.out.println(": DISPLAY TREE (ATTR " + attributeLevel + ") :");
        expandTree(this,0, attributeLevel);
    }
    public void generateTree() {
        generateTree(0);
    }

    /** @name expandTree (1 inclusion)
     * A recursive function to allow the entire tree to be expanded and displayed.
     * @param node the branch to be expanded.
     * @param depth how deep into the tree this branch is.
     * @param attributeLevel see above.
     */
    public void expandTree(Map<String, Object> node, int depth, int attributeLevel) {
        String indent = "  ";
        for (int i = 0; i < depth; i += 1) {
            indent += "    ";
        }
        ArrayList<Entry<String, Object>> nodeList = new ArrayList<>(node.entrySet());
        nodeList.sort(Entry.comparingByKey());
        for (Entry<String, Object> childNode : nodeList) {
            String element = childNode.getKey();
            if (childNode.getValue() instanceof Map) {
                element = indent + "+ " + element;
                System.out.println(element);
                expandTree((Map) childNode.getValue(), depth + 1, attributeLevel);
            }
            else {
                element = indent + element;
                boolean categorised = true;
                if (attributeLevel > 0) {
                    String active = "Inactive";
                    if (isActive(childNode.getValue())) {
                        active = "Active";
                    }
                    if (childNode.getValue() instanceof JButton) {
                        String name = ((JButton) childNode.getValue()).getText();
                        element += " (" + active + " JButton";
                        if (attributeLevel < 4) {
                            if (attributeLevel >= 2) {
                                if (name == null) {
                                    element += " called " + name + "";
                                } else {
                                    element += " called '" + name + "'";
                                }
                            }
                        }
                        else {
                            element += " " + childNode.getValue();
                        }
                    } else if (childNode.getValue() instanceof JComboBox) {
                        JComboBox comboBox = (JComboBox) childNode.getValue();
                        element += " (" + active + " JComboBox";
                        if (attributeLevel < 4) {
                            if (attributeLevel >= 2) {
                                element += " with " + comboBox.getItemCount() + " components";
                            }
                            if (attributeLevel >= 3) {
                                element += ", including ";
                                if (comboBox.getItemCount() > 0) {
                                    element += "'" + comboBox.getItemAt(0) + "'";
                                    for (int i = 0; i < comboBox.getItemCount(); i += 1) {
                                        element += ", '" + comboBox.getItemAt(i) + "'";
                                    }
                                }
                            }
                        }
                        else {
                            element += " " + childNode.getValue();
                        }
                    } else if (childNode.getValue() instanceof JPanel) {
                        JPanel panel = (JPanel) childNode.getValue();
                        element += " (" + active + " JPanel";
                        if (attributeLevel < 4) {
                            if (attributeLevel >= 2) {
                                element += " with " + panel.getComponentCount() + " components";
                            }
                            if (attributeLevel >= 3) {
                                element += ", including ";
                                if (panel.getComponentCount() > 0) {
                                    element += panel.getComponent(0);
                                    for (int i = 0; i < panel.getComponentCount(); i += 1) {
                                        element += ", " + panel.getComponent(i);
                                    }
                                }
                            }
                        }
                        else {
                            element += " " + childNode.getValue();
                        }
                    } else if (childNode.getValue() instanceof DataTable) {
                        DataTable table = (DataTable) childNode.getValue();
                        element += " (" + active + " DataTable";
                        if (attributeLevel < 4) {
                            if (attributeLevel >= 2) {
                                element += " with " + table.headers.length + " columns and " + table.data.length + " rows";
                            }
                            if (attributeLevel >= 3) {
                                element += ", using headers ";
                                if (table.headers.length > 0) {
                                    element += "'" + table.headers[0] + "'";
                                    for (int i = 0; i < table.headers.length; i += 1) {
                                        element += ", '" + table.headers[i] + "'";
                                    }
                                }
                            }
                        }
                        else {
                            element += " " + childNode.getValue();
                        }
                    } else if (childNode.getValue() instanceof JScrollPane) {
                        JScrollPane scrollbar = (JScrollPane) childNode.getValue();
                        element += " (" + active + " JScrollPane";
                        if (attributeLevel < 4) {
                            if (attributeLevel >= 2) {
                                element += " assigned to " + scrollbar.getViewport().getComponents();
                            }
                        }
                        else {
                            element += " " + childNode.getValue();
                        }
                    } else {
                        if (childNode.getValue() instanceof ListController) {
                            element += " (" + active + " ListController";
                        } else if (childNode.getValue() instanceof JTextField) {
                            element += " (" + active + " JTextField";
                        }
                        if (attributeLevel == 4) {
                            element += " " + childNode.getValue();
                        }
                        else {
                            categorised = false;
                        }
                    }
                    if (categorised) {
                        element += ")";
                    }
                }

                System.out.println(element);
            }
        }
    }
}
