package org.ninthworld.greedymeshing;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class FontManager {
	
	public static final int FONT_DEFAULT = 0;
	public static final int FONT_DEFAULT2 = 1;
	
	private HashMap<Integer, Integer> fonts;
	private int currentFont;
	private float currentSize;
	
	private static final int font_rows = 16;
	private static final int font_cols = 16;
	
	public FontManager(){
		currentFont = FONT_DEFAULT;
		currentSize = 16f;
		fonts = new HashMap<Integer, Integer>();
	}
	
	public void load(){
		addFont(FONT_DEFAULT, "font01.png");
		addFont(FONT_DEFAULT2, "font02.png");
	}
	
	public void setFont(int font){
		currentFont = font;
	}
	
	public void setSize(float size){
		currentSize = size;
	}
	
	public void useFont(ShaderManager sm){
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getFontTextureID(currentFont));
		GL20.glUniform1i(sm.shaderUniform("texture0"), 0);
	}
	
	public void drawString(float x, float y, String str, Color color){
		boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		GL11.glColor4f(color.r, color.g, color.b, color.a);
		for(int i=0; i<str.length(); i++){
			float row = (float)Math.floor((str.charAt(i))/(float)font_rows)/(float)font_rows;
			float col = ((str.charAt(i))%font_cols)/(float)font_cols;
			
			//System.out.println(row + ", " + col);
			//float size = 16f;
			GL11.glPushMatrix();
			GL11.glTranslatef(x+i*currentSize, y, 0);
			//GL11.glScalef(currentSize/size, currentSize/size, 0);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2f(col, row);
				GL11.glVertex2f(0, 0);
				
				GL11.glTexCoord2f(col + 1/(float)font_cols, row);
				GL11.glVertex2f(currentSize, 0);
				
				GL11.glTexCoord2f(col + 1/(float)font_cols, row + 1/(float)font_rows);
				GL11.glVertex2f(currentSize, currentSize);
				
				GL11.glTexCoord2f(col, row + 1/(float)font_rows);
				GL11.glVertex2f(0, currentSize);
			GL11.glEnd();
			GL11.glPopMatrix();
		}
		
		if(!blendEnabled)
			GL11.glDisable(GL11.GL_BLEND);
	}
	
	public int addFont(int font, String url) {
		BufferedImage texture = loadImage("./res/fonts/" + url);
		int id = loadTexture(texture);
		fonts.put(font, id);
		return id;
	}
	
	public int getFontTextureID(int font){
		return fonts.get(font);
	}
	
	public void cleanUp() {
		for(int id : fonts.keySet()){
			GL11.glDeleteTextures(fonts.get(id));
		}
	}
	
    private static int loadTexture(BufferedImage image) {
    	int BYTES_PER_PIXEL = 4;
    	int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * BYTES_PER_PIXEL);

        for(int y = 0; y < image.getHeight(); y++){
        	for(int x = 0; x < image.getWidth(); x++){
        		int pixel = pixels[y * image.getWidth() + x];
        		buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
				buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
				buffer.put((byte) (pixel & 0xFF));               // Blue component
				buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
        	}
        }
        buffer.flip();
        
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        return textureID;
    }
    
    public static BufferedImage loadImage(String loc) {
    	try {
    		return ImageIO.read(new File(loc));
        } catch (IOException e) {
        }
        return null;
    }
}
