#version 430 core

out vec4 color;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main(void) {
    color = vec4(0.6, 0.6, 0.0, 1.0);
}