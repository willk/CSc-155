package a2.commands;

import a2.GameWorld;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 9/22/2015.
 */
public class Down extends AbstractAction {
    private static Down instance = null;
    private GameWorld gameWorld;

    private Down() {
        super("Down");
    }

    public static Down getInstance() {
        if (instance == null) instance = new Down();
        return instance;
    }

    public void setTarget(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameWorld.down();
    }
}
