package org.ninthworld.greedymeshing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

public class ShaderManager {
	
	private int shaderProgram, vertShader, fragShader;
	
	public ShaderManager(){
		shaderProgram = GL20.glCreateProgram();
		vertShader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragShader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
	}
	
	public void useProgram(){
		GL20.glUseProgram(shaderProgram);
	}
	public void stopProgram(){
		GL20.glUseProgram(0);
	}
	
	public int shaderUniform(String str){
		return GL20.glGetUniformLocation(shaderProgram, str);
	}
	
	public void loadShader(String vert, String frag){
		String vertSource = null;
		String fragSource = null;
		try {
			vertSource = readShader(new File("./res/shaders/" + vert));
			fragSource = readShader(new File("./res/shaders/" + frag));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GL20.glShaderSource(vertShader, vertSource);
		GL20.glShaderSource(fragShader, fragSource);
		GL20.glCompileShader(vertShader);
		if(GL20.glGetShader(vertShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
			System.err.println("Vert Shader compile error.");
		}
		GL20.glCompileShader(fragShader);
		if(GL20.glGetShader(fragShader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE){
			System.err.println("Frag Shader compile error.");
		}
		GL20.glAttachShader(shaderProgram, vertShader);
		GL20.glAttachShader(shaderProgram, fragShader);
		GL20.glLinkProgram(shaderProgram);
		GL20.glValidateProgram(shaderProgram);
	}
	
	
	public void cleanUp() {
		GL20.glDeleteProgram(shaderProgram);
		GL20.glDeleteShader(vertShader);
		GL20.glDeleteShader(fragShader);
	}
	
	private String readShader(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null){
			builder.append(line + "\n");
		}
		return builder.toString();
	}
}
