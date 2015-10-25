package a2.objects;

/**
 * Created by willk on 10/24/2015.
 */
public class Cube {
    private float[] vertexPosistions, texalPosistions;

    public Cube() {
        vertexPosistions = new float[]{
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
    }

    public float[] getVertexPositions() {
        return vertexPosistions;
    }

    public float[] getTexalPosistions() {
        return texalPosistions;
    }
}
