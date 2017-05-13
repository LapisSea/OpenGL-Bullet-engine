package com.lapissea.opengl.program.opengl.assets;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.program.rendering.gl.texture.UvArea;

public class TextureTiled<TileType extends UvArea>extends BasicTexture{
	
	public final List<TileType> tiles=new ArrayList<>();
	
	public TextureTiled(String path){
		super(path);
	}
	
}
