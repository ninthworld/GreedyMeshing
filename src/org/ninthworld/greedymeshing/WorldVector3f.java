package org.ninthworld.greedymeshing;

import org.lwjgl.util.vector.Vector3f;

public class WorldVector3f extends Vector3f {

	public WorldVector3f(float x, float y, float z){
		super(x, y, z);
	}
	
	public static VoxelVector3i toVoxelVector(WorldVector3f a){
		return new VoxelVector3i(
			(int)Math.floor(a.x/Chunk.VOXEL_SIZE),
			(int)Math.floor(a.y/Chunk.VOXEL_SIZE),
			(int)Math.floor(a.z/Chunk.VOXEL_SIZE)
		);
	}
	
	public static ChunkVector3i toChunkVector(WorldVector3f a){
		return new ChunkVector3i(
			(int)Math.floor(a.x/Chunk.VOXEL_SIZE/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.y/Chunk.VOXEL_SIZE/(float)Chunk.CHUNK_SIZE),
			(int)Math.floor(a.z/Chunk.VOXEL_SIZE/(float)Chunk.CHUNK_SIZE)
		);
	}
}
