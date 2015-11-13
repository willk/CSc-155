#version 430

in vec2 tc;
in vec3 varyingNormal;
in vec3 varyingLightDirection;
in vec3 varyingPosition;
in vec3 varyingHalfVector;

out vec4 fragColor;

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
    vec3 L = normalize(varyingLightDirection);
    vec3 N = normalize(varyingNormal);
    vec3 P = normalize(-varyingPosition);
    // varyingHalfVector that was computed in the Vertex Shader.
    vec3 H = varyingHalfVector;

    // Angle between light and surface normal
    float theta = dot(L, N);

    fragColor = globalAmbient * material.ambient + light.ambient * material.ambient +
                light.diffuse * material.diffuse * max(theta, 0) +
                light.specular * material.specular * pow(max(dot(H, N), 0.0), material.shininess * 3.0) +
                texture2D(s, tc) * 0.3;
}