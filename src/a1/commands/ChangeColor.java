package a1.commands;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 9/22/2015.
 */
public class ChangeColor extends AbstractAction {
    private static ChangeColor instance = null;

    private ChangeColor() {
        super("Change Color");
    }

    public static ChangeColor getInstance() {
        if (instance == null) instance = new ChangeColor();
        return instance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
