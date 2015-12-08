#version 430

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tex_coord;
out vec2 tc;

layout (binding=0) uniform sampler2D s;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;

void main(void) {
    gl_Position = proj_matrix * mv_matrix * position;
	tc = tex_coord;
}