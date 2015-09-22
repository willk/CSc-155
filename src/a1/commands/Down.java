package a1.commands;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 9/22/2015.
 */
public class Down extends AbstractAction {
    private static Down instance = null;

    private Down() {
        super("Down");
    }

    public static Down getInstance() {
        if (instance == null) instance = new Down();
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
