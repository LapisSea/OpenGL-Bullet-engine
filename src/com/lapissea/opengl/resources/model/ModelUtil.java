package com.lapissea.opengl.resources.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.SimpleLoadable;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ModelUtil{
	
	private static boolean checkNoProvoking(IntList indices, int faceSize, int pos) {
		int usedId=indices.getInt(pos);
		for(int i=0;i<indices.size();i+=faceSize) {
			if(indices.getInt(i)==usedId)return false;
		}
		return true;
	}
	public static void fixIndexedFlatShading(/*all model data in indexed form*/ModelDataBuilder model, int faceSize, /*attributes of date before it was indexed (for getting accurate data) ==>*/ float[] unindexedVertices, int[] unindexedMaterialIds, float[] unindexedNormals, float[] unindexedUvs){
		Vec3f copyVec=new Vec3f();
		
		for(int faceNum=0,faceCount=model.indices.size()/faceSize;faceNum<faceCount;faceNum++){
			int faceStart=faceNum*faceSize;
			
			if(checkNoProvoking(model.indices,faceSize,faceStart)) continue; //check if there is any collision
			
			//rotate faces to increase indexing efficiency
			boolean success=false;
			int rotCount=1;// no need to check position 0 because that's already checked above
			for(;rotCount<faceSize;rotCount++){
				if(checkNoProvoking(model.indices,faceSize,faceStart+rotCount)){//
					success=true;
					break;
				}
			}
			if(success){
				int mat=0,old=faceNum*faceSize;//get data of the face that should be on the provoking vertex
				float u=0,v=0;
				if(!model.normals.isEmpty()) copyVec.load(old*3, unindexedNormals);
				if(!model.materials.isEmpty()) mat=unindexedMaterialIds[old];
				if(!model.uvs.isEmpty()){
					u=unindexedUvs[old*2+0];
					v=unindexedUvs[old*2+1];
				}
				
				//rotate indexes in face to move provoking vertex to a free position
				int[] org=model.indices.subList(faceStart, faceStart+faceSize).toIntArray();
				for(int j=0;j<faceSize;j++){
					int pos=j-rotCount;
					if(pos<0) pos+=faceSize;
					model.indices.set(pos+faceStart, org[j]);
				}
				
				//set accurate face data
				int pos=model.indices.getInt(faceStart);
				if(!model.normals.isEmpty()){
					copyVec.write(pos*3, model.normals);
				}
				if(!model.uvs.isEmpty()){
					model.uvs.set(pos*2+0, u);
					model.uvs.set(pos*2+1, v);
				}
				if(!model.materials.isEmpty()) model.materials.set(pos, mat);
				
				continue;
			}
			
			//all vertices are used in face... a new vertex needs to be generated
			
			//it's fastest to just append vertex to end of model
			model.indices.set(faceStart,model.vertices.size()/3);
			
			int pos=faceNum*faceSize;
			copyVec.load(pos*3, unindexedVertices).put(model.vertices);//load and put are a convenient way to copy data
			if(!model.normals.isEmpty()) copyVec.load(pos*3, unindexedNormals).put(model.normals);
			if(!model.uvs.isEmpty()){
				model.uvs.add(unindexedUvs[pos*2+0]);
				model.uvs.add(unindexedUvs[pos*2+1]);
			}
			if(!model.materials.isEmpty()) model.materials.add(unindexedMaterialIds[pos]);
		}
	}
	
	public static float[] uncompress(float[] data, int[] indices, int partSize){
		return uncompress(new float[indices.length*partSize], data, indices, partSize);
	}
	
	public static float[] uncompress(float[] dest, float[] data, int[] indices, int partSize){
		int counter=0;
		for(int i=0;i<indices.length;i++){
			int pos=indices[i]*partSize;
			
			for(int j=0;j<partSize;j++){
				dest[counter++]=data[pos+j];
			}
		}
		
		return dest;
	}
	public static float[] uncompress(FloatList data, IntList indices, int partSize){
		return uncompress(new float[indices.size()*partSize], data, indices, partSize);
	}
	
	public static float[] uncompress(float[] dest, FloatList data, IntList indices, int partSize){
		int counter=0;
		for(int i=0;i<indices.size();i++){
			int pos=indices.getInt(i)*partSize;
			
			for(int j=0;j<partSize;j++){
				dest[counter++]=data.getFloat(pos+j);
			}
		}
		
		return dest;
	}
	public static int[] uncompress(IntList data, IntList indices, int partSize){
		return uncompress(new int[indices.size()*partSize], data, indices, partSize);
	}
	
	public static int[] uncompress(int[] dest, IntList data, IntList indices, int partSize){
		int counter=0;
		for(int i=0;i<indices.size();i++){
			int pos=indices.getInt(i)*partSize;
			
			for(int j=0;j<partSize;j++){
				dest[counter++]=data.getInt(pos+j);
			}
		}
		
		return dest;
	}
	
	public static Vec3f calcNormal(Vec3f v0, Vec3f v1, Vec3f v2){
		return calcNormal(new Vec3f(), new Vec3f(), v0, v1, v2);
	}
	
	public static Vec3f calcNormal(Vec3f calc, Vec3f v0, Vec3f v1, Vec3f v2){
		return calcNormal(new Vec3f(), calc, v0, v1, v2);
	}
	
	public static Vec3f calcNormal(Vec3f dest, Vec3f calc, Vec3f v0, Vec3f v1, Vec3f v2){
		return calc.set(v2).subRev(v1).crossProduct(dest.set(v1).subRev(v0), dest);
	}
	
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
		ModelUtil.iterate(data, -1, instance, consumer);
	}
	
	public static <T extends SimpleLoadable<T>> void iterate(float[] data, int end, T instance, Consumer<T> consumer){
		ModelUtil.iterate(data, 0, end, instance, consumer);
	}
	
	public static <T extends SimpleLoadable<T>> void iterate(float[] data, int start, int end, T instance, Consumer<T> consumer){
		int perPart=instance.getValueCount();
		
		start*=perPart;
		
		if(end<0) end=data.length;
		else end*=perPart;
		
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
