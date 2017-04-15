package org.ninthworld.greedymeshing;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Vector3f;

public class Model {
	public int[][][] voxels;
	public List<List<Mesh>> meshes;
	public FloatBuffer[] fbo_verts;
	public FloatBuffer[] fbo_norms;
	public FloatBuffer[] fbo_colors;
	public IntBuffer[] vbo_verts;
	public IntBuffer[] vbo_norms;
	public IntBuffer[] vbo_colors;
	public int passes;
	
	public Model(int[][][] model){
		this.voxels = model;
		
		this.passes = 2;
		this.meshes = new ArrayList<List<Mesh>>();
		this.fbo_verts = new FloatBuffer[this.passes];
		this.fbo_norms = new FloatBuffer[this.passes];
		this.fbo_colors = new FloatBuffer[this.passes];
		this.vbo_verts = new IntBuffer[this.passes];
		this.vbo_norms = new IntBuffer[this.passes];
		this.vbo_colors = new IntBuffer[this.passes];
		for(int i=0; i<passes; i++){
			this.meshes.add(new ArrayList<Mesh>());
		}
		
		createMeshes();
	}
	
	public void createMeshes(){
		if(fbo_verts[0] != null)
			clearBuffers();
		for(List<Mesh> m : meshes){
			m.clear();
		}
		
		List<BlockData[][][]> dataPass = new ArrayList<BlockData[][][]>();
		
		for(int p=0; p<passes; p++){
			BlockData[][][] blockData = new BlockData[voxels.length][voxels[0].length][voxels[0][0].length];
			dataPass.add(blockData);
			for(int x=0; x<voxels.length; x++){
				for(int y=0; y<voxels[x].length; y++){
					for(int z=0; z<voxels[x][y].length; z++){
						BlockData block = blockData[x][y][z] = new BlockData(voxels[x][y][z]);
						if(block.id > 0 && ((p == 0 && !Material.hasAlpha(block.id)) || (p == 1 && Material.hasAlpha(block.id))) ){
							int b0 = getVoxelAt(new Vector3f(x+1, y, z));
							int b1 = getVoxelAt(new Vector3f(x-1, y, z));
							int b2 = getVoxelAt(new Vector3f(x, y+1, z));
							int b3 = getVoxelAt(new Vector3f(x, y-1, z));
							int b4 = getVoxelAt(new Vector3f(x, y, z+1));
							int b5 = getVoxelAt(new Vector3f(x, y, z-1));
							block.faces[0] = (block.id != b0 ? Material.isTransparent(b0) : false);
							block.faces[1] = (block.id != b1 ? Material.isTransparent(b1) : false);
							block.faces[2] = (block.id != b2 ? Material.isTransparent(b2) : false);
							block.faces[3] = (block.id != b3 ? Material.isTransparent(b3) : false);
							block.faces[4] = (block.id != b4 ? Material.isTransparent(b4) : false);
							block.faces[5] = (block.id != b5 ? Material.isTransparent(b5) : false);
						}
					}
				}	
			}
			
			greedyMesh(p, dataPass.get(p));
			createFloatBuffer(p);
		}
	}
	
	public void greedyMesh(int p, BlockData[][][] blockData){
		boolean[][][][] mask = new boolean[blockData.length][blockData[0].length][blockData[0][0].length][6];
		
		for(int side=0; side<6; side++){
			for(int x=0; x<blockData.length; x++){
				for(int y=0; y<blockData[0].length; y++){
					for(int z=0; z<blockData[0][0].length; z++){
						if(voxels[x][y][z] > Material.AIR && !mask[x][y][z][side] && blockData[x][y][z].faces[side]){
							if(side == 0 || side == 1){
								int width = 0;
								int height = 0;
								loop:
								for(int i=y; i<blockData[0].length; i++){
									if(i == y){
										for(int j=z; j<blockData[0][0].length; j++){
											if(!mask[x][i][j][side] && blockData[x][i][j].id == blockData[x][y][z].id && blockData[x][i][j].faces[side]){
												width++;
											}else{
												break;
											}
										}
									}else{
										for(int j=0; j<width; j++){
											if(mask[x][i][z+j][side] || blockData[x][i][z+j].id != blockData[x][y][z].id || !blockData[x][i][z+j].faces[side]){
												break loop;
											}
										}
									}
									height++;
								}
								for(int i=0; i<height; i++){
									for(int j=0; j<width; j++){
										mask[x][y+i][z+j][side] = true;
									}
								}
								
								if(side == 0)
									meshes.get(p).add(new Mesh(new Vector3f(x+1, y, z), new Vector3f(x+1, y+height, z+width), new Vector3f(1, 0, 0), Material.getColor(voxels[x][y][z])));
								else
									meshes.get(p).add(new Mesh(new Vector3f(x, y, z+width), new Vector3f(x, y+height, z), new Vector3f(-1, 0, 0), Material.getColor(voxels[x][y][z])));
							}else if(side == 2 || side == 3){
								int width = 0;
								int height = 0;
								loop:
								for(int i=x; i<blockData.length; i++){
									if(i == x){
										for(int j=z; j<blockData[0][0].length; j++){
											if(!mask[i][y][j][side] && blockData[i][y][j].id == blockData[x][y][z].id && blockData[i][y][j].faces[side]){
												width++;
											}else{
												break;
											}
										}
									}else{
										for(int j=0; j<width; j++){
											if(mask[i][y][z+j][side] || blockData[i][y][z+j].id != blockData[x][y][z].id || !blockData[i][y][z+j].faces[side]){
												break loop;
											}
										}
									}
									height++;
								}
								for(int i=0; i<height; i++){
									for(int j=0; j<width; j++){
										mask[x+i][y][z+j][side] = true;
									}
								}
								
								if(side == 2)
									meshes.get(p).add(new Mesh(new Vector3f(x, y+1, z+width), new Vector3f(x+height, y+1, z), new Vector3f(0, 1, 0), Material.getColor(voxels[x][y][z])));
								else
									meshes.get(p).add(new Mesh(new Vector3f(x+height, y, z+width), new Vector3f(x, y, z), new Vector3f(0, -1, 0), Material.getColor(voxels[x][y][z])));
							}else if(side == 4 || side == 5){
								int width = 0;
								int height = 0;
								loop:
								for(int i=x; i<blockData.length; i++){
									if(i == x){
										for(int j=y; j<blockData[0].length; j++){
											if(!mask[i][j][z][side] && blockData[i][j][z].id == blockData[x][y][z].id && blockData[i][j][z].faces[side]){
												width++;
											}else{
												break;
											}
										}
									}else{
										for(int j=0; j<width; j++){
											if(mask[i][y+j][z][side] || blockData[i][y+j][z].id != blockData[x][y][z].id || !blockData[i][y+j][z].faces[side]){
												break loop;
											}
										}
									}
									height++;
								}
								for(int i=0; i<height; i++){
									for(int j=0; j<width; j++){
										mask[x+i][y+j][z][side] = true;
									}
								}
								
								if(side == 4)
									meshes.get(p).add(new Mesh(new Vector3f(x+height, y, z+1), new Vector3f(x, y+width, z+1), new Vector3f(0, 0, 1), Material.getColor(voxels[x][y][z])));
								else
									meshes.get(p).add(new Mesh(new Vector3f(x, y, z), new Vector3f(x+height, y+width, z), new Vector3f(0, 0, -1), Material.getColor(voxels[x][y][z])));
							}
						}
					}
				}
			}
		}
	}
	
	public void createFloatBuffer(int p){
		fbo_verts[p] = BufferUtils.createFloatBuffer(meshes.get(p).size()*4*3);
		fbo_norms[p] = BufferUtils.createFloatBuffer(meshes.get(p).size()*4*3);
		fbo_colors[p] = BufferUtils.createFloatBuffer(meshes.get(p).size()*4*4);
		vbo_verts[p] = BufferUtils.createIntBuffer(1); 
		vbo_norms[p] = BufferUtils.createIntBuffer(1); 
		vbo_colors[p] = BufferUtils.createIntBuffer(1); 
				
		for(Mesh mesh : meshes.get(p)){
			fbo_verts[p].put(mesh.pos1.x).put(mesh.pos1.y).put(mesh.pos1.z);
			fbo_verts[p].put(mesh.norm.z==0?mesh.pos1.x:mesh.pos2.x).put(mesh.pos1.y).put(mesh.norm.x!=0||mesh.norm.y!=0?mesh.pos2.z:mesh.pos1.z);
			fbo_verts[p].put(mesh.pos2.x).put(mesh.pos2.y).put(mesh.pos2.z);
			fbo_verts[p].put(mesh.norm.z==0?mesh.pos2.x:mesh.pos1.x).put(mesh.pos2.y).put(mesh.norm.x!=0||mesh.norm.y!=0?mesh.pos1.z:mesh.pos2.z);
		}
		fbo_verts[p].flip();
		for(Mesh mesh : meshes.get(p)){
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
			fbo_norms[p].put(mesh.norm.x).put(mesh.norm.y).put(mesh.norm.z);
		}
		fbo_norms[p].flip();
		for(Mesh mesh : meshes.get(p)){
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
			fbo_colors[p].put(mesh.color.r).put(mesh.color.g).put(mesh.color.b).put(mesh.color.a);
		}
		fbo_colors[p].flip();
		
		GL15.glGenBuffers(vbo_verts[p]);
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_verts[p].get(0));
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (Float.SIZE/Byte.SIZE)*(fbo_verts[p].capacity()), GL15.GL_STATIC_DRAW);
	    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fbo_verts[p]);
	    
	    GL15.glGenBuffers(vbo_norms[p]);
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_norms[p].get(0));
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (Float.SIZE/Byte.SIZE)*(fbo_norms[p].capacity()), GL15.GL_STATIC_DRAW);
	    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fbo_norms[p]);

	    GL15.glGenBuffers(vbo_colors[p]);
	    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_colors[p].get(0));
	    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (Float.SIZE/Byte.SIZE)*(fbo_colors[p].capacity()), GL15.GL_STATIC_DRAW);
	    GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, fbo_colors[p]);
	}
	
	public void drawFloatBuffer(int p){
		GL11.glPushMatrix();
		
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_verts[p].get(0));
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_norms[p].get(0));
        GL11.glNormalPointer(GL11.GL_FLOAT, 0, 0);
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo_colors[p].get(0));
        GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);
        
        GL11.glDrawArrays(GL11.GL_QUADS, 0, fbo_verts[p].capacity()/3);
        
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        
        GL11.glPopMatrix();
	}
	
	public void render(int p){
		drawFloatBuffer(p);
	}
	
	public void clearBuffers(){
		for(int p=0; p<passes; p++){
			fbo_verts[p].clear();
			fbo_norms[p].clear();
			fbo_colors[p].clear();
			vbo_verts[p].clear();
			vbo_norms[p].clear();
			vbo_colors[p].clear();
		}
	}
	
	public int getVoxelAt(Vector3f p){
		if(p.x < 0 || p.x >= voxels.length || p.y < 0 || p.y >= voxels[0].length || p.z < 0 || p.z >= voxels[0][0].length)
			return Material.AIR;
		else
			return voxels[(int)p.x][(int)p.y][(int)p.z];
	}
}
