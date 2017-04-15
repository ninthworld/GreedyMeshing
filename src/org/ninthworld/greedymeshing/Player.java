package org.ninthworld.greedymeshing;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class Player extends Entity {

	public int keyConfig = 1;
	private static final int[][] keyMap = {
		{Keyboard.KEY_UP, Keyboard.KEY_DOWN, Keyboard.KEY_LEFT, Keyboard.KEY_RIGHT, Keyboard.KEY_LCONTROL},
		{Keyboard.KEY_W, Keyboard.KEY_S, Keyboard.KEY_A, Keyboard.KEY_D, Keyboard.KEY_SPACE}
	};
	
	public boolean collisionWireframe, aabbWireframe;
	public boolean inWater;
	public boolean thirdPerson;
	public boolean onGround;
	
	public EntityModel[] bodyParts;
	
	public WorldVector3f inclusiveLookAtPos;
	public WorldVector3f exclusiveLookAtPos;
	public boolean isLookAt;
	
	private int animInterval;
	private int maxAnimInterval;
	
	public Player(Vector3f pos, ModelManager mm) {
		super(pos, new Vector3f(8f, 16f, 8f));
		this.inWater = false;
		this.collisionWireframe = false;
		this.aabbWireframe = false;
		this.thirdPerson = true;
		this.onGround = false;
		this.inclusiveLookAtPos = new WorldVector3f(0,0,0);
		this.exclusiveLookAtPos = new WorldVector3f(0,0,0);
		this.isLookAt = false;
		
		this.animInterval = 0;
		this.maxAnimInterval = (int)(moveSpeed*10);
		
		float scale = 1f;
		this.bodyParts = new EntityModel[7];
		this.bodyParts[0] = new EntityModel(ModelManager.MODEL_CHARACTER1_SHOE, new Vector3f(0,0,0), new Vector3f(0,0,0), scale);
		this.bodyParts[1] = new EntityModel(ModelManager.MODEL_CHARACTER1_SHOE, new Vector3f(0,0,0), new Vector3f(0,0,0), scale);
		this.bodyParts[2] = new EntityModel(ModelManager.MODEL_CHARACTER1_LEFTHAND, new Vector3f(0,0,0), new Vector3f(0,0,0), scale);
		this.bodyParts[3] = new EntityModel(ModelManager.MODEL_CHARACTER1_RIGHTHAND, new Vector3f(0,0,0), new Vector3f(0,0,0), scale);
		this.bodyParts[4] = new EntityModel(ModelManager.MODEL_CHARACTER1_BODY, new Vector3f(0,0,0), new Vector3f(0,0,0), scale);
		this.bodyParts[5] = new EntityModel(ModelManager.MODEL_CHARACTER1_HEAD, new Vector3f(0,0,0), new Vector3f(0,0,0), scale);
		this.bodyParts[6] = new EntityModel(ModelManager.MODEL_CHARACTER1_HAIR, new Vector3f(0,0,0), new Vector3f(0,0,0), scale);
	}
	
	public void keyDown(int key){
		if(key == keyMap[keyConfig][0]){
			move = 1;
		}
		if(key == keyMap[keyConfig][1]){
			move = -1;
		}
		if(key == keyMap[keyConfig][2]){
			if(isStrafe)
				strafe = 1;
			else
				rotate = 1;
		}
		if(key == keyMap[keyConfig][3]){
			if(isStrafe)
				strafe = -1;
			else
				rotate = -1;
		}
		if(key == keyMap[keyConfig][4]){
			if(onGround)
				vel.y = 12f;
		}
	}
	
	public void keyUp(int key){
		if((key == keyMap[keyConfig][0] && move > 0) || (key == keyMap[keyConfig][1] && move < 0)){
			move = 0;
		}
		if((key == keyMap[keyConfig][2] && (isStrafe ? strafe > 0 : rotate > 0)) || (key == keyMap[keyConfig][3] && (isStrafe ? strafe < 0 : rotate < 0))){
			if(isStrafe)
				strafe = 0;
			else
				rotate = 0;
		}
		
	}
	
	public static final float GRAVITY = -8f;
	public void update(Camera cam, ChunkManager cm){
		rotation += rotateSpeed*rotate;
		vel.x = (float)Math.sin(rotation)*moveSpeed*move + (float)Math.sin(rotation + Math.PI/2f)*strafeSpeed*strafe;
		vel.z = (float)Math.cos(rotation)*moveSpeed*move + (float)Math.cos(rotation + Math.PI/2f)*strafeSpeed*strafe;
		
		if(vel.y > GRAVITY){
			vel.y -= 1f;
		}else{
			vel.y = GRAVITY;
		}
		
		Vector3f newPos = Vector3f.add(pos, new Vector3f(0,0,0), null);
		
		if(!isCollide(Vector3f.add(newPos, new Vector3f(vel.x,0,0), null), cm)){
			pos.x += vel.x;
		}
		if(!isCollide(Vector3f.add(newPos, new Vector3f(0,vel.y,0), null), cm)){
			pos.y += vel.y;
			onGround = false;
		}else{
			onGround = true;
		}
		if(!isCollide(Vector3f.add(newPos, new Vector3f(0,0,vel.z), null), cm)){
			pos.z += vel.z;
		}
		
		isLookAt = false;
		WorldVector3f prevLookPos = lookAt(cam, cm, 0);
		int dist = (thirdPerson?64:32);
		for(int i=0; i<Chunk.VOXEL_SIZE*dist; i++){
			WorldVector3f lookPos = lookAt(cam, cm, i/2f);
			int blockID = cm.getBlockAt(lookPos);
			if(blockID > Material.AIR){
				inclusiveLookAtPos = lookPos;
				exclusiveLookAtPos = prevLookPos;
				isLookAt = true;
				break;
			}
			prevLookPos = lookPos;
		}
	}
	
	public boolean isCollide(Vector3f newPos, ChunkManager cm){
		inWater = false;
		VoxelVector3i corner = new VoxelVector3i((int)Math.floor((newPos.x-half.x)/Chunk.VOXEL_SIZE), (int)Math.floor((newPos.y-half.y)/Chunk.VOXEL_SIZE), (int)Math.floor((newPos.z-half.z)/Chunk.VOXEL_SIZE));
		for(int x=0; x<Math.ceil((newPos.x+half.x)/Chunk.VOXEL_SIZE)-Math.floor((newPos.x-half.x)/Chunk.VOXEL_SIZE); x++){
			for(int y=0; y<Math.ceil((newPos.y+half.y)/Chunk.VOXEL_SIZE)-Math.floor((newPos.y-half.y)/Chunk.VOXEL_SIZE); y++){
				for(int z=0; z<Math.ceil((newPos.z+half.z)/Chunk.VOXEL_SIZE)-Math.floor((newPos.z-half.z)/Chunk.VOXEL_SIZE); z++){
					int id = cm.getBlockAt(new VoxelVector3i((int)corner.x+x, (int)corner.y+y, (int)corner.z+z));
					if(id > Material.AIR){
						if(id == Material.BLOCK_WATER){
							inWater = true;
						}else{
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public void render(int p, ModelManager mm){
		float tempRotation = rotation;
		rotation += strafe*Math.PI*.25f;
		
		if(move != 0 || strafe != 0){
			animInterval++;
			if(animInterval > maxAnimInterval)
				animInterval = 0;
		}else{
			animInterval = 0;
		}
		
		//Left Shoe
		bodyParts[0].pos = new Vector3f(
				(float)(pos.x+Math.sin(rotation+Math.PI/2f)*(bodyParts[0].getModelHalf(mm).z) + Math.sin(rotation)*(bodyParts[0].getModelHalf(mm).z*Math.sin((animInterval/(float)maxAnimInterval)*Math.PI*2f))), 
				pos.y-half.y, 
				(float)(pos.z+Math.cos(rotation+Math.PI/2f)*(bodyParts[0].getModelHalf(mm).z) + Math.cos(rotation)*(bodyParts[0].getModelHalf(mm).z*Math.sin((animInterval/(float)maxAnimInterval)*Math.PI*2f)))
			);
		bodyParts[0].rotation = new Vector3f(
				0f, 
				(float)(rotation+Math.PI/2f), 
				(float)(-Math.sin((animInterval/(float)maxAnimInterval)*Math.PI*2f)*Math.PI/4f)
			);
		
		//Right Shoe
		bodyParts[1].pos = new Vector3f(
				(float)(pos.x+Math.sin(rotation-Math.PI/2f)*(bodyParts[1].getModelHalf(mm).z) + Math.sin(rotation)*(bodyParts[1].getModelHalf(mm).z*-Math.sin((animInterval/(float)maxAnimInterval)*Math.PI*2f))), 
				pos.y-half.y, 
				(float)(pos.z+Math.cos(rotation-Math.PI/2f)*(bodyParts[1].getModelHalf(mm).z) + Math.cos(rotation)*(bodyParts[1].getModelHalf(mm).z*-Math.sin((animInterval/(float)maxAnimInterval)*Math.PI*2f)))
			);
		bodyParts[1].rotation = new Vector3f(
				0f, 
				(float)(rotation+Math.PI/2f), 
				(float)(Math.sin((animInterval/(float)maxAnimInterval)*Math.PI*2f)*Math.PI/4f)
			);
		
		//Left Hand
		float bP2_DistZ = bodyParts[4].getModelHalf(mm).z + bodyParts[2].getModelHalf(mm).z*2f - bodyParts[2].scale;
		float bP2_DistX = bodyParts[2].getModelHalf(mm).z * (float)Math.sin( ( animInterval / (float)maxAnimInterval ) * Math.PI*2f );
		float bP2_Dist = (float) Math.sqrt(Math.pow(bP2_DistZ, 2) + Math.pow(bP2_DistX, 2));
		
		float bP2_XOs = bodyParts[4].getModelHalf(mm).z * (float)Math.sin( Math.PI + ( animInterval / (float)maxAnimInterval ) * Math.PI*2f );
	
		bodyParts[2].pos = new Vector3f(
				(float)(pos.x+Math.sin(rotation+Math.PI/2f)*bP2_Dist + Math.sin(rotation)*bP2_XOs),
				pos.y-half.y+bodyParts[0].getModelHalf(mm).y*2f+bodyParts[4].getModelHalf(mm).y*.25f+bodyParts[2].scale, 
				(float)(pos.z+Math.cos(rotation+Math.PI/2f)*bP2_Dist + Math.cos(rotation)*bP2_XOs)
			);
		bodyParts[2].rotation = new Vector3f(
				0f, 
				(float)(rotation+Math.PI/2f),
				(float)(Math.sin((animInterval/(float)maxAnimInterval)*Math.PI*2f)*Math.PI/4f)
			);
		
		//Right Hand
		
		float bP3_DistZ = bodyParts[4].getModelHalf(mm).z + bodyParts[3].getModelHalf(mm).z*2f - bodyParts[3].scale;
		float bP3_DistX = bodyParts[3].getModelHalf(mm).z * (float)Math.sin( ( animInterval / (float)maxAnimInterval ) * Math.PI*2f );
		float bP3_Dist = (float) Math.sqrt(Math.pow(bP3_DistZ, 2) + Math.pow(bP3_DistX, 2));
		
		float bP3_XOs = bodyParts[4].getModelHalf(mm).z * (float)Math.sin( ( animInterval / (float)maxAnimInterval ) * Math.PI*2f );
		
		bodyParts[3].pos = new Vector3f(
				(float)(pos.x+Math.sin(rotation-Math.PI/2f) * bP3_Dist + Math.sin(rotation)*bP3_XOs),
				pos.y-half.y+bodyParts[0].getModelHalf(mm).y*2f+bodyParts[4].getModelHalf(mm).y*.25f+bodyParts[3].scale, 
				(float)(pos.z+Math.cos(rotation-Math.PI/2f) * bP3_Dist + Math.cos(rotation)*bP3_XOs)
			);
		bodyParts[3].rotation = new Vector3f(
				0f, 
				(float)(rotation+Math.PI/2f),
				(float)(Math.sin(Math.PI + (animInterval/(float)maxAnimInterval)*Math.PI*2f)*Math.PI/4f)
			);
		
		//Body
		bodyParts[4].pos = new Vector3f(pos.x, pos.y-half.y+bodyParts[0].getModelHalf(mm).y*2f+bodyParts[4].getModelHalf(mm).y*.25f, pos.z);
		bodyParts[4].rotation = new Vector3f(0, (float)(rotation-Math.PI), 0);

		//Head
		bodyParts[5].pos = new Vector3f(pos.x, pos.y-half.y+bodyParts[0].getModelHalf(mm).y*2f+bodyParts[4].getModelHalf(mm).y*1.25f+bodyParts[5].getModelHalf(mm).y-bodyParts[5].scale, pos.z);
		bodyParts[5].rotation = new Vector3f(0, (float)(rotation-Math.PI), 0);
		
		//Hair
		bodyParts[6].pos = new Vector3f(pos.x, pos.y-half.y+bodyParts[0].getModelHalf(mm).y*2f+bodyParts[4].getModelHalf(mm).y*1.25f+bodyParts[6].getModelHalf(mm).y-bodyParts[6].scale, pos.z);
		bodyParts[6].rotation = new Vector3f(0, (float)(rotation-Math.PI), 0);
		
		
		for(int i=0; i<bodyParts.length; i++){
			if(i == 2){
				bodyParts[i].render(p, mm, true);
			}else{
				bodyParts[i].render(p, mm);
			}
		}
		
		rotation = tempRotation;
	}
	
	public void renderWireframe(ChunkManager cm){
		if(aabbWireframe){
			GL11.glColor4f(0,0,0,1);
			draw3DWireframe(pos, half, rotation, true);
		}
		if(collisionWireframe){
			Vector3f corner = new Vector3f((int)Math.floor((pos.x-half.x)/Chunk.VOXEL_SIZE), (int)Math.floor((pos.y-half.y)/Chunk.VOXEL_SIZE), (int)Math.floor((pos.z-half.z)/Chunk.VOXEL_SIZE));
			for(int x=0; x<Math.ceil((pos.x+half.x)/Chunk.VOXEL_SIZE)-Math.floor((pos.x-half.x)/Chunk.VOXEL_SIZE); x++){
				for(int y=0; y<Math.ceil((pos.y+half.y)/Chunk.VOXEL_SIZE)-Math.floor((pos.y-half.y)/Chunk.VOXEL_SIZE); y++){
					for(int z=0; z<Math.ceil((pos.z+half.z)/Chunk.VOXEL_SIZE)-Math.floor((pos.z-half.z)/Chunk.VOXEL_SIZE); z++){
						if(cm.getBlockAt(new VoxelVector3i((int)corner.x+x, (int)corner.y+y, (int)corner.z+z)) > 0){
							GL11.glColor4f(1,1,1,1);
						}else{
							GL11.glColor4f(1,0,0,1);							
						}
						draw3DWireframe( new Vector3f((corner.x+x)*Chunk.VOXEL_SIZE+Chunk.VOXEL_SIZE/2f, (corner.y+y)*Chunk.VOXEL_SIZE+Chunk.VOXEL_SIZE/2f, (corner.z+z)*Chunk.VOXEL_SIZE+Chunk.VOXEL_SIZE/2f), new Vector3f(Chunk.VOXEL_SIZE/2f, Chunk.VOXEL_SIZE/2f, Chunk.VOXEL_SIZE/2f), 0f, false);
						
					}
				}
			}
		}
	}
	public static void draw3DWireframe(Vector3f p, Vector3f h, float r, boolean d){
		draw3DWireframe(p, h, r, d, new Color(0,0,0));
	}
	public static void draw3DWireframe(Vector3f p, Vector3f h, float r, boolean d, Color c){
		GL11.glPushMatrix();
		GL11.glTranslatef(p.x, p.y, p.z);
		GL11.glRotatef((r/(float)Math.PI)*180f, 0, 1, 0);
		GL11.glColor4f(c.r,c.g,c.b,1);
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
		
		if(d){
			GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3f(0, 0, h.z);
				GL11.glVertex3f(0, 0, 2*h.z);
				
				GL11.glVertex3f(0, 0, 2*h.z);
				GL11.glVertex3f(.5f*h.x, 0, 1.5f*h.z);
				GL11.glVertex3f(0, 0, 2*h.z);
				GL11.glVertex3f(-.5f*h.x, 0, 1.5f*h.z);
			GL11.glEnd();
		}
		
		GL11.glPopMatrix();
	}
	
	public WorldVector3f lookAt(Camera cam, ChunkManager cm, float dist){
		Vector3f rot = new Vector3f((float) Math.toRadians(cam.getRotationX()), (float) Math.toRadians(cam.getRotationY()), (float) Math.toRadians(cam.getRotationZ()));
		
		WorldVector3f endPoint = new WorldVector3f(
				(float) ( dist * Math.cos(rot.x) * Math.cos(rot.y - Math.PI/2f) ),
				(float) ( dist * Math.cos(rot.x + Math.PI/2f) ),
				(float) ( dist * Math.cos(rot.x) * Math.sin(rot.y - Math.PI/2f) )
			);
		
		return new WorldVector3f(cam.getX()+endPoint.x, cam.getY()+endPoint.y, cam.getZ()+endPoint.z);
		//return new WorldVector3f(pos.x+endPoint.x, pos.y+endPoint.y, pos.z+endPoint.z);
	}
	
}
