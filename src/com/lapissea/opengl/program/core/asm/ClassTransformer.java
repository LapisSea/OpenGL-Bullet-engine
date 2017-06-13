package com.lapissea.opengl.program.core.asm;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer{
	
	boolean transform(String name, ClassNode node);
	
}
