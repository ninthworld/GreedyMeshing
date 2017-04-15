package org.ninthworld.greedymeshing;

import org.lwjgl.util.vector.Vector3f;

public class Entity {
	public Vector3f pos;
	public float rotation;
	public Vector3f half;
	
	public Vector3f vel;
	public float moveSpeed;
	public float strafeSpeed;
	public float rotateSpeed;
	
	public int move;
	public int strafe;
	public int rotate;
	
	public boolean isStrafe;
	
	public Entity(Vector3f pos, Vector3f half){
		this.pos = pos;
		this.rotation = 0f;
		this.half = half;
		this.vel = new Vector3f(0,0,0);
		this.moveSpeed = 4f;
		this.strafeSpeed = 2f;
		this.rotateSpeed = 0.08f;
		
		this.move = 0;
		this.strafe = 0;
		this.rotate = 0;
		
		this.isStrafe = true;
	}
	
	public void update(){
		
	}
}
