package a1;

import a1.views.VersionView;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.GLSLUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL4.GL_TRIANGLES;

public class OpenGLFrame extends JFrame implements GLEventListener, MouseWheelListener {
    private GLCanvas glCanvas;
    private int renderer, cNumber;
    private static float xAxis, yAxis, sAmt;
    private int VAO[] = new int[1];
    private VersionView vv;
    private FPSAnimator animator;
    private JButton up, down, changeColor;
    private JPanel buttons;
    private Package p = Package.getPackage("com.jogamp.opengl");

    public OpenGLFrame() {
        vv = new VersionView();
        this.setTitle("William Kinderman - CSc 155 - A1");
        this.setSize(1280, 800);
        this.setLocationRelativeTo(null);
        this.addMouseWheelListener(this);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        cNumber = 0;
        xAxis = 0.0f;
        yAxis = 0.0f;
        sAmt = 1.0f;

        up = new JButton();
        up.setText("Up");
        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (yAxis < 1.0) yAxis += 0.03;
            }
        });

        down = new JButton();
        down.setText("Down");
        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (yAxis > -1.0) yAxis -= 0.03;
            }
        });

        changeColor = new JButton();
        changeColor.setText("Change Color");
        changeColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cNumber += 1;
                cNumber %= 4;
            }
        });

        buttons = new JPanel();
        buttons.add(up);
        buttons.add(down);
        buttons.add(changeColor);

        glCanvas = new GLCanvas();
        glCanvas.addGLEventListener(this);

        this.getContentPane().add(glCanvas, BorderLayout.CENTER);
        this.getContentPane().add(buttons, BorderLayout.SOUTH);
        this.getContentPane().add(vv, BorderLayout.NORTH);
        this.setVisible(true);
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();

        FloatBuffer color = FloatBuffer.allocate(4);
        FloatBuffer attrib = FloatBuffer.allocate(4);

        xAxis += 1.0f;
        if (xAxis > 359.0) xAxis = 0;

        float xRadians = (float) Math.toRadians(xAxis);

        color.put(0, 0);
        color.put(1, 0);
        color.put(2, 0);
        color.put(3, 1);

        attrib.put(0, (float) (Math.sin(xRadians)));
        attrib.put(1, yAxis);
        attrib.put(2, 0);
        attrib.put(3, 0);


        gl.glClearBufferfv(GL_COLOR, 0, color);
        gl.glVertexAttribI1i(2, cNumber);
        gl.glVertexAttrib1f(1, sAmt);
        gl.glVertexAttrib4fv(0, attrib);
        gl.glUseProgram(renderer);

        gl.glFrontFace(GL_CCW);

        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) glAutoDrawable.getGL();

        // Get the OpenGL version number
        vv.updateOpenGLVersion(gl.glGetString(GL.GL_VERSION));
        System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) +
                " JOGL Version: " + p.getImplementationVersion());

        renderer = createShaderPrograms(glAutoDrawable);

        gl.glGenVertexArrays(VAO.length, VAO, 0);
        gl.glBindVertexArray(VAO[0]);

        animator = new FPSAnimator(glCanvas, 120);
        animator.start();
    }

    private int createShaderPrograms(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) glAutoDrawable.getGL();
        int[] vertexCompiled, fragmentCompiled, linked;
        vertexCompiled = new int[1];
        fragmentCompiled = new int[1];
        linked = new int[1];

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
        GLSLUtils.printOpenGLError(glAutoDrawable);
        gl.glGetShaderiv(vShader, GL4.GL_COMPILE_STATUS, vertexCompiled, 0);
        if (vertexCompiled[0] == 1) System.out.println("Vertex compilation success.");
        else System.out.println("Vertex compilation failure.");

        gl.glCompileShader(fShader);
        GLSLUtils.printOpenGLError(glAutoDrawable);
        gl.glGetShaderiv(fShader, GL4.GL_COMPILE_STATUS, fragmentCompiled, 0);
        if (vertexCompiled[0] == 1) System.out.println("Fragment compilation success.");
        else System.out.println("Fragment compilation failure.");

        int vfprogram = gl.glCreateProgram();
        gl.glAttachShader(vfprogram, vShader);
        gl.glAttachShader(vfprogram, fShader);

        gl.glLinkProgram(vfprogram);
        GLSLUtils.printOpenGLError(glAutoDrawable);
        gl.glGetProgramiv(vfprogram, GL4.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 1) System.out.println("Linking succeeded.");
        else {
            System.out.println("Linking failed.");
            GLSLUtils.printOpenGLError(glAutoDrawable);
        }

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);

        return vfprogram;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) sAmt += 0.1;
        else if (sAmt > 0.2) sAmt -= 0.1;
    }
}