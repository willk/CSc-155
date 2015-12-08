#version 430
layout (location = 0) in vec3 position;
layout (location = 1) in vec2 tCoord;
layout (location = 2) in vec3 normals;

out vec2 tc;
out vec3 varyingNormal;
out vec3 varyingLightDirection;
out vec3 varyingPosition;
out vec3 varyingHalfVector;

struct PositionalLight {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    vec3 position;
};

struct Material {
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    float shininess;
};

layout (binding = 0) uniform sampler2D s;

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 mv_matrix;
uniform mat4 proj_matrix;
uniform mat4 n_matrix;

void main(void) {
    float x = sin(1.1 * gl_InstanceID) * 1.2;
    float y = cos(0.8 * gl_InstanceID) * 1.2;
    float z = sin(0.7 * gl_InstanceID) * cos(1.5 * gl_InstanceID) * 1.2;

    vec3 newPosition = position + vec3(x, y, z);

    varyingPosition = (mv_matrix * vec4(newPosition, 1.0)).xyz;
    varyingLightDirection = light.position - varyingPosition;
    varyingNormal = (n_matrix * vec4(normals, 1.0)).xyz;
    varyingHalfVector = normalize(normalize(varyingLightDirection) + normalize(-varyingPosition)).xyz;

    gl_Position = proj_matrix * mv_matrix * vec4(newPosition, 1.0);
    tc = tCoord;
}