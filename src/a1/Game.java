package a1;

import a1.commands.ChangeColor;
import a1.commands.Down;
import a1.commands.Up;
import a1.views.ButtonView;
import a1.views.MapView;
import a1.views.VersionView;

import javax.swing.*;
import java.awt.*;

/*
 * GameWorld Initializer
 *
 */

public class Game extends JFrame {
    private ButtonView bv;
    private VersionView vv;
    private MapView mv;
    private Package p = Package.getPackage("com.jogamp.opengl");
    private GameWorld gw;

    public Game() {
        gw = new GameWorld();
        vv = new VersionView();
        bv = new ButtonView();
        gw.initLayout();

        this.setTitle("William Kinderman - CSc 155 - A1");
        this.setSize(1280, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.add(mv, BorderLayout.CENTER);
        this.add(bv, BorderLayout.SOUTH);
        this.add(vv, BorderLayout.NORTH);

        Up up = Up.getInstance();
        Down down = Down.getInstance();
        ChangeColor changeColor = ChangeColor.getInstance();

        up.setTarget(gw);
        down.setTarget(gw);
        down.setTarget(gw);

        this.setVisible(true);
        this.requestFocus();
    }
}