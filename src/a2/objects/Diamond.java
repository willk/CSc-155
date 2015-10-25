package a2.objects;

/**
 * Created by willk on 10/24/2015.
 */
public class Diamond {

    private float[] vertexPositions, texalPositions;

    public Diamond() {
        vertexPositions = new float[]{
                0, 1, 0, 1, 0, 1, -1, 0, 1, 0, 1, 0, 1, 0, -1, 1, 0, 1,
                0, 1, 0, -1, 0, -1, 1, 0, -1, 0, 1, 0, -1, 0, 1, -1, 0, -1,

                0, -1, 0, -1, 0, 1, 1, 0, 1, 0, -1, 0, 1, 0, 1, 1, 0, -1,
                0, -1, 0, 1, 0, -1, -1, 0, -1, 0, -1, 0, -1, 0, -1, -1, 0, 1,
        };

        texalPositions = new float[]{
                .125f, 1, .25f, 0, 0, 0,
                .375f, 1, .5f, 0, .25f, 0,
                .625f, 1, .75f, 0, .5f, 0,
                .875f, 1, 1, 0, .75f, 0,

                .125f, 1, 0, 0, .25f, 0,
                .375f, 1, .25f, 0, .5f, 0,
                .625f, 1, .5f, 0, .75f, 0,
                .875f, 1, .75f, 0, 1, 0
        };
    }

    public float[] getVertexPositions() {
        return vertexPositions;
    }

    public float[] getTexalPositions() {
        return texalPositions;
    }
}
