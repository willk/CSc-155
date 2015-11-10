package a3.objects;

/**
 * Created by willk on 10/24/2015.
 */
public class Cube {
    private final float[] normals, vertices, texals;

    public Cube() {
        vertices = new float[]{
                -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, // Front Face
                1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, // Right Face
                1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, // Back Face
                -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, // Left Face
                -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, // Bottom Face
                -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f  // Top Face
        };

        texals = new float[]{
                1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f
        };

        normals = new float[]{
                0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f,
                4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f,
                0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f,
                -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f,
                0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f, 0f, 4f, 0f,
                0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f, 0f, -4f, 0f
        };
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTexals() {
        return texals;
    }

    public float[] getNormals() {
        return normals;
    }
}
