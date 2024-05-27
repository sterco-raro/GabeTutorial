#type vertex
#version 330 core

// aVar = attribute var
// fVar = output var to fragment
// uVar = uniform

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;
layout (location = 2) in vec2 aTexCoords;

uniform mat4 uProjection;
uniform mat4 uView;
uniform float uTime;

out vec4 fColor;
out vec2 fTexCoords;

void main() {
    fColor = aColor;
    fTexCoords = aTexCoords;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core

uniform float uTime;
uniform sampler2D TEX_SAMPLER;

in vec4 fColor;
in vec2 fTexCoords;

out vec4 color;
float rand2D(in vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float rand3D(in vec3 co){
    return fract(sin(dot(co.xyz ,vec3(12.9898,78.233,144.7272))) * 43758.5453);
}

void main() {
//    float average = (fColor.r + fColor.g + fColor.b) / 3;

//    color = sin(uTime) * fColor; // From bright colors to black and viceversa
//    color = vec4(average, average, average, 1.0f); // Black and white
//    color = fColor * rand2D(vec2(fColor.x, fColor.y));
    color = texture(TEX_SAMPLER, fTexCoords);
}
