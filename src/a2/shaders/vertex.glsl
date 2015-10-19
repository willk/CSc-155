#version 430
layout (location = 0) in vec4 offset;
layout (location = 1) in float scale;
layout (location = 2) in int n;

out vec4 color;

void main(void) {
    const vec4 v[3] = vec4[3] (
        vec4( 0.25 * scale, -0.25 * scale,  0.5, 1.0),
        vec4(-0.25 * scale, -0.25 * scale,  0.5, 1.0),
        vec4( 0.00 * scale,  0.25 * scale, -0.5, 1.0)
    );

    const vec4 colors[3] = vec4[3] (
            vec4(1.000, 0.388, 0.388, 1.000),
            vec4(0.388, 1.000, 0.388, 1.000),
            vec4(0.388, 0.388, 1.000, 1.000)
    );


    if (n == 3) color = colors[gl_VertexID];
    else color = colors[n];
    gl_Position = v[gl_VertexID] + offset;
}