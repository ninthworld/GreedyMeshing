package org.ninthworld.greedymeshing;

import org.lwjgl.util.vector.Vector3f;

public class ChunkVector3i extends Vector3f {
	
	public ChunkVector3i(int x, int y, int z){
		super((float) x, (float) y, (float) z);
	}
	
	public static WorldVector3f toWorldVector(ChunkVector3i a){
		return new WorldVector3f(
			(float) a.x * Chunk.VOXEL_SIZE * Chunk.CHUNK_SIZE,
			(float) a.y * Chunk.VOXEL_SIZE * Chunk.CHUNK_SIZE,
			(float) a.z * Chunk.VOXEL_SIZE * Chunk.CHUNK_SIZE
		);
	}
	
	public static VoxelVector3i toVoxelVector(ChunkVector3i a){
		return new VoxelVector3i(
			(int) a.x * Chunk.CHUNK_SIZE,
			(int) a.y * Chunk.CHUNK_SIZE,
			(int) a.z * Chunk.CHUNK_SIZE
		);
	}
}
