#version 430
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 tCoord;

out vec2 tc;

layout (binding = 0) uniform sampler2D s;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform vec4 clip_plane = vec4(0.0, 0.0, 1.0, 0.2);
uniform int flipNormal;

void main(void) {
    gl_ClipDistance[0] = dot(vertPos, clip_plane);

    if (flipNormal==1) varyingNormal = -varyingNormal;

    gl_Position = proj_matrix * mv_matrix * vec4(position, 1.0);
    tc = tCoord;
}