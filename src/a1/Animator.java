package a1;

import com.jogamp.opengl.awt.GLCanvas;

/**
 * Created by willk on 9/22/2015.
 */
public class Animator {
    private GLCanvas glCanvas;
    private int framerate;

    public Animator(GLCanvas glCanvas, int framerate) {
        this.glCanvas = glCanvas;
        this.framerate = framerate;
    }

    public Animator(GLCanvas glCanvas) {
        this.glCanvas = glCanvas;
        this.framerate = 60;
    }

    public void start() {
        while (true) {
            glCanvas.display();
            try {
                Thread.sleep(framerate);
            } catch (InterruptedException e) {
            }
        }
    }
}
