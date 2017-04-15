package org.ninthworld.greedymeshing;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Main {
		
	private boolean closeRequested;
	private long lastFrame;
	private long lastFPS;
	private int fps;
	
	private Camera camera;
		
	private ShaderManager shaderManager;
	private ShaderManager shaderManager_ssao;
	private ShaderManager shaderManager_noEffect;
	
	private ChunkManager chunkManager;
	
	private ModelManager modelManager;
	
	private FontManager fontManager;
	
	private int color_texID, depth_texID;
	
	private int preloadDistance = 4;
	private float viewDistance = 2;
	private float viewDistVertical = 6;
	
	private boolean wireframe = false, glsl_shader = true, glsl_ssao = true, freefly = false, selectArea = false, crosshair = true;
	
	private int selectedMaterial = Material.AIR;
	
	private VoxelVector3i selectA = new VoxelVector3i(0,0,0);
	private VoxelVector3i selectB = new VoxelVector3i(0,0,0);
	
	private Player player;
	
	private boolean isWorldLoaded;

	private GUIButton newWorldButton;
	
	private void initialize() {
		//initDisplay(720, 480);
		initDisplay(1080, 640);
		//initDisplay(1680, 900);
		initGL();
		getDelta();
		lastFPS = getTime();
		
		isWorldLoaded = false;
		
		// Load Font
		
		fontManager = new FontManager();
		fontManager.load();
		
		// GUI
		
		newWorldButton = new GUIButton(new Vector2f(Display.getWidth()/2f, Display.getHeight()/2f), new Vector2f(96f, 32f), "New World");
		
		while(!closeRequested){
			int delta = getDelta();
			
			if(isWorldLoaded){
				camera.acceptInputRotate(20);
				camera.acceptInputGrab();
				if(freefly)
					camera.acceptInputMove(delta);
				
				pollInputWorld();
				updateWorld(delta);
				
				renderGLWorld();
			}else{
				pollInputMainMenu();
				renderGLMainMenu();
			}

			Display.update();
			Display.sync(60);
			
			if(Display.isCloseRequested())
				closeRequested = true;
		}
		cleanUp();	
	}
	
	public void initializeWorld(){
		
		// Initialize Camera

		camera = new Camera();
		camera.create();
		
		// Initialize Shaders

		shaderManager = new ShaderManager();
		shaderManager.loadShader("shader.vert", "shader.frag");

		shaderManager_ssao = new ShaderManager();
		shaderManager_ssao.loadShader("ssao.vert", "ssao.frag");
		

		shaderManager_noEffect = new ShaderManager();
		shaderManager_noEffect.loadShader("noeffect.vert", "noeffect.frag");
		
		// Initialize Chunks
		
		chunkManager = new ChunkManager();
		chunkManager.createWorld("world1", 9001);
		chunkManager.loadWorld("world1");
				
		// Create texture buffers
		
		color_texID = makeTexture(null, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, Display.getWidth(), Display.getHeight());
		depth_texID = makeTexture(null, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, Display.getWidth(), Display.getHeight());

		// Load model
		modelManager = new ModelManager();
		modelManager.load();
		
		// Init Entities
		
		player = new Player(new Vector3f(0,460,0), modelManager);
		
		// Preload
		
		for(int i=(int)-preloadDistance; i<=preloadDistance; i++){
			for(int k=(int)-preloadDistance; k<=preloadDistance; k++){
				//chunkManager.generateVerticalChunk(new Vector2f(i, k), 4, modelManager);
				chunkManager.loadVerticalChunks(new Vector2f(i, k), 4);
			}
		}

	}
	
	long lastLeftClick, lastRightClick = 0;
	private void pollInputMainMenu(){
		if(Mouse.isButtonDown(0) && (getTime()-lastLeftClick) > 100){
			lastLeftClick = getTime();
			
			if(newWorldButton.inBounds(Mouse.getX(), Mouse.getY())){
				initializeWorld();
				isWorldLoaded = true;
			}
		}
	}
	
	private void pollInputWorld(){
		while(Keyboard.next()) {
		    if(Keyboard.getEventKeyState()){
		    	if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
					closeRequested = true;
		    	if(Keyboard.getEventKey() == Keyboard.KEY_1){
		    		wireframe = !wireframe;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_2){
		    		glsl_shader = !glsl_shader;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_3){
		    		glsl_ssao = !glsl_ssao;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_4){
		    		player.collisionWireframe = !player.collisionWireframe;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_5){
		    		player.aabbWireframe = !player.aabbWireframe;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_6){
		    		player.thirdPerson = !player.thirdPerson;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_7){
		    		freefly = !freefly;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_8){
		    		selectArea = !selectArea;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_0){
		    		crosshair = !crosshair;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_TAB){
		    		player.isStrafe = !player.isStrafe;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_9){
		    		if(selectArea){
		    			int[][][] voxels = new int[(int)Math.abs(selectA.x-selectB.x)+1][(int)Math.abs(selectA.y-selectB.y)+1][(int)Math.abs(selectA.z-selectB.z)+1];
						VoxelVector3i dir = new VoxelVector3i(selectA.x<selectB.x?1:-1, selectA.y<selectB.y?1:-1, selectA.z<selectB.z?1:-1);
		    			for(int i=0; i<voxels.length; i++){
		    				for(int j=0; j<voxels[i].length; j++){
		    					for(int k=0; k<voxels[i][j].length; k++){
		    						voxels[i][j][k] = chunkManager.getBlockAt(new VoxelVector3i((int)(selectA.x+dir.x*i), (int)(selectA.y+dir.y*j), (int)(selectA.z+dir.z*k)));
		    					}
		    				}
		    			}
		    			Model model = new Model(voxels);
		    			
		    			int i = 1;
		    			String str = "tempModel";
		    			String finalStr = str+i+".model";
		    			File f = new File(finalStr);
		    			
		    			while(f.exists()){
		    				i++;
		    				finalStr = str+i+".model";
		    				f = new File(finalStr);
		    			}
		    			
		    			ModelManager.saveModel(model, finalStr);
		    		}
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_UP){
		    		viewDistance++;
		    	}
		    	if(Keyboard.getEventKey() == Keyboard.KEY_DOWN){
		    		viewDistance = Math.max(viewDistance-1, 0);
		    	}
		    	
		    	if(!freefly)
		    		player.keyDown(Keyboard.getEventKey());
		    }else{
		    	if(!freefly)
		    		player.keyUp(Keyboard.getEventKey());
		    }
		}
		
		//if(freefly){
			if(Mouse.isButtonDown(0) && (getTime()-lastLeftClick) > 100){
				lastLeftClick = getTime();
				if(selectArea){
					selectA = WorldVector3f.toVoxelVector(player.inclusiveLookAtPos);
				}else{
					if(Material.getColor(selectedMaterial) != null){
						chunkManager.putBlockAt(player.exclusiveLookAtPos, selectedMaterial);
					}
				}
			}
			
			if(Mouse.isButtonDown(1) && (getTime()-lastRightClick) > 100){
				lastRightClick = getTime();
				if(selectArea){
					selectB = WorldVector3f.toVoxelVector(player.inclusiveLookAtPos);
				}else{
					chunkManager.removeBlockAt(player.inclusiveLookAtPos);
				}
			}
			
			int d = Mouse.getDWheel();
			if(d != 0){
				selectedMaterial = (int) wrap(selectedMaterial+(d>0?1:-1), Material.MAX_ID+1);
			}
		//}
	}
	
	private void updateWorld(int delta){
		player.update(camera, chunkManager);
		
		if(!freefly){
			if(player.isStrafe){
				player.rotation = (float) -Math.toRadians(camera.getRotationY()-180f);
			}			
			
			if(player.thirdPerson){
				if(player.isStrafe){
					camera.setX(player.pos.x - (float)(Math.sin(Math.toRadians(camera.getRotationX()+90f))*Math.sin(player.rotation)*220f));
					camera.setY(player.pos.y + player.half.y/2f - (float)(Math.cos(Math.toRadians(camera.getRotationX()-90f))*-200f));
					camera.setZ(player.pos.z - (float)(Math.sin(Math.toRadians(camera.getRotationX()+90f))*Math.cos(player.rotation)*220f));
				}else{
					camera.setX(player.pos.x - (float)(Math.sin(Math.toRadians(camera.getRotationX()+90f))*Math.cos(Math.toRadians(camera.getRotationY()-90f))*220f));
					camera.setY(player.pos.y + player.half.y/2f - (float)(Math.cos(Math.toRadians(camera.getRotationX()-90f))*-200f));
					camera.setZ(player.pos.z - (float)(Math.sin(Math.toRadians(camera.getRotationX()+90f))*Math.sin(Math.toRadians(camera.getRotationY()-90f))*220f));
				}
			}else{
				camera.setX(player.pos.x);
				camera.setY(player.pos.y + player.half.y/2f);
				camera.setZ(player.pos.z);
			}
		}
		
		updateFPS();
	}
	
	private void renderGLMainMenu(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		init2DMode();
		
		newWorldButton.render(fontManager);
	}
	
	private void renderGLWorld(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		int width = Display.getWidth();
		int height = Display.getHeight();
		
		init3DMode();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		if(glsl_shader)
			shaderManager.useProgram();
		GL11.glPushMatrix();
		camera.apply();
		
		chunkManager.loadChunks(viewDistance, viewDistVertical, player.pos, modelManager);
		for(int p=0; p<2; p++){
			for(Chunk chunk : chunkManager.loaded){
				Vector3f playerPos;
				float cameraRot;
				
				if(player.isStrafe){
					cameraRot = player.rotation;
					playerPos = player.pos;
				}else{
					cameraRot = (float)Math.toRadians(-camera.getRotationY()+180f);
					playerPos = player.pos;//camera.getPos();
				}
				playerPos = new Vector3f(playerPos.x-(float)Math.sin(cameraRot)*220f,0,playerPos.z-(float)Math.cos(cameraRot)*220f);
				
				float degTo1 = (float) Math.atan2(chunk.pos.x*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.x, chunk.pos.z*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.z);
				degTo1 = (float) (degTo1 < 0 ? Math.PI*2f - Math.abs(degTo1) : degTo1);
				
				float degTo2 = (float) Math.atan2((chunk.pos.x+1)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.x, chunk.pos.z*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.z);
				degTo2 = (float) (degTo2 < 0 ? Math.PI*2f - Math.abs(degTo2) : degTo2);

				float degTo3 = (float) Math.atan2((chunk.pos.x)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.x, (chunk.pos.z+1)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.z);
				degTo3 = (float) (degTo3 < 0 ? Math.PI*2f - Math.abs(degTo3) : degTo3);

				float degTo4 = (float) Math.atan2((chunk.pos.x+1)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.x, (chunk.pos.z+1)*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE-playerPos.z);
				degTo4 = (float) (degTo4 < 0 ? Math.PI*2f - Math.abs(degTo4) : degTo4);
				
				
				cameraRot = (float) (cameraRot < 0 ? Math.PI*2f-(Math.abs(cameraRot)%(Math.PI*2f)) : cameraRot%(Math.PI*2f));
				
				float camAngHigh = wrap(cameraRot+Math.PI/4f, Math.PI*2f);
				float camAngLow = wrap(cameraRot-Math.PI/4f, Math.PI*2f);
				
				if( (wrap(degTo1-camAngLow, Math.PI*2f) >= 0f && wrap(degTo1-camAngLow, Math.PI*2f) <= 2f*Math.PI/4f) ||
					(wrap(degTo2-camAngLow, Math.PI*2f) >= 0f && wrap(degTo2-camAngLow, Math.PI*2f) <= 2f*Math.PI/4f) ||
					(wrap(degTo3-camAngLow, Math.PI*2f) >= 0f && wrap(degTo3-camAngLow, Math.PI*2f) <= 2f*Math.PI/4f) ||
					(wrap(degTo4-camAngLow, Math.PI*2f) >= 0f && wrap(degTo4-camAngLow, Math.PI*2f) <= 2f*Math.PI/4f) ){
					
					GL11.glPushMatrix();
					GL11.glTranslatef(chunk.pos.x*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE, chunk.pos.y*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE, chunk.pos.z*Chunk.CHUNK_SIZE*Chunk.VOXEL_SIZE);
					
					chunk.render(p);
	
					for(EntityModel em : chunk.entityModels){
						em.render(p, modelManager);
					}
					
					GL11.glPopMatrix();
				}
			}
			if(player.thirdPerson){
				player.render(p, modelManager);
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		
		player.renderWireframe(chunkManager);
		if(player.isLookAt/* && (freefly || !player.thirdPerson)*/){
			ChunkManager.drawVoxelWireframe(player.inclusiveLookAtPos);
			//ChunkManager.drawVoxelWireframe(player.exclusiveLookAtPos);
		}
		
		if(selectArea){
			ChunkManager.drawVoxelWireframe(VoxelVector3i.toWorldVector(selectA), new Color(1, 0, 0));
			ChunkManager.drawVoxelWireframe(VoxelVector3i.toWorldVector(selectB), new Color(0, 1, 0));
			
			Vector3f center = new Vector3f(
					(selectA.x+selectB.x+1)/2f * Chunk.VOXEL_SIZE, 
					(selectA.y+selectB.y+1)/2f * Chunk.VOXEL_SIZE,
					(selectA.z+selectB.z+1)/2f * Chunk.VOXEL_SIZE
				);
			Vector3f half = new Vector3f(
					( (selectA.x > selectB.x ? selectA.x-selectB.x : selectB.x-selectA.x)+1 )/2f * Chunk.VOXEL_SIZE, 
					( (selectA.y > selectB.y ? selectA.y-selectB.y : selectB.y-selectA.y)+1 )/2f * Chunk.VOXEL_SIZE,
					( (selectA.z > selectB.z ? selectA.z-selectB.z : selectB.z-selectA.z)+1 )/2f * Chunk.VOXEL_SIZE
				);
			
			Player.draw3DWireframe(center, half, 0f, false);
		}
		
		GL11.glPopMatrix();			
		shaderManager.stopProgram();

		frameSave(color_texID);
		frameSave(depth_texID);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		
		init2DMode();		
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		if(glsl_ssao){
			shaderManager_ssao.useProgram();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, depth_texID);
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, color_texID);
			GL20.glUniform1i(shaderManager_ssao.shaderUniform("texture0"), 0);
			GL20.glUniform1i(shaderManager_ssao.shaderUniform("texture1"), 1);
			GL20.glUniform2f(shaderManager_ssao.shaderUniform("camerarange"), 1, 1000);
			GL20.glUniform2f(shaderManager_ssao.shaderUniform("screensize"), Display.getWidth(), Display.getHeight());
		}else{
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, color_texID);
		}
		
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(1, 0);		
			GL11.glVertex2f(width, height);
			GL11.glTexCoord2f(1, 1);			
			GL11.glVertex2f(width, 0);
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2f(0, height);
		GL11.glEnd();
		GL11.glPopMatrix();
		shaderManager_ssao.stopProgram();

		Color selectedMaterialColor = Material.getColor(selectedMaterial);
		shaderManager_noEffect.useProgram();
			fontManager.useFont(shaderManager_noEffect);
			
			if(selectedMaterialColor != null){
				fontManager.setSize(8);
				fontManager.drawString(64f+24f, Display.getHeight()-48f, Material.getName(selectedMaterial), new Color(0,0,0));
			}
			
		shaderManager_noEffect.stopProgram();

		
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		// UI
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glPushMatrix();
		// Crosshair
		if(crosshair){
			GL11.glColor4f(0f, 0f, 0f, 0.4f);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(width/2f - 2f, height/2f - 12f);	
				GL11.glVertex2f(width/2f + 2f, height/2f - 12f);	
				GL11.glVertex2f(width/2f + 2f, height/2f + 12f);	
				GL11.glVertex2f(width/2f - 2f, height/2f + 12f);
				
				GL11.glVertex2f(width/2f - 12f, height/2f - 2f);	
				GL11.glVertex2f(width/2f + 12f, height/2f - 2f);	
				GL11.glVertex2f(width/2f + 12f, height/2f + 2f);	
				GL11.glVertex2f(width/2f - 12f, height/2f + 2f);	
			GL11.glEnd();
		}
		
		// Selected Material
		if(selectedMaterialColor != null){
			GL11.glColor4f(selectedMaterialColor.r, selectedMaterialColor.g, selectedMaterialColor.b, selectedMaterialColor.a);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(16f, height-16f-64f);	
				GL11.glVertex2f(16f+64f, height-16f-64f);	
				GL11.glVertex2f(16f+64f, height-16f);	
				GL11.glVertex2f(16f, height-16f);	
			GL11.glEnd();
		}		
		GL11.glPopMatrix();

		GL11.glDisable(GL11.GL_BLEND);
	}

	private static int allocateTexture(){
		IntBuffer textureHandle = BufferUtils.createIntBuffer(1);
		GL11.glGenTextures(textureHandle);
		return textureHandle.get(0);
	}
	
	private static int makeTexture(ByteBuffer pixels, int type, int type2, int w, int h){
		int textureHandle = allocateTexture();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, type, w, h, 0, type, type2, pixels);
		return textureHandle;
	}
	
	private void frameSave(int textureHandle){
		GL11.glColor4f(1,  1, 1, 1);
		GL11.glReadBuffer(GL11.GL_BACK);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureHandle);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, Display.getWidth(), Display.getHeight());
	}
	
	private void cleanUp(){
		shaderManager.cleanUp();
		shaderManager_ssao.cleanUp();
		shaderManager_noEffect.cleanUp();
		
		/*for(Chunk c : chunkManager.getChunkList()){
			c.clearBuffers();
		}*/
		
		fontManager.cleanUp();
		
		Display.destroy();
		System.exit(0);
	}
	
	private void initDisplay(int width, int height){
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setVSyncEnabled(true);
			Display.create(new PixelFormat(4,24,0,4));
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void initGL(){
		init3DMode();

		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(0.14f, 0.48f, 0.64f, 1.0f);
		GL11.glClearDepth(1.0f);
		//GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
		
	}
	
	private void init3DMode(){
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45f, ((float)Display.getWidth()/(float)Display.getHeight()), 1f, 10000f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_FRONT);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
		
		if(wireframe)
			GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	}
	
	private void init2DMode(){
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}
	

	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public int getDelta() {
		long time = getTime();
		int delta = (int) (time - lastFrame);
		lastFrame = time;
		
		return delta;
	}
	
	public void updateFPS() {
		if(getTime() - lastFPS > 1000){
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public static void main(String[] args){
		Main main = new Main();
		main.initialize();
	}
	
	public static float wrap(float num, float max){
		return (float)((((num)%max)+max)%max);
	}
	public static float wrap(double num, double max){
		return (float)((((num)%max)+max)%max);
	}
}