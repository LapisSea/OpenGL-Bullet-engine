package com.lapissea.opengl.program.rendering.gl.shader.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.light.DirLight;
import com.lapissea.opengl.program.rendering.gl.shader.light.LineLight;
import com.lapissea.opengl.program.rendering.gl.shader.light.PointLight;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.ints.UniformInt1;
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
	UniformFloat4[]	pointLightColor;
	UniformFloat3[]	pointLightAttenuation;
	UniformInt1		numberOfPointLights;
	
	UniformFloat3[]	lineLightPos1;
	UniformFloat3[]	lineLightPos2;
	UniformFloat4[]	lineLightColor;
	UniformFloat3[]	lineLightAttenuation;
	UniformInt1		numberOfLineLights;
	
	UniformFloat3[]	dirLightDirection;
	UniformFloat4[]	dirLightColor;
	UniformInt1		numberOfDirLights;
	
	protected UniformFloat1 minBrightness;
	
	protected static final Vec3f UNIT_VEC=new Vec3f();
	
	public static int	MAX_POINT_LIGHT	=2;
	public static int	MAX_LINE_LIGHT	=2;
	public static int	MAX_DIR_LIGHT	=2;
	
	private static final List<PointLight>	POINTS	=new LimitedList<PointLight>(MAX_POINT_LIGHT);
	private static final List<LineLight>	LINES	=new LimitedList<LineLight>(MAX_LINE_LIGHT);
	private static final List<DirLight>		DIRS	=new LimitedList<>(MAX_DIR_LIGHT);
	
	public ShaderModuleLight(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		//FS
		pointLightColor=getUniformArray(UniformFloat4.class, i->"pointLights["+i+"].color");
		pointLightAttenuation=getUniformArray(UniformFloat3.class, i->"pointLights["+i+"].attenuation");
		pointLightPos=getUniformArray(UniformFloat3.class, i->"pointLights["+i+"].pos");
		
		numberOfPointLights=getUniform(UniformInt1.class, "numberOfPointLights");
		
		lineLightColor=getUniformArray(UniformFloat4.class, i->"lineLights["+i+"].color");
		lineLightAttenuation=getUniformArray(UniformFloat3.class, i->"lineLights["+i+"].attenuation");
		lineLightPos1=getUniformArray(UniformFloat3.class, i->"lineLights["+i+"].pos1");
		lineLightPos2=getUniformArray(UniformFloat3.class, i->"lineLights["+i+"].pos2");
		
		numberOfLineLights=getUniform(UniformInt1.class, "numberOfLineLights");
		
		dirLightColor=getUniformArray(UniformFloat4.class, i->"dirLights["+i+"].color");
		dirLightDirection=getUniformArray(UniformFloat3.class, i->"dirLights["+i+"].direction");
		
		numberOfDirLights=getUniform(UniformInt1.class, "numberOfDirLights");
		minBrightness=getUniform(UniformFloat1.class, "minBrightness");
		
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
			light.dir.normalise(UNIT_VEC);
			dirLightDirection[i].upload(UNIT_VEC);
		}
	}
	
}
