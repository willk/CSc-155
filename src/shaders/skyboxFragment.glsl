#version 430

in vec3 tc;
out vec4 fragColor;

uniform mat4 view_matrix;
uniform mat4 proj_matrix;
layout (binding = 3) uniform samplerCube s;

void main(void) {
    fragColor = texture(s,tc);
}