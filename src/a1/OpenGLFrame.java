package a1;

import a1.views.ButtonView;
import a1.views.VersionView;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import graphicslib3D.GLSLUtils;

import javax.swing.*;
import java.awt.*;

import static com.jogamp.opengl.GL4.GL_TRIANGLES;

/*
 * TODO:
 *  FPSAnimator
 *  Buttons to work
 *  Get JOGL version
 *  Get triangle animated
 *  Get triangle to change color
 */

public class OpenGLFrame extends JFrame implements GLEventListener {
    private GLCanvas glCanvas;
    private int renderer;
    private int VAO[] = new int[1];
    private ButtonView bv;
    private VersionView vv;

    public OpenGLFrame() {
        bv = new ButtonView();
        vv = new VersionView();
        this.setTitle("William Kinderman - CSc 155 - A1");
        this.setSize(1280, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        glCanvas = new GLCanvas();
        glCanvas.addGLEventListener(this);

        this.getContentPane().add(glCanvas, BorderLayout.CENTER);
        this.getContentPane().add(bv, BorderLayout.SOUTH);
        this.getContentPane().add(vv, BorderLayout.NORTH);
        this.setVisible(true);
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
        gl.glUseProgram(renderer);
        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();

        // Set the OpenGL version number
        vv.updateOpenGLVersion(gl.glGetString(GL.GL_VERSION));
        renderer = createShaderPrograms(drawable);
        gl.glGenVertexArrays(VAO.length, VAO, 0);
        gl.glBindVertexArray(VAO[0]);
    }

    private int createShaderPrograms(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();

        String vshaderSource[] = GLSLUtils.readShaderSource("src/a1/vertex.glsl");
        String fshaderSource[] = GLSLUtils.readShaderSource("src/a1/fragment.glsl");
        int lengths[];

        int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);

        lengths = new int[vshaderSource.length];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = vshaderSource[i].length();
        }
        gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, lengths, 0);

        lengths = new int[fshaderSource.length];
        for (int i = 0; i < lengths.length; i++) {
            lengths[i] = fshaderSource[i].length();
        }
        gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, lengths, 0);

        gl.glCompileShader(vShader);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);
        return vfprogram;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}