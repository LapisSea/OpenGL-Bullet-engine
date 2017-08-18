package com.lapissea.opengl.program.rendering.gl.shader.modules;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.Global;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;

public class ShaderModuleTime extends ShaderModule implements Global{
	
	public static class Loader extends ShaderModuleSrcLoader{
		
		public Loader(){
			super("Time");
		}
		
		@Override
		public String load(String extension, String[] args){
			return "\nuniform float worldTime;\nuniform float systemTime;\n";
		}
	}
	
	protected UniformFloat1	worldTime;
	protected UniformFloat1	systemTime;
	
	public ShaderModuleTime(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		worldTime=getUniform("worldTime");
		systemTime=getUniform("systemTime");
	}
	
	@Override
	public void uploadUniformsGlobal(){
		if(worldTime!=null) worldTime.upload((float)((Game.get().world.time()+(double)Game.getPartialTicks())/Game.get().timer.getUps()));
		
		if(systemTime!=null) systemTime.upload((float)(System.currentTimeMillis()/1000D));
		
	}
	
}
