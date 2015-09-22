package a1.commands;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 9/22/2015.
 */
public class Up extends AbstractAction {
    private static Up instance = null;

    private Up() {
        super("Up");
    }

    public static Up getInstance() {
        if (instance == null) instance = new Up();
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
