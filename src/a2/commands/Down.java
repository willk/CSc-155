package a2.commands;

import a2.objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/20/2015.
 */
public class Down extends AbstractAction {
    private static Down ourInstance = new Down();
    private Camera c;

    private Down() {
        super("Move Down");
    }

    public static Down getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.movey(-.25);
    }
}
