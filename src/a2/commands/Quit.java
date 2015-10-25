package a2.commands;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 10/20/2015.
 */
public class Quit extends AbstractAction {
    private static Quit ourInstance = new Quit();

    private Quit() {
        super("Quit");
    }

    public static Quit getInstance() {
        return ourInstance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
