package a1;

import a1.commands.ChangeColor;
import a1.commands.Down;
import a1.commands.Up;
import a1.views.ButtonView;
import a1.views.MapView;
import a1.views.VersionView;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import java.awt.*;

/*
 * GameWorld Initializer
 *
 */

public class Game extends JFrame {
    private MapView mv;
    private VersionView vv;
    private ButtonView bv;
    private GameWorld gw;
    private FPSAnimator fpsAnimator;

    public Game() {
        gw = new GameWorld();
        mv = new MapView();
        bv = new ButtonView();
        vv = new VersionView();

        gw.initLayout();
        gw.addObserver(mv);
        gw.addObserver(vv);
        gw.notifyObservers();

        this.setTitle("William Kinderman - CSc 155 - A1");
        this.setSize(1280, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.add(mv, BorderLayout.CENTER);
        this.add(bv, BorderLayout.SOUTH);
        this.add(vv, BorderLayout.NORTH);

        fpsAnimator = new FPSAnimator(mv, 120);
        fpsAnimator.start();

        Up up = Up.getInstance();
        Down down = Down.getInstance();
        ChangeColor changeColor = ChangeColor.getInstance();

        up.setTarget(gw);
        down.setTarget(gw);
        changeColor.setTarget(gw);

        this.setVisible(true);
        this.requestFocus();
    }
}