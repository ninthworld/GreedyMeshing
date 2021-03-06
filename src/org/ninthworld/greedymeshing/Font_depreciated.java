package org.ninthworld.greedymeshing;

import org.lwjgl.opengl.GL11;

public class Font_depreciated {
	private static char[] index = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	};
	private static int width = 5;
	private static int height = 7;
	private static int[][][] map = {
		{ // A
			{0,0,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0} 
		},{ // B
			{0,1,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,0,0} 
		},{ // C
			{0,0,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,0,1,1,0} 
		},{ // D
			{0,1,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,0,0} 
		},{ // E
			{0,1,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0} 
		},{ // F
			{0,1,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0} 
		},{ // G
			{0,0,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0} 
		},{ // H
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0} 
		},{ // I
			{0,1,1,1,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,1,1,1,0} 
		},{ // J
			{0,1,1,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0} 
		},{ // K
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0} 
		},{ // L
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0} 
		},{ // M
			{1,0,0,0,1},
			{1,1,0,1,1},
			{1,0,1,0,1},
			{1,0,0,0,1},
			{1,0,0,0,1},
			{1,0,0,0,1},
			{1,0,0,0,1} 
		},{ // N
			{1,0,0,0,1},
			{1,1,0,0,1},
			{1,1,0,0,1},
			{1,0,1,0,1},
			{1,0,1,0,1},
			{1,0,0,1,1},
			{1,0,0,0,1} 
		},{ // O
			{0,1,1,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,1,0} 
		},{ // P
			{0,1,1,0,0},
			{0,1,0,1,0},
			{0,1,1,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0} 
		},{ // Q
			{0,1,1,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,1,0},
			{0,1,1,0,1} 
		},{ // R
			{0,1,1,0,0},
			{0,1,0,1,0},
			{0,1,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0} 
		},{ // S
			{0,0,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,1,1,0,0} 
		},{ // T
			{0,1,1,1,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0} 
		},{ // U
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,1,0} 
		},{ // V
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0},
			{0,0,1,0,0} 
		},{ // W
			{1,0,0,0,1},
			{1,0,0,0,1},
			{1,0,0,0,1},
			{1,0,1,0,1},
			{1,0,1,0,1},
			{0,1,0,1,0},
			{0,1,0,1,0} 
		},{ // X
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0} 
		},{ // Y
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0} 
		},{ // Z
			{0,1,1,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,0,1,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0} 
		},{ // SPACE
			{0,0,0,0,0},
			{0,0,0,0,0},
			{0,0,0,0,0},
			{0,0,0,0,0},
			{0,0,0,0,0},
			{0,0,0,0,0},
			{0,0,0,0,0} 
		},{ // 0
			{0,0,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0} 
		},{ // 1
			{0,0,1,0,0},
			{0,1,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,1,1,1,0} 
		},{ // 2
			{0,0,1,1,0},
			{0,1,0,1,0},
			{0,0,0,1,0},
			{0,0,1,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0} 
		},{ // 3
			{0,1,1,0,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,1,1,0,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,1,1,0,0} 
		},{ // 4
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0} 
		},{ // 5
			{0,1,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,0,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,1,1,0,0} 
		},{ // 6
			{0,0,1,1,0},
			{0,1,0,0,0},
			{0,1,0,0,0},
			{0,1,1,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,1,1,1,0} 
		},{ // 7
			{0,1,1,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,0,1,0,0},
			{0,0,1,0,0},
			{0,1,0,0,0},
			{0,1,0,0,0} 
		},{ // 8
			{0,0,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,0,0} 
		},{ // 9
			{0,0,1,1,0},
			{0,1,0,1,0},
			{0,1,0,1,0},
			{0,0,1,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0},
			{0,0,0,1,0} 
		}
	};
	
	public static int[][] getMap(char c){
		for(int i=0; i<index.length; i++){
			if(index[i] == c){
				return map[i];
			}
		}
		return null;
	}
	
	public static void drawText(float x, float y, float s, Color color, String str){
		str = str.toUpperCase();
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 0);
		GL11.glScalef(s, s, 0);
		GL11.glColor3f(color.r, color.g, color.b);
		for(int i=0; i<str.length(); i++){
			char c = str.charAt(i);
			int[][] m = getMap(c);
			
			if(m != null){
				GL11.glBegin(GL11.GL_QUADS);
				for(int j=0; j<m.length; j++){
					for(int k=0; k<m[j].length; k++){
						if(m[j][k] >= 1){
							GL11.glVertex2f(k+i*width, j);
							GL11.glVertex2f(k+i*width+1, j);
							GL11.glVertex2f(k+i*width+1, j+1);
							GL11.glVertex2f(k+i*width, j+1);
						}
					}
				}
				GL11.glEnd();
			}
		}
		
		GL11.glPopMatrix();
	}
}
