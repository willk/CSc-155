package a1.views;

import javax.swing.*;


/**
 * Created by willk on 9/24/2015.
 */
public class VersionView extends JPanel {
    private JLabel openGLVersion, JOGLVersion;

    public VersionView() {
        openGLVersion = new JLabel("OpenGL Version: ");
        JOGLVersion = new JLabel("JOGL Version: ");

        this.add(openGLVersion);
        this.add(JOGLVersion);
    }

    public void updateOpenGLVersion(String s) {
        openGLVersion.setText("OpenGL Version: " + s);
    }
}
