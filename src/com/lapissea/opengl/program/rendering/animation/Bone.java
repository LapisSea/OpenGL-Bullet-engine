package com.lapissea.opengl.program.rendering.animation;

import java.util.List;
import java.util.function.Consumer;

public class Bone{
	
	private List<Bone> children;
	
	
	public List<Bone> getChildren(){
		return children;
	}
	
	public void forChildren(Consumer<Bone> consumer){
		List<Bone> children=getChildren();
		if(children!=null){
			for(Bone b:children){
				consumer.accept(b);
			}
		}
	}
	
}
