#version 430
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 tCoord;

out vec2 tc;
//out vec4 vColor;

layout (binding = 0) uniform sampler2D s;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main(void) {
    gl_Position = proj_matrix * mv_matrix * vec4(position, 1.0);
    tc = tCoord;
//    vColor = vec4(position, 1.0) * 2 + vec4(0.5, 0.5, 0.5, 0);
}