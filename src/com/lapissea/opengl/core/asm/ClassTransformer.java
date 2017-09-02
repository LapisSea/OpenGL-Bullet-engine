package com.lapissea.opengl.core.asm;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer{
	
	boolean transform(String name, ClassNode node);
	
}
