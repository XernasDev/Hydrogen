#version 450 core

// Input vertex attributes
layout (location = 0) in vec3 in_vertexPosition; // Renamed for clarity
layout (location = 1) in vec2 in_texCoord;       // Renamed for clarity
layout (location = 2) in vec3 in_vertexNormal;   // Renamed for clarity

// --- UNIFORMS ---
uniform mat4 u_modelMatrix;
uniform mat4 u_projectionMatrix;

// --- OUTPUTS to Fragment Shader ---
out vec3 vs_out_worldPos;    // Vertex position in world space
out vec2 vs_out_texCoord;    // Texture coordinates

void main() {
    // Calculate world position
    vec4 worldPos4 = u_modelMatrix * vec4(in_vertexPosition, 1.0);
    vs_out_worldPos = worldPos4.xyz; // Pass only vec3

    // Calculate final clip space position using pre-combined matrix
    gl_Position = u_projectionMatrix * worldPos4;

    // Pass texture coordinates through
    vs_out_texCoord = in_texCoord;
}