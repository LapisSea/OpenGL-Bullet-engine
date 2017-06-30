package com.lapissea.opengl.program.rendering.gl.shader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.core.Globals;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.data.MapOfLists;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.util.LogUtil;
import com.lapissea.util.UtilL;

public abstract class Shader{
	
	public int			program,vertex,geometry,fragment;
	private boolean		bound,loaded;
	public final String	name;
	public ShaderLoader	loader;
	
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
		this(new ImportShaderLoader());
	}
	
	public Shader(ShaderLoader loader){
		this(null, loader);
	}
	
	public Shader(String name){
		this(name, new ImportShaderLoader());
	}
	
	public Shader(String name, ShaderLoader loader){
		if(name==null) name=getClass().getSimpleName().replace("Shader", "").toLowerCase();
		if(name.isEmpty()) throw new IllegalStateException("You can't call a shader \"Shader\"!");
		this.name=name;
		this.loader=loader;
		load();
	}
	
	public Map<String,String> getCompileValues(){
		return null;
	}
	
	public void load(){
		delete();
		
		loader.shader=this;
		
		loadShader(loader.getFragment(), GL20.GL_FRAGMENT_SHADER, ".fs", id->fragment=id);
		loadShader(loader.getGeometry(), GL32.GL_GEOMETRY_SHADER, ".gs", id->geometry=id);
		loadShader(loader.getVertex(), GL20.GL_VERTEX_SHADER, ".vs", id->vertex=id);
		glCtx(()->{
			
			modules.forEach(module->{
				if(module instanceof ShaderModule.Global) modulesGlobal.add((ShaderModule.Global)module);
				if(module instanceof ShaderModule.Instance) modulesInstance.add((ShaderModule.Instance)module);
				if(module instanceof ShaderModule.ModelUniforms) modulesModelUniforms.add((ShaderModule.ModelUniforms)module);
			});
			
			program=GL20.glCreateProgram();
			
			GL20.glAttachShader(program, vertex);
			GL20.glAttachShader(program, geometry);
			GL20.glAttachShader(program, fragment);
			
			bindAttributes();
			modules.forEach(ShaderModule::bindAttributes);
			
			GL20.glLinkProgram(program);
			GL20.glValidateProgram(program);
			setUpUniforms();
			modules.forEach(ShaderModule::setUpUniforms);
			
			loaded=true;
			
			LogUtil.println("Loaded shader:", name);
		});
	}
	
	protected static final Pattern ERROR_TYPE=Pattern.compile("\\w+ \\w+");
	
	protected void loadShader(PairM<String,Collection<ShaderModule>> data, int type, String ext, IntConsumer set){
		if(data==null||data.obj1==null) return;
		
		if(data.obj2!=null) data.obj2.stream().filter(m->!modules.contains(m)).forEach(modules::add);
		
		if(Globals.DEV_ENV) try{
			File f=new File("res/shaders/compiled output/"+name+ext);
			f.getParentFile().mkdirs();
			Files.write(f.toPath(), data.obj1.getBytes());
		}catch(IOException e){
			e.printStackTrace();
		}
		
		glCtx(()->{
			GL20.glUseProgram(0);
			
			int shaderID=GL20.glCreateShader(type);
			
			GL20.glShaderSource(shaderID, data.obj1);
			GL20.glCompileShader(shaderID);
			
			if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS)==GL11.GL_FALSE){
				LogUtil.printlnEr("Could not compile", name+ext);
				String[] errors=GL20.glGetShaderInfoLog(shaderID, 2048).split("\n");
				
				MapOfLists<String,String> errs=new MapOfLists<>();
				MapOfLists<String,String> wans=new MapOfLists<>();
				
				for(int i=0;i<errors.length;i++){
					String error=errors[i];
					String lineNum=error.substring(error.indexOf('(')+1, error.indexOf(')'));
					
					Matcher errType=ERROR_TYPE.matcher(error);
					errType.find();
					
					String[] erType=errType.group(0).split(" ");
					
					(erType[0].equals("error")?errs:wans).add(lineNum, error.substring(errType.end()+2));
				}
				if(!errs.isEmpty()){
					LogUtil.printlnEr("ERRORS:");
					printMsgs(errs);
				}
				if(!errs.isEmpty()){
					LogUtil.printlnEr("WARNINGS:");
					printMsgs(wans);
				}
				System.exit(-1);
			}
			set.accept(shaderID);
		});
		
	}
	
	private void printMsgs(MapOfLists<String,String> data){
		data.forEach((line, msgs)->{
			if(msgs.size()==1) LogUtil.printlnEr("    Line", line+":", msgs.get(0));
			else{
				LogUtil.printlnEr("    Line", line+":");
				msgs.forEach(msg->LogUtil.printlnEr("        "+msg));
			}
		});
	}
	
	public boolean isLoaded(){
		return loaded;
	}
	
	public void uploadTransformMat(Matrix4f mat){
		if(transformMat!=null) transformMat.upload(mat);
	}
	
	public void uploadProjectionAndViewMat(Matrix4f project, Matrix4f view){
		if(viewMat==null) return;
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
		
		return unis.isEmpty()?null:unis.toArray(UtilL.array(uniformType, unis.size()));
	}
	
	public <T extends AbstractUniform> T getUniform(Class<T> uniformType, String name){
		GLUtil.checkError();
		try{
			int id=getUniformId(name);
			GLUtil.checkError();
//			LogUtil.println(id, name);
			if(id==-1) return null;
			Constructor<T> ctrs=uniformType.getDeclaredConstructor(Shader.class, int.class, String.class);
			ctrs.setAccessible(true);
			return ctrs.newInstance(this, id, name);
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
		if(!loaded) return;
		
		modules.clear();
		modulesGlobal.clear();
		modulesInstance.clear();
		modulesModelUniforms.clear();
		
		GLUtil.deleteDetachShader(program, vertex);
		GLUtil.deleteDetachShader(program, geometry);
		GLUtil.deleteDetachShader(program, fragment);
		
		GL20.glDeleteProgram(program);
		
		vertex=fragment=geometry=program=0;
		loaded=false;
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
	
	protected void glCtx(Runnable r){
		Game.glCtx(r);
	}
	
}
