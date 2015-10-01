package a1.views;

import a1.GameWorld;
import a1.IObservable;
import a1.IObserver;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import graphicslib3D.GLSLUtils;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.GL_CCW;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;

/**
 * Created by willk on 9/29/2015.
 */
public class MapView extends GLJPanel implements GLEventListener, MouseWheelListener, IObserver {
    private int renderer, VAO[], colorNumber;
    private float xAxis, yAxis, scaleAmount;
    private boolean versionFlag;
    private String glVersion, joglVersion;
    private GameWorld gameWorld;

    public MapView() {

        colorNumber = 0;
        xAxis = 0;
        yAxis = 0;
        scaleAmount = 1;
        VAO = new int[1];
        versionFlag = false;

        this.addMouseWheelListener(this);
        this.addGLEventListener(this);
    }

    @Override
    public void init(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) glAutoDrawable.getGL();

        // Get the OpenGL version number
        glVersion = gl.glGetString(GL.GL_VERSION);
        Package p = Package.getPackage("com.jogamp.opengl");
        joglVersion = p.getImplementationVersion();

        renderer = createShaderPrograms(glAutoDrawable);


        gl.glGenVertexArrays(VAO.length, VAO, 0);
        gl.glBindVertexArray(VAO[0]);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL4 gl = (GL4) glAutoDrawable.getGL();

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
        gl.glVertexAttribI1i(2, colorNumber);
        gl.glVertexAttrib1f(1, scaleAmount);
        gl.glVertexAttrib4fv(0, attrib);
        gl.glUseProgram(renderer);

        gl.glFrontFace(GL_CCW);

        gl.glDrawArrays(GL_TRIANGLES, 0, 3);
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
        if (linked[0] == 1) System.out.println("Linking success.");
        else {
            System.out.println("Linking failed.");
            GLSLUtils.printOpenGLError(glAutoDrawable);
        }

        gl.glDeleteShader(vShader);
        gl.glDeleteShader(fShader);

        return vfprogram;
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int i, int i1, int i2, int i3) {
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) scaleAmount += 0.1;
        else if (scaleAmount > 0.2) scaleAmount -= 0.1;
    }

    @Override
    public void update(IObservable o) {
        this.gameWorld = (GameWorld) o;
        if (!versionFlag) {
            versionFlag = true;
            gameWorld.setVersion(glVersion, joglVersion);
        }
        this.yAxis = gameWorld.getYAxis();
        this.colorNumber = gameWorld.getColorNumber();
    }
}
