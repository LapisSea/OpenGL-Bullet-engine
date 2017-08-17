package com.lapissea.opengl.program.resources.model;

import java.util.List;
import java.util.function.Consumer;

import com.lapissea.opengl.window.api.util.SimpleLoadable;

import it.unimi.dsi.fastutil.floats.FloatList;

public class ModelUtil{
	
	public static void triangulate(List<int[]> ids){
		
	}

	public static <T extends SimpleLoadable<T>> void iterate(float[] data, T instance, Consumer<T> consumer){
		ModelUtil.iterate(data, 0, instance, consumer);
	}

	public static <T extends SimpleLoadable<T>> void iterate(float[] data, int end, T instance, Consumer<T> consumer){
		ModelUtil.iterate(data, 0, end, instance, consumer);
	}

	public static <T extends SimpleLoadable<T>> void iterate(float[] data, int start, int end, T instance, Consumer<T> consumer){
		int perPart=instance.getValueCount();
		start*=perPart;
		end*=perPart;
		while(start<end){
			consumer.accept(instance.load(start, data));
			start+=perPart;
		}
	}

	public static void set(FloatList src, int srcPos, float[] dest, int destPos, int perPart){
		srcPos*=perPart;
		destPos*=perPart;
		for(int i=0;i<perPart;i++){
			dest[destPos+i]=src.getFloat(srcPos+i);
		}
	}

	public static void set(float[] src, int srcPos, float[] dest, int destPos, int perPart){
		srcPos*=perPart;
		destPos*=perPart;
		for(int i=0;i<perPart;i++){
			dest[destPos+i]=src[srcPos+i];
		}
	}

	public static void set(int[] src, int srcPos, int[] dest, int destPos, int perPart){
		srcPos*=perPart;
		destPos*=perPart;
		for(int i=0;i<perPart;i++){
			dest[destPos+i]=src[srcPos+i];
		}
	}

	public static float get(float[] data, int perPart, int pos, int id){
		return data[perPart*pos+id];
	}

	public static int get(int[] data, int perPart, int pos, int id){
		return data[perPart*pos+id];
	}
	
}
