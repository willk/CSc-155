package commands;

import a4.GLWorld;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/20/2015.
 */
public class Lights extends AbstractAction {
    private static Lights ourInstance = new Lights();
    private GLWorld target;

    private Lights() {
        super("Toggle World Axes");
    }

    public static Lights getInstance() {
        return ourInstance;
    }

    public void setTarget(GLWorld target) {
        this.target = target;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        target.toggleLights();
    }
}
