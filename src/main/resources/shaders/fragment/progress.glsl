#version 450 core

out vec4 fragColor;

in vec2 vs_out_texCoord;

uniform sampler2D u_sampler;
uniform bool u_useTexture;
uniform vec3 u_color;

uniform float u_progress;

void main() {
    if (vs_out_texCoord.x > u_progress) {
        discard;
    }
    vec4 currentColor = vec4(u_color, 1.0);
    if (u_useTexture) currentColor = texture(u_sampler, vs_out_texCoord);
    fragColor = vec4(currentColor.xyz, 1.0);
}