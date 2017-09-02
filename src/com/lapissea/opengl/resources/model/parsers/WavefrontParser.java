package com.lapissea.opengl.resources.model.parsers;

import static com.lapissea.opengl.util.UtilM.*;
import static com.lapissea.util.UtilL.*;
import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lapissea.opengl.resources.model.ModelBuilder;
import com.lapissea.opengl.resources.model.ModelDataBuilder;
import com.lapissea.opengl.resources.model.ModelParser;
import com.lapissea.opengl.resources.model.ModelUtil;
import com.lapissea.opengl.util.Objholder;
import com.lapissea.opengl.util.PairM;
import com.lapissea.opengl.util.math.vec.Vec2f;
import com.lapissea.opengl.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.util.MathUtil;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.impl.assets.Material;

import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntList;

public class WavefrontParser extends ModelParser{
	
	public WavefrontParser(){
		super("obj");
	}
	
	@Override
	public ModelBuilder load(String name, InputStream modelStream) throws IOException{
		
		ModelDataBuilder model=new ModelDataBuilder(name);
		Objholder<IMaterial> activeMaterial=new Objholder<>();
		Objholder<Boolean> hasFlat=new Objholder<>(false);
		Vec3f vec3=new Vec3f();
		Vec2f vec2=new Vec2f();
		
		List<int[]> idsVert=new ArrayList<>();
		List<int[]> idsUv=new ArrayList<>();
		List<int[]> idsNorm=new ArrayList<>();
		//find mtl file pointer
		
		fileLines(modelStream, line->{
			
			switch(line.charAt(0)){
			case '#':
				return;
			case 'v':
				char secondChar=line.charAt(1);
				if(secondChar==' '){
					model.addVertex(vec3.load(line, 2));
					return;
				}else if(secondChar=='n'){
					model.addNormal(vec3.load(line, 3));
					return;
				}else if(secondChar=='t'){
					model.addUv(vec2.load(line, 3));
					return;
				}
			case 'f':
				addIndex(model, line, activeMaterial.obj, idsVert, idsUv, idsNorm);
				return;
			case 's':
				String val=line.substring(2);
				if(val.equals("0")||val.equals("off")||val.equals("false")) hasFlat.obj=true;
			}
			
			if(line.startsWith("mtllib ")){
				loadMlt(model, line, name);
				
			}else if(line.startsWith("usemtl ")){
				String nm=line.substring(7);
				activeMaterial.obj=model.materialDefs.stream().filter(m->m.getName().equals(nm)).findAny().orElse(null);
			}
		});
		
		Map<Integer,Long> faceTypes=idsVert.stream().collect(Collectors.groupingBy(p->p.length, Collectors.counting()));
		if(faceTypes.size()>1){//mixed quads and triangles not allowed!
			
			ModelUtil.triangulateSingleNum(model.materials, idsVert);
			ModelUtil.triangulate(idsVert);
			ModelUtil.triangulate(idsUv);
			ModelUtil.triangulate(idsNorm);
			
			model.format=GL_TRIANGLES;
		}else{
			model.format=faceTypes.keySet().stream().findAny().orElse(-1)==4?GL_QUADS:GL_TRIANGLES;
		}
		
		if(hasFlat.obj){
			uncompress(idsVert, model.vertecies, 3);
			uncompress(idsUv, model.uvs, 2);
			uncompress(idsNorm, model.normals, 3);
		}else{
			idsVert.forEach(face->{
				for(int i:face){
					model.indices.add(i);
				}
			});
			
			compressedToIndexed(idsUv, model.indices, model.uvs);
			compressedToIndexed(idsNorm, model.indices, model.normals);
		}
//		if(model.name.startsWith("icosphere ")){
//			LogUtil.println(model.name);
//		}
		return model.compile();
	}
	
	private void compressedToIndexed(List<int[]> faces, IntList indices, FloatList data){
		data.size(indices.size());
		
	}
	
	private void uncompress(List<int[]> faces, FloatList data, int perPart){
		if(faces.isEmpty()) return;
		
		float[] old=data.toFloatArray();
		data.clear();
		
		for(int[] face:faces){
			for(int id:face){
				for(int i=0;i<perPart;i++){
					data.add(ModelUtil.get(old, perPart, id, i));
				}
			}
		}
	}
	
	private void addIndex(ModelDataBuilder model, String line, IMaterial mat, List<int[]> idsVert, List<int[]> idsUv, List<int[]> idsNorm){
		String[] parts=line.substring(2).split(" ");
		
		int[] faceVert=new int[parts.length],faceUv=new int[parts.length],faceNorm=new int[parts.length];
		boolean hasUv=false,hasNorm=false;
		
		for(int vertexId=0;vertexId<parts.length;vertexId++){
			int id=0,num=-1;
			String vertex=parts[vertexId];
			
			for(int i=0, j=vertex.length();i<j;i++){
				char c=vertex.charAt(i);
				boolean last=i+1==j;
				
				if(last||c=='/'){
					if(last){
						int n=Character.digit(c, 10);
						num=num==-1?n:num*10+n;
					}
					if(num!=-1){
						num--;
						switch(id){
						case 0:
							faceVert[vertexId]=num;
							break;
						case 1:
							faceUv[vertexId]=num;
							hasUv=true;
							break;
						case 2:
							faceNorm[vertexId]=num;
							hasNorm=true;
							break;
						}
						num=-1;
					}
					id++;
				}else{
					int n=Character.digit(c, 10);
					num=num==-1?n:num*10+n;
				}
			}
			
			model.materials.add(mat!=null?mat.getId():0);
		}
		idsVert.add(faceVert);
		if(hasUv) idsUv.add(faceUv);
		if(hasNorm) idsNorm.add(faceNorm);
	}
	
	private void loadMlt(ModelDataBuilder model, String lin, String name){
		String mtllib=lin.substring("mtllib".length()).trim();
		
		String path;
		int pos=name.lastIndexOf('/');
		if(pos==-1) path="models/"+mtllib;
		else path="models/"+name.substring(0, pos+1)+mtllib;
		
		try(InputStream mtlSrc=getResource(path)){
			
			if(mtlSrc==null){
				//LogUtil.printlnEr("Model mtl file", path, "does not exist!");
				return;
			}
			
			PairM<Material,Integer> mat=new PairM<>(null, 0);
			
			fileLines(mtlSrc, line->{
				if(line.isEmpty()) return;
				
				char firstChar=line.charAt(0);
				if(firstChar=='#') return;
				
				if(line.startsWith("newmtl ")){
					model.materialDefs.add(mat.obj1=new Material(mat.obj2++, line.substring("newmtl ".length())));
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
			
		}catch(IOException e){}
	}
	
	private void loadLighting(Material material, String line){
		// @formatter:off
		switch(line.charAt(1)){
		case 'a':material.getAmbient(). load(line, 3);break;
		case 'd':material.getDiffuse(). load(line, 3);break;
		case 's':material.getSpecular().load(line, 3);break;
		case 'e':material.getEmission().load(line, 3);break;
		}
		// @formatter:on
	}
	
}
