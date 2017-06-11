package com.lapissea.opengl.program.rendering.gl.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.MathUtil;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.impl.assets.Material;
import com.lapissea.opengl.window.impl.assets.Model;
import com.lapissea.util.LogUtil;
import com.lapissea.util.UtilL;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ObjModelLoader{
	
	public static class ModelData{
		
		public String name;
		
		public List<Vec3f>		vertecies	=new ArrayList<>();
		public List<Vec2f>		uvs			=new ArrayList<>();
		public FloatList		materialIds	=new FloatArrayList();
		public List<Vec3f>		normals		=new ArrayList<>();
		public List<IMaterial>	materials	=new ArrayList<>();
		public boolean			hasUvs,hasMaterials,hasNormals;
		
		public int format;
		
		public float[] getVert(){
			float[] data=new float[vertecies.size()*3];
			for(int i=0;i<vertecies.size();i++){
				Vec3f part=vertecies.get(i);
				data[i*3+0]=part.x();
				data[i*3+1]=part.y();
				data[i*3+2]=part.z();
			}
			return data;
		}
		
		public float[] getMat(){
			return hasMaterials?materialIds.toFloatArray():null;
		}
		
		public float[] getNorm(){
			if(!hasNormals) return null;
			
			float[] data=new float[normals.size()*3];
			for(int i=0;i<normals.size();i++){
				Vec3f part=normals.get(i);
				data[i*3+0]=part.x();
				data[i*3+1]=part.y();
				data[i*3+2]=part.z();
			}
			return data;
		}
		
		public float[] getUv(){
			if(!hasUvs) return null;
			
			float[] data=new float[uvs.size()*2];
			for(int i=0;i<uvs.size();i++){
				Vec2f part=uvs.get(i);
				data[i*2+0]=part.x();
				data[i*2+1]=part.y();
			}
			return data;
		}
		
	}
	
	private static String[] getFileLines(String path){
		//		LogUtil.println(path);
		String srcAll=UtilM.getTxtResource(path);
		if(srcAll==null) return null;
		String[] src=srcAll.replaceAll(" +", " ").split("\n");
		return src;
	}
	
	public static ModelData load(String name){
		name=name.replace('\\', '/');
		LogUtil.println("Loading model:", name);
		
		if(name.contains("\\")) name=name.replace('\\', '/');
		name=name.replaceAll("/+", "/");
		if(name.startsWith("/")) name=name.substring(1, name.length());
		
		String[] src=getFileLines("models/"+name+(name.endsWith(".obj")?"":".obj"));
		if(src==null){
			LogUtil.println("Model", name, "does not exist!");
			return null;
		}
		
		String mtllib=null;
		Map<String,IMaterial> materials=null;
		
		//find mtl file pointer
		for(int i=0;i<src.length;i++){
			String line=src[i];
			if(line.isEmpty()||line.charAt(0)=='#') continue;
			if(line.startsWith("mtllib ")){
				int pos=name.lastIndexOf('/');
				String s=(pos!=-1?name.substring(0, pos+1):"");
				mtllib=s+line.substring("mtllib ".length());
				break;
			}
		}
		//read materials
		if(mtllib!=null){
			materials=new HashMap<>();
			String path="models/"+mtllib;
			String[] mtlSrc=getFileLines(path);
			
			if(mtlSrc==null){
				LogUtil.println("Model mtl file", path, "does not exist!");
				return null;
			}
			Material material=null;
			int matId=0;
			
			for(int i=0;i<mtlSrc.length;i++){
				String line=mtlSrc[i];
				if(line.isEmpty()||line.charAt(0)=='#') continue;
				
				if(line.startsWith("newmtl ")){
					material=new Material(matId++, line.substring("newmtl ".length()));
					
					materials.put(material.getName(), material);
					continue;
				}
				if(material!=null){
					String[] segments=line.split(" ");
					float[] numbers=new float[segments.length-1];
					for(int j=1;j<numbers.length;j++){
						numbers[j-1]=Float.parseFloat(segments[j]);
					}
					
					if(line.startsWith("Ns ")) material.setShineDamper(Float.parseFloat(line.split(" ")[1]));
					else if(line.startsWith("Ka ")) material.getAmbient().load(numbers);
					else if(line.startsWith("Kd ")) material.getDiffuse().load(numbers);
					else if(line.startsWith("Ks ")) material.getSpecular().load(numbers);
					else if(line.startsWith("illum ")) material.setIllum(MathUtil.snap(2-numbers[0], 0, 1));
					else if(line.startsWith("jelly ")) material.setJelly(MathUtil.snap(numbers[0], 0, 1));
					
				}
				
			}
			
		}
		ModelData model=new ModelData();
		
		List<Vec3f> vertecies=new ArrayList<>();
		List<Vec2f> uvs=new ArrayList<>();
		List<Vec3f> normals=new ArrayList<>();
		
		List<int[]> idsVert=new ArrayList<>(),idsUv=new ArrayList<>(),idsNorm=new ArrayList<>();
		IntList idsMater=new IntArrayList();
		
		IMaterial material=null;
		
		//read raw data
		for(int i=0;i<src.length;i++){
			String line=src[i];
			if(line.isEmpty()||line.charAt(0)=='#') continue;
			
			if(line.startsWith("v ")){
				String[] parts=line.split(" ");
				vertecies.add(new Vec3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
			}
			else if(line.startsWith("vn ")){
				String[] parts=line.split(" ");
				normals.add(new Vec3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
			}
			else if(line.startsWith("vt ")){
				String[] parts=line.split(" ");
				uvs.add(new Vec2f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
			}
			else if(line.startsWith("usemtl ")){
				material=materials.get(line.substring("usemtl ".length()));
			}
			else if(line.startsWith("f ")){
				
				String[] parts=line.substring(2).split(" ");
				
				int[] faceVert=new int[parts.length],faceUv=new int[parts.length],faceNorm=new int[parts.length];
				boolean hasUv=false,hasNorm=false;
				
				for(int j=0;j<parts.length;j++){
					String segment=parts[j];
					String[] segments=segment.split("/");
					
					faceVert[j]=Integer.parseInt(segments[0])-1;
					if(segments.length==1) continue;
					
					if(segments.length>=2&&!segments[1].isEmpty()){
						faceUv[j]=Integer.parseInt(segments[1])-1;
						hasUv=true;
					}
					if(segments.length>=3){
						faceNorm[j]=Integer.parseInt(segments[2])-1;
						hasNorm=true;
					}
				}
				idsMater.add(material!=null?material.getId():0);
				idsVert.add(faceVert);
				idsUv.add(hasUv?faceUv:null);
				idsNorm.add(hasNorm?faceNorm:null);
			}
		}
		
		boolean hasQuad=idsVert.stream().anyMatch(face->face.length==4),hasTriang=idsVert.stream().anyMatch(face->face.length==3);
		
		if(hasQuad&&hasTriang){
			//mixed quads and triangles! Triangulating required!
			
			hasQuad=false;
			
			List<int[]> idsVert0=idsVert,idsUv0=idsUv,idsNorm0=idsNorm;
			IntList idsMater0=idsMater;
			
			idsMater=new IntArrayList();
			idsVert=new ArrayList<>();
			idsUv=new ArrayList<>();
			idsNorm=new ArrayList<>();
			
			for(int i=0;i<idsVert0.size();i++){
				
				if(idsVert0.get(i).length==3){
					idsVert.add(idsVert0.get(i));
					idsUv.add(idsUv0.get(i));
					idsNorm.add(idsNorm0.get(i));
					idsMater.add(idsMater0.get(i));
				}
				else{
					
					int[] ids=idsVert0.get(i);
					idsVert.add(new int[]{ids[3],ids[0],ids[1]});
					idsVert.add(new int[]{ids[3],ids[1],ids[2]});
					
					ids=idsUv0.get(i);
					if(ids==null){
						idsUv.add(null);
						idsUv.add(null);
					}
					else{
						idsUv.add(new int[]{ids[3],ids[0],ids[1]});
						idsUv.add(new int[]{ids[3],ids[1],ids[2]});
					}
					
					ids=idsNorm0.get(i);
					if(ids==null){
						idsNorm.add(null);
						idsNorm.add(null);
					}
					else{
						idsNorm.add(new int[]{ids[3],ids[0],ids[1]});
						idsNorm.add(new int[]{ids[3],ids[1],ids[2]});
					}
					
					int id=idsMater0.get(i);
					idsMater.add(id);
					idsMater.add(id);
					
				}
			}
		}
		
		//export data to model
		for(int i=0;i<idsVert.size();i++){
			int[] face=idsVert.get(i);
			for(int id:face){
				model.vertecies.add(vertecies.get(id));
			}
			
			if(idsUv.get(i)!=null){
				model.hasUvs=true;
				for(int id:idsUv.get(i)){
					model.uvs.add(uvs.get(id));
				}
			}
			else{
				model.uvs.add(new Vec2f(0, 0));
				model.uvs.add(new Vec2f(1, 0));
				model.uvs.add(new Vec2f(1, 1));
				if(hasQuad) model.uvs.add(new Vec2f(0, 1));
			}
			
			if(idsNorm.get(i)!=null){
				model.hasNormals=true;
				for(int id:idsNorm.get(i)){
					model.normals.add(normals.get(id));
				}
			}
			else{
				model.normals.add(null);
				model.normals.add(null);
				model.normals.add(null);
				if(hasQuad) model.normals.add(null);
			}
			
			int matId=idsMater.getInt(i);
			
			if(matId>0) model.hasMaterials=true;
			
			model.materialIds.add(matId);
			model.materialIds.add(matId);
			model.materialIds.add(matId);
			if(hasQuad) model.materialIds.add(matId);
		}
		model.format=hasQuad?GL11.GL_QUADS:GL11.GL_TRIANGLES;
		
		if(!model.hasMaterials) model.materialIds=null;
		else materials.values().forEach(model.materials::add);
		
		if(!model.hasNormals) model.normals=null;
		if(!model.hasUvs) model.uvs=null;
		
		model.name=name;
		
		return model;
	}
	
	public static ModelData[] loadArr(String name){
		String[] mds=UtilM.getResourceFolderContent("models/"+name, s->s.endsWith(".obj")&&Character.isDigit(s.charAt(0)));
		ModelData[] arr=new ModelData[mds.length];
		for(int i=0;i<arr.length;i++){
			arr[i]=load(name+"/"+mds[i]);
		}
		return arr;
	}
	
	public static Model[] loadAndBuildArr(String name){
		return loadAndBuildArr(Model.class, name);
	}
	
	public static <T extends Model> T[] loadAndBuildArr(Class<T> type, String name){
		String[] mds=UtilM.getResourceFolderContent("models/"+name, s->s.endsWith(".obj")&&Character.isDigit(s.charAt(0)));
		T[] arr=UtilL.array(type, mds.length);
		for(int i=0;i<arr.length;i++){
			arr[i]=loadAndBuild(type, name+"/"+mds[i]);
		}
		return arr;
	}
	
	public static Model loadAndBuild(String name){
		return ModelLoader.buildModel(Model.class, load(name));
	}
	
	public static <T extends Model> T loadAndBuild(Class<T> type, String name){
		return ModelLoader.buildModel(type, load(name));
	}
	
}
