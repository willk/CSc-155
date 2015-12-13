#version 430

in vec2 tc;
in vec3 vNormal, vLightDir, vVertPos, vHalfVec;
in vec4 shadow_coord;
out vec4 fragColor;
 
struct PositionalLight {
    vec4 ambient, diffuse, specular;
    vec3 position;
};

struct Material {
    vec4 ambient, diffuse, specular;
    float shininess;
};

uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 mv_matrix; 
uniform mat4 proj_matrix;
uniform mat4 normalMat;
uniform mat4 shadowMVP;
layout (binding=0) uniform sampler2DShadow shadowTex;
layout (binding=1) uniform sampler2D s;
layout (binding=4) uniform samplerCube t;

void main(void) {
    vec3 L = normalize(vLightDir);
    vec3 N = normalize(vNormal);
    vec3 V = normalize(-vVertPos);
    vec3 H = normalize(vHalfVec);

    float inShadow = textureProj(shadowTex, shadow_coord);

    vec3 r = reflect(V, N);

    if (inShadow != 0.0) {
        fragColor = (globalAmbient * material.ambient
                + light.ambient * material.ambient
                + light.diffuse * material.diffuse * max(dot(L,N),0.0)
                + light.specular * material.specular * pow(max(dot(H,N),0.0),material.shininess*3.0)) * 0.6
                + (texture(t, r)) * 0.4;
    } else {
        fragColor = (globalAmbient * material.ambient
                + light.ambient * material.ambient) * 0.6
                + (texture(t, r)) * 0.4;
    }
}
