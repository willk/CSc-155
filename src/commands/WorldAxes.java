package commands;

import a4.GLWorld;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/20/2015.
 */
public class WorldAxes extends AbstractAction {
    private static WorldAxes ourInstance = new WorldAxes();
    private GLWorld target;

    private WorldAxes() {
        super("Toggle World Axes");
    }

    public static WorldAxes getInstance() {
        return ourInstance;
    }

    public void setTarget(GLWorld target) {
        this.target = target;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        target.toggleLines();
    }
}
