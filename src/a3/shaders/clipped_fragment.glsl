#version 430

in vec2 tc;

out vec4 fragColor;

uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform vec4 clip_plane = vec4(0.0, 0.0, 1.0, 0.2);
uniform int flipNormal;

layout (binding = 0) uniform sampler2D s;

void main(void) {
    fragColor = texture2D(s, tc);
}