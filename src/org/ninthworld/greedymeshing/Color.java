package org.ninthworld.greedymeshing;

import org.lwjgl.opengl.GL11;

public class Color {
	public float r, g, b, a;

	public Color(int r, int g, int b){
		this.r = r/255f;
		this.g = g/255f;
		this.b = b/255f;
		this.a = 1f;
	}
	public Color(int r, int g, int b, float a){
		this.r = r/255f;
		this.g = g/255f;
		this.b = b/255f;
		this.a = a;
	}
	
	public Color(float r, float g, float b){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 1;
	}	
	
	public Color(float r, float g, float b, float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public void glColor(){
		GL11.glColor4f(r, g, b, a);
	}
}
