package com.lapissea.opengl.program.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

public class BufferUtil{
	
	public static FloatBuffer store(float[] data){
		return store(BufferUtils.createFloatBuffer(data.length), data);
	}
	
	public static FloatBuffer store(FloatBuffer buf, float[] data){
		buf.put(data);
		buf.flip();
		return buf;
	}
	
	public static IntBuffer store(int[] data){
		IntBuffer buf=BufferUtils.createIntBuffer(data.length);
		buf.put(data);
		buf.flip();
		return buf;
	}
	
}
