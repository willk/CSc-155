package a2;

import a2.commands.*;
import a2.objects.*;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.GLSLUtils;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import graphicslib3D.Vertex3D;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL4.*;

public class GLWorld extends JFrame implements GLEventListener {
    private JPanel panel;
    private GLCanvas canvas;
    private int renderer, lineRenderer, vao[], vbo[], earth, p1, p2, moon;
    private Camera camera;
    private Cube cube;
    private Sphere sphere;
    private Diamond diamond;
    private TextureReader tr;

    private float sunx, suny, sunz;
    private boolean lines;

    public GLWorld() {
        this.setTitle("William Kinderman - CSc 155 - A2");
        this.setSize(800, 800);

        tr = new TextureReader();
        lines = true;
        camera = new Camera(.25f, .25f, 5);

        panel = new JPanel();
        this.initWorld();

        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        getContentPane().add(panel);
        getContentPane().add(canvas);

        vao = new int[1];
        vbo = new int[7];

        cube = new Cube();
        diamond = new Diamond();
        sphere = new Sphere(48);

        this.setVisible(true);
        FPSAnimator fpsAnimator = new FPSAnimator(canvas, 120);
        fpsAnimator.start();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    @SuppressWarnings("Duplicates")
    private void initWorld() {
        Forward.getInstance().setCamera(camera);
        Back.getInstance().setCamera(camera);
        Down.getInstance().setCamera(camera);
        Up.getInstance().setCamera(camera);
        StrafeRight.getInstance().setCamera(camera);
        StrafeLeft.getInstance().setCamera(camera);
        PanRight.getInstance().setCamera(camera);
        PanLeft.getInstance().setCamera(camera);
        PitchDown.getInstance().setCamera(camera);
        PitchUp.getInstance().setCamera(camera);
        WorldAxes.getInstance().setTarget(this);

        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), KeyEvent.VK_SPACE);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), KeyEvent.VK_UP);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), KeyEvent.VK_DOWN);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), KeyEvent.VK_LEFT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), KeyEvent.VK_RIGHT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), KeyEvent.VK_W);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), KeyEvent.VK_S);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), KeyEvent.VK_A);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), KeyEvent.VK_D);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), KeyEvent.VK_Q);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), KeyEvent.VK_E);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), KeyEvent.VK_ESCAPE);

        ActionMap actionMap = panel.getActionMap();
        actionMap.put(KeyEvent.VK_SPACE, WorldAxes.getInstance());
        actionMap.put(KeyEvent.VK_UP, PitchUp.getInstance());
        actionMap.put(KeyEvent.VK_DOWN, PitchDown.getInstance());
        actionMap.put(KeyEvent.VK_LEFT, PanRight.getInstance());
        actionMap.put(KeyEvent.VK_RIGHT, PanLeft.getInstance());
        actionMap.put(KeyEvent.VK_Q, Up.getInstance());
        actionMap.put(KeyEvent.VK_E, Down.getInstance());
        actionMap.put(KeyEvent.VK_W, Forward.getInstance());
        actionMap.put(KeyEvent.VK_S, Back.getInstance());
        actionMap.put(KeyEvent.VK_A, StrafeLeft.getInstance());
        actionMap.put(KeyEvent.VK_D, StrafeRight.getInstance());
        actionMap.put(KeyEvent.VK_ESCAPE, Quit.getInstance());
    }

    // Puts the things into the VBOs
    private void setupVertices(GL4 gl) {
        int[] sphereIndices = sphere.getIndices();
        Vertex3D[] sphereVertices = sphere.getVertices();

        float[] p = new float[sphereIndices.length * 3];
        float[] t = new float[sphereIndices.length * 2];
        float[] n = new float[sphereIndices.length * 3];

        for (int i = 0; i < sphere.getIndices().length; i++) {
            p[i * 3] = (float) (sphereVertices[sphereIndices[i]].getX());
            p[i * 3 + 1] = (float) (sphereVertices[sphereIndices[i]].getY());
            p[i * 3 + 2] = (float) (sphereVertices[sphereIndices[i]].getZ());

            t[i * 2] = (float) (sphereVertices[sphereIndices[i]].getS());
            t[i * 2 + 1] = (float) (sphereVertices[sphereIndices[i]].getT());

            n[i * 3] = (float) (sphereVertices[sphereIndices[i]]).getNormalX();
            n[i * 3 + 1] = (float) (sphereVertices[sphereIndices[i]]).getNormalY();
            n[i * 3 + 2] = (float) (sphereVertices[sphereIndices[i]]).getNormalZ();
        }

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        // Sun
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer sphereBuffer = FloatBuffer.wrap(p);
        gl.glBufferData(GL_ARRAY_BUFFER, sphereBuffer.limit() * 4, sphereBuffer, GL_STATIC_DRAW);

        // Sun TS
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer texBuffer = FloatBuffer.wrap(t);
        gl.glBufferData(GL_ARRAY_BUFFER, texBuffer.limit() * 4, texBuffer, GL_STATIC_DRAW);

        // Sun Normal
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer normalBuffers = FloatBuffer.wrap(t);
        gl.glBufferData(GL_ARRAY_BUFFER, normalBuffers.limit() * 4, normalBuffers, GL_STATIC_DRAW);

        // Cube
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        FloatBuffer cubeBuffer = FloatBuffer.wrap(cube.getVertexPositions());
        gl.glBufferData(GL_ARRAY_BUFFER, cubeBuffer.limit() * 4, cubeBuffer, GL_STATIC_DRAW);

        // Cube TC
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        FloatBuffer cubeTCBuffer = FloatBuffer.wrap(cube.getVertexPositions());
        gl.glBufferData(GL_ARRAY_BUFFER, cubeTCBuffer.limit() * 4, cubeTCBuffer, GL_STATIC_DRAW);

        // Diamond
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        FloatBuffer diamondBuffer = FloatBuffer.wrap(diamond.getVertexPositions());
        gl.glBufferData(GL_ARRAY_BUFFER, diamondBuffer.limit() * 4, diamondBuffer, GL_STATIC_DRAW);

        // Diamond TC
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        FloatBuffer diamondTSBuffer = FloatBuffer.wrap(diamond.getVertexPositions());
        gl.glBufferData(GL_ARRAY_BUFFER, diamondTSBuffer.limit() * 4, diamondTSBuffer, GL_STATIC_DRAW);
    }

    private Matrix3D perspective(float fovy, float aspect, float n, float f) {
        float A, B, C, q;
        Matrix3D r = new Matrix3D(), rt;

        q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        A = q / aspect;
        B = (n + f) / (n - f);
        C = (2.0f * n * f) / (n - f);

        r.setElementAt(0, 0, A);
        r.setElementAt(1, 1, q);
        r.setElementAt(2, 2, B);
        r.setElementAt(2, 3, -1.0f);
        r.setElementAt(3, 2, C);
        r.setElementAt(3, 3, 0.0f);
        rt = r.transpose();

        return rt;
    }

    private int createShaderPrograms(GLAutoDrawable d, String v, String f) {
        GL4 gl = (GL4) d.getGL();

        GLSLUtils u = new GLSLUtils();
        int l[], vertexShader, fragmentShader, p;
        String vShaderSource[], fShaderSource[];

        vShaderSource = u.readShaderSource(v);
        fShaderSource = u.readShaderSource(f);

        vertexShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        fragmentShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);

        l = new int[vShaderSource.length];
        for (int i = 0; i < l.length; i++) l[i] = vShaderSource[i].length();
        gl.glShaderSource(vertexShader, vShaderSource.length, vShaderSource, l, 0);

        l = new int[fShaderSource.length];
        for (int i = 0; i < l.length; i++) l[i] = fShaderSource[i].length();
        gl.glShaderSource(fragmentShader, fShaderSource.length, fShaderSource, l, 0);

        gl.glCompileShader(vertexShader);
        gl.glCompileShader(fragmentShader);

        p = gl.glCreateProgram();
        gl.glAttachShader(p, vertexShader);
        gl.glAttachShader(p, fragmentShader);
        gl.glLinkProgram(p);
        return p;
    }

    public void display(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();
        int mv_loc, proj_loc;
        float aspect;
        Matrix3D pMatrix;

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        FloatBuffer background = FloatBuffer.allocate(4);
        gl.glClearBufferfv(GL_COLOR, 0, background);

        gl.glClear(GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(renderer);

        mv_loc = gl.glGetUniformLocation(renderer, "mv_matrix");
        proj_loc = gl.glGetUniformLocation(renderer, "proj_matrix");

        aspect = canvas.getWidth() / canvas.getHeight();
        pMatrix = perspective(50.0f, aspect, 0.1f, 1000.0f);

        MatrixStack s = new MatrixStack(20);
        s.pushMatrix(); // Push camera Matrix
        s.loadMatrix(camera.getVTM()); // apply camera transforms
        double amt = (double) (System.currentTimeMillis() % 360000) / 1000.0;

        // SUN
        s.pushMatrix(); // SUN matrix
        s.translate(sunx, suny, sunz);
        s.pushMatrix(); // Sun's rotation
        s.rotate((System.currentTimeMillis() % 3600) / 10, 1, 5, 0);
        s.pushMatrix();
        s.scale(.25, .25, .25);
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, earth);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, sphere.getIndices().length);
        s.popMatrix(); // pop Scale
        s.popMatrix(); // pop Rotation

        // PLANET
        s.pushMatrix(); // Keep sun's translate, add
        s.translate(Math.sin(amt), 0.0f, Math.cos(amt));
        s.pushMatrix();
        s.rotate((System.currentTimeMillis() % 3600) / 5, 0, 1, 0);
        s.pushMatrix();
        s.scale(.25, .25, .25);
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, p1);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_TRIANGLES, 0, cube.getVertexPositions().length);
        s.popMatrix(); // pop scale
        s.popMatrix(); // pop rotation

        // MOON
        s.pushMatrix();
        s.translate(0.0f, Math.sin(amt) / 2.0f, Math.cos(amt) / 2.0f);
        s.pushMatrix();
        s.rotate((System.currentTimeMillis() % 3600) / 5, 0, 0, 1);
        s.pushMatrix();
        s.scale(0.0625, 0.0625, 0.0625);
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, moon);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_TRIANGLES, 0, diamond.getVertexPositions().length);
        s.popMatrix(); // pop scale
        s.popMatrix(); // pop rotate
        // END MOON
        s.popMatrix(); // pop planet translate
        // END PLANET

        // PLANET2 Hierarchical
        s.pushMatrix(); // P2 trans
        s.translate(Math.sin(amt) * 1.25, Math.sin(amt), Math.cos(amt) * 1.25);
        s.pushMatrix(); // P2 rotate
        s.rotate((System.currentTimeMillis() % 3600) / 2.5, 0, 5, 0);
        s.pushMatrix(); // P2 scale
        s.scale(.125, .125, .125);
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, p2);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_TRIANGLES, 0, diamond.getVertexPositions().length);

        // PLANET2 TOP
        s.pushMatrix();
        s.translate(0, 1, 0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, p2);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glDrawArrays(GL_TRIANGLES, 0, diamond.getVertexPositions().length);
        s.popMatrix(); // P2 TOP pop trans


        s.popMatrix(); // p2 pop scale
        s.popMatrix(); // p2 pop rotation
        s.popMatrix(); // p2 pop trans

        s.popMatrix(); // pop sun's translate
        s.popMatrix();
        // END SUN

        if (lines) {
            gl.glUseProgram(lineRenderer);
            mv_loc = gl.glGetUniformLocation(lineRenderer, "mv_matrix");
            proj_loc = gl.glGetUniformLocation(lineRenderer, "proj_matrix");
            gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
            gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
            gl.glDrawArrays(GL_LINES, 0, 6);
        }
    }

    public void init(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();
        renderer = createShaderPrograms(d, "src/a2/shaders/vertex.glsl", "src/a2/shaders/fragment.glsl");
        lineRenderer = createShaderPrograms(d, "src/a2/shaders/lineVertex.glsl", "src/a2/shaders/lineFragment.glsl");

        setupVertices(gl);

        sunx = 0.0f;
        suny = 0.0f;
        sunz = 0.0f;

        earth = tr.loadTexture(d, "src/a2/textures/earth.jpg");
        p1 = tr.loadTexture(d, "src/a2/textures/cracked.jpg");
        p2 = tr.loadTexture(d, "src/a2/textures/rusty.jpg");
        moon = tr.loadTexture(d, "src/a2/textures/yarn.jpg");
    }

    public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable d) {
    }

    public void toggleLines() {
        lines = !lines;
    }
}