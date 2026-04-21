#version 450 core

out vec4 fragColor;

in vec2 vs_out_texCoord;

uniform sampler2D u_ScreenTexture;

void main() {
    fragColor = texture(u_ScreenTexture, vs_out_texCoord);
}