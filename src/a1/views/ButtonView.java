package a1.views;

import a1.commands.ChangeColor;
import a1.commands.Down;
import a1.commands.Up;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created by willk on 9/24/2015.
 */

public class ButtonView extends JPanel {
    private ArrayList<JButton> buttons;
    private JButton up, down, changeColor;

    public ButtonView() {
        buttons = new ArrayList<>();

        up = new JButton(Up.getInstance());
        down = new JButton(Down.getInstance());
        changeColor = new JButton(ChangeColor.getInstance());

        buttons.add(up);
        buttons.add(down);
        buttons.add(changeColor);

        for (JButton button : buttons) {
            this.add(button);
        }
    }
}
