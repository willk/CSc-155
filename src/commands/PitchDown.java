package commands;

import objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/24/2015.
 */
public class PitchDown extends AbstractAction {
    private static PitchDown ourInstance = new PitchDown();
    private Camera c;

    private PitchDown() {
        super("Pitch up");
    }

    public static PitchDown getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.pitch(-.25);
    }
}
