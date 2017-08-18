package com.lapissea.opengl.program.rendering.shader.modules;

import static com.lapissea.opengl.program.rendering.shader.modules.ShaderModuleArrayList.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.Renderer;
import com.lapissea.opengl.program.rendering.shader.Shader;
import com.lapissea.opengl.program.rendering.shader.light.DirLight;
import com.lapissea.opengl.program.rendering.shader.light.LineLight;
import com.lapissea.opengl.program.rendering.shader.light.PointLight;
import com.lapissea.opengl.program.rendering.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.program.rendering.shader.uniforms.ints.UniformInt1;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class ShaderModuleLight extends ShaderModule implements ShaderModule.Global{
	
	private static final class LimitedList<T>extends ArrayList<T>{
		
		private static final long serialVersionUID=-2196130503622087282L;
		
		final int max;
		
		public LimitedList(int max){
			super(max);
			this.max=max;
		}
		
		@Override
		public boolean add(T e){
			return size()<max&&super.add(e);
		}
		
	}
	
	UniformFloat3[]	pointLightPos;
	UniformFloat3[]	pointLightColor;
	UniformFloat3[]	pointLightAttenuation;
	UniformInt1		numberOfPointLights;
	
	UniformFloat3[]	lineLightPos1;
	UniformFloat3[]	lineLightPos2;
	UniformFloat3[]	lineLightColor;
	UniformFloat3[]	lineLightAttenuation;
	UniformInt1		numberOfLineLights;
	
	UniformFloat3[]	dirLightDirection;
	UniformFloat3[]	dirLightColor;
	UniformFloat3[]	dirLightAmbientColor;
	UniformInt1		numberOfDirLights;
	
	protected UniformFloat1 minBrightness;
	
	protected static final Vec3f UNIT_VEC=new Vec3f();
	
	public static int	MAX_POINT_LIGHT	=6;
	public static int	MAX_LINE_LIGHT	=2;
	public static int	MAX_DIR_LIGHT	=2;
	
	private static final List<PointLight>	POINTS	=new LimitedList<>(MAX_POINT_LIGHT);
	private static final List<LineLight>	LINES	=new LimitedList<>(MAX_LINE_LIGHT);
	private static final List<DirLight>		DIRS	=new LimitedList<>(MAX_DIR_LIGHT);
	
	public ShaderModuleLight(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		//FS
		pointLightColor=getUniformArray(arrayList("pointLights", "color"));
		pointLightAttenuation=getUniformArray(arrayList("pointLights", "attenuation"));
		pointLightPos=getUniformArray(arrayList("pointLights", "pos"));
		numberOfPointLights=getUniform(arrayListSize("pointLights"));
		
		lineLightColor=getUniformArray(arrayList("lineLights", "color"));
		lineLightAttenuation=getUniformArray(arrayList("lineLights", "attenuation"));
		lineLightPos1=getUniformArray(arrayList("lineLights", "pos1"));
		lineLightPos2=getUniformArray(arrayList("lineLights", "pos2"));
		numberOfLineLights=getUniform(arrayListSize("lineLights"));
		
		dirLightColor=getUniformArray(arrayList("dirLights", "color"));
		dirLightDirection=getUniformArray(arrayList("dirLights", "direction"));
		dirLightAmbientColor=getUniformArray(arrayList("dirLights", "ambient"));
		numberOfDirLights=getUniform(arrayListSize("dirLights"));
		
		minBrightness=getUniform("minBrightness");
		
	}
	
	@Override
	public void uploadUniformsGlobal(){
		Renderer r=Game.get().renderer;
		POINTS.clear();
		LINES.clear();
		DIRS.clear();
		
		if(r==null) return;
		
		int ps=r.pointLights.size(),ds=r.dirLights.size(),ls=r.lineLights.size();
		
		if(ps>0){
			Stream<PointLight> s=r.pointLights.stream().filter(light->light.color.a()>0);
			if(r.dirLights.size()>MAX_POINT_LIGHT) s.sorted((l1, l2)->Float.compare(l1.color.a(), l2.color.a()));
			s.forEach(POINTS::add);
		}
		if(ls>0){
			Stream<LineLight> s=r.lineLights.stream().filter(light->light.color.a()>0);
			if(r.dirLights.size()>MAX_LINE_LIGHT) s.sorted((l1, l2)->Float.compare(l1.color.a(), l2.color.a()));
			s.forEach(LINES::add);
		}
		
		if(ds>0){
			Stream<DirLight> s=r.dirLights.stream().filter(light->light.color.a()>0);
			if(r.dirLights.size()>MAX_DIR_LIGHT) s.sorted((l1, l2)->Float.compare(l1.color.a(), l2.color.a()));
			s.forEach(DIRS::add);
		}
		numberOfPointLights.upload(POINTS.size());
		for(int i=0;i<POINTS.size();i++){
			PointLight light=POINTS.get(i);
			pointLightPos[i].upload(light.pos);
			pointLightColor[i].upload(light.color);
			pointLightAttenuation[i].upload(light.attenuation);
		}
		numberOfLineLights.upload(LINES.size());
		for(int i=0;i<LINES.size();i++){
			LineLight light=LINES.get(i);
			lineLightPos1[i].upload(light.pos1);
			lineLightPos2[i].upload(light.pos2);
			lineLightColor[i].upload(light.color);
			lineLightAttenuation[i].upload(light.attenuation);
		}
		
		numberOfDirLights.upload(DIRS.size());
		for(int i=0;i<DIRS.size();i++){
			DirLight light=DIRS.get(i);
			dirLightColor[i].upload(light.color);
			dirLightAmbientColor[i].upload(light.ambient);
			light.dir.normalise(UNIT_VEC);
			dirLightDirection[i].upload(UNIT_VEC);
		}
	}
	
	@Override
	public Map<String,String> getCompileValues(){
		Map<String,String> map=new HashMap<>();
		map.put("MAX_POINT_LIGHT", ""+MAX_POINT_LIGHT);
		map.put("MAX_LINE_LIGHT", ""+MAX_LINE_LIGHT);
		map.put("MAX_DIR_LIGHT", ""+MAX_DIR_LIGHT);
		return map;
	}
	
}
