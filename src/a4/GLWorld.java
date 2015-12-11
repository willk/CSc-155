package a4;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import commands.*;
import graphicslib3D.*;
import graphicslib3D.light.PositionalLight;
import graphicslib3D.shape.Torus;
import objects.*;

import javax.swing.*;
import java.awt.event.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2GL3.GL_CLIP_DISTANCE0;

/**
 * Created by willk on 12/7/2015.
 */
public class GLWorld extends JFrame implements GLEventListener, MouseMotionListener, MouseWheelListener, MouseListener {
    private JPanel panel;
    private GLCanvas canvas;
    private int renderer, lineRenderer, lightRenderer, clipRenderer, lightOffRenderer, fishRenderer, vao[], vbo[], flipLocation;
    private Camera camera;
    private Cube cube;
    private Torus torus;
    private Sphere sphere;
    private ImportedModel pyramid;
    private ImportedModel fish;
    private TextureReader tr;
    private Material silverMaterial, jadeMaterial, goldMaterial;
    private boolean lineFlag, lightFlag;
    private float[] globalAmbient;
    private PositionalLight positionalLight;
    private Point3D lightPosition;

    private int fishyTexture, jadeTexture, goldTexture, waterTexture;

    public GLWorld() {
        this.setTitle("William Kinderman - CSc 155 - A4");
        this.setSize(800, 800);

        camera = new Camera(.25f, .25f, 25);

        panel = new JPanel();
        this.initWorld();

        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        getContentPane().add(panel);
        getContentPane().add(canvas);

        this.setVisible(true);
        FPSAnimator fpsAnimator = new FPSAnimator(canvas, 120);
        fpsAnimator.start();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

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
        Lights.getInstance().setTarget(this);
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
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, 0), KeyEvent.VK_L);

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
        actionMap.put(KeyEvent.VK_L, Lights.getInstance());
    }


    // Puts the things into the VBOs
    private void setupVertices(GL4 gl) {
        int[] sphere_indices = sphere.getIndices(), torus_indices = torus.getIndices(), pyramid_indices = pyramid.getIndices();
        Vertex3D[] sphere_vertices = sphere.getVertices(), torus_vertices = torus.getVertices(), pyramid_vertices = pyramid.getVertices();

        // The Sphere
        float[] sp = new float[sphere.getIndices().length * 3];
        float[] st = new float[sphere.getIndices().length * 2];
        float[] sn = new float[sphere.getIndices().length * 3];

        for (int i = 0; i < sphere.getIndices().length; i++) {
            sp[i * 3] = (float) sphere_vertices[sphere_indices[i]].getX();
            sp[i * 3 + 1] = (float) sphere_vertices[sphere_indices[i]].getY();
            sp[i * 3 + 2] = (float) sphere_vertices[sphere_indices[i]].getZ();

            st[i * 2] = (float) sphere_vertices[sphere_indices[i]].getS();
            st[i * 2 + 1] = (float) sphere_vertices[sphere_indices[i]].getT();

            sn[i * 3] = (float) sphere_vertices[sphere_indices[i]].getNormalX();
            sn[i * 3 + 1] = (float) sphere_vertices[sphere_indices[i]].getNormalY();
            sn[i * 3 + 2] = (float) sphere_vertices[sphere_indices[i]].getNormalZ();
        }

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vBuffer = FloatBuffer.wrap(sp);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer tBuffer = FloatBuffer.wrap(st);
        gl.glBufferData(GL_ARRAY_BUFFER, tBuffer.limit() * 4, tBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer nBuffer = FloatBuffer.wrap(sn);
        gl.glBufferData(GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL_STATIC_DRAW);
        // END Sphere

        // The Torus
        float[] tp = new float[torus.getIndices().length * 3];
        float[] tt = new float[torus.getIndices().length * 2];
        float[] tn = new float[torus.getIndices().length * 3];

        for (int i = 0; i < torus.getIndices().length; i++) {
            tp[i * 3] = (float) torus_vertices[torus_indices[i]].getX();
            tp[i * 3 + 1] = (float) torus_vertices[torus_indices[i]].getY();
            tp[i * 3 + 2] = (float) torus_vertices[torus_indices[i]].getZ();

            tt[i * 2] = (float) torus_vertices[torus_indices[i]].getS();
            tt[i * 2 + 1] = (float) torus_vertices[torus_indices[i]].getT();

            tn[i * 3] = (float) torus_vertices[torus_indices[i]].getNormalX();
            tn[i * 3 + 1] = (float) torus_vertices[torus_indices[i]].getNormalY();
            tn[i * 3 + 2] = (float) torus_vertices[torus_indices[i]].getNormalZ();
        }

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        vBuffer = FloatBuffer.wrap(tp);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        tBuffer = FloatBuffer.wrap(tt);
        gl.glBufferData(GL_ARRAY_BUFFER, tBuffer.limit() * 4, tBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        nBuffer = FloatBuffer.wrap(tn);
        gl.glBufferData(GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL_STATIC_DRAW);
        // END Torus

        // The Pyramid
        float[] pp = new float[pyramid.getIndices().length * 3];
        float[] pt = new float[pyramid.getIndices().length * 2];
        float[] pn = new float[pyramid.getIndices().length * 3];

        for (int i = 0; i < pyramid.getIndices().length; i++) {
            pp[i * 3] = (float) pyramid_vertices[pyramid_indices[i]].getX();
            pp[i * 3 + 1] = (float) pyramid_vertices[pyramid_indices[i]].getY();
            pp[i * 3 + 2] = (float) pyramid_vertices[pyramid_indices[i]].getZ();

            pt[i * 2] = (float) pyramid_vertices[pyramid_indices[i]].getS();
            pt[i * 2 + 1] = (float) pyramid_vertices[pyramid_indices[i]].getT();

            pn[i * 3] = (float) pyramid_vertices[pyramid_indices[i]].getNormalX();
            pn[i * 3 + 1] = (float) pyramid_vertices[pyramid_indices[i]].getNormalY();
            pn[i * 3 + 2] = (float) pyramid_vertices[pyramid_indices[i]].getNormalZ();
        }

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        vBuffer = FloatBuffer.wrap(pp);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        tBuffer = FloatBuffer.wrap(pt);
        gl.glBufferData(GL_ARRAY_BUFFER, tBuffer.limit() * 4, tBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        nBuffer = FloatBuffer.wrap(pn);
        gl.glBufferData(GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL_STATIC_DRAW);

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
        GLErrors err = new GLErrors(d);

        GLSLUtils u = new GLSLUtils();
        int l[], vertexShader, fragmentShader, p;
        String vShaderSource[], fShaderSource[];

        vShaderSource = GLSLUtils.readShaderSource(v);
        fShaderSource = GLSLUtils.readShaderSource(f);

        vertexShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
        fragmentShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);

        l = new int[vShaderSource.length];
        for (int i = 0; i < l.length; i++) l[i] = vShaderSource[i].length();
        gl.glShaderSource(vertexShader, vShaderSource.length, vShaderSource, l, 0);

        l = new int[fShaderSource.length];
        for (int i = 0; i < l.length; i++) l[i] = fShaderSource[i].length();
        gl.glShaderSource(fragmentShader, fShaderSource.length, fShaderSource, l, 0);

        gl.glCompileShader(vertexShader);
        err.OpenGLError();
        err.vertexError(vertexShader, v);

        gl.glCompileShader(fragmentShader);
        err.OpenGLError();
        err.fragmentError(fragmentShader, f);

        p = gl.glCreateProgram();
        gl.glAttachShader(p, vertexShader);
        gl.glAttachShader(p, fragmentShader);

        gl.glLinkProgram(p);
        err.OpenGLError();
        err.linkError(p);

        return p;
    }

    public void display(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        positionalLight = new PositionalLight();
//        if (lightFlag) {
//            positionalLight.setPosition(lightPosition);
//            positionalLight.setAmbient(new float[]{1, 0, 0, 1});
//            positionalLight.setDiffuse(new float[]{1, 1, 1, 1});
//            positionalLight.setSpecular(new float[]{1, 1, 1, 1});
//
//            positionalLight.setConstantAtt(0);
//        } else {
//            positionalLight.setPosition(lightPosition);
//            positionalLight.setAmbient(new float[]{0, 0, 0, 1});
//            positionalLight.setDiffuse(new float[]{1, 1, 1, 1});
//            positionalLight.setSpecular(new float[]{1, 1, 1, 1});
//            positionalLight.setConstantAtt(0);
//        }

        Matrix3D pMatrix;
        int mv_loc, proj_loc, n_loc;
        float amt = (float) (System.currentTimeMillis() % 36000) * (float) (Math.pow(2, -10)), aspect;

        // Need this to clear the coordinate planes that are drawn.
        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glClearBufferfv(GL_COLOR, 0, FloatBuffer.allocate(4));

        gl.glEnable(GL_DEPTH_TEST);
        gl.glEnable(GL_CULL_FACE);
        gl.glDepthFunc(GL_LEQUAL);


        aspect = canvas.getWidth() / canvas.getHeight();
        pMatrix = perspective(50.0f, aspect, 0.1f, 1000.0f);

        // World building.
        MatrixStack s = new MatrixStack(20);
        s.pushMatrix(); // Push camera Matrix
        s.multMatrix(camera.getVTM()); // apply camera transforms

        /* Pyramid */
        /* vbo 6, 7, 8 */
        /* Pyramid Renderer */
        gl.glUseProgram(renderer);
        mv_loc = gl.glGetUniformLocation(renderer, "mv_matrix");
        proj_loc = gl.glGetUniformLocation(renderer, "proj_matrix");
        n_loc = gl.glGetUniformLocation(renderer, "n_matrix");

        /* Lighting */
        positionalLight.setPosition(lightPosition);
        createLighting(d, s.peek(), renderer, goldMaterial);
        /* End Lighting*/
        /* End Pyramid Renderer */

        s.pushMatrix();
        s.translate(0, 0, 0);
        s.pushMatrix();
        s.scale(5, 5, 5);

        // Taking the top of the Stack and putting it in the Uniform
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_loc, 1, false, s.peek().inverse().transpose().getFloatValues(), 0);

        // I am working with this buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        // (location = 0, take in 3 at a time, floats, *, *, *);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        // Enable location = 0
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, goldTexture);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glFrontFace(GL_CCW);
        gl.glDrawArraysInstanced(GL_TRIANGLES, 0, pyramid.getNumIndices(), 3);

        s.popMatrix();
        s.popMatrix();
        /* End Pyramid */

        /* Torus */
        /* vbo 3, 4, 5 */
        /* Pyramid Renderer */
        gl.glUseProgram(renderer);
        mv_loc = gl.glGetUniformLocation(renderer, "mv_matrix");
        proj_loc = gl.glGetUniformLocation(renderer, "proj_matrix");
        n_loc = gl.glGetUniformLocation(renderer, "n_matrix");

        /* Lighting */
        positionalLight.setPosition(lightPosition);
        createLighting(d, s.peek(), renderer, jadeMaterial);
        /* End Lighting*/
        /* End Torus Renderer */

        s.pushMatrix();
        s.translate(0, amt * 10, 0);

        // Taking the top of the Stack and putting it in the Uniform
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_loc, 1, false, s.peek().inverse().transpose().getFloatValues(), 0);

        // I am working with this buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        // (location = 0, take in 3 at a time, floats, *, *, *);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        // Enable location = 0
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, jadeTexture);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glFrontFace(GL_CCW);
        gl.glDrawArraysInstanced(GL_TRIANGLES, 0, torus.getIndices().length, 3);

        s.popMatrix();
        /* End Torus */

        /* Light */
        s.pushMatrix();
        s.translate(lightPosition.getX(), lightPosition.getY(), lightPosition.getZ());
        gl.glPointSize(30f);

        if (lightFlag) {
            gl.glUseProgram(lightRenderer);
            mv_loc = gl.glGetUniformLocation(lightRenderer, "mv_matrix");
            proj_loc = gl.glGetUniformLocation(lightRenderer, "proj_matrix");
        } else {
            gl.glUseProgram(lightOffRenderer);
            mv_loc = gl.glGetUniformLocation(lightOffRenderer, "mv_matrix");
            proj_loc = gl.glGetUniformLocation(lightOffRenderer, "proj_matrix");
        }
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);

        gl.glDrawArrays(GL_POINTS, 0, 1);
        gl.glPointSize(1f);

        s.popMatrix();
        /* End Light */

        /* Lines */
        if (!lineFlag) {
            gl.glUseProgram(lineRenderer);
            mv_loc = gl.glGetUniformLocation(lineRenderer, "mv_matrix");
            proj_loc = gl.glGetUniformLocation(lineRenderer, "proj_matrix");
            gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
            gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
            gl.glDrawArrays(GL_LINES, 0, 6);
        }
        /* End Lines */
    }

    private void createLighting(GLAutoDrawable d, Matrix3D matrixStack, int renderingProgram, Material material) {
        GL4 gl = (GL4) d.getGL();

        Point3D lp = positionalLight.getPosition();
//        Point3D lpv = lp.mult(matrixStack);
        Point3D lpv = lp;

        float[] currentLightPosition = new float[]{(float) lpv.getX(), (float) lpv.getY(), (float) lpv.getZ()};

        // sets the current global ambient settings
        int globalAmbientLocation = gl.glGetUniformLocation(renderingProgram, "globalAmbient");
        gl.glProgramUniform4fv(renderingProgram, globalAmbientLocation, 1, globalAmbient, 0);

        // get the locs of the positionalLight and mats fields from the shader

        int ambientLocation = gl.glGetUniformLocation(renderingProgram, "light.ambient");
        int diffuseLocation = gl.glGetUniformLocation(renderingProgram, "light.diffuse");
        int spectralLocation = gl.glGetUniformLocation(renderingProgram, "light.specular");
        int position = gl.glGetUniformLocation(renderingProgram, "light.position");
        int mAmbientLocation = gl.glGetUniformLocation(renderingProgram, "material.ambient");
        int mDiffuseLocation = gl.glGetUniformLocation(renderingProgram, "material.diffuse");
        int mSpectralLocation = gl.glGetUniformLocation(renderingProgram, "material.specular");
        int mShinyLocation = gl.glGetUniformLocation(renderingProgram, "material.shininess");

        // set the values in the a3.shaders
        gl.glProgramUniform4fv(renderingProgram, ambientLocation, 1, positionalLight.getAmbient(), 0);
        gl.glProgramUniform4fv(renderingProgram, diffuseLocation, 1, positionalLight.getDiffuse(), 0);
        gl.glProgramUniform4fv(renderingProgram, spectralLocation, 1, positionalLight.getSpecular(), 0);
        gl.glProgramUniform3fv(renderingProgram, position, 1, currentLightPosition, 0);
        gl.glProgramUniform4fv(renderingProgram, mAmbientLocation, 1, material.getAmbient(), 0);
        gl.glProgramUniform4fv(renderingProgram, mDiffuseLocation, 1, material.getDiffuse(), 0);
        gl.glProgramUniform4fv(renderingProgram, mSpectralLocation, 1, material.getSpecular(), 0);
        gl.glProgramUniform1f(renderingProgram, mShinyLocation, material.getShininess());

    }

    public void init(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        // Setup models
        pyramid = new ImportedModel("pyramid.obj");
        sphere = new Sphere(48);
        torus = new Torus(0.6f, 0.4f, 48);

        tr = new TextureReader();
        lineFlag = true;
        lightFlag = true;

        goldMaterial = Material.GOLD;
        silverMaterial = Material.SILVER;

        jadeMaterial = new Material();
        jadeMaterial.setAmbient(new float[]{0.135f, 0.2225f, 0.1575f, 0.95f});
        jadeMaterial.setDiffuse(new float[]{0.54f, 0.89f, 0.63f, 0.95f});
        jadeMaterial.setSpecular(new float[]{0.3162f, 0.3162f, 0.3162f, 0.3162f});
        jadeMaterial.setShininess(12.8f);

        vao = new int[1];
        vbo = new int[12];

        lightPosition = new Point3D(0, 0, 10);
        globalAmbient = new float[]{1, 1, 1, 1.0f};

        gl.glEnable(GL_CLIP_DISTANCE0);

        lineRenderer = createShaderPrograms(d, "src/shaders/line_vertex.glsl", "src/shaders/line_fragment.glsl");
        fishRenderer = createShaderPrograms(d, "src/shaders/fish_vertex.glsl", "src/shaders/fish_fragment.glsl");
        renderer = createShaderPrograms(d, "src/shaders/default_vertex.glsl", "src/shaders/default_fragment.glsl");
        lightRenderer = createShaderPrograms(d, "src/shaders/light_vertex.glsl", "src/shaders/light_fragment.glsl");
        clipRenderer = createShaderPrograms(d, "src/shaders/clipped_vertex.glsl", "src/shaders/clipped_fragment.glsl");
        lightOffRenderer = createShaderPrograms(d, "src/shaders/light_off_vertex.glsl", "src/shaders/light_off_fragment.glsl");


        setupVertices(gl);

        jadeTexture = tr.loadTexture(d, "src/textures/jade.jpg");
        goldTexture = tr.loadTexture(d, "src/textures/gold.jpg");
//        waterTexture = tr.loadTexture(d, "src/textures/water.jpg");
//        fishyTexture = tr.loadTexture(d, "src/textures/fish.jpg");
    }

    public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable d) {
    }

    public void toggleLines() {
        lineFlag = !lineFlag;
    }

    public void toggleLights() {
        lightFlag = !lightFlag;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float z = (float) lightPosition.getZ();

        if (e.getX() > this.getWidth() / 2 && e.getX() < this.getWidth())
            lightPosition.setX(this.getWidth() / 2 + (e.getX() - this.getWidth()));
        else if (e.getX() > this.getWidth()) lightPosition.setX(this.getWidth());
        else if (e.getX() < this.getWidth() / 2) lightPosition.setX(e.getX() - this.getWidth() / 2);
        else lightPosition.setX(0);

        if (e.getY() > this.getHeight() / 2 && e.getY() < this.getHeight())
            lightPosition.setY(-(this.getHeight() / 2 + (e.getY() - this.getHeight())));
        else if (e.getY() > this.getHeight()) lightPosition.setY(this.getHeight());
        else if (e.getY() < this.getHeight() / 2) lightPosition.setY(-(e.getY() - this.getHeight() / 2));
        else lightPosition.setY(0);


        lightPosition.scale(Math.pow(2, -6));
        lightPosition.setZ(z);
        canvas.display();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!e.isControlDown()) {
            if (e.getWheelRotation() < 0) lightPosition.setY(lightPosition.getY() + 0.5);
            else lightPosition.setY(lightPosition.getY() - 0.5);
        } else {
            if (e.getWheelRotation() < 0) lightPosition.setZ(lightPosition.getZ() + 0.5);
            else lightPosition.setZ(lightPosition.getZ() - 0.5);
        }

        canvas.display();
    }
}

