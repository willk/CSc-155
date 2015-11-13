/*
 * TODO:
 *  1. Make all the things lit
 *      a. materials
 *      ----b. dot for the light----
 *      c. move the light with the mouse
 *  2. Figure out wtf is going on with clipped objs
 */
package a3;

import a3.commands.*;
import a3.objects.*;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.*;
import graphicslib3D.light.PositionalLight;
import graphicslib3D.shape.Torus;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL4.*;

public class GLWorld extends JFrame implements GLEventListener {
    private JPanel panel;
    private GLCanvas canvas;
    private int renderer, lineRenderer, lightRenderer, clipRenderer, lightOffRenderer, vao[], vbo[], flip_location;
    private Camera camera;
    private Cube cube;
    private Sphere sphere;
    private Torus torus;
    private ImportedModel fish;
    private TextureReader tr;
    private Material jade_material, gold_material;
    private boolean lines, lights;
    private float[] globalAmbient;
    private PositionalLight light;
    private Point3D light_position;

    private int fishy_texture, jade_texture, concrete_texture, gold_texture, rusty_texture, cracked_texture, yarn_texture, water_texture;

    public GLWorld() {
        this.setTitle("William Kinderman - CSc 155 - A3");
        this.setSize(800, 800);

        camera = new Camera(.25f, .25f, 25);

        panel = new JPanel();
        this.initWorld();

        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
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
        int[] fish_indices = fish.getIndices(), donut_indices = torus.getIndices(), sphere_indices = sphere.getIndices();
        Vertex3D[] fish_vertices = fish.getVertices(), donut_vertices = torus.getVertices(), sphere_vertices = sphere.getVertices();

        // THE FISH
        float[] fp = new float[fish.getNumIndices() * 3];
        float[] ft = new float[fish.getNumIndices() * 2];
        float[] fn = new float[fish.getNumIndices() * 3];

        for (int i = 0; i < fish.getNumIndices(); i++) {
            fp[i * 3] = (float) fish_vertices[fish_indices[i]].getX();
            fp[i * 3 + 1] = (float) fish_vertices[fish_indices[i]].getY();
            fp[i * 3 + 2] = (float) fish_vertices[fish_indices[i]].getZ();

            ft[i * 2] = (float) fish_vertices[fish_indices[i]].getS();
            ft[i * 2 + 1] = (float) fish_vertices[fish_indices[i]].getT();

            fn[i * 3] = (float) fish_vertices[fish_indices[i]].getNormalX();
            fn[i * 3 + 1] = (float) fish_vertices[fish_indices[i]].getNormalY();
            fn[i * 3 + 2] = (float) fish_vertices[fish_indices[i]].getNormalZ();
        }

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vBuffer = FloatBuffer.wrap(fp);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer tBuffer = FloatBuffer.wrap(ft);
        gl.glBufferData(GL_ARRAY_BUFFER, tBuffer.limit() * 4, tBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer nBuffer = FloatBuffer.wrap(fn);
        gl.glBufferData(GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL_STATIC_DRAW);
        // END FISH

        // THE DONUT
        float[] dp = new float[torus.getIndices().length * 3];
        float[] dt = new float[torus.getIndices().length * 2];
        float[] dn = new float[torus.getIndices().length * 3];

        for (int i = 0; i < torus.getIndices().length; i++) {
            dp[i * 3] = (float) donut_vertices[donut_indices[i]].getX();
            dp[i * 3 + 1] = (float) donut_vertices[donut_indices[i]].getY();
            dp[i * 3 + 2] = (float) donut_vertices[donut_indices[i]].getZ();

            dt[i * 2] = (float) donut_vertices[donut_indices[i]].getS();
            dt[i * 2 + 1] = (float) donut_vertices[donut_indices[i]].getT();

            dn[i * 3] = (float) donut_vertices[donut_indices[i]].getNormalX();
            dn[i * 3 + 1] = (float) donut_vertices[donut_indices[i]].getNormalY();
            dn[i * 3 + 2] = (float) donut_vertices[donut_indices[i]].getNormalZ();
        }

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        vBuffer = FloatBuffer.wrap(dp);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        tBuffer = FloatBuffer.wrap(dt);
        gl.glBufferData(GL_ARRAY_BUFFER, tBuffer.limit() * 4, tBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        nBuffer = FloatBuffer.wrap(dn);
        gl.glBufferData(GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL_STATIC_DRAW);
        // END DONUT

        // The Cube
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        vBuffer = FloatBuffer.wrap(cube.getVertices());
        gl.glBufferData(GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        tBuffer = FloatBuffer.wrap(cube.getTexals());
        gl.glBufferData(GL_ARRAY_BUFFER, tBuffer.limit() * 4, tBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        nBuffer = FloatBuffer.wrap(cube.getNormals());
        gl.glBufferData(GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL_STATIC_DRAW);
        // END Cube

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
        // END Sphere

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
        vBuffer = FloatBuffer.wrap(sp);
        gl.glBufferData(GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
        tBuffer = FloatBuffer.wrap(st);
        gl.glBufferData(GL_ARRAY_BUFFER, tBuffer.limit() * 4, tBuffer, GL_STATIC_DRAW);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
        nBuffer = FloatBuffer.wrap(sn);
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

        /* DEFAULT renderer */
        gl.glUseProgram(renderer);
        mv_loc = gl.glGetUniformLocation(renderer, "mv_matrix");
        proj_loc = gl.glGetUniformLocation(renderer, "proj_matrix");
        n_loc = gl.glGetUniformLocation(renderer, "n_matrix");

        /* Lighting */
        light.setPosition(light_position);
        create_lighting(d, s.peek(), renderer);
        /* End Lighting */
        /* END DEFAULT renderer */


        /* Fish */
        /* vbo 0, 1, 2 */
        s.pushMatrix();
        s.translate(Math.sin(amt) * 3, Math.sin(amt) * 2, Math.cos(amt) * 3);
        s.pushMatrix();
        s.rotate(amt * Math.pow(2, 5), Math.sin(amt), Math.sin(amt), Math.sin(amt));

        // Taking the top of the Stack and putting it in the Uniform
        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_loc, 1, false, s.peek().inverse().transpose().getFloatValues(), 0);

        // I am working with this buffer
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        // (location = 0, take in 3 at a time, floats, *, *, *);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        // Enable location = 0
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, fishy_texture);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, fish.getNumIndices());

        s.popMatrix();
        s.popMatrix();
        /* End Fish */

        /* Box */
        /* vbo 6, 7, 8 */
        s.pushMatrix();
        s.translate(-5, 0, 5);

        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_loc, 1, false, s.peek().inverse().transpose().getFloatValues(), 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, jade_texture);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glFrontFace(GL_CW);
        gl.glDrawArrays(GL_TRIANGLES, 0, cube.getVertices().length);

        s.popMatrix();
        /* End Box */

        /* Torus */
        /* vbo 3, 4, 5*/
        s.pushMatrix();
        s.translate(0, 0, 15);
        s.pushMatrix();
        s.scale(.25, .25, .25);

        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_loc, 1, false, s.peek().inverse().transpose().getFloatValues(), 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        /* First Draw */
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, gold_texture);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, sphere.getIndices().length);

        s.popMatrix();
        s.popMatrix();
        /* End Torus */

        /* Clip Renderer */
        gl.glUseProgram(clipRenderer);
        mv_loc = gl.glGetUniformLocation(clipRenderer, "mv_matrix");
        proj_loc = gl.glGetUniformLocation(clipRenderer, "proj_matrix");
        n_loc = gl.glGetUniformLocation(clipRenderer, "n_matrix");

        /* Lighting */
        light.setPosition(light_position);
        create_lighting(d, s.peek(), clipRenderer);
        /* End Lighting */
        /* End Clip Renderer*/

        /* Sphere */
        /* vbo 9, 10, 11*/
        s.pushMatrix();
        s.scale(10, 5, 8.5);

        gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_loc, 1, false, s.peek().inverse().transpose().getFloatValues(), 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
        gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        /* First Draw */
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, concrete_texture);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glFrontFace(GL_CCW);
        gl.glDrawArrays(GL_TRIANGLES, 0, sphere.getIndices().length);
        /* End First Draw*/

        /* Second Draw */
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
        gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glActiveTexture(GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, water_texture);

        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
        gl.glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glFrontFace(GL_CW);
        gl.glDrawArrays(GL_TRIANGLES, 0, sphere.getIndices().length);
        /* End Second Draw */

        s.popMatrix();
        /* End Sphere */

        /* Light */
        s.pushMatrix();
        s.translate(light_position.getX(), light_position.getY(), light_position.getZ());
        gl.glPointSize(30f);

        if (lights) {
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
        if (lines) {
            gl.glUseProgram(lineRenderer);
            mv_loc = gl.glGetUniformLocation(lineRenderer, "mv_matrix");
            proj_loc = gl.glGetUniformLocation(lineRenderer, "proj_matrix");
            gl.glUniformMatrix4fv(mv_loc, 1, false, s.peek().getFloatValues(), 0);
            gl.glUniformMatrix4fv(proj_loc, 1, false, pMatrix.getFloatValues(), 0);
            gl.glDrawArrays(GL_LINES, 0, 6);
        }
        /* End Lines */
    }

    private void create_lighting(GLAutoDrawable d, Matrix3D s, int r) {
        GL4 gl = (GL4) d.getGL();

        Point3D lp = light.getPosition();
        Point3D lpv = lp.mult(s);
        Material m = gold_material;

        float[] currentLightPosition = new float[]{(float) lpv.getX(), (float) lpv.getY(), (float) lpv.getZ()};

        // sets the current global ambient settings
        int globalAmbientLocation = gl.glGetUniformLocation(r, "globalAmbient");
        gl.glProgramUniform4fv(r, globalAmbientLocation, 1, globalAmbient, 0);

        // get the locs of the light and mats fields from the shader
        int ambientLocation, diffuseLocation, spectralLocation, position, mAmbientLocation, mDiffuseLocation, mSpectralLocation, mShinyLocation;
        ambientLocation = gl.glGetUniformLocation(r, "light.ambient");
        diffuseLocation = gl.glGetUniformLocation(r, "light.diffuse");
        spectralLocation = gl.glGetUniformLocation(r, "light.specular");
        position = gl.glGetUniformLocation(r, "light.position");
        mAmbientLocation = gl.glGetUniformLocation(r, "material.ambient");
        mDiffuseLocation = gl.glGetUniformLocation(r, "material.diffuse");
        mSpectralLocation = gl.glGetUniformLocation(r, "material.specular");
        mShinyLocation = gl.glGetUniformLocation(r, "material.shininess");

        // set the values in the shaders
        gl.glProgramUniform4fv(r, ambientLocation, 1, light.getAmbient(), 0);
        gl.glProgramUniform4fv(r, diffuseLocation, 1, light.getDiffuse(), 0);
        gl.glProgramUniform4fv(r, spectralLocation, 1, light.getSpecular(), 0);
        gl.glProgramUniform3fv(r, position, 1, currentLightPosition, 0);
        gl.glProgramUniform4fv(r, mAmbientLocation, 1, m.getAmbient(), 0);
        gl.glProgramUniform4fv(r, mDiffuseLocation, 1, m.getDiffuse(), 0);
        gl.glProgramUniform4fv(r, mSpectralLocation, 1, m.getSpecular(), 0);
        gl.glProgramUniform1f(r, mShinyLocation, m.getShininess());

    }

    public void init(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        // Setup models
        fish = new ImportedModel("fish.obj");
        torus = new Torus(4, 2, 48);
        sphere = new Sphere(48);
        cube = new Cube();

        tr = new TextureReader();
        lines = true;
        lights = true;

        vao = new int[1];
        vbo = new int[12];

        light = new PositionalLight();
        light_position = new Point3D(5, 5, 10);
        globalAmbient = new float[]{0.7f, 0.7f, 0.7f, 1.0f};

        gl.glEnable(GL_CLIP_DISTANCE0);

        lineRenderer = createShaderPrograms(d, "src/a3/shaders/line_vertex.glsl", "src/a3/shaders/line_fragment.glsl");
        renderer = createShaderPrograms(d, "src/a3/shaders/default_vertex.glsl", "src/a3/shaders/default_fragment.glsl");
        lightRenderer = createShaderPrograms(d, "src/a3/shaders/light_vertex.glsl", "src/a3/shaders/light_fragment.glsl");
        clipRenderer = createShaderPrograms(d, "src/a3/shaders/clipped_vertex.glsl", "src/a3/shaders/clipped_fragment.glsl");
        lightOffRenderer = createShaderPrograms(d, "src/a3/shaders/light_off_vertex.glsl", "src/a3/shaders/light_off_fragment.glsl");

        gold_material = Material.GOLD;

        setupVertices(gl);

        jade_texture = tr.loadTexture(d, "src/a3/textures/jade.jpg");
        yarn_texture = tr.loadTexture(d, "src/a3/textures/yarn.jpg");
        gold_texture = tr.loadTexture(d, "src/a3/textures/gold.jpg");
        fishy_texture = tr.loadTexture(d, "src/a3/textures/fish.jpg");
        rusty_texture = tr.loadTexture(d, "src/a3/textures/rusty.jpg");
        water_texture = tr.loadTexture(d, "src/a3/textures/water.jpg");
        cracked_texture = tr.loadTexture(d, "src/a3/textures/cracked.jpg");
        concrete_texture = tr.loadTexture(d, "src/a3/textures/concrete.jpg");
    }

    public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable d) {
    }

    public void toggleLines() {
        lines = !lines;
    }

    public void toggleLights() {
        lights = !lights;
    }
}