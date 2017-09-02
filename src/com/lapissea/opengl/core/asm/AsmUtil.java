package com.lapissea.opengl.program.core.asm;

import java.util.List;

import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;

public class AsmUtil{
	
	public static String classNameToAsm(Class<?> c1ass){
		return classNameToAsm(c1ass.getName());
	}
	
	public static String classNameToAsm(String name){
		return "L"+name.replace('.', '/')+";";
	}
	
	@SuppressWarnings("unchecked")
	public static boolean hasAnnotation(ClassNode node, Class<?> annotation){
		String desc=classNameToAsm(annotation);
		if(node.visibleAnnotations!=null){
			for(AnnotationNode an:(List<AnnotationNode>)node.visibleAnnotations){
				if(an.desc.equals(desc)) return true;
			}
		}
		if(node.invisibleAnnotations!=null){
			for(AnnotationNode an:(List<AnnotationNode>)node.invisibleAnnotations){
				if(an.desc.equals(desc)) return true;
			}
		}
		if(node.visibleTypeAnnotations!=null){
			for(AnnotationNode an:(List<AnnotationNode>)node.visibleTypeAnnotations){
				if(an.desc.equals(desc)) return true;
			}
		}
		if(node.invisibleTypeAnnotations!=null){
			for(AnnotationNode an:(List<AnnotationNode>)node.invisibleTypeAnnotations){
				if(an.desc.equals(desc)) return true;
			}
		}
		
		return false;
	}
}
