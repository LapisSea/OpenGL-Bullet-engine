package com.lapissea.opengl.resources.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.lapissea.opengl.window.api.util.SimpleLoadable;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ModelUtil{
	
	public static void triangulateSingleNum(IntList data, List<int[]> idsVert){
		IntList dataNew=new IntArrayList(idsVert.size());
		int count=0;
		
		for(int[] face:idsVert){
			
			dataNew.add(data.getInt(count));
			dataNew.add(data.getInt(count+1));
			dataNew.add(data.getInt(count+2));
			if(face.length>3){
				dataNew.add(data.getInt(count));
				dataNew.add(data.getInt(count+2));
				dataNew.add(data.getInt(count+3));
			}
			count+=face.length;
		}
		
		data.clear();
		data.addAll(dataNew);
	}
	
	public static void triangulate(List<int[]> ids){
		List<int[]> idsNew=new ArrayList<>(ids.size());
		ids.forEach(face->{
			if(face.length<4){
				idsNew.add(face);
				return;
			}
			idsNew.add(new int[]{face[0],face[1],face[2]});
			idsNew.add(new int[]{face[0],face[2],face[3]});
		});
		if(idsNew.size()!=ids.size()){
			ids.clear();
			ids.addAll(idsNew);
		}
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
