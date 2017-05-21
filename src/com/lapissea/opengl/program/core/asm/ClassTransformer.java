package com.lapissea.opengl.program.core.asm;


public interface ClassTransformer{
	
	byte[] transform(String name, byte[] src);
}
