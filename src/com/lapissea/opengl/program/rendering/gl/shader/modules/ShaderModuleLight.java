package com.lapissea.opengl.program.rendering.gl.shader.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.Renderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.light.DirectionalLight;
import com.lapissea.opengl.program.rendering.gl.shader.light.PointLight;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.ints.UniformInt1;
import com.lapissea.opengl.program.util.math.vec.Vec3f;

public class ShaderModuleLight extends ShaderModule implements Global{
	
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
	
	UniformFloat3[]	dirLightDirection;
	UniformFloat4[]	dirLightColor;
	UniformInt1		numberOfDirLights;
	
	protected UniformFloat1 minBrightness;
	
	protected static final Vec3f UNIT_VEC=new Vec3f();
	
	public static final int	MAX_POINT_LIGHT	=25;
	public static final int	MAX_DIR_LIGHT	=5;
	
	private static final List<PointLight>		POINTS	=new LimitedList<PointLight>(MAX_POINT_LIGHT);
	private static final List<DirectionalLight>	DIRS	=new LimitedList<>(MAX_DIR_LIGHT);
	
	public ShaderModuleLight(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		//VS
		pointLightPos=getUniformArray(UniformFloat3.class, "pointLightPos");
		numberOfPointLights=getUniform(UniformInt1.class, "numberOfPointLights");
		
		//FS
		pointLightColor=getUniformArray(UniformFloat4.class, i->"pointLights["+i+"].color");
		pointLightAttenuation=getUniformArray(UniformFloat3.class, i->"pointLights["+i+"].attenuation");
		
		dirLightColor=getUniformArray(UniformFloat4.class, i->"dirLights["+i+"].color");
		dirLightDirection=getUniformArray(UniformFloat3.class, i->"dirLights["+i+"].direction");
		
		numberOfDirLights=getUniform(UniformInt1.class, "numberOfDirLights");
		minBrightness=getUniform(UniformFloat1.class, "minBrightness");
		
	}
	
	@Override
	public void uploadUniformsGlobal(){
		Renderer r=Game.get().renderer;
		POINTS.clear();
		DIRS.clear();
		
		if(r==null) return;
		
		int ps=r.pointLights.size(),ds=r.dirLights.size();
		
		if(ps>0){
			Stream<PointLight> s=r.pointLights.stream().filter(light->light.color.a()>0);
			if(r.dirLights.size()>MAX_POINT_LIGHT) s.sorted((l1, l2)->Float.compare(l1.color.a(), l2.color.a()));
			s.forEach(POINTS::add);
		}
		
		if(ds>0){
			Stream<DirectionalLight> s=r.dirLights.stream().filter(light->light.color.a()>0);
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
		
		numberOfDirLights.upload(DIRS.size());
		for(int i=0;i<DIRS.size();i++){
			DirectionalLight light=DIRS.get(i);
			dirLightColor[i].upload(light.color);
			light.dir.normalise(UNIT_VEC);
			dirLightDirection[i].upload(UNIT_VEC);
		}
	}
	
}
