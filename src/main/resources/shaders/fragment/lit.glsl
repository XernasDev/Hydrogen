#version 450 core

out vec4 fragColor;

in vec3 vs_out_worldPos;
in vec2 vs_out_texCoord;
in vec3 vs_out_normal;

uniform sampler2D u_sampler;
uniform bool u_useTexture;
uniform vec3 u_color;

uniform vec3 lightPos;
uniform float lightIntensity;

void main() {
    vec4 currentColor = vec4(u_color, 1.0);
    if (u_useTexture) currentColor = texture(u_sampler, vs_out_texCoord);
    vec3 normal = normalize(vs_out_normal);
    vec3 lightDir = normalize(lightPos - vs_out_worldPos);
    float diffuse = max(dot(normal, lightDir), 0.0);
    float lighting = max(diffuse * lightIntensity, 0.1);
    fragColor = vec4(currentColor.xyz * min(lighting, 1.0), 1.0);
}