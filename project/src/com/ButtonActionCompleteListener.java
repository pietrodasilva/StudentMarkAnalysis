package com;

//A listener that updates the output after operation with the data
public class ButtonActionCompleteListener implements OperationCompleteListener {

    @Override
    public void operationComplete() {
        System.out.println("Completion listener fired");
    }
}
