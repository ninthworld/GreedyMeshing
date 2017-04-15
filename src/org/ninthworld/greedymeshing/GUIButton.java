package org.ninthworld.greedymeshing;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class GUIButton {
	public Vector2f pos;
	public Vector2f half;
	public String text;
	
	public GUIButton(Vector2f pos, Vector2f half, String text){
		this.pos = pos;
		this.half = half;
		this.text = text;
	}
	
	public boolean inBounds(float mX, float mY){
		return (mX >= pos.x-half.x && mX <= pos.x+half.x && mY >= pos.y-half.y && mY <= pos.y+half.y);
	}
	
	public void render(FontManager fm){
		GL11.glPushMatrix();
			GL11.glTranslatef(pos.x, pos.y, 0f);
			GL11.glColor4f(0.5f, 0f, 0f, 1f);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(-half.x, -half.y);
				GL11.glVertex2f(half.x, -half.y);
				GL11.glVertex2f(half.x, half.y);
				GL11.glVertex2f(-half.x, half.y);
			GL11.glEnd();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
				fm.drawString(-half.x/2f, 0, text, new Color(0,0,0));
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
	}
}
