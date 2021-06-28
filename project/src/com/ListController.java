package com;

import javax.swing.*;

//Class that holds methods for manipulating the data
public class ListController {
    //The data
    String[][] list;

    /**
     *
     * @param data
     */
    ListController (String[][] data) {
        list = data;
    }

    void doSomethingWithTheList(String input) { }

    void test() {
        System.out.println("Controller called successfully");
    }


}
