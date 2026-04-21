#version 450 core

out vec4 fragColor;

in vec3 vs_out_worldPos;
in vec3 vs_out_normal;

void main() {
    vec3 normal = normalize(vs_out_normal);
    fragColor = vec4(normal, 1.0);
}