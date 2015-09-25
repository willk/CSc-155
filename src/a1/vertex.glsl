#version 430

void main() {
    vec4 v[3] = vec4[3] (
        vec4( 0.25, -0.25,  0.5, 1.0),
        vec4(-0.25, -0.25,  0.5, 1.0),
        vec4( 0.25,  0.25, -0.5, 1.0)
    );
    gl_Position = v[gl_VertexID];
}
