package org.ninthworld.greedymeshing;

public class Material {
	public static final int MAX_ID = 40;
	
	public static final int NULL = -1;
	public static final int AIR = 0;
	public static final int BLOCK_STONE1 = 1;
	public static final int BLOCK_STONE2 = 2;
	public static final int BLOCK_DIRT1 = 3;
	public static final int BLOCK_DIRT2 = 4;
	public static final int BLOCK_GRASS1 = 5;
	public static final int BLOCK_GRASS2 = 6;
	public static final int BLOCK_GRASS3 = 7;
	public static final int BLOCK_WATER = 8;
	public static final int MODEL_TREE1_LEAVES1 = 10;
	public static final int MODEL_TREE1_LEAVES2 = 11;
	public static final int MODEL_TREE1_LEAVES3 = 12;
	public static final int MODEL_TREE1_LEAVES4 = 13;
	public static final int MODEL_TREE1_TRUNK1 = 14;
	public static final int MODEL_TREE1_TRUNK2 = 15;
	
	public static Color getColor(int mat){
		switch(mat){
		case AIR:
			return null;
		case BLOCK_STONE1:
			return new Color(95, 101, 106);
		case BLOCK_STONE2:
			return new Color(90, 98, 99);
		case BLOCK_DIRT1:
			return new Color(143, 128, 83);
		case BLOCK_DIRT2:
			return new Color(127, 112, 70);
		case BLOCK_GRASS1:
			return new Color(117, 157, 95);
		case BLOCK_GRASS2:
			return new Color(103, 147, 90);
		case BLOCK_GRASS3:
			return new Color(89, 137, 86);
		case BLOCK_WATER:
			return new Color(64, 164, 223, 0.4f);
		case MODEL_TREE1_LEAVES1:
			return new Color(89, 137, 86);
		case MODEL_TREE1_LEAVES2:
			return new Color(52, 95, 50);
		case MODEL_TREE1_LEAVES3:
			return new Color(50, 83, 48);
		case MODEL_TREE1_LEAVES4:
			return new Color(41, 67, 39);
		case MODEL_TREE1_TRUNK1:
			return new Color(47, 41, 31);
		case MODEL_TREE1_TRUNK2:
			return new Color(40, 36, 29);
		}
		return new Color(0,0,0);
	}
	
	public static String getName(int mat){
		switch(mat){
		case AIR:
			return "Air";
		case BLOCK_STONE1:
			return "Stone1";
		case BLOCK_STONE2:
			return "Stone2";
		case BLOCK_DIRT1:
			return "Dirt1";
		case BLOCK_DIRT2:
			return "Dirt2";
		case BLOCK_GRASS1:
			return "Grass1";
		case BLOCK_GRASS2:
			return "Grass2";
		case BLOCK_GRASS3:
			return "Grass3";
		case BLOCK_WATER:
			return "Water";
		case MODEL_TREE1_LEAVES1:
			return "Tree1Leaves1";
		case MODEL_TREE1_LEAVES2:
			return "Tree1Leaves2";
		case MODEL_TREE1_LEAVES3:
			return "Tree1Leaves3";
		case MODEL_TREE1_LEAVES4:
			return "Tree1Leaves4";
		case MODEL_TREE1_TRUNK1:
			return "Tree1Trunk1";
		case MODEL_TREE1_TRUNK2:
			return "Tree1Trunk2";
		}
		return "NULL";
	}
	
	public static boolean isTransparent(int mat){
		switch(mat){
		case AIR:
			return true;
		case BLOCK_WATER:
			return true;
		}
		return false;
	}
	
	public static boolean hasAlpha(int mat){
		switch(mat){
		case BLOCK_WATER:
			return true;
		}
		return false;
	}
}
