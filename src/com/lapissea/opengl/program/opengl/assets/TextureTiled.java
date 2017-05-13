package com.lapissea.opengl.program.rendering.gl.texture;

import java.util.ArrayList;
import java.util.List;

public class TextureTiled<TileType extends UvArea>extends BasicTexture{
	
	public final List<TileType> tiles=new ArrayList<>();
	
	public TextureTiled(String path){
		super(path);
	}
	
}
