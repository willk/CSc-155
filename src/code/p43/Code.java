package code.p43;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import graphicslib3D.GLSLUtils;
import graphicslib3D.Matrix3D;
import graphicslib3D.MatrixStack;
import objects.Cube;
import objects.Sphere;

import javax.swing.*;
import java.nio.FloatBuffer;

import static com.jogamp.opengl.GL4.*;

public class Code extends JFrame implements GLEventListener {
    private GLCanvas myCanvas;
    private int rendering_program;
    private int vao[] = new int[1];
    private int vbo[] = new int[2];
    private float cameraX, cameraY, cameraZ;
    private float cubeLocX, cubeLocY, cubeLocZ;
    private float pyrLocX, pyrLocY, pyrLocZ;
    private Sphere d = new Sphere(48);
    private GLSLUtils util = new GLSLUtils();

    public Code() {
        setTitle("Chapter4 - program1");
        setSize(600, 600);
        myCanvas = new GLCanvas();
        myCanvas.addGLEventListener(this);
        getContentPane().add(myCanvas);
        this.setVisible(true);
        FPSAnimator fpsAnimator = new FPSAnimator(myCanvas, 120);
        fpsAnimator.start();
//        Animator animator = new Animator(myCanvas);
//        Thread thread =
//                new Thread(new Runnable() { public void run() { animator.start(); }} );
//        thread.start();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        System.out.println(myCanvas.getHeight());
    }

    public static void main(String[] args) {
        new Code();
    }

    public void display(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();

        gl.glClear(GL_DEPTH_BUFFER_BIT);
        FloatBuffer background = FloatBuffer.allocate(4);
        gl.glClearBufferfv(GL_COLOR, 0, background);

        gl.glClear(GL_DEPTH_BUFFER_BIT);

        gl.glUseProgram(rendering_program);

        int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
        int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

        float aspect = myCanvas.getWidth() / myCanvas.getHeight();
        Matrix3D pMat = perspective(50.0f, aspect, 0.1f, 1000.0f);

        Matrix3D vMat = new Matrix3D();
        vMat.translate(-cameraX, -cameraY, -cameraZ);


        MatrixStack mvStack = new MatrixStack(20);
        // push view matrix onto the stack
        mvStack.pushMatrix();
        mvStack.translate(-cameraX, -cameraY, -cameraZ);
        double amt = (double) (System.currentTimeMillis() % 360000) / 1000.0;

        // ----------------------  pyramid == sun
        mvStack.pushMatrix();
        mvStack.translate(pyrLocX, pyrLocY, pyrLocZ);
        mvStack.scale(.5, .5, .5);
        mvStack.rotate((System.currentTimeMillis() % 3600) / 10.0, 1.0, 0.0, 0.0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CCW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDrawArrays(GL_TRIANGLES, 0, d.getIndices().length);
        mvStack.popMatrix();

        //-----------------------  cube == planet
        mvStack.pushMatrix();
        mvStack.translate(Math.sin(amt), 0.0f, Math.cos(amt));
        mvStack.pushMatrix();
        mvStack.rotate((System.currentTimeMillis() % 3600) / 10.0, 0.0, 1.0, 0.0);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();

        //-----------------------  smaller cube == moon
        mvStack.pushMatrix();
        mvStack.translate(0.0f, Math.sin(amt) / 2.0f, Math.cos(amt) / 2.0f);
        mvStack.rotate((System.currentTimeMillis() % 3600) / 10.0, 0.0, 0.0, 1.0);
        mvStack.scale(0.25, 0.25, 0.25);
        gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
        gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0);
        gl.glEnableVertexAttribArray(0);
        gl.glEnable(GL_CULL_FACE);
        gl.glFrontFace(GL_CW);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDrawArrays(GL_TRIANGLES, 0, 36);
        mvStack.popMatrix();
        mvStack.popMatrix();
        mvStack.popMatrix();
    }

    public void init(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();
        GLU glu = new GLU();
        rendering_program = createShaderPrograms(drawable);
        setupVertices(gl);
        cameraX = 0.0f;
        cameraY = 0.0f;
        cameraZ = 3.0f;
        cubeLocX = 0.0f;
        cubeLocY = -0.5f;
        cubeLocZ = 0.0f;
        pyrLocX = 0.0f;
        pyrLocY = 0.0f;
        pyrLocZ = -0.5f;
    }

    private void setupVertices(GL4 gl) {
        float[] cube_positions = {
                -0.25f, 0.25f, -0.25f, -0.25f, -0.25f, -0.25f, 0.25f, -0.25f, -0.25f,
                0.25f, -0.25f, -0.25f, 0.25f, 0.25f, -0.25f, -0.25f, 0.25f, -0.25f,
                0.25f, -0.25f, -0.25f, 0.25f, -0.25f, 0.25f, 0.25f, 0.25f, -0.25f,
                0.25f, -0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f, -0.25f,
                0.25f, -0.25f, 0.25f, -0.25f, -0.25f, 0.25f, 0.25f, 0.25f, 0.25f,
                -0.25f, -0.25f, 0.25f, -0.25f, 0.25f, 0.25f, 0.25f, 0.25f, 0.25f,
                -0.25f, -0.25f, 0.25f, -0.25f, -0.25f, -0.25f, -0.25f, 0.25f, 0.25f,
                -0.25f, -0.25f, -0.25f, -0.25f, 0.25f, -0.25f, -0.25f, 0.25f, 0.25f,
                -0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, -0.25f,
                0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, 0.25f,
                -0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, 0.25f,
                0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f, -0.25f
        };


        float[] pyramid_positions = {
                0.25f, -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, 0.0f, 0.25f, 0.0f,
                -0.25f, -0.25f, -0.25f, -0.25f, -0.25f, 0.25f, 0.0f, 0.25f, 0.0f,
                -0.25f, -0.25f, 0.25f, 0.25f, -0.25f, 0.25f, 0.0f, 0.25f, 0.0f,
                0.25f, -0.25f, 0.25f, 0.25f, -0.25f, -0.25f, 0.0f, 0.25f, 0.0f,
                -0.25f, -0.25f, -0.25f, 0.25f, -0.25f, 0.25f, -0.25f, -0.25f, 0.25f,
                -0.25f, -0.25f, -0.25f, 0.25f, -0.25f, -0.25f, 0.25f, -0.25f, 0.25f
        };

        gl.glGenVertexArrays(vao.length, vao, 0);
        gl.glBindVertexArray(vao[0]);
        gl.glGenBuffers(vbo.length, vbo, 0);
        Cube c = new Cube();
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0]);
        FloatBuffer cubeBuf = FloatBuffer.wrap(c.getVertices());
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeBuf.limit() * 4, cubeBuf, GL.GL_STATIC_DRAW);

        float[] svalues = new float[d.getIndices().length * 3];
        for (int i = 0; i < d.getIndices().length; i++) {
            svalues[i * 3] = (float) (d.getVertices()[d.getIndices()[i]].getX());
            svalues[i * 3 + 1] = (float) (d.getVertices()[d.getIndices()[i]].getY());
            svalues[i * 3 + 2] = (float) (d.getVertices()[d.getIndices()[i]].getZ());
        }

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1]);
        FloatBuffer pyrBuf = FloatBuffer.wrap(svalues);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, pyrBuf.limit() * 4, pyrBuf, GL.GL_STATIC_DRAW);
    }

    private Matrix3D perspective(float fovy, float aspect, float n, float f) {
        float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);
        Matrix3D r = new Matrix3D();
        Matrix3D rt; // = new Matrix3D();
        r.setElementAt(0, 0, A);
        r.setElementAt(1, 1, q);
        r.setElementAt(2, 2, B);
        r.setElementAt(2, 3, -1.0f);
        r.setElementAt(3, 2, C);
        rt = r.transpose();
        return rt;
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    public void dispose(GLAutoDrawable drawable) {
    }

    private int createShaderPrograms(GLAutoDrawable drawable) {
        GL4 gl = (GL4) drawable.getGL();

        String vshaderSource[] = GLSLUtils.readShaderSource("src/p43/a3.shaders/default_vertex.glsl");
        String fshaderSource[] = GLSLUtils.readShaderSource("src/p43/a3.shaders/default_fragment.glsl");
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
}