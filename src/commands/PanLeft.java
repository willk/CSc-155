package commands;

import objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/24/2015.
 */
public class PanLeft extends AbstractAction {
    private static PanLeft ourInstance = new PanLeft();
    private Camera c;

    private PanLeft() {
        super("Pan down.");
    }

    public static PanLeft getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.pan(-.25);
    }
}
