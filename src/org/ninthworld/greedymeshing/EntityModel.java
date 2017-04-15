package org.ninthworld.greedymeshing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class EntityModel {
	public static int nbtProperties = 8;
	
	public int modelID;
	public Vector3f pos;
	public Vector3f rotation;
	public float scale;
	
	public EntityModel(){
		this.modelID = 0;
		this.pos = new Vector3f(0,0,0);
		this.rotation = new Vector3f(0,0,0);
		this.scale = 0f;
	}
	
	public EntityModel(int modelID, Vector3f pos, Vector3f rot, float scale){
		this.modelID = modelID;
		this.pos = pos;
		this.rotation = rot;
		this.scale = scale;
	}
	
	public void render(int p, ModelManager mm){
		render(p, mm, false);
	}
	
	public void render(int p, ModelManager mm, boolean invert){
		Model model = mm.getModel(modelID);
		Vector3f half = getModelHalf(mm);
		
		GL11.glPushMatrix();
		
			GL11.glTranslatef(pos.x, pos.y, pos.z);	
			GL11.glRotatef((float)(rotation.x/Math.PI)*180f, 1, 0, 0);
			GL11.glRotatef((float)(rotation.y/Math.PI)*180f, 0, 1, 0);
			GL11.glRotatef((float)(rotation.z/Math.PI)*180f, 0, 0, 1);
			GL11.glTranslatef(-half.x, 0, -half.z*(invert?-1:1));
			GL11.glScalef(scale, scale, scale*(invert?-1:1));

			if(invert)
				GL11.glCullFace(GL11.GL_BACK);
			model.render(p);
			if(invert)
				GL11.glCullFace(GL11.GL_FRONT);
		GL11.glPopMatrix();
	}
	
	public Vector3f getModelHalf(ModelManager mm){
		Model model = mm.getModel(modelID);
		return new Vector3f((model.voxels.length/2f)*scale, (model.voxels[0].length/2f)*scale, (model.voxels[0][0].length/2f)*scale);
	}
}
