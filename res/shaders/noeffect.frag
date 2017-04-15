#version 110

uniform sampler2D texture0;

void main(){
	vec4 color = texture2D(texture0, gl_TexCoord[0].st).rgba;
	if(color == vec4(0,0,0,1)){
		color = vec4(0,0,0,0);
	}
	
    gl_FragColor = color;
}