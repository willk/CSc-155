
package a3.commands;


import a3.objects.Camera;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/20/2015.
 */
public class StrafeRight extends AbstractAction {
    private static StrafeRight ourInstance = new StrafeRight();
    private Camera c;

    private StrafeRight() {
        super("Strafe Right");
    }

    public static StrafeRight getInstance() {
        return ourInstance;
    }

    public void setCamera(Camera c) {
        this.c = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        c.movex(.25);
    }
}
