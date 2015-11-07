#version 430

out vec4 lineColor;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;


void main(void){
    const vec4 v[6] = vec4[6](
        vec4( 0.0,  0.0,  0.0, 1.0),
        vec4(41.0,  0.0,  0.0, 1.0),
        vec4( 0.0,  0.0,  0.0, 1.0),
        vec4( 0.0, 41.0,  0.0, 1.0),
        vec4( 0.0,  0.0,  0.0, 1.0),
        vec4( 0.0,  0.0, 41.0, 1.0)
    );

    const vec4 colors[6] = vec4[6](
        vec4( 1.0, 0.0, 0.0, 1.0),
        vec4( 1.0, 0.0, 0.0, 1.0),
        vec4( 0.0, 1.0, 0.0, 1.0),
        vec4( 0.0, 1.0, 0.0, 1.0),
        vec4( 0.0, 0.0, 1.0, 1.0),
        vec4( 0.0, 0.0, 1.0, 1.0)
    );

    lineColor = colors[gl_VertexID];
    gl_Position = proj_matrix * mv_matrix * v[gl_VertexID];
}
