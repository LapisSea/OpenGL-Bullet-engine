package com.lapissea.opengl.program.rendering.gl.shader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.MainOGL;
import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.model.ModelAttribute;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.ShaderModuleSrcLoader;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.UtilM;

public abstract class Shader{
	
	private static final String IMPORT_MARK="#include";
	
	public int			program,vertex,fragment;
	private boolean		bound,loaded;
	public final String	name;
	
	protected UniformMat4	transformMat;
	protected UniformMat4	projectionMat;
	protected UniformMat4	viewMat;
	protected UniformFloat1	shineDamper;
	protected UniformFloat1	reflectivity;
	protected UniformFloat1	lightTroughput;
	
	protected List<ShaderModule>				modules				=new ArrayList<>();
	protected List<ShaderModule.Global>			modulesGlobal		=new ArrayList<>();
	protected List<ShaderModule.Instance>		modulesInstance		=new ArrayList<>();
	protected List<ShaderModule.ModelUniforms>	modulesModelUniforms=new ArrayList<>();
	
	public Shader(){
		this.name=getClass().getSimpleName().replace("Shader", "").toLowerCase();
		if(name.isEmpty()) throw new IllegalStateException("You can't call a shader \"Shader\"!");
		load();
	}
	
	public Shader(String name){
		if(name.isEmpty()) throw new IllegalStateException("Empty shader names are not allowed!");
		this.name=name;
		load();
	}
	
	protected Map<String,String> getCompileValues(){
		Map<String,String> values=new HashMap<>();
		return values;
	}
	
	protected String getVsSrc(){
		return UtilM.getTxtResource("shaders/"+name+".vs");
	}
	
	protected String getFsSrc(){
		return UtilM.getTxtResource("shaders/"+name+".fs");
	}
	
	public void load(){
		loaded=false;
		modules.clear();
		modulesGlobal.clear();
		modulesInstance.clear();
		modulesModelUniforms.clear();
		List<String> allModules=new ArrayList<>();
		loadShader(false, allModules);
		loadShader(true, allModules);
	}
	
	private void setUp(List<String> allModules){
		if(vertex!=-1&&fragment!=-1){
			allModules.forEach(md->{
				ShaderModule module=ShaderModule.getNew(md, this);
				if(module!=null){
					modules.add(module);
					if(module instanceof ShaderModule.Global) modulesGlobal.add((ShaderModule.Global)module);
					if(module instanceof ShaderModule.Instance) modulesInstance.add((ShaderModule.Instance)module);
					if(module instanceof ShaderModule.ModelUniforms) modulesModelUniforms.add((ShaderModule.ModelUniforms)module);
				}
			});
			
			program=GL20.glCreateProgram();
			GL20.glAttachShader(program, vertex);
			GL20.glAttachShader(program, fragment);
			
			bindAttributes();
			modules.forEach(ShaderModule::bindAttributes);
			
			GL20.glLinkProgram(program);
			GL20.glValidateProgram(program);
			setUpUniforms();
			modules.forEach(ShaderModule::setUpUniforms);
			
			loaded=true;
			
			LogUtil.println("Loaded shader:", name);
		}
		else vertex=fragment=0;
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
	public void uploadTransformMat(Matrix4f mat){
		transformMat.upload(mat);
	}
	
	public void uploadProjectionAndViewMat(Matrix4f project, Matrix4f view){
		viewMat.upload(view);
		projectionMat.upload(project);
	}
	
	protected void setUpUniforms(){
		transformMat=getUniform(UniformMat4.class, "transformMat");
		projectionMat=getUniform(UniformMat4.class, "projectionMat");
		viewMat=getUniform(UniformMat4.class, "viewMat");
	}
	
	protected int getUniformId(String name){
		return GL20.glGetUniformLocation(program, name);
	}
	
	public <T extends AbstractUniform> T[] getUniformArray(Class<T> uniformType, String name){
		return getUniformArray(uniformType, i->name+"["+i+"]");
	}
	
	public <T extends AbstractUniform> T[] getUniformArray(Class<T> uniformType, Function<Integer,String> name){
		
		List<T> unis=new ArrayList<>();
		T unif;
		
		while((unif=getUniform(uniformType, name.apply(unis.size())))!=null){
			unis.add(unif);
		}
		
		return unis.isEmpty()?null:unis.toArray(UtilM.newArray(uniformType, unis.size()));
	}
	
	public <T extends AbstractUniform> T getUniform(Class<T> uniformType, String name){
		GLUtil.checkError();
		try{
			int id=getUniformId(name);
			GLUtil.checkError();
			//LogUtil.println(id, name);
			if(id==-1) return null;
			return uniformType.getDeclaredConstructor(Shader.class, int.class, String.class).newInstance(this, id, name);
		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(1);
		return null;
	}
	
	public void bind(){
		if(isBound()) return;
		bound=true;
		GL20.glUseProgram(program);
	}
	
	public void unbind(){
		if(!isBound()) return;
		bound=false;
		GL20.glUseProgram(0);
	}
	
	public boolean isBound(){
		return bound;
	}
	
	public void delete(){
		unbind();
		if(program==0) return;
		GL20.glDetachShader(program, vertex);
		GL20.glDetachShader(program, fragment);
		GL20.glDeleteShader(vertex);
		GL20.glDeleteShader(fragment);
		GL20.glDeleteProgram(program);
	}
	
	protected abstract void bindAttributes();
	
	public void bindAttribute(ModelAttribute attr){
		bindAttribute(attr, attr.defaultShaderName);
	}
	
	public void bindAttribute(ModelAttribute attr, String name){
		bindAttribute(attr.id, name);
	}
	
	public void bindAttribute(int attr, String name){
		GLUtil.checkError();
		GL20.glBindAttribLocation(program, attr, name);
		GLUtil.checkError();
	}
	
	public void bindingProttect(){
		if(!isLoaded()) throw new IllegalStateException("Shader "+name+" is not loaded!");
		if(!isBound()) throw new IllegalStateException("Shader "+name+" is not bound!");
	}
	
	private void loadShader(boolean isFragment, List<String> allModules){
		List<String> modules=new ArrayList<>();
		String src=processShaderFile(isFragment?getFsSrc():getVsSrc(), name, isFragment, modules);
		if(src==null){
			LogUtil.println("Failed to load", name, "shader!");
			System.exit(1);
		}
		
		modules.forEach(md->{
			if(!allModules.contains(md)) allModules.add(md);
		});
		
		Map<String,String> values=getCompileValues();
		
		allModules.forEach(moduleName->values.putAll(ShaderModule.getArgs(moduleName)));
		
		for(String valueName:values.keySet()){
			src=src.replace("\"<"+valueName+">\"", values.get(valueName));
		}
		
		final String srcFinal=src;
		
		if(MainOGL.DEV_ENV) try{
			Files.write(new File("res/shaders/compiled output/compiled_"+name+(isFragment?".fs":".vs")).toPath(), srcFinal.getBytes());
		}catch(IOException e){
			e.printStackTrace();
		}
		
		Game.glCtx(()->{
			synchronized(this){
				GL20.glUseProgram(0);
				int shaderID=GL20.glCreateShader(isFragment?GL20.GL_FRAGMENT_SHADER:GL20.GL_VERTEX_SHADER);
				GLUtil.checkError();
				
				GL20.glShaderSource(shaderID, srcFinal);
				GLUtil.checkError();
				GL20.glCompileShader(shaderID);
				GLUtil.checkError();
				if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS)==GL11.GL_FALSE){
					System.err.println("Could not compile "+name+(isFragment?".fs":".vs"));
					System.err.println(GL20.glGetShaderInfoLog(shaderID, 500));
					System.exit(-1);
				}
				GLUtil.checkError();
				if(isFragment){
					fragment=shaderID;
					setUp(allModules);
				}
				else vertex=shaderID;
				GLUtil.checkError();
			}
		});
		
	}
	
	private static String processShaderFile(String src, String path, boolean isFragment, List<String> modules){
		
		if(src==null){
			LogUtil.println("Failed to load shader file", path+(isFragment?".fs":".vs")+"!");
			return null;
		}
		
		StringBuilder srcBuilt=new StringBuilder();
		String[] lines=src.split("(\r\n|\r|\n)");
		
		for(int i=0;i<lines.length;i++){
			String line=lines[i];
			String trimmedLine=line.trim();
			
			if(trimmedLine.startsWith(IMPORT_MARK)){
				String name=trimmedLine.substring(IMPORT_MARK.length(), trimmedLine.length()).trim();
				
				if(!name.startsWith("\"")){
					LogUtil.println("Invalid", IMPORT_MARK, "at line", i+", missing start quote!");
					return null;
				}
				if(!name.endsWith("\"")){
					LogUtil.println("Invalid", IMPORT_MARK, "at line", i+", missing end quote!");
					return null;
				}
				if(name.length()==2){
					LogUtil.println("Invalid", IMPORT_MARK, "at line", i+", empty name!");
					return null;
				}
				name=name.substring(1, name.length()-1);
				String[] args=null;
				int argStarter=name.indexOf(':');
				if(argStarter!=-1){
					String all=name;
					name=all.substring(0, argStarter);
					args=all.substring(argStarter+1).split(";;");
					for(int j=0;j<args.length;j++){
						args[j]=args[j].trim();
					}
				}
				
				if(!modules.contains(name)){
					modules.add(name);
					ShaderModuleSrcLoader loader=ShaderModule.getLoader(name);
					String src0=loader==null?UtilM.getTxtResource("shaders/modules/"+name+(isFragment?".fsmd":".vsmd")):loader.load(isFragment, args);
					String importSrc=processShaderFile(src0, "modules/"+name, isFragment, modules);
					if(importSrc==null) return null;
					
					srcBuilt.append(importSrc);
				}
				else srcBuilt.append("//Duplicate "+IMPORT_MARK+" \""+name+"\"");
				
			}
			else srcBuilt.append(line);
			
			srcBuilt.append('\n');
		}
		
		src=srcBuilt.toString();
		
		return src;
	}
}
