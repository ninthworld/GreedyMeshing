package org.ninthworld.greedymeshing;

public class BlockData {
	public int id;
	public boolean[] faces;
	
	public BlockData(int id){
		this.id = id;
		this.faces = new boolean[6]; 
		
		/*
		 *  0 = +X  1 = -X
		 *  2 = +Y  3 = -Y
		 *  4 = +Z  5 = -Z
		 *  
		 */
	}
}
