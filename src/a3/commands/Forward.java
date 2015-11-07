package a3.commands;

import a3.objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/20/2015.
 */
public class Forward extends AbstractAction {
    private static Forward ourInstance = new Forward();
    private Camera c;

    private Forward() {
        super("Move Forward");
    }

    public static Forward getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.movez(.25);
    }
}
