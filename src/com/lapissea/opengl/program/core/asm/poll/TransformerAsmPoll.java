package com.lapissea.opengl.program.core.asm.poll;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import com.lapissea.opengl.program.core.asm.ClassTransformer;
import com.lapissea.opengl.program.core.asm.LapisClassLoader;

public class TransformerAsmPoll implements ClassTransformer{
	
	public static void register(LapisClassLoader loader){
		loader.registerTransformer("com.lapissea.opengl.launch.Test", new TransformerAsmPoll());
	}
	
	private TransformerAsmPoll(){}
	
	@Override
	public byte[] transform(String name, byte[] src){
		try{
			
			ClassReader cr=new ClassReader(src);
			ClassNode cn=new ClassNode(Opcodes.ASM5);
			cr.accept(cn, ClassReader.EXPAND_FRAMES);
			
			
			ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
			cn.accept(cw);
			return cw.toByteArray();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
		return src;
	}
	
}
