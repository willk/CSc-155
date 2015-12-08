package a3.commands;

import a3.objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/24/2015.
 */
public class PitchUp extends AbstractAction {
    private static PitchUp ourInstance = new PitchUp();
    private Camera c;

    private PitchUp() {
        super("Pitch Up");
    }

    public static PitchUp getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.pitch(.25);
    }
}
