package com.lapissea.opengl.program.rendering.gl.model;

import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.lapissea.opengl.program.util.Objholder;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.MathUtil;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.impl.assets.Material;
import com.lapissea.opengl.window.impl.assets.Model;
import com.lapissea.util.LogUtil;
import com.lapissea.util.UtilL;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class ObjModelLoader{
	
	public static class ModelData{
		
		public String name;
		
		public List<Vec3f>		vertecies	=new ArrayList<>();
		public List<Vec2f>		uvs			=new ArrayList<>();
		public IntList			materialIds	=new IntArrayList();
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
		
		public int[] getMat(){
			return hasMaterials?materialIds.toIntArray():null;
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
	
	public static ModelData load(String name){
		name=name.replace('\\', '/');
		LogUtil.println("Loading model:", name);
		
//		name=name.replaceAll("/+", "/");
		if(name.startsWith("/")) name=name.substring(1, name.length());
		
		try(InputStream modelStream=UtilM.getResource("models/"+name+(name.endsWith(".obj")?"":".obj"))){
			
			if(modelStream==null){
				LogUtil.println("Model", name, "does not exist!");
				return null;
			}
			return load(name, modelStream);
		}catch(IOException e){
			throw new IllegalStateException(e);
		}
	}
	
	public static ModelData load(String name, InputStream modelStream) throws IOException{
		
		ModelData model=new ModelData();
		
		List<Vec3f> vertecies=new ArrayList<>();
		List<Vec2f> uvs=new ArrayList<>();
		List<Vec3f> normals=new ArrayList<>();
		
		List<int[]> idsVert=new ArrayList<>(),idsUv=new ArrayList<>(),idsNorm=new ArrayList<>();
		IntList idsMater=new IntArrayList();
		
		Objholder<IMaterial> material=new Objholder<>();
		
		//find mtl file pointer
		lines(modelStream, line->{
			
			if(line.isEmpty()||line.charAt(0)=='#') return;
			if(line.startsWith("mtllib ")){
				try{
					loadMlt(model.materials::add, line, name);
				}catch(IOException e){
					e.printStackTrace();
				}
				return;
			}
			switch(line.charAt(0)){
			case 'v':
				char secondChar=line.charAt(1);
				if(secondChar==' '){
					vertecies.add(new Vec3f(line, 2));
					break;
				}else if(secondChar=='n'){
					normals.add(new Vec3f(line, 3));
					break;
				}else if(secondChar=='t'){
					uvs.add(new Vec2f(line, 3));
					break;
				}
			case 'f':{
				
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
				idsMater.add(material.obj!=null?material.obj.getId():0);
				idsVert.add(faceVert);
				idsUv.add(hasUv?faceUv:null);
				idsNorm.add(hasNorm?faceNorm:null);
			}
			break;
			default:{
				if(line.startsWith("usemtl ")){
					String nm=line.substring(7);
					material.obj=model.materials.stream().filter(m->m.getName().equals(nm)).findAny().orElse(null);
				}
			}
			break;
			}
		});
		
		boolean hasQuad=idsVert.stream().anyMatch(face->face.length==4),hasTriang=idsVert.stream().anyMatch(face->face.length==3);
		
		if(hasQuad&&hasTriang){
			//mixed quads and triangles! Triangulating required!
			
			hasQuad=false;
			
			List<int[]> idsVert0=new ArrayList<>(idsVert),idsUv0=new ArrayList<>(idsUv),idsNorm0=new ArrayList<>(idsNorm);
			IntList idsMater0=new IntArrayList(idsMater);
			
			idsMater.clear();
			idsVert.clear();
			idsUv.clear();
			idsNorm.clear();
			
			for(int i=0;i<idsVert0.size();i++){
				
				if(idsVert0.get(i).length==3){
					idsVert.add(idsVert0.get(i));
					idsUv.add(idsUv0.get(i));
					idsNorm.add(idsNorm0.get(i));
					idsMater.add(idsMater0.get(i));
				}else{
					
					int[] ids=idsVert0.get(i);
					idsVert.add(new int[]{ids[3],ids[0],ids[1]});
					idsVert.add(new int[]{ids[3],ids[1],ids[2]});
					
					ids=idsUv0.get(i);
					if(ids==null){
						idsUv.add(null);
						idsUv.add(null);
					}else{
						idsUv.add(new int[]{ids[3],ids[0],ids[1]});
						idsUv.add(new int[]{ids[3],ids[1],ids[2]});
					}
					
					ids=idsNorm0.get(i);
					if(ids==null){
						idsNorm.add(null);
						idsNorm.add(null);
					}else{
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
			}else{
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
			}else{
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
		model.format=hasQuad?GL_QUADS:GL_TRIANGLES;
		
		if(!model.hasMaterials)model.materialIds=null;
		
		if(!model.hasNormals) model.normals=null;
		if(!model.hasUvs) model.uvs=null;
		
		model.name=name;
		
		return model;
	}
	
	private static void lines(InputStream steam, Consumer<String> cons) throws IOException{
		StringBuilder lineBuild=new StringBuilder();
		int ch;
		
		while(true){
			String line;
			while(true){
				ch=steam.read();
				if(ch==-1){
					line=lineBuild.toString();
					lineBuild.setLength(0);
					break;
				}
				if(ch=='\n'){
					ch=steam.read();
					line=lineBuild.toString();
					lineBuild.setLength(0);
					if(ch!='\r'&&ch!='\n') lineBuild.append((char)ch);
					break;
				}
				lineBuild.append((char)ch);
			}
			cons.accept(line);
			
			if(ch==-1) return;
		}
	}
	
	private static void loadMlt(Consumer<Material> newMat, String lin, String name) throws IOException{
		String mtllib=lin.substring("mtllib".length()).trim();
		
		String path;
		int pos=name.lastIndexOf('/');
		if(pos==-1) path="models/"+mtllib;
		else path="models/"+name.substring(0, pos+1)+mtllib;
		
		try(InputStream mtlSrc=UtilM.getResource(path)){
			
			if(mtlSrc==null){
				LogUtil.printlnEr("Model mtl file", path, "does not exist!");
				return;
			}
			
			PairM<Material,Integer> mat=new PairM<>(null, 0);
			
			lines(mtlSrc, line->{
				if(line.isEmpty()) return;
				
				char firstChar=line.charAt(0);
				if(firstChar=='#') return;
				
				if(line.startsWith("newmtl ")){
					newMat.accept(mat.obj1=new Material(mat.obj2++, line.substring("newmtl ".length())));
					return;
				}
				Material mater=mat.obj1;
				
				if(mater!=null){
					
					if(firstChar=='K') loadLighting(mater, line);
					else if(firstChar=='d') mater.getDiffuse().a(Float.parseFloat(line.substring(2)));
					else if(firstChar=='N'){
						if(line.charAt(1)=='s') mater.setShineDamper(Float.parseFloat(line.substring(2)));
					}else if(line.startsWith("jelly ")) mater.setJelly(MathUtil.snap(Float.parseFloat(line.substring("jelly ".length())), 0, 1));
					
				}
			});
			
		}
	}
	
	private static void loadLighting(Material material, String line){
		// @formatter:off
		switch(line.charAt(1)){
		case 'a':material.getAmbient(). load(line, 3);break;
		case 'd':material.getDiffuse(). load(line, 3);break;
		case 's':material.getSpecular().load(line, 3);break;
		case 'e':material.getEmission().load(line, 3);break;
		}
		// @formatter:on
	}
	
	public static ModelData[] loadArr(String name){
		String[] mds=UtilM.getResourceFolderContent("models/"+name, s->s.endsWith(".obj")&&Character.isDigit(s.charAt(0)));
		ModelData[] arr=new ModelData[mds.length];
		for(int i=0;i<arr.length;i++){
			arr[i]=load(name+"/"+mds[i]);
		}
		return arr;
	}
	
	public static IModel[] loadAndBuildArr(String name){
		return loadAndBuildArr(Model.class, name);
	}
	
	public static <T extends IModel> T[] loadAndBuildArr(Class<T> type, String name){
		String[] mds=UtilM.getResourceFolderContent("models/"+name, s->s.endsWith(".obj")&&Character.isDigit(s.charAt(0)));
		T[] arr=UtilL.array(type, mds.length);
		for(int i=0;i<arr.length;i++){
			arr[i]=loadAndBuild(type, name+"/"+mds[i]);
		}
		return arr;
	}
	
	public static IModel loadAndBuild(String name){
		return ModelLoader.buildModel(Model.class, load(name));
	}
	
	public static <T extends IModel> T loadAndBuild(Class<T> type, String name){
		return ModelLoader.buildModel(type, load(name));
	}
	
}
