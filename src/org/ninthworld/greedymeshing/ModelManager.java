package org.ninthworld.greedymeshing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

public class ModelManager {
	public static final int MODEL_YELLOWFLOWER = 1;
	public static final int MODEL_REDFLOWER = 2;
	public static final int MODEL_TALLGRASS = 3;
	public static final int MODEL_CACTUS = 4;
	public static final int MODEL_ROCK = 5;
	
	public static final int MODEL_CHARACTER1_SHOE = 6;
	public static final int MODEL_CHARACTER1_LEFTHAND = 7;
	public static final int MODEL_CHARACTER1_RIGHTHAND = 8;
	public static final int MODEL_CHARACTER1_BODY = 9;
	public static final int MODEL_CHARACTER1_HEAD = 10;
	public static final int MODEL_CHARACTER1_HAIR = 11;
	
	public HashMap<Integer, Model> models;
	
	public ModelManager(){
		this.models = new HashMap<Integer, Model>();
	}
	
	public void load(){
		String path = "./res/models/";
		
		models.put(0, null);
		models.put(MODEL_YELLOWFLOWER, ModelManager.loadModel(path + "yellowFlower.model"));
		models.put(MODEL_REDFLOWER, ModelManager.loadModel(path + "redFlower.model"));
		models.put(MODEL_TALLGRASS, ModelManager.loadModel(path + "tallGrass.model"));
		models.put(MODEL_CACTUS, ModelManager.loadModel(path + "cactus.model"));
		models.put(MODEL_ROCK, ModelManager.loadModel(path + "rock.model"));

		models.put(MODEL_CHARACTER1_SHOE, ModelManager.loadModel(path + "character1_shoe.model"));
		models.put(MODEL_CHARACTER1_LEFTHAND, ModelManager.loadModel(path + "character1_handleft.model"));
		models.put(MODEL_CHARACTER1_RIGHTHAND, ModelManager.loadModel(path + "character1_handright.model"));
		models.put(MODEL_CHARACTER1_BODY, ModelManager.loadModel(path + "character1_body.model"));
		models.put(MODEL_CHARACTER1_HEAD, ModelManager.loadModel(path + "character1_head.model"));
		models.put(MODEL_CHARACTER1_HAIR, ModelManager.loadModel(path + "character1_hair.model"));
	}
	
	public Model getModel(int modelID){
		return models.get(modelID);
	}
	
	public static Model loadModel(String url){
		Model model = null;
		InputStream in = null;
		try {
			try {
				in = new BufferedInputStream(new FileInputStream(new File(url)));
				int x_size, y_size, z_size;
				
				x_size = in.read();
				y_size = in.read();
				z_size = in.read();
				
				int[] working = new int[x_size*y_size*z_size];
				int read = 0;
				while(read < working.length){
					working[read] = in.read();
					read++;
				}
				
				int[][][] voxels = new int[x_size][y_size][z_size];
				for(int x=0; x<x_size; x++){
					for(int y=0; y<y_size; y++){
						for(int z=0; z<z_size; z++){
							voxels[x][y][z] = working[z + (y*z_size) + (x*z_size*y_size)];
						}
					}
				}
				model = new Model(voxels);
			} finally {
				in.close();
			}
		} catch(FileNotFoundException e) {			
		} catch(IOException e) {			
		}
		
		return model;
	}
	
	public static void saveModel(Model m, String url){
		int x_size = m.voxels.length;
		int y_size = m.voxels[0].length;
		int z_size = m.voxels[0][0].length;
		
		byte[] output = new byte[3 + x_size*y_size*z_size];
		output[0] = (byte) (x_size & 0xFF);
		output[1] = (byte) (y_size & 0xFF);
		output[2] = (byte) (z_size & 0xFF);
		
		for(int x=0; x<x_size; x++){
			for(int y=0; y<y_size; y++){
				for(int z=0; z<z_size; z++){
					output[3 + z + (y*z_size) + (x*z_size*y_size)] = (byte) (m.voxels[x][y][z] & 0xFF);
				}
			}
		}
		
		OutputStream out = null;
		try {
			try {
				out = new BufferedOutputStream(new FileOutputStream(url));
				out.write(output);
			} finally {
				out.close();
			}
		} catch(FileNotFoundException e) {			
		} catch(IOException e) {			
		}
	}
}

/*
models = new ArrayList<Model>();

int[][][] yellowFlower = new int[5][8][3];
for(int x=0; x<yellowFlower.length; x++){
	for(int y=0; y<yellowFlower[x].length; y++){
		for(int z=0; z<yellowFlower[x][y].length; z++){
			if(x==2 && z==1 && y>=0 && y<=2)
				yellowFlower[x][y][z] = Material.FLOWER_STEM_2;
			if((x==3 && z==1 && y==2) || (x>=0 && x<=2 && z==1 && y==3) || (x==2 && z==1 && y>=4 && y<=5))
				yellowFlower[x][y][z] = Material.FLOWER_STEM_1;
			if(z==0 && ((x==2&&y==4)||((x==1||x==3)&&y==5)||(x==2&&y==6)))
				yellowFlower[x][y][z] = Material.FLOWER_PETAL_YELLOW_2;
			if(z==0 && ((x==1||x==3) && (y==4||y==6)))
				yellowFlower[x][y][z] = Material.FLOWER_PETAL_YELLOW_1;
			if(z==0 && x==2 && y==5)
				yellowFlower[x][y][z] = Material.FLOWER_HEAD_CENTER_1;
		}
	}
}
ModelManager.saveModel(new Model(yellowFlower), "yellowFlower.model");
models.add(ModelManager.loadModel("yellowFlower.model"));

int[][][] redFlowerData = new int[3][8][3];
for(int x=0; x<redFlowerData.length; x++){
	for(int y=0; y<redFlowerData[x].length; y++){
		for(int z=0; z<redFlowerData[x][y].length; z++){
			if(x==1 && z==1 && y>=0 && y<=5)
				redFlowerData[x][y][z] = Material.FLOWER_STEM_1;
			if((x==0&&z==1&&y==4)||(x==1&&z==0&&y==4)||(x==2&&z==1&&y==3))
				redFlowerData[x][y][z] = Material.FLOWER_STEM_2;
			if((y==6 && (x%2==0 && z%2==0)) || (y==7 && ((z%2==0 && x%2!=0) || (z%2!=0 && x%2==0))))
				redFlowerData[x][y][z] = Material.FLOWER_PETAL_RED_1;
			if(y==6 && ((z%2==0 && x%2!=0) || (z%2!=0 && x%2==0)))
				redFlowerData[x][y][z] = Material.FLOWER_PETAL_RED_2;
			if(z==1 && x==1 && y==6)
				redFlowerData[x][y][z] = Material.FLOWER_HEAD_CENTER_1;
		}
	}
}
ModelManager.saveModel(new Model(redFlowerData), "redFlower.model");
models.add(ModelManager.loadModel("redFlower.model"));

int[][][] tallGrassData = new int[8][8][8];
for(int i=0; i<(int)(Math.random()*8)+4; i++){
	int x_pos = (int)(Math.random()*8);
	int z_pos = (int)(Math.random()*8);
	int height = (int)(Math.random()*4)+2;
	for(int x=0; x<tallGrassData.length; x++){
		for(int y=0; y<tallGrassData[x].length; y++){
			for(int z=0; z<tallGrassData[x][y].length; z++){
					if(x==x_pos && z==z_pos && y>=0 && y<=height){
						tallGrassData[x][y][z] = Material.BLOCK_GRASS;
				}
			}
		}
	}
}
ModelManager.saveModel(new Model(tallGrassData), "tallGrass.model");
models.add(ModelManager.loadModel("tallGrass.model"));

int[][][] cactusData = new int[7][16][7];
for(int x=0; x<cactusData.length; x++){
	for(int y=0; y<cactusData[x].length; y++){
		for(int z=0; z<cactusData[x][y].length; z++){
				if(x>=1 && x<=5 && z>=1 && z<=5 && y>=0 && y<=15)
					cactusData[x][y][z] = Material.FLOWER_STEM_1;
				if((((z==1||z==5)&&(x==1||x==3||x==5))||((x==1||x==5)&&(z==3))  ) && y>=0 && y<=15)
					cactusData[x][y][z] = Material.FLOWER_STEM_2;	
			
		}
	}
}
ModelManager.saveModel(new Model(cactusData), "cactus.model");
models.add(ModelManager.loadModel("cactus.model"));

int[][][] rockData = new int[4][2][3];
for(int x=0; x<rockData.length; x++){
	for(int y=0; y<rockData[x].length; y++){
		for(int z=0; z<rockData[x][y].length; z++){
				rockData[x][y][z] = Material.BLOCK_STONE;
			
		}
	}
}
ModelManager.saveModel(new Model(rockData), "rock.model");
models.add(ModelManager.loadModel("rock.model"));
*/