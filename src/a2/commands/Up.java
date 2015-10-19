package a2.commands;

import a2.GameWorld;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Created by willk on 9/22/2015.
 */
public class Up extends AbstractAction {
    private static Up instance = null;
    private GameWorld gameWorld;

    private Up() {
        super("Up");
    }

    public static Up getInstance() {
        if (instance == null) instance = new Up();
        return instance;
    }

    public void setTarget(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        gameWorld.up();
    }
}
