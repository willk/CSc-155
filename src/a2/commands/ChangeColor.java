package a2.commands;

import a2.GameWorld;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 9/22/2015.
 */
public class ChangeColor extends AbstractAction {
    private static ChangeColor instance = null;
    private GameWorld gameWorld;

    private ChangeColor() {
        super("Change Color");
    }

    public static ChangeColor getInstance() {
        if (instance == null) instance = new ChangeColor();
        return instance;
    }

    public void setTarget(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameWorld.changeColor();
    }
}
