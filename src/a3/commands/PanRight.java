package a3.commands;

import a3.objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/24/2015.
 */
public class PanRight extends AbstractAction {
    private static PanRight ourInstance = new PanRight();
    private Camera c;

    private PanRight() {
        super("Pan up.");
    }

    public static PanRight getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.pan(.25);
    }
}
