package org.ninthworld.greedymeshing;

import org.lwjgl.util.vector.Vector3f;

public class Mesh {
	public Vector3f pos1;
	public Vector3f pos2;
	public Vector3f norm;
	public Color color;
	
	public Mesh(Vector3f pos1, Vector3f pos2, Vector3f norm, Color c){
		this.pos1 = pos1;
		this.pos2 = pos2;
		this.norm = norm;
		this.color = c;
	}
}
