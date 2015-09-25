package a1;

import com.jogamp.opengl.awt.GLCanvas;

/**
 * Created by willk on 9/22/2015.
 */
public class Animator {
    private GLCanvas glCanvas;
    private int fps;

    public Animator(GLCanvas glCanvas, int fps) {
        this.glCanvas = glCanvas;
        this.fps = fps;
    }

    public Animator(GLCanvas glCanvas) {
        this.glCanvas = glCanvas;
        this.fps = 60;
    }

    public void start() {
        while (true) {
            glCanvas.display();
            try {
                Thread.sleep(fps);
            } catch (InterruptedException e) {
            }
        }
    }
}
