package objects;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

/**
 * Created by willk on 11/11/2015.
 */
public class GLErrors {
    private GL4 gl;
    private GLU glu;

    public GLErrors(GLAutoDrawable d) {
        this.gl = (GL4) d.getGL();
        this.glu = new GLU();
    }

    public boolean OpenGLError() {
        boolean error = false;
        int glError = gl.glGetError();
        while (glError != GL.GL_NO_ERROR) {
            System.err.println("GLError: " + glu.gluErrorString(glError));
            error = true;
            glError = gl.glGetError();
        }
        return error;
    }


    private void printShaderLogs(int shader) {

        int[] length = new int[1], characters = new int[1];
        byte[] log = null;

        // determine the length of the shader compilation log
        gl.glGetShaderiv(shader, GL4.GL_INFO_LOG_LENGTH, length, 0);
        if (length[0] > 0) {
            log = new byte[length[0]];
            gl.glGetShaderInfoLog(shader, length[0], characters, 0, log, 0);
            System.out.println("Shader Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
        }
    }

    private void printProgramLogs(int program) {

        int[] length = new int[1], characters = new int[1];
        byte[] log = null;

        // determine the length of the shader compilation log
        gl.glGetProgramiv(program, GL4.GL_INFO_LOG_LENGTH, length, 0);
        if (length[0] > 0) {
            log = new byte[length[0]];
            gl.glGetProgramInfoLog(program, length[0], characters, 0, log, 0);
            System.out.println("Program Info Log: ");
            for (int i = 0; i < log.length; i++) {
                System.out.print((char) log[i]);
            }
        }
    }

    /**
     * Determines if there is an error in compiling the vertex shader
     */
    public void vertexError(int shader, String s) {

        int[] vertex = shaderError(shader, s);

        if (vertex[0] != 1) {
            System.out.println("\nError in compilation; return-flags:");
            System.out.println("Vertex Shader Compiled = " + vertex[0]);
        }
    }

    /**
     * Determines if there is an error in compiling the fragment shader
     */
    public void fragmentError(int shader, String s) {

        int[] fragment = shaderError(shader, s);

        if (fragment[0] != 1) {
            System.out.println("\nError in compilation; return-flags:");
            System.out.println("Frag Shader Compiled = " + fragment[0]);
        }
    }


    /**
     * Shader error detection method
     */
    private int[] shaderError(int shader, String s) {

        int[] shaderCompiled = new int[1];

        gl.glGetShaderiv(shader, GL4.GL_COMPILE_STATUS, shaderCompiled, 0);

        if (shaderCompiled[0] == 1) {
            System.out.println("Success: " + s);
        } else {
            System.out.println("Failure: " + s);
            printShaderLogs(shader);
        }

        return shaderCompiled;
    }

    /**
     * Detects if an error occurs during linking the a3.shaders to the shader program
     */
    public void linkError(int program) {
        int[] linked = new int[1];
        gl.glGetProgramiv(program, GL4.GL_LINK_STATUS, linked, 0);
        if (linked[0] == 1) {
            System.out.println("Program linked.");
        } else {
            System.out.println("Program Failed.");
            printProgramLogs(program);
        }
        System.out.println();
    }
}
