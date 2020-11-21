package com.matrixeater.src;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FunctionalAction extends AbstractAction {

    private ActionListener myaction;

    public FunctionalAction(ActionListener customaction) {
        this.myaction = customaction;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        myaction.actionPerformed(e);
    }

    public ActionListener getMyaction() {
        return myaction;
    }

    public void setMyaction(ActionListener myaction) {
        this.myaction = myaction;
    }

}