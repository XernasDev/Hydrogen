#version 450 core

out vec4 fragColor;

in vec2 vs_out_texCoord;

uniform sampler2D u_sampler;
uniform vec3 u_color;

void main() {
    float alpha = texture(u_sampler, vs_out_texCoord).a;
    if (alpha < 0.1) {
        discard;
    }
    fragColor = vec4(u_color, alpha);
}