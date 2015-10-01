package a1.views;

import a1.GameWorld;
import a1.IObservable;
import a1.IObserver;

import javax.swing.*;


/**
 * Created by willk on 9/24/2015.
 */
public class VersionView extends JPanel implements IObserver {
    private JLabel openGLVersion, JOGLVersion;

    public VersionView() {
        openGLVersion = new JLabel("OpenGL Version: UNKNOWN");
        JOGLVersion = new JLabel("JOGL Version: UNKNOWN");

        this.add(openGLVersion);
        this.add(JOGLVersion);
    }

    private void updateOpenGLVersion(String s) {
        openGLVersion.setText("OpenGL Version: " + s);
    }

    private void updateJOGLVersion(String s) {
        JOGLVersion.setText("JOGL Version: " + s);
    }


    @Override
    public void update(IObservable o) {
        updateOpenGLVersion(((GameWorld) o).getGLVersion());
        updateJOGLVersion(((GameWorld) o).getJOGLVersion());
    }
}
