#version 430

out vec3 tc;

uniform mat4 view_matrix;
uniform mat4 proj_matrix;
layout (binding = 3) uniform samplerCube s;

void main(void)
{
    vec3[4] verts = vec3[4](vec3(-1.0, -1.0, -1.0),
                vec3(1.0, -1.0, -1.0),
                vec3(-1.0, 1.0, -1.0),
                vec3(1.0, 1.0, -1.0));
    tc = mat3(view_matrix) * verts[gl_VertexID];
    gl_Position = proj_matrix * vec4(verts[gl_VertexID], 1.0);
}
