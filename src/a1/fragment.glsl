#version 430

in vec4 color;
out vec4 outColor;

void main(void) {
//	outColor = vec4(0.0, 0.8, 1.0, 1.0);
    outColor = color;
}