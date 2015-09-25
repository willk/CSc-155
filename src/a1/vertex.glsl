#version 430
layout (location = 0) in vec4 offset;
void main() {
    vec4 v[3] = vec4[3] (
        vec4( 0.25, -0.25,  0.5, 1.0),
        vec4(-0.25, -0.25,  0.5, 1.0),
        vec4( 0.25,  0.25, -0.5, 1.0)
    );
    gl_Position = v[gl_VertexID];
}
