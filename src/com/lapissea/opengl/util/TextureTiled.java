package com.lapissea.opengl.util;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.resources.texture.UvArea;
import com.lapissea.opengl.window.impl.assets.BasicTexture;

public class TextureTiled<TileType extends UvArea>extends BasicTexture{
	
	public final List<TileType> tiles=new ArrayList<>();
	
	public TextureTiled(String path){
		super(path);
	}
	
}
