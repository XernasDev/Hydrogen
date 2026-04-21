#version 450 core

out vec4 fragColor;

in vec3 vs_out_worldPos;
in vec2 vs_out_texCoord;
in vec3 vs_out_normal;

uniform sampler2D u_sampler;
uniform bool u_useTexture;
uniform vec3 u_color;

uniform float u_ambiantLight;
uniform int u_lightCount;
uniform vec3 u_lightPos[50];
uniform vec3 u_lightColor[50];
uniform float u_lightIntensity[50];

void main() {
    vec4 currentColor = vec4(u_color, 1.0);
    if (u_useTexture) currentColor = texture(u_sampler, vs_out_texCoord);
    vec3 normal = normalize(vs_out_normal);
    vec3 lighting = vec3(max(0, u_ambiantLight));
    for (int i = 0; i < u_lightCount; i++) {
        vec3 lightDir = normalize(u_lightPos[i] - vs_out_worldPos);
        float diffuse = max(dot(normal, lightDir), 0.0);
        lighting += diffuse * u_lightColor[i] * u_lightIntensity[i];
    }
    fragColor = vec4(currentColor.xyz * min(lighting, 1.0), 1.0);
}