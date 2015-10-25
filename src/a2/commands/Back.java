package a2.commands;

import a2.objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/20/2015.
 */
public class Back extends AbstractAction {
    private static Back ourInstance = new Back();
    private Camera c;

    private Back() {
        super("Move Back");
    }

    public static Back getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.movez(-.25);
    }
}
