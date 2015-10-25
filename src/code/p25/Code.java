package code.p25;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL4.GL_COLOR;
import static com.jogamp.opengl.GL4.GL_POINTS;

public class Code extends JFrame implements GLEventListener {
    private static double x = 0.0;
    private GLCanvas myCanvas;
    private int rendering_program;
    private int VAO[] = new int[1];

    public Code() {
        setTitle("Chapter2 - program5");
        setSize(400, 200);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        getContentPane().add(myCanvas);
        setVisible(true);
        Animator animator = new Animator(myCanvas);
        Thread thread =
                new Thread(new Runnable() {
                    public void run() {
                        animator.start();
                    }
                });
        thread.start();
    }

    public static void main(String[] args) {
        new Code();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
        x = x + 10.0;
        if (x > 359.0) x = 0.0;
        float y = (float) Math.toRadians(x);
        FloatBuffer red = FloatBuffer.allocate(4);
        red.put(0, (float) (Math.sin(y) * 0.5 + 0.5));
        red.put(1, (float) (Math.cos(y) * 0.5 + 0.5));
        red.put(2, 0.0f);
        red.put(3, 1.0f);
        gl.glClearBufferfv(GL_COLOR, 0, red);

        gl.glUseProgram(rendering_program);
        gl.glDrawArrays(GL_POINTS, 0, 1);
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
        rendering_program = createShaderPrograms(drawable);
        gl.glGenVertexArrays(VAO.length, VAO, 0);
        gl.glBindVertexArray(VAO[0]);
    }

    private int createShaderPrograms(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();

        String vshaderSource[] =
                {"#version 430    \n",
                        "void main(void) \n",
                        "{ gl_Position = vec4(0.0, 0.0, 0.5, 1.0); } \n",
                };
        String fshaderSource[] =
                {"#version 430    \n",
                        "out vec4 color; \n",
                        "void main(void) \n",
                        "{ color = vec4(0.0, 0.8, 1.0, 1.0); } \n"
                };

        int vShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        gl.glShaderSource(vShader, 3, vshaderSource, null);
        gl.glCompileShader(vShader);

        int fShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
        gl.glShaderSource(fShader, 4, fshaderSource, null);
        gl.glCompileShader(fShader);

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);
        gl.glLinkProgram(vfprogram);

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);
        return vfprogram;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }
}