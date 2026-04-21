#version 450 core

// Input vertex attributes
layout (location = 0) in vec3 in_vertexPosition; // Renamed for clarity
layout (location = 1) in vec2 in_texCoord;
layout (location = 2) in vec3 in_vertexNormal;   // Renamed for clarity

out vec2 vs_out_texCoord;

void main() {
    gl_Position = vec4(in_vertexPosition, 1.0);

    // Pass texture coordinates through
    vs_out_texCoord = in_texCoord;
}