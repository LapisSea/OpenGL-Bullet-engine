package com.lapissea.opengl.program.core.asm;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

import com.lapissea.opengl.program.core.asm.poll.AsmPoll.AsmPolling;

public interface ClassTransformer{
	
	byte[] transform(String name, byte[] src);
	
	default String classNameToAsm(Class<?> c1ass){
		return classNameToAsm(c1ass.getName());
	}

	default String classNameToAsm(String name){
		return "L"+name.replace('.', '/')+";";
	}
	default String hasAnnotation(ClassNode src, Class<?> annotation){

		for(int i=0;i<cn.invisibleAnnotations.size();i++){
			AnnotationNode an=(AnnotationNode)cn.invisibleAnnotations.get(i);
			if(an.desc.equals(classNameToAsm(AsmPolling.class)))return true;
		}
	}
}
