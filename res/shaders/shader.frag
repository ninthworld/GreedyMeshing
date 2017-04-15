#version 120

varying vec3 position;
varying vec3 normal;
varying vec4 color;

void main(){
	vec4 ambient = vec4( vec3(abs(normal.x)*.8 + abs(normal.z)*.9  + abs(normal.y)*1), 1);
	    
    gl_FragColor = vec4(color) * ambient;
}