package org.ninthworld.greedymeshing;

import org.lwjgl.util.vector.Vector3f;

public class VoxelVector3i extends Vector3f {
	
	public VoxelVector3i(int x, int y, int z){
		super((float) x, (float) y, (float) z);
	}
	
	public static ChunkVector3i toChunkVector(VoxelVector3i a){
		return new ChunkVector3i(
			(int)Math.floor(a.x/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.y/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.z/(float)Chunk.CHUNK_SIZE)
		);
	}
	
	public static WorldVector3f toWorldVector(VoxelVector3i a){
		return new WorldVector3f(
			(float) a.x * Chunk.VOXEL_SIZE,
			(float) a.y * Chunk.VOXEL_SIZE,
			(float) a.z * Chunk.VOXEL_SIZE
		);
	}
	
	public static VoxelVector3i toRelativeVoxelVector(VoxelVector3i a){
		return new VoxelVector3i(
			(int)floorMod((int)a.x, Chunk.CHUNK_SIZE), 
			(int)floorMod((int)a.y, Chunk.CHUNK_SIZE), 
			(int)floorMod((int)a.z, Chunk.CHUNK_SIZE)
		);
	}
	
	public static int floorMod(int a, int b){
		return (((a % b) + b) % b);
	}
}
