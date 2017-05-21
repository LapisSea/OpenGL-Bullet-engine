package com.lapissea.opengl.program.core.asm.poll;

import com.lapissea.opengl.program.core.asm.ClassTransformer;
import com.lapissea.opengl.program.util.LogUtil;

public class TransformerAsmPoll implements ClassTransformer{
	
	public static void register(){
//		((LapisClassLoader)Thread.currentThread().getContextClassLoader()).registerTransformer("com.lapissea.opengl", new TransformerAsmPoll());
	}
	
	private TransformerAsmPoll(){}
	
	@Override
	public byte[] transform(String name, byte[] src){
		LogUtil.println(name,new String(src));
		return src;
	}
	
}
