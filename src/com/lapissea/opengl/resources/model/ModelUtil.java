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
	
	public static void fixIndexedFlatShading(/*all model data in indexed form*/ModelDataBuilder model, int faceSize, List<int[]> faces, /*attributes of date before it was indexed (for getting accurate data) ==>*/ float[] unindexedVertices, int[] unindexedMaterialIds, float[] unindexedNormals, float[] unindexedUvs){
		Vec3f copyVec=new Vec3f();
		
		for(int faceNum=0;faceNum<faces.size();faceNum++){
			int[] face=faces.get(faceNum);
			if(faces.stream().noneMatch(fac->fac[0]==face[0])) continue; //check if there is any collision
			
			//rotate faces to increase indexing efficiency
			boolean success=false;
			int rotCount=1;// no need to check position 0 because that's already checked above
			for(;rotCount<face.length;rotCount++){
				int rotCount0=rotCount;
				if(faces.stream().limit(faceNum).noneMatch(fac->fac[0]==face[rotCount0])){//
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
				int[] org=face.clone();
				for(int j=0;j<face.length;j++){
					int pos=j-rotCount;
					if(pos<0) pos+=face.length;
					face[pos]=org[j];
				}
				
				//set accurate face data
				if(!model.normals.isEmpty()){
					copyVec.write(face[0]*3, model.normals);
				}
				if(!model.uvs.isEmpty()){
					model.uvs.set(face[0]*2+0, u);
					model.uvs.set(face[0]*2+1, v);
				}
				if(!model.materials.isEmpty()) model.materials.set(face[0], mat);
				
				continue;
			}
			
			//all vertices are used in face... a new vertex needs to be generated
			
			//it's fastest to just append vertex to end of model
			face[0]=model.vertecies.size()/3;
			
			int pos=faceNum*faceSize;
			copyVec.load(pos*3, unindexedVertices).put(model.vertecies);//load and put are a convenient way to copy data
			if(!model.normals.isEmpty()) copyVec.load(pos*3, unindexedNormals).put(model.normals);
			if(!model.uvs.isEmpty()){
				model.uvs.add(unindexedUvs[pos*2+0]);
				model.uvs.add(unindexedUvs[pos*2+1]);
			}
			if(!model.materials.isEmpty()) model.materials.add(unindexedMaterialIds[pos]);
		}
		
		//update indices to use fixed faces
		model.indices.clear();
		faces.forEach(face0->{
			for(int i:face0){
				model.indices.add(i);
			}
		});
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
