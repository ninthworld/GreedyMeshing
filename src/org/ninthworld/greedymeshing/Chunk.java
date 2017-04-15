package org.ninthworld.greedymeshing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jnbt.FloatTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTOutputStream;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.vector.Vector3f;
import org.ninthworld.greedymeshing.Color;

public class Chunk extends Model {
	public static final int CHUNK_SIZE = 16;
	public static final float VOXEL_SIZE = 16f;
	
	public ChunkVector3i pos;
	private ChunkManager parent;
	public List<EntityModel> entityModels;
	
	public Chunk(ChunkVector3i pos, ChunkManager parent){
		super(new int[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE]);
		this.pos = pos;
		this.parent = parent;
		entityModels = new ArrayList<EntityModel>();
	}
	
	@Override
	public int getVoxelAt(Vector3f p){
		return parent.getBlockAt(new VoxelVector3i((int)(pos.x*Chunk.CHUNK_SIZE+p.x), (int)(pos.y*Chunk.CHUNK_SIZE+p.y), (int)(pos.z*Chunk.CHUNK_SIZE+p.z)));
	}
	
	public void render(int p){
		GL11.glPushMatrix();
			GL11.glScalef(VOXEL_SIZE, VOXEL_SIZE, VOXEL_SIZE);
			drawFloatBuffer(p);
		GL11.glPopMatrix();
	}
	
	
	public int getBlockAt(VoxelVector3i blockPos){
		if(blockPos.x >= 0 && blockPos.y >= 0 && blockPos.z >= 0 && blockPos.x < CHUNK_SIZE && blockPos.y < CHUNK_SIZE && blockPos.z < CHUNK_SIZE){
			return voxels[(int)blockPos.x][(int)blockPos.y][(int)blockPos.z];
		}else{
			return -1;
		}
	}
	
	public void setBlockAt(VoxelVector3i blockPos, int blockID){
		if(blockPos.x >= 0 && blockPos.y >= 0 && blockPos.z >= 0 && blockPos.x < CHUNK_SIZE && blockPos.y < CHUNK_SIZE && blockPos.z < CHUNK_SIZE){
			voxels[(int)blockPos.x][(int)blockPos.y][(int)blockPos.z] = blockID;
		}
	}
	
	public void removeBlockAt(VoxelVector3i blockPos){
		setBlockAt(blockPos, Material.AIR);
		createMeshes();
		updateAdjChunks(blockPos);
		
		saveChunk();
	}
	
	public void putBlockAt(VoxelVector3i blockPos, int blockID){
		setBlockAt(blockPos, blockID);
		createMeshes();
		updateAdjChunks(blockPos);
		
		saveChunk();
	}
	
	public void saveChunk(){
		File f = new File("worlds/"+parent.name+"/chunks");
		f.mkdirs();
		ModelManager.saveModel(this, f.toString()+"/voxels_"+(int)pos.x+"_"+(int)pos.y+"_"+(int)pos.z+".chunk");
		
		NBTOutputStream out = null;
		try {
			out = new NBTOutputStream( new FileOutputStream(f.toString()+"/models_"+(int)pos.x+"_"+(int)pos.y+"_"+(int)pos.z+".chunk"));

			out.writeTag(new IntTag("entityCount", entityModels.size()));
			for(int i=0; i<entityModels.size(); i++){
				out.writeTag(new IntTag("entity_"+i+"_id", entityModels.get(i).modelID));
				out.writeTag(new FloatTag("entity_"+i+"_posx", entityModels.get(i).pos.x));
				out.writeTag(new FloatTag("entity_"+i+"_posy", entityModels.get(i).pos.y));
				out.writeTag(new FloatTag("entity_"+i+"_posz", entityModels.get(i).pos.z));
				out.writeTag(new FloatTag("entity_"+i+"_rotationx", entityModels.get(i).rotation.x));
				out.writeTag(new FloatTag("entity_"+i+"_rotationy", entityModels.get(i).rotation.y));
				out.writeTag(new FloatTag("entity_"+i+"_rotationz", entityModels.get(i).rotation.z));
				out.writeTag(new FloatTag("entity_"+i+"_scale", entityModels.get(i).scale));
			}
			
		} catch (IOException ex) {
		} finally {
		   try {
			   out.close();
		   } catch (Exception ex) {
		   }
		}
	}
	
	public void updateAdjChunks(){
		Chunk c1 = parent.getChunkAt(new ChunkVector3i((int)pos.x-1, (int)pos.y, (int)pos.z));
		if(c1 != null)
			c1.createMeshes();
	
		Chunk c2 = parent.getChunkAt(new ChunkVector3i((int)pos.x+1, (int)pos.y, (int)pos.z));
		if(c2 != null)
			c2.createMeshes();
	
		Chunk c3 = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y-1, (int)pos.z));
		if(c3 != null)
			c3.createMeshes();
	
		Chunk c4 = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y+1, (int)pos.z));
		if(c4 != null)
			c4.createMeshes();
	
		Chunk c5 = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y, (int)pos.z-1));
		if(c5 != null)
			c5.createMeshes();
	
		Chunk c6 = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y, (int)pos.z+1));
		if(c6 != null)
			c6.createMeshes();
	}
	
	public void updateAdjChunks(VoxelVector3i blockPos){
		if((int)blockPos.x <= 0){
			Chunk c = parent.getChunkAt(new ChunkVector3i((int)pos.x-1, (int)pos.y, (int)pos.z));
			if(c != null)
				c.createMeshes();
		}
		if((int)blockPos.x >= Chunk.CHUNK_SIZE-1){
			Chunk c = parent.getChunkAt(new ChunkVector3i((int)pos.x+1, (int)pos.y, (int)pos.z));
			if(c != null)
				c.createMeshes();
		}
		if((int)blockPos.y <= 0){
			Chunk c = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y-1, (int)pos.z));
			if(c != null)
				c.createMeshes();
		}
		if((int)blockPos.y >= Chunk.CHUNK_SIZE-1){
			Chunk c = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y+1, (int)pos.z));
			if(c != null)
				c.createMeshes();
		}
		if((int)blockPos.z <= 0){
			Chunk c = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y, (int)pos.z-1));
			if(c != null)
				c.createMeshes();
		}
		if((int)blockPos.z >= Chunk.CHUNK_SIZE-1){
			Chunk c = parent.getChunkAt(new ChunkVector3i((int)pos.x, (int)pos.y, (int)pos.z+1));
			if(c != null)
				c.createMeshes();
		}
	}
}
