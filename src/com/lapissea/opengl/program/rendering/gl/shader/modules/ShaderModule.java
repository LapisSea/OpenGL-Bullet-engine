package com.lapissea.opengl.program.rendering.gl.shader.modules;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

public abstract class ShaderModule{
	
	private static final Map<String,ModuleEntry> MODULES=new HashMap<>();
	
	private static class ModuleEntry{
		
		final Constructor<? extends ShaderModule>	constr;
		final Map<String,String>					args=new HashMap<>();
		final ShaderModuleSrcLoader					loader;
		
		ModuleEntry(Class<? extends ShaderModule> module, ShaderModuleSrcLoader loader, String[] args) throws Exception{
			this.constr=module.getDeclaredConstructor(Shader.class);
			this.loader=loader;
			for(int i=0;i<args.length;i+=2){
				this.args.put(args[i], args[i+1]);
			}
		}
		
		ShaderModule newMod(Shader shader) throws Exception{
			return constr.newInstance(shader);
		}
	}
	
	public static class ShaderModuleSrcLoader{
		
		protected final String name;
		
		public ShaderModuleSrcLoader(String name){
			this.name=name;
		}
		
		public String load(boolean isFragment, String[] args){
			return UtilM.getTxtResource("shaders/modules/"+name+(isFragment?".fsmd":".vsmd"));
		}
	}
	
	public static void register(){
		registerModule("Light", ShaderModuleLight.class, null, "MAX_POINT_LIGHT", String.valueOf(ShaderModuleLight.MAX_POINT_LIGHT), "MAX_DIR_LIGHT", String.valueOf(ShaderModuleLight.MAX_DIR_LIGHT), "MAX_LINE_LIGHT", String.valueOf(ShaderModuleLight.MAX_LINE_LIGHT));
		registerModule("Fog", ShaderModuleFog.class, null);
		registerModule("Material", ShaderModuleMaterial.class, null, "MATERIAL_MAX_COUNT", String.valueOf(ShaderModuleMaterial.MATERIAL_MAX_COUNT));
		registerModule("Texture", ShaderModuleTexture.class, new ShaderModuleTexture.Loader());
		registerModule("ScreenSize", ShaderModuleScreenSize.class, null);
		registerModule("MousePosition", ShaderModuleMousePosition.class, null);
	}
	
	/**
	 * @param name = name that matches the string inside of shader module.<br>
	 * Example:<br>
	 * //shader<br>
	 * #include "FooBar"<br>
	 * module name = FooBar
	 * 
	 * @param module = module class whos instance handles the shader module uniforms and such.<br>
	 * Example:<br>
	 * A module contains "uniform vec3 fooUni;" the module instance should load uniform "fooUni" with type {@link UniformFloat3} and upload desired value to it.
	 * 
	 * @param args = "value1 name", "value1", "value2 name", "value2"....
	 */
	public static void registerModule(String name, Class<? extends ShaderModule> module, ShaderModuleSrcLoader loader, String...args){
		if(args==null) args=new String[0];
		else if(args.length%2!=0) throw new IllegalArgumentException("Invalid number of arguments!");
		try{
			MODULES.put(name, new ModuleEntry(module, loader, args));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static Map<String,String> getArgs(String name){
		ModuleEntry module=MODULES.get(name);
		if(module!=null) return new HashMap<>(module.args);
		return new HashMap<>();
	}
	
	public static ShaderModule getNew(String name, Shader sh){
		try{
			ModuleEntry module=MODULES.get(name);
			if(module!=null) return module.newMod(sh);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static ShaderModuleSrcLoader getLoader(String name){
		try{
			ModuleEntry module=MODULES.get(name);
			if(module!=null) return module.loader;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	protected final Shader parent;
	
	public ShaderModule(Shader parent){
		this.parent=parent;
	}
	
	public interface Global{
		
		void uploadUniformsGlobal();
	}
	
	public interface Instance{
		
		void uploadUniformsInstance(ModelTransformed instance);
	}
	
	public interface ModelUniforms{
		
		void uploadUniformsModel(IModel model);
	}
	
	public void setUpUniforms(){}
	
	public void bindAttributes(){}
	
	public <T extends AbstractUniform> T[] getUniformArray(Class<T> uniformType, String name){
		return parent.getUniformArray(uniformType, name);
	}
	
	public <T extends AbstractUniform> T[] getUniformArray(Class<T> uniformType, Function<Integer,String> name){
		return parent.getUniformArray(uniformType, name);
	}
	
	protected <T extends AbstractUniform> T getUniform(Class<T> uniformType, String name){
		return parent.getUniform(uniformType, name);
	}
	
	public void bindAttribute(ModelAttribute attr){
		parent.bindAttribute(attr);
	}
	
	protected void bindAttribute(ModelAttribute attr, String name){
		parent.bindAttribute(attr, name);
	}
	
	protected void bindAttribute(int attr, String name){
		parent.bindAttribute(attr, name);
	}
	
}
