package org.ninthworld.greedymeshing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.jnbt.FloatTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class ChunkManager {

	private HashMap<Integer, HashMap<Integer, HashMap<Integer, Chunk>>> chunks;
	public List<Chunk> loaded;
	private SimplexNoise[] noise;
	private long seed;
	public String name;
	
	public ChunkManager(){
		
	}
	
	public static void createWorld(String name, long seed){
		NBTOutputStream out = null;
		File file = new File("./worlds/"+name);
		try {
			file.mkdirs();
			out = new NBTOutputStream( new FileOutputStream(file.toString()+"/world.config") );
			out.writeTag(new LongTag("seed", seed));
		} catch (IOException ex) {
		} finally {
		   try {
			   out.close();
		   } catch (Exception ex) {
		   }
		}
	}
	
	public void loadWorld(String name){
		this.name = name;
		chunks = new HashMap<Integer, HashMap<Integer, HashMap<Integer, Chunk>>>();
		loaded = new ArrayList<Chunk>();
		seed = 0;
		
		NBTInputStream in = null;
		try {
			in = new NBTInputStream(new FileInputStream("worlds/"+this.name+"/world.config"));
			Tag tag;
			while((tag = in.readTag()) != null){
				if(tag.getName().equals("seed")){
					seed = (Long) tag.getValue();
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
		noise = new SimplexNoise[6];
		for(int i=0; i<noise.length; i++){
			noise[i] = new SimplexNoise(seed + i);
		}
	}
	
	public void loadChunk(ChunkVector3i chunkPos){
		Chunk c = new Chunk(chunkPos, this);
				
		String path = "worlds/"+this.name+"/chunks/voxels_"+(int)chunkPos.x+"_"+(int)chunkPos.y+"_"+(int)chunkPos.z+".chunk";
		File f = new File(path);
		if(f.exists()){
			Model m = ModelManager.loadModel(path);
			c.voxels = m.voxels;
			
			NBTInputStream in = null;
			try {
				in = new NBTInputStream( new FileInputStream("worlds/"+this.name+"/chunks/models_"+(int)chunkPos.x+"_"+(int)chunkPos.y+"_"+(int)chunkPos.z+".chunk"));
				
				//Tag tag;
				EntityModel[] models = new EntityModel[ (Integer)in.readTag().getValue() ];
				for(int i=0; i<models.length; i++){
					models[i] = new EntityModel();
					for(int j=0; j<EntityModel.nbtProperties; j++){
						Tag tag = in.readTag();
						String[] parts = tag.getName().split("_");
						if(parts[2].equals("id")){
							models[i].modelID = (Integer)tag.getValue();
						}else if(parts[2].equals("posx")){
							models[i].pos.x = (Float)tag.getValue();
						}else if(parts[2].equals("posy")){
							models[i].pos.y = (Float)tag.getValue();
						}else if(parts[2].equals("posz")){
							models[i].pos.z = (Float)tag.getValue();
						}else if(parts[2].equals("rotationx")){
							models[i].rotation.x = (Float)tag.getValue();
						}else if(parts[2].equals("rotationy")){
							models[i].rotation.y = (Float)tag.getValue();
						}else if(parts[2].equals("rotationz")){
							models[i].rotation.z = (Float)tag.getValue();
						}else if(parts[2].equals("scale")){
							models[i].scale = (Float)tag.getValue();
						}
					}
					
					c.entityModels.add(models[i]);
				}
				
			} catch (IOException ex) {
			} finally {
			   try {
				   in.close();
			   } catch (Exception ex) {
			   }
			}
			
		}else{
			c = generateChunk(chunkPos);
		}

		addChunk(chunkPos, c);
		c.createMeshes();
		c.updateAdjChunks();
	}
	
	public void loadVerticalChunks(Vector2f pos, int yCount){
		for(int i=0; i<yCount; i++){
			loadChunk(new ChunkVector3i((int)pos.x, i, (int)pos.y));
		}
	}
	
	public Chunk generateChunk(ChunkVector3i chunkPos){
		Chunk chunk = new Chunk(chunkPos, this);

		Random rX = new Random((long)(seed + Chunk.CHUNK_SIZE*chunkPos.x));
		Random rY = new Random((long)(seed + Chunk.CHUNK_SIZE*chunkPos.y));
		Random rZ = new Random((long)(seed + Chunk.CHUNK_SIZE*chunkPos.z));						
		Random r = new Random((long)(rX.nextInt() + rY.nextInt() + rZ.nextInt()));
		
		float freq1_1 = 800; float amp1_1 = 1; // Water or land
		float freq1_2 = 600; float amp1_2 = 1; // Grass/Mountain or Desert
		float freq1_3 = 400; float amp1_3 = 1; // Grass or Mountain

		float freq2_1 = 80; float amp2_1 = 20; // Hilly
		float freq2_2 = 400; float amp2_2 = 4; // Desert/Underwater
		float freq2_3 = 60; float amp2_3 = 60; // Mountain
		
		for(int x=0; x<Chunk.CHUNK_SIZE; x++){
			for(int z=0; z<Chunk.CHUNK_SIZE; z++){
				
				float height1_1 = noise[0].noise((chunkPos.x*Chunk.CHUNK_SIZE+x)/freq1_1, (chunkPos.z*Chunk.CHUNK_SIZE+z)/freq1_1);
				height1_1 = (height1_1+1f)/2f * amp1_1;
				float height1_2 = noise[1].noise((chunkPos.x*Chunk.CHUNK_SIZE+x)/freq1_2, (chunkPos.z*Chunk.CHUNK_SIZE+z)/freq1_2);
				height1_2 = (height1_2+1f)/2f * amp1_2;
				float height1_3 = noise[2].noise((chunkPos.x*Chunk.CHUNK_SIZE+x)/freq1_3, (chunkPos.z*Chunk.CHUNK_SIZE+z)/freq1_3);
				height1_3 = (height1_3+1f)/2f * amp1_3;
				
				float height2_1 = noise[3].noise((chunkPos.x*Chunk.CHUNK_SIZE+x)/freq2_1, (chunkPos.z*Chunk.CHUNK_SIZE+z)/freq2_1);
				height2_1 = ((height2_1+1f)/2f) * amp2_1;
				float height2_2 = noise[4].noise((chunkPos.x*Chunk.CHUNK_SIZE+x)/freq2_2, (chunkPos.z*Chunk.CHUNK_SIZE+z)/freq2_2);
				height2_2 = ((height2_2+1f)/2f) * amp2_2;
				float height2_3 = noise[5].noise((chunkPos.x*Chunk.CHUNK_SIZE+x)/freq2_3, (chunkPos.z*Chunk.CHUNK_SIZE+z)/freq2_3);
				height2_3 = ((height2_3+1f)/2f) * amp2_3;
				
				//                  Underwater                    (Raise)      Desert                                 Grassy hill              Mountain     
				float height = height1_1*height2_2 + (1f-height1_1)*(6f + height1_2*height2_2 + (1f-height1_2)*(height1_3*height2_1 + (1f-height1_3)*height2_3));
				height = (float)Math.ceil(height);
				
				float WATER_LEVEL = 8f;
				for(int y=0; y<Chunk.CHUNK_SIZE; y++){
					float trueY = Chunk.CHUNK_SIZE*(int)chunkPos.y + y;
					if(trueY <= height){
						
						VoxelVector3i blockPos = new VoxelVector3i(x, y, z);
						Vector3f objPos = new Vector3f((float)x*Chunk.VOXEL_SIZE+Chunk.VOXEL_SIZE/2f, (float)(y+1)*Chunk.VOXEL_SIZE, (float)z*Chunk.VOXEL_SIZE+Chunk.VOXEL_SIZE/2f);
						Vector3f objRot = new Vector3f(0, (float)(r.nextDouble()*Math.PI*2), 0);
						if(height1_1 < .5f){
							if(height1_2 < .5f){
								if(height1_3 < .5f){
									chunk.setBlockAt(blockPos, Material.BLOCK_STONE1); // Mountain
								}else{
									chunk.setBlockAt(blockPos, Material.BLOCK_GRASS1); // Hilly
									if(trueY==height && trueY >= WATER_LEVEL){
										//mm.getModel(
										if(r.nextDouble() > 0.994d){
											chunk.entityModels.add(new EntityModel(ModelManager.MODEL_YELLOWFLOWER, objPos, objRot, 2f)); // Yellow Flower
										}else if(r.nextDouble() > 0.994d){
											chunk.entityModels.add(new EntityModel(ModelManager.MODEL_REDFLOWER, objPos, objRot, 2f)); // Red Flower											
										}else if(r.nextDouble() > 0.94d){
											chunk.entityModels.add(new EntityModel(ModelManager.MODEL_TALLGRASS, objPos, objRot, 2f)); // Tall Grass											
										}else if(r.nextDouble() > 0.996d){
											chunk.entityModels.add(new EntityModel(ModelManager.MODEL_ROCK, objPos, objRot, 2f)); // Pebble/Rock											
										}
									}
								}
							}else{
								chunk.setBlockAt(blockPos, Material.BLOCK_STONE2); // Desert 
								if(trueY==height && trueY >= WATER_LEVEL){
									if(r.nextDouble() > 0.996d){
										chunk.entityModels.add(new EntityModel(ModelManager.MODEL_CACTUS, objPos, objRot, 2f)); // Cactus										
									}
								}
							}
						}else{
							chunk.setBlockAt(blockPos, Material.BLOCK_STONE2); // Ocean Floor 
						}
					}
					if(trueY>height && trueY<=WATER_LEVEL){
						chunk.setBlockAt(new VoxelVector3i(x, y, z), Material.BLOCK_WATER);
					}
				}
			}
		}
		
		return chunk;
	}
	
	public void addChunk(ChunkVector3i chunkPos, Chunk chunk){
		if(!chunks.containsKey((int)chunkPos.x)){
			chunks.put((int)chunkPos.x, new HashMap<Integer, HashMap<Integer, Chunk>>());
		}
		if(!chunks.get((int)chunkPos.x).containsKey((int)chunkPos.y)){
			chunks.get((int)chunkPos.x).put((int)chunkPos.y, new HashMap<Integer, Chunk>());
		}
		chunks.get((int)chunkPos.x).get((int)chunkPos.y).put((int)chunkPos.z, chunk);
	}
	
	public Chunk getChunkAt(ChunkVector3i chunkPos){
		if(!chunks.containsKey((int)chunkPos.x)){
			return null;
		}
		if(!chunks.get((int)chunkPos.x).containsKey((int)chunkPos.y)){
			return null;
		}
		if(!chunks.get((int)chunkPos.x).get((int)chunkPos.y).containsKey((int)chunkPos.z)){
			return null;
		}
		return chunks.get((int)chunkPos.x).get((int)chunkPos.y).get((int)chunkPos.z);
	}
	
	public Chunk getChunkAt(WorldVector3f worldPos){
		return getChunkAt( WorldVector3f.toChunkVector(worldPos) );
	}
	
	public Chunk getChunkAt(VoxelVector3i blockPos){
		return getChunkAt( VoxelVector3i.toChunkVector(blockPos) );
	}
	
	public int getBlockAt(VoxelVector3i blockPos){
		Chunk chunk = getChunkAt(blockPos);
		
		if(chunk != null){
			return chunk.getBlockAt( VoxelVector3i.toRelativeVoxelVector(blockPos) );
		}else{
			return Material.NULL;
		}
	}

	public int getBlockAt(WorldVector3f worldPos){
		return getBlockAt( WorldVector3f.toVoxelVector(worldPos) );
	}
	
	public int getBlockAt(Chunk chunk, VoxelVector3i blockPos){
		return getBlockAt(
				new VoxelVector3i(
						(int)(chunk.pos.x*Chunk.CHUNK_SIZE+blockPos.x),
						(int)(chunk.pos.y*Chunk.CHUNK_SIZE+blockPos.y),
						(int)(chunk.pos.z*Chunk.CHUNK_SIZE+blockPos.z)
					)
				);
	}	
	
	public void setBlockAt(VoxelVector3i blockPos, int blockID){
		Chunk chunk = getChunkAt( blockPos );
		
		if(chunk != null){
			chunk.setBlockAt( VoxelVector3i.toRelativeVoxelVector(blockPos), blockID);
		}
	}

	public void setBlockAt(WorldVector3f worldPos, int blockID){
		setBlockAt( WorldVector3f.toVoxelVector(worldPos), blockID);
	}
	
	public void setBlockAt(Chunk chunk, VoxelVector3i blockPos, int blockID){
		setBlockAt(
			new VoxelVector3i(
				(int)(chunk.pos.x*Chunk.CHUNK_SIZE+blockPos.x),
				(int)(chunk.pos.y*Chunk.CHUNK_SIZE+blockPos.y),
				(int)(chunk.pos.z*Chunk.CHUNK_SIZE+blockPos.z)
			),
			blockID
		);
	}	
	
	public void removeBlockAt(WorldVector3f worldPos){
		removeBlockAt( WorldVector3f.toVoxelVector(worldPos) );
	}
	
	public void removeBlockAt(VoxelVector3i blockPos){
		Chunk c = getChunkAt(blockPos);
		if(c != null)
			c.removeBlockAt( VoxelVector3i.toRelativeVoxelVector(blockPos) );	
	}

	public void putBlockAt(WorldVector3f worldPos, int blockID){
		putBlockAt(WorldVector3f.toVoxelVector(worldPos), blockID);
	}
	
	public void putBlockAt(VoxelVector3i blockPos, int blockID){
		Chunk c = getChunkAt(blockPos);
		if(c != null)
			c.putBlockAt(VoxelVector3i.toRelativeVoxelVector(blockPos), blockID);	
	}
	
	public void loadChunks(float viewDistance, float viewDistVertical, Vector3f camera, ModelManager mm){
		List<Chunk> newLoad = new ArrayList<Chunk>();
		
		for(int i=(int)-viewDistance; i<=viewDistance; i++){
			for(int j=(int)-viewDistVertical; j<=viewDistVertical; j++){
				for(int k=(int)-viewDistance; k<=viewDistance; k++){
					Vector3f adjCamPos = new Vector3f((float)Math.floor(camera.getX()/(float)(Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE)), (float)Math.floor(camera.getY()/(float)(Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE)), (float)Math.floor(camera.getZ()/(float)(Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE)));
					Chunk chunk = this.getChunkAt(new ChunkVector3i((int)adjCamPos.x+i, (int)Math.max(Math.min(adjCamPos.y+j, 3), 0), (int)adjCamPos.z+k));
					if(chunk == null){
						loadVerticalChunks(new Vector2f(adjCamPos.x+i, adjCamPos.z+k), 4);
						chunk = this.getChunkAt(new ChunkVector3i((int)adjCamPos.x+i, (int)Math.max(Math.min(adjCamPos.y+j, 3), 0), (int)adjCamPos.z+k));
					}
					newLoad.add(chunk);
				}
			}
		}
		
		for(Chunk c : loaded){
			boolean keepLoaded = false;
			for(Chunk nC : newLoad){
				if(nC == c)
					keepLoaded = true;
			}
			if(!keepLoaded)
				c.clearBuffers();
		}
		
		loaded.clear();
		loaded = newLoad;
	}
	
	public static void drawVoxelWireframe(WorldVector3f worldPos){
		drawVoxelWireframe(worldPos, new Color(0,0,0));
	}
	
	public static void drawVoxelWireframe(WorldVector3f worldPos, Color color){		
		if(worldPos != null){
			Vector3f adjPos = new Vector3f( (float)Math.floor(worldPos.x/Chunk.VOXEL_SIZE)*Chunk.VOXEL_SIZE, (float)Math.floor(worldPos.y/Chunk.VOXEL_SIZE)*Chunk.VOXEL_SIZE, (float)Math.floor(worldPos.z/Chunk.VOXEL_SIZE)*Chunk.VOXEL_SIZE );			
			Vector3f h = new Vector3f(Chunk.VOXEL_SIZE/2f, Chunk.VOXEL_SIZE/2f, Chunk.VOXEL_SIZE/2f);
			
			Vector3f.add(adjPos, h, adjPos);
			Vector3f.add(h, new Vector3f(1f, 1f, 1f), h);
			
			GL11.glPushMatrix();
			GL11.glTranslatef(adjPos.x, adjPos.y, adjPos.z);
			GL11.glColor3f(color.r, color.g, color.b);
			GL11.glBegin(GL11.GL_LINE_LOOP);
				GL11.glVertex3f(-h.x, -h.y, -h.z);
				GL11.glVertex3f(h.x, -h.y, -h.z);
				GL11.glVertex3f(h.x, -h.y, h.z);
				GL11.glVertex3f(-h.x, -h.y, h.z);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINE_LOOP);
				GL11.glVertex3f(-h.x, h.y, -h.z);
				GL11.glVertex3f(h.x, h.y, -h.z);
				GL11.glVertex3f(h.x, h.y, h.z);
				GL11.glVertex3f(-h.x, h.y, h.z);
			GL11.glEnd();
			GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(-h.x, -h.y, -h.z);
				GL11.glVertex3f(-h.x, h.y, -h.z);
				
				GL11.glVertex3f(h.x, -h.y, -h.z);
				GL11.glVertex3f(h.x, h.y, -h.z);
				
				GL11.glVertex3f(h.x, -h.y, h.z);
				GL11.glVertex3f(h.x, h.y, h.z);
				
				GL11.glVertex3f(-h.x, -h.y, h.z);
				GL11.glVertex3f(-h.x, h.y, h.z);
			GL11.glEnd();
			
			GL11.glPopMatrix();
		}
	}
}
