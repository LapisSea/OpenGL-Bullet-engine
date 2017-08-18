package com.lapissea.opengl.program.core;

import com.lapissea.opengl.program.game.terrain.Chunk;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.Renderer;
import com.lapissea.opengl.program.rendering.shader.modules.ShaderModuleLight;

public class GameSettings{
	
	public int	maxPointLight		=5;
	public int	maxDirLight			=2;
	public int	skyResolutionDevider=5;
	public int	physicsCubeAmmount	=5;
	public int	perChunkGrassMin	=50;
	public int	perChunkGrassRand	=50;
	public int	chunkResolution		=5;
	public int	chunkGridSize		=18;
	public int	worldMultisampling	=4;
	
	public GameSettings(String name){
		ShaderModuleLight.MAX_POINT_LIGHT=maxPointLight;
		ShaderModuleLight.MAX_DIR_LIGHT=maxPointLight;
		
		Renderer.SKY_RESOLUTION_DEVIDER=skyResolutionDevider;
		
		World.PHYSICS_CUBE_AMMOUNT=physicsCubeAmmount;
		World.CHUNK_GRID_SIZE=chunkGridSize;
		
		Chunk.GRASS_MIN=perChunkGrassMin;
		Chunk.GRASS_RAND=perChunkGrassRand;
		Chunk.RESOLUTION=chunkResolution;
	}
	
	public GameSettings(String name, int maxPointLight, int maxDirLight, int skyResolutionDevider, int physicsCubeAmmount, int perChunkGrassMin, int perChunkGrassRand, int chunkResolution, int chunkHeight, int chunkGridSize){
		this.maxPointLight=maxPointLight;
		this.maxDirLight=maxDirLight;
		
		this.skyResolutionDevider=skyResolutionDevider;
		
		this.physicsCubeAmmount=physicsCubeAmmount;
		
		this.perChunkGrassMin=perChunkGrassMin;
		this.perChunkGrassRand=perChunkGrassRand;
		
		this.chunkResolution=chunkResolution;
		
		this.chunkGridSize=chunkGridSize;
		
		ShaderModuleLight.MAX_POINT_LIGHT=maxPointLight;
		ShaderModuleLight.MAX_DIR_LIGHT=maxPointLight;
		
		Renderer.SKY_RESOLUTION_DEVIDER=skyResolutionDevider;
		
		World.PHYSICS_CUBE_AMMOUNT=physicsCubeAmmount;
		World.CHUNK_GRID_SIZE=chunkGridSize;
		
		Chunk.GRASS_MIN=perChunkGrassMin;
		Chunk.GRASS_RAND=perChunkGrassRand;
		Chunk.RESOLUTION=chunkResolution;
	}
	
}
