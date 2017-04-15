#version 120

varying vec3 position;
varying vec3 normal;
varying vec4 color;

void main(){
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    gl_FrontColor = gl_Color;
	position = vec3(gl_Vertex);
	normal = vec3(gl_Normal);
	color = vec4(gl_Color);
}