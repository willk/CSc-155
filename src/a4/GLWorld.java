package a4;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import commands.*;
import graphicslib3D.*;
import graphicslib3D.light.PositionalLight;
import graphicslib3D.shape.Torus;
import objects.Camera;
import objects.ImportedModel;
import objects.Sphere;

import javax.swing.*;
import java.awt.event.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL4.*;

public class GLWorld extends JFrame implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener {
    private final GLCanvas canvas;
    private final JPanel panel;

    private final Camera camera = new Camera(0, 0, 5);
    private final Vector3D up = new Vector3D(0.0, 1.0, 0.0);
    Matrix3D lightVMatrix = new Matrix3D(), lightPMatrix = new Matrix3D(),
            shadowMVP1 = new Matrix3D(), shadowMVP2 = new Matrix3D(),
            b = new Matrix3D(),
            mMatrix = new Matrix3D(),
            vMatrix = new Matrix3D(),
            mvMatrix = new Matrix3D(),
            projMatrix = new Matrix3D();
    // Objects and Models
    ImportedModel pyramid = new ImportedModel("pyramid.obj");
    Torus torus = new Torus(10f, 2f, 48);
    Sphere sphere = new Sphere(48);
    // Material
    private Material material;
    ;
    private Point3D torusLocation = new Point3D(0, 0, 0),
            pyramidLocation = new Point3D(0, 1.5, 0),
            sphereLocation = new Point3D(0, 0, 0),
            lightLocation = new Point3D(3, -0.8, 7, 1),
            origin = new Point3D(0.0, 0.0, 0.0);
    // Lighting
    private float[] globalAmbient = new float[]{0.7f, 0.7f, 0.7f, 1.0f};
    private PositionalLight light = new PositionalLight();

    private int aspect,
            screenX,
            screenY,
            mv_location,
            proj_location,
            n_location,
            passOneRenderer,
            passTwoRenderer,
            lightRenderer,
            concreteTexture;

    private int[] vao,
            vbo,
            shadowTex = new int[1],
            shadowBuffer = new int[1];

    private String[] shadowVertex,
            defaultVertex,
            defaultFragment,
            lightVertex,
            lightFragment;

    private GLSLUtils util = new GLSLUtils();

    public GLWorld() {
        this.setTitle("William Kinderman - CSc 155 - A4");
        setSize(800, 800);

        // Event Listeners
        panel = new JPanel();
        this.initWorld();

        // The world itself
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addMouseWheelListener(this);
        canvas.addMouseMotionListener(this);
        this.getContentPane().add(panel);
        this.getContentPane().add(canvas);
        this.setVisible(true);

        FPSAnimator animator = new FPSAnimator(canvas, 120);
        animator.start();

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void display(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        light.setPosition(lightLocation);
        aspect = canvas.getWidth() / canvas.getHeight();
        projMatrix = perspective(50.0f, aspect, 0.1f, 1000.0f);

        gl.glClearBufferfv(GL_COLOR, 0, FloatBuffer.allocate(4));

        float depthClearVal[] = new float[1];
        depthClearVal[0] = 1.0f;
        gl.glClearBufferfv(GL_DEPTH, 0, depthClearVal, 0);

        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);
        gl.glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, shadowTex[0], 0);

        gl.glDrawBuffer(GL.GL_NONE);
        gl.glEnable(GL_DEPTH_TEST);

        // Help the acne
        gl.glEnable(GL_POLYGON_OFFSET_FILL);
        gl.glPolygonOffset(2.0f, 4.0f);

        firstPass(d);

        // Help the acne
        gl.glDisable(GL_POLYGON_OFFSET_FILL);

        gl.glBindFramebuffer(GL_FRAMEBUFFER, 0);
        gl.glActiveTexture(gl.GL_TEXTURE0);
        gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);

        gl.glDrawBuffer(GL.GL_FRONT);

        secondPass(d);
    }

    public void firstPass(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        gl.glUseProgram(passOneRenderer);

        lightVMatrix.setToIdentity();
        lightPMatrix.setToIdentity();

        lightVMatrix = lookAt(light.getPosition(), origin, up);    // vector from light to origin
        lightPMatrix = perspective(50.0f, aspect, 0.1f, 1000.0f);

        /*
         * Draw Torus
         */
        mMatrix.setToIdentity();
        mMatrix.translate(torusLocation.getX(), torusLocation.getY(), torusLocation.getZ());
        mMatrix.rotateX(25.0);

        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightPMatrix);
        shadowMVP1.concatenate(lightVMatrix);
        shadowMVP1.concatenate(mMatrix);
        int shadow_location = gl.glGetUniformLocation(passOneRenderer, "shadowMVP");
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);

        // Torus Vertex VBO
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, torus.getIndices().length);

        /*
         * Draw Sphere
         */
        mMatrix.setToIdentity();
        mMatrix.translate(sphereLocation.getX(), sphereLocation.getY(), sphereLocation.getZ());

        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightPMatrix);
        shadowMVP1.concatenate(lightVMatrix);
        shadowMVP1.concatenate(mMatrix);
        shadow_location = gl.glGetUniformLocation(passOneRenderer, "shadowMVP");
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);

        // Sphere Vertex VBO
        gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

       /*
        * Draw Pyramid
        */
        gl.glUseProgram(passOneRenderer);
        mv_location = gl.glGetUniformLocation(passOneRenderer, "mv_matrix");
        proj_location = gl.glGetUniformLocation(passOneRenderer, "proj_matrix");

        //  build the MODEL matrix
        mMatrix.setToIdentity();
        mMatrix.translate(pyramidLocation.getX(), pyramidLocation.getY(), pyramidLocation.getZ());
        mMatrix.scale(.5, .5, .5);
        mMatrix.rotateX(30.0);
        mMatrix.rotateY(40.0);

        shadowMVP1.setToIdentity();
        shadowMVP1.concatenate(lightPMatrix);
        shadowMVP1.concatenate(lightVMatrix);
        shadowMVP1.concatenate(mMatrix);

        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP1.getFloatValues(), 0);

        // set up vertices buffer
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, pyramid.getNumVertices());
    }

    public void secondPass(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        /*
         * Draw Torus
         */
        material = Material.SILVER;
        createLights(passTwoRenderer, vMatrix, d);
        gl.glUseProgram(passTwoRenderer);

        mv_location = gl.glGetUniformLocation(passTwoRenderer, "mv_matrix");
        proj_location = gl.glGetUniformLocation(passTwoRenderer, "proj_matrix");
        n_location = gl.glGetUniformLocation(passTwoRenderer, "normalMat");
        int shadow_location = gl.glGetUniformLocation(passTwoRenderer, "shadowMVP");

        //  build the MODEL matrix
        mMatrix.setToIdentity();
        mMatrix.translate(torusLocation.getX(), torusLocation.getY(), torusLocation.getZ());
        double amt = (double) (System.currentTimeMillis() % 36000) / 100.0;
        mMatrix.rotateX(25.0);

        //  build the VIEW matrix
        vMatrix.setToIdentity();
        vMatrix.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        //  build the MODEL-VIEW matrix
        mvMatrix.setToIdentity();
        mvMatrix.concatenate(vMatrix);
        mvMatrix.concatenate(mMatrix);

        shadowMVP2.setToIdentity();
        shadowMVP2.concatenate(b);
        shadowMVP2.concatenate(lightPMatrix);
        shadowMVP2.concatenate(lightVMatrix);
        shadowMVP2.concatenate(mMatrix);

        //  put the MV and PROJ matrices into the corresponding uniforms
        gl.glUniformMatrix4fv(mv_location, 1, false, mvMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, projMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mvMatrix.inverse()).transpose().getFloatValues(), 0);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

        // Torus Vertex VBO
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[3]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // Torus Normal VBO
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[5]);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, torus.getIndices().length);


        /*
         * Draw Sphere
         */
        material = Material.SILVER;
        createLights(passTwoRenderer, vMatrix, d);
        gl.glUseProgram(passTwoRenderer);

        mv_location = gl.glGetUniformLocation(passTwoRenderer, "mv_matrix");
        proj_location = gl.glGetUniformLocation(passTwoRenderer, "proj_matrix");
        n_location = gl.glGetUniformLocation(passTwoRenderer, "normalMat");
        shadow_location = gl.glGetUniformLocation(passTwoRenderer, "shadowMVP");

        //  build the MODEL matrix
        mMatrix.setToIdentity();
        mMatrix.translate(sphereLocation.getX(), sphereLocation.getY(), sphereLocation.getZ());

        //  build the VIEW matrix
        vMatrix.setToIdentity();
        vMatrix.translate(-camera.getX(), -camera.getY(), -camera.getZ());

        //  build the MODEL-VIEW matrix
        mvMatrix.setToIdentity();
        mvMatrix.concatenate(vMatrix);
        mvMatrix.concatenate(mMatrix);

        shadowMVP2.setToIdentity();
        shadowMVP2.concatenate(b);
        shadowMVP2.concatenate(lightPMatrix);
        shadowMVP2.concatenate(lightVMatrix);
        shadowMVP2.concatenate(mMatrix);

        //  put the MV and PROJ matrices into the corresponding uniforms
        gl.glUniformMatrix4fv(mv_location, 1, false, mvMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, projMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mvMatrix.inverse()).transpose().getFloatValues(), 0);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

        // Sphere Vertex VBO
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[6]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // Sphere Normal VBO
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[8]);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, sphere.getIndices().length);


        /*
         * Draw Pyramid
         */
        material = Material.GOLD;
        createLights(passTwoRenderer, vMatrix, d);

        gl.glUseProgram(passTwoRenderer);
        mv_location = gl.glGetUniformLocation(passTwoRenderer, "mv_matrix");
        proj_location = gl.glGetUniformLocation(passTwoRenderer, "proj_matrix");
        n_location = gl.glGetUniformLocation(passTwoRenderer, "normalMat");

        //  build the MODEL matrix
        mMatrix.setToIdentity();
        mMatrix.translate(pyramidLocation.getX(), pyramidLocation.getY(), pyramidLocation.getZ());
        mMatrix.scale(.5, .5, .5);
        mMatrix.rotateX(30.0);
        mMatrix.rotateY(40.0);

        //  build the MODEL-VIEW matrix
        mvMatrix.setToIdentity();
        mvMatrix.concatenate(vMatrix);
        mvMatrix.concatenate(mMatrix);

        shadowMVP2.setToIdentity();
        shadowMVP2.concatenate(b);
        shadowMVP2.concatenate(lightPMatrix);
        shadowMVP2.concatenate(lightVMatrix);
        shadowMVP2.concatenate(mMatrix);
        gl.glUniformMatrix4fv(shadow_location, 1, false, shadowMVP2.getFloatValues(), 0);

        //  put the MV and PROJ matrices into the corresponding uniforms
        gl.glUniformMatrix4fv(mv_location, 1, false, mvMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, projMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(n_location, 1, false, (mvMatrix.inverse()).transpose().getFloatValues(), 0);

        // Pyramid Vertex VBO
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);

        // Pyramid Normal VBO
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(1);

        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);

        gl.glDrawArrays(GL_TRIANGLES, 0, pyramid.getNumVertices());

        // Draw the light
        gl.glPointSize(25);
        gl.glUseProgram(lightRenderer);
        mv_location = gl.glGetUniformLocation(lightRenderer, "mv_matrix");
        proj_location = gl.glGetUniformLocation(lightRenderer, "proj_matrix");

        mMatrix.setToIdentity();
        mMatrix.translate(lightLocation.getX(), lightLocation.getY(), lightLocation.getZ());

        //  build the MODEL-VIEW matrix
        mvMatrix.setToIdentity();
        mvMatrix.concatenate(vMatrix);
        mvMatrix.concatenate(mMatrix);

        gl.glUniformMatrix4fv(mv_location, 1, false, mvMatrix.getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_location, 1, false, projMatrix.getFloatValues(), 0);

        gl.glDrawArrays(GL_POINTS, 0, 1);
        gl.glPointSize(1);
    }

    public void init(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        vao = new int[1];
        vbo = new int[20];

        createShaderPrograms(d);
        setupVertices(gl);
        setupShadowBuffers(d);

        b.setElementAt(0, 0, 0.5);
        b.setElementAt(0, 1, 0.0);
        b.setElementAt(0, 2, 0.0);
        b.setElementAt(0, 3, 0.5f);
        b.setElementAt(1, 0, 0.0);
        b.setElementAt(1, 1, 0.5);
        b.setElementAt(1, 2, 0.0);
        b.setElementAt(1, 3, 0.5f);
        b.setElementAt(2, 0, 0.0);
        b.setElementAt(2, 1, 0.0);
        b.setElementAt(2, 2, 0.5);
        b.setElementAt(2, 3, 0.5f);
        b.setElementAt(3, 0, 0.0);
        b.setElementAt(3, 1, 0.0);
        b.setElementAt(3, 2, 0.0);
        b.setElementAt(3, 3, 1.0f);

        // Help reduce shadow boarder artifacts.
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public void setupShadowBuffers(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        screenX = canvas.getWidth();
        screenY = canvas.getHeight();

        gl.glGenFramebuffers(1, shadowBuffer, 0);
        gl.glBindFramebuffer(GL_FRAMEBUFFER, shadowBuffer[0]);

        gl.glGenTextures(1, shadowTex, 0);
        gl.glBindTexture(GL_TEXTURE_2D, shadowTex[0]);
        gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT32, screenX, screenY, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);

        // may reduce shadow border artifacts
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
    }

    public void reshape(GLAutoDrawable d, int x, int y, int width, int height) {
        setupShadowBuffers(d);
    }

    private void setupVertices(GL4 gl) {
        int numberPyramidIndices;
        int[] pyramidIndices = pyramid.getIndices(), torusIndices = torus.getIndices(), sphereIndices = sphere.getIndices();
        Vertex3D[] pyramidVertices = pyramid.getVertices(), torusVertices = torus.getVertices(), sphereVertices = sphere.getVertices();

        numberPyramidIndices = pyramid.getNumIndices();

        // The Pyramid
        float[] pp = new float[numberPyramidIndices * 3];
        float[] pt = new float[numberPyramidIndices * 2];
        float[] pn = new float[numberPyramidIndices * 3];
        float[] pc = new float[numberPyramidIndices * 3];

        for (int i = 0; i < numberPyramidIndices; i++) {
            pp[i * 3] = (float) (pyramidVertices[pyramidIndices[i]]).getX();
            pp[i * 3 + 1] = (float) (pyramidVertices[pyramidIndices[i]]).getY();
            pp[i * 3 + 2] = (float) (pyramidVertices[pyramidIndices[i]]).getZ();

            pt[i * 2] = (float) (pyramidVertices[pyramidIndices[i]]).getS();
            pt[i * 2 + 1] = (float) (pyramidVertices[pyramidIndices[i]]).getT();

            pn[i * 3] = (float) (pyramidVertices[pyramidIndices[i]]).getNormalX();
            pn[i * 3 + 1] = (float) (pyramidVertices[pyramidIndices[i]]).getNormalY();
            pn[i * 3 + 2] = (float) (pyramidVertices[pyramidIndices[i]]).getNormalZ();
        }

        for (int i = numberPyramidIndices / 2; i < numberPyramidIndices; i++) {
            pc[i * 3] = 0.0f;
            pc[i * 3 + 1] = 1.0f;
            pc[i * 3 + 2] = 0.0f;
        }


        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);

        //  Pyramid Vertices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer vBuffer = FloatBuffer.wrap(pp);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL.GL_STATIC_DRAW);

        // Pyramid Texels

        // Pyramid Normals
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[2]);
        FloatBuffer nBuffer = FloatBuffer.wrap(pn);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL.GL_STATIC_DRAW);


        // END Pyramid

        float[] tp = new float[torusIndices.length * 3];
        float[] tt = new float[torusIndices.length * 2];
        float[] tn = new float[torusIndices.length * 3];
        float[] tc = new float[torusIndices.length * 3];

        for (int i = 0; i < torusIndices.length; i++) {
            tp[i * 3] = (float) (torusVertices[torusIndices[i]]).getX();
            tp[i * 3 + 1] = (float) (torusVertices[torusIndices[i]]).getY();
            tp[i * 3 + 2] = (float) (torusVertices[torusIndices[i]]).getZ();

            tt[i * 2] = (float) (torusVertices[torusIndices[i]]).getS();
            tt[i * 2 + 1] = (float) (torusVertices[torusIndices[i]]).getT();

            tn[i * 3] = (float) (torusVertices[torusIndices[i]]).getNormalX();
            tn[i * 3 + 1] = (float) (torusVertices[torusIndices[i]]).getNormalY();
            tn[i * 3 + 2] = (float) (torusVertices[torusIndices[i]]).getNormalZ();
        }

        for (int i = torusIndices.length / 2; i < torusIndices.length; i++) {
            tc[i * 3] = 0.0f;
            tc[i * 3 + 1] = 1.0f;
            tc[i * 3 + 2] = 0.0f;
        }

        //  Torus Vertices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[3]);
        vBuffer = FloatBuffer.wrap(tp);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL.GL_STATIC_DRAW);

        // Torus Texel vbo 4

        // Torus Normals
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[5]);
        nBuffer = FloatBuffer.wrap(tn);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL.GL_STATIC_DRAW);

        // END Torus

        // The Sphere
        float[] sp = new float[sphere.getIndices().length * 3];
        float[] st = new float[sphere.getIndices().length * 2];
        float[] sn = new float[sphere.getIndices().length * 3];

        for (int i = 0; i < sphere.getIndices().length; i++) {
            sp[i * 3] = (float) sphereVertices[sphereIndices[i]].getX();
            sp[i * 3 + 1] = (float) sphereVertices[sphereIndices[i]].getY();
            sp[i * 3 + 2] = (float) sphereVertices[sphereIndices[i]].getZ();

            st[i * 2] = (float) sphereVertices[sphereIndices[i]].getS();
            st[i * 2 + 1] = (float) sphereVertices[sphereIndices[i]].getT();

            sn[i * 3] = (float) sphereVertices[sphereIndices[i]].getNormalX();
            sn[i * 3 + 1] = (float) sphereVertices[sphereIndices[i]].getNormalY();
            sn[i * 3 + 2] = (float) sphereVertices[sphereIndices[i]].getNormalZ();
        }
        // END Sphere

        // Sphere Vertices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[6]);
        vBuffer = FloatBuffer.wrap(sp);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, vBuffer.limit() * 4, vBuffer, GL.GL_STATIC_DRAW);

        // Sphere Normals
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[8]);
        nBuffer = FloatBuffer.wrap(pn);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, nBuffer.limit() * 4, nBuffer, GL.GL_STATIC_DRAW);
    }

    @Override
    public void dispose(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();
        gl.glDeleteVertexArrays(1, vao, 0);
    }

    private void createShaderPrograms(GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        shadowVertex = util.readShaderSource("src/shaders/shadowVertex.glsl");
        defaultVertex = util.readShaderSource("src/shaders/defaultVertex.glsl");
        defaultFragment = util.readShaderSource("src/shaders/defaultFragment.glsl");
        lightVertex = util.readShaderSource("src/shaders/lightVertex.glsl");
        lightFragment = util.readShaderSource("src/shaders/lightFragment.glsl");

        int shadowVertexShader = gl.glCreateShader(GL_VERTEX_SHADER),
                defaultVertexShader = gl.glCreateShader(GL_VERTEX_SHADER),
                defaultFragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER),
                lightVertexShader = gl.glCreateShader(GL_VERTEX_SHADER),
                lightFragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

        int[] l = new int[shadowVertex.length];
        for (int i = 0; i < l.length; i++) l[i] = shadowVertex[i].length();
        gl.glShaderSource(shadowVertexShader, shadowVertex.length, shadowVertex, l, 0);

        l = new int[defaultVertex.length];
        for (int i = 0; i < l.length; i++) l[i] = defaultVertex[i].length();
        gl.glShaderSource(defaultVertexShader, defaultVertex.length, defaultVertex, l, 0);

        l = new int[defaultFragment.length];
        for (int i = 0; i < l.length; i++) l[i] = defaultFragment[i].length();
        gl.glShaderSource(defaultFragmentShader, defaultFragment.length, defaultFragment, l, 0);

        l = new int[lightVertex.length];
        for (int i = 0; i < l.length; i++) l[i] = lightVertex[i].length();
        gl.glShaderSource(lightVertexShader, lightVertex.length, lightVertex, l, 0);

        l = new int[lightFragment.length];
        for (int i = 0; i < l.length; i++) l[i] = lightFragment[i].length();
        gl.glShaderSource(lightFragmentShader, lightFragment.length, lightFragment, l, 0);

        gl.glCompileShader(shadowVertexShader);
        gl.glCompileShader(defaultVertexShader);
        gl.glCompileShader(defaultFragmentShader);
        gl.glCompileShader(lightVertexShader);
        gl.glCompileShader(lightFragmentShader);

        passOneRenderer = gl.glCreateProgram();
        passTwoRenderer = gl.glCreateProgram();
        lightRenderer = gl.glCreateProgram();

        gl.glAttachShader(passOneRenderer, shadowVertexShader);
        gl.glAttachShader(passTwoRenderer, defaultVertexShader);
        gl.glAttachShader(passTwoRenderer, defaultFragmentShader);
        gl.glAttachShader(lightRenderer, lightVertexShader);
        gl.glAttachShader(lightRenderer, lightFragmentShader);

        gl.glLinkProgram(passOneRenderer);
        gl.glLinkProgram(passTwoRenderer);
        gl.glLinkProgram(lightRenderer);
    }

    /*
     * From Dr. Gordon's Code
     */
    private Matrix3D perspective(float fovy, float aspect, float n, float f) {
        float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);
        Matrix3D r = new Matrix3D();
        r.setElementAt(0, 0, A);
        r.setElementAt(1, 1, q);
        r.setElementAt(2, 2, B);
        r.setElementAt(3, 2, -1.0f);
        r.setElementAt(2, 3, C);
        r.setElementAt(3, 3, 0.0f);
        return r;
    }

    /*
     * From Dr. Gordon's Code
     */
    private Matrix3D lookAt(Point3D eyeP, Point3D centerP, Vector3D upV) {
        Vector3D eyeV = new Vector3D(eyeP);
        Vector3D cenV = new Vector3D(centerP);
        Vector3D f = (cenV.minus(eyeV)).normalize();
        Vector3D sV = (f.cross(upV)).normalize();
        Vector3D nU = (sV.cross(f)).normalize();

        Matrix3D l = new Matrix3D();
        l.setElementAt(0, 0, sV.getX());
        l.setElementAt(0, 1, nU.getX());
        l.setElementAt(0, 2, -f.getX());
        l.setElementAt(0, 3, 0.0f);
        l.setElementAt(1, 0, sV.getY());
        l.setElementAt(1, 1, nU.getY());
        l.setElementAt(1, 2, -f.getY());
        l.setElementAt(1, 3, 0.0f);
        l.setElementAt(2, 0, sV.getZ());
        l.setElementAt(2, 1, nU.getZ());
        l.setElementAt(2, 2, -f.getZ());
        l.setElementAt(2, 3, 0.0f);
        l.setElementAt(3, 0, sV.dot(eyeV.mult(-1)));
        l.setElementAt(3, 1, nU.dot(eyeV.mult(-1)));
        l.setElementAt(3, 2, (f.mult(-1)).dot(eyeV.mult(-1)));
        l.setElementAt(3, 3, 1.0f);
        return (l.transpose());
    }

    /*
     * From Dr. Gordon's Code
     */
    private void createLights(int rendering_program, Matrix3D v_matrix, GLAutoDrawable d) {
        GL4 gl = (GL4) d.getGL();

        Material currentMaterial = new Material();
        currentMaterial = material;

        Point3D lightP = light.getPosition();
        Point3D lightPv = lightP.mult(v_matrix);

        float[] currLightPos = new float[]{(float) lightPv.getX(),
                (float) lightPv.getY(),
                (float) lightPv.getZ()};

        // get the location of the global ambient light field in the shader
        int globalAmbLoc = gl.glGetUniformLocation(rendering_program, "globalAmbient");

        // set the current globalAmbient settings
        gl.glProgramUniform4fv(rendering_program, globalAmbLoc, 1, globalAmbient, 0);

        // get the locations of the light and material fields in the shader
        int ambLoc = gl.glGetUniformLocation(rendering_program, "light.ambient");
        int diffLoc = gl.glGetUniformLocation(rendering_program, "light.diffuse");
        int specLoc = gl.glGetUniformLocation(rendering_program, "light.specular");
        int posLoc = gl.glGetUniformLocation(rendering_program, "light.position");

        int MambLoc = gl.glGetUniformLocation(rendering_program, "material.ambient");
        int MdiffLoc = gl.glGetUniformLocation(rendering_program, "material.diffuse");
        int MspecLoc = gl.glGetUniformLocation(rendering_program, "material.specular");
        int MshiLoc = gl.glGetUniformLocation(rendering_program, "material.shininess");

        // set the uniform light and material values in the shader
        gl.glProgramUniform4fv(rendering_program, ambLoc, 1, light.getAmbient(), 0);
        gl.glProgramUniform4fv(rendering_program, diffLoc, 1, light.getDiffuse(), 0);
        gl.glProgramUniform4fv(rendering_program, specLoc, 1, light.getSpecular(), 0);
        gl.glProgramUniform3fv(rendering_program, posLoc, 1, currLightPos, 0);

        gl.glProgramUniform4fv(rendering_program, MambLoc, 1, currentMaterial.getAmbient(), 0);
        gl.glProgramUniform4fv(rendering_program, MdiffLoc, 1, currentMaterial.getDiffuse(), 0);
        gl.glProgramUniform4fv(rendering_program, MspecLoc, 1, currentMaterial.getSpecular(), 0);
        gl.glProgramUniform1f(rendering_program, MshiLoc, currentMaterial.getShininess());
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
        float z = (float) lightLocation.getZ();

        if (e.getX() > this.getWidth() / 2 && e.getX() < this.getWidth())
            lightLocation.setX(this.getWidth() / 2 + (e.getX() - this.getWidth()));
        else if (e.getX() > this.getWidth()) lightLocation.setX(this.getWidth());
        else if (e.getX() < this.getWidth() / 2) lightLocation.setX(e.getX() - this.getWidth() / 2);
        else lightLocation.setX(0);

        if (e.getY() > this.getHeight() / 2 && e.getY() < this.getHeight())
            lightLocation.setY(-(this.getHeight() / 2 + (e.getY() - this.getHeight())));
        else if (e.getY() > this.getHeight()) lightLocation.setY(this.getHeight());
        else if (e.getY() < this.getHeight() / 2) lightLocation.setY(-(e.getY() - this.getHeight() / 2));
        else lightLocation.setY(0);


        lightLocation.scale(Math.pow(2, -6));
        lightLocation.setZ(z);
        canvas.display();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!e.isControlDown()) {
            if (e.getWheelRotation() < 0) lightLocation.setY(lightLocation.getY() + 0.5);
            else lightLocation.setY(lightLocation.getY() - 0.5);
        } else {
            if (e.getWheelRotation() < 0) lightLocation.setZ(lightLocation.getZ() + 0.5);
            else lightLocation.setZ(lightLocation.getZ() - 0.5);
        }
        canvas.display();
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
}