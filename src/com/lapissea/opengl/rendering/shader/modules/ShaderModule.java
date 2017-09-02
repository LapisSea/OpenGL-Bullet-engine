package com.lapissea.opengl.rendering.shader.modules;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.lapissea.opengl.rendering.ModelTransformed;
import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.util.UtilM;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

public abstract class ShaderModule{
	
	private static final Map<String,ModuleEntry> MODULES=new HashMap<>();
	
	private static class ModuleEntry{
		
		final Constructor<? extends ShaderModule>	constr;
		final ShaderModuleSrcLoader					loader;
		
		ModuleEntry(Class<? extends ShaderModule> module, ShaderModuleSrcLoader loader) throws Exception{
			constr=module.getDeclaredConstructor(Shader.class);
			this.loader=loader;
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
		
		public String load(String extension, String[] args){
			return UtilM.getTxtResource("shaders/modules/"+name+extension);
		}
	}
	
	public static void register(){
		registerModule("Light", ShaderModuleLight.class);
		registerModule("Fog", ShaderModuleFog.class);
		registerModule("Material", ShaderModuleMaterial.class);
		registerModule("Texture", ShaderModuleTexture.class, new ShaderModuleTexture.Loader());
		registerModule("ScreenSize", ShaderModuleScreenSize.class, new ShaderModuleGlobalUniform.Loader("vec2", "screenSize"));
		registerModule("MousePosition", ShaderModuleMousePosition.class, new ShaderModuleGlobalUniform.Loader("vec2", "mousePos"));
		registerModule("Time", ShaderModuleTime.class, new ShaderModuleTime.Loader());
		registerModule("ArrayList", ShaderModuleArrayList.class, new ShaderModuleArrayList.Loader());
		registerModule("Armature", ShaderModuleArmature.class);
	}
	
	public static void registerModule(String name, Class<? extends ShaderModule> module){
		registerModule(name, module, null);
	}
	
	/**
	 * @param name
	 *            = name that matches the string inside of shader module.<br>
	 *            Example:<br>
	 *            //shader<br>
	 *            #include "FooBar"<br>
	 *            module name = FooBar
	 * 
	 * @param module
	 *            = module class whos instance handles the shader module
	 *            uniforms and such.<br>
	 *            Example:<br>
	 *            A module contains "uniform vec3 fooUni;" the module instance
	 *            should load uniform "fooUni" with type {@link UniformFloat3}
	 *            and upload desired value to it.
	 * 
	 * @param args
	 *            = "value1 name", "value1", "value2 name", "value2"....
	 */
	public static void registerModule(String name, Class<? extends ShaderModule> module, ShaderModuleSrcLoader loader){
		try{
			MODULES.put(name, new ModuleEntry(module, loader));
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	public interface ModelMdl{
		
		void uploadUniformsModel(IModel model);
	}
	
	public void setUpUniforms(){}
	
	public void bindAttributes(){}
	
	public <T extends AbstractUniform> T[] getUniformArray(String name){
		return parent.getUniformArray(name);
	}
	
	public <T extends AbstractUniform> T[] getUniformArray(Function<Integer,String> name){
		return parent.getUniformArray(name);
	}
	
	protected <T extends AbstractUniform> T getUniform(String name){
		return parent.getUniform(name);
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
	
	public Map<String,String> getCompileValues(){
		return null;
	}
	
}
