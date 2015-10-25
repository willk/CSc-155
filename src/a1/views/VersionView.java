package a1.views;

import a1.IObservable;
import a1.IObserver;
import a1.commands.GLVersion;

import javax.swing.*;


/**
 * Created by willk on 9/24/2015.
 */
public class VersionView extends JPanel implements IObserver {
    private JLabel openGLVersion, JOGLVersion;
    private Package p = Package.getPackage("com.jogamp.opengl");

    public VersionView() {
        openGLVersion = new JLabel("OpenGL Version: " + GLVersion.getInstance().getVersion());
        JOGLVersion = new JLabel("JOGL Version: " + p.getImplementationVersion());

        this.add(openGLVersion);
        this.add(JOGLVersion);
    }

    private void updateOpenGLVersion(String s) {
        openGLVersion.setText("OpenGL Version: " + s);
    }

    @Override
    public void update(IObservable o) {
        System.out.println("JOGL Version: " + p.getImplementationVersion());
        System.out.println("OpenGL Version: " + GLVersion.getInstance().getVersion());
        updateOpenGLVersion(GLVersion.getInstance().getVersion());
    }
}
