package com.lapissea.opengl.program.rendering.gl.shader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.core.Globals;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.AbstractUniform;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformBoolean;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat2;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat4;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.data.MapOfLists;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.util.LogUtil;
import com.lapissea.util.UtilL;

public abstract class Shader{
	
	public static final int NOT_LOADED=0,FAILED=-1,NOT_FOUND=-2;
	
	private static Map<Integer,UniformFactory> UNIFORMS=new HashMap<>();
	
	private interface UniformFactory{
		
		default AbstractUniform get(Shader shader, int id, String name){
			try{
				return get0(shader, id, name);
			}catch(InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException e){
				throw new RuntimeException(e);
			}
		}
		
		AbstractUniform get0(Shader shader, int id, String name) throws InstantiationException,IllegalAccessException,IllegalArgumentException,InvocationTargetException;
	}
	
	public static <T extends AbstractUniform> void registerUniform(int uniformType, Class<T> uniform){
		Integer key=uniformType;
		if(UNIFORMS.containsKey(key)) return;
		try{
			Constructor<T> constuct=uniform.getDeclaredConstructor(Shader.class, int.class, String.class);
			constuct.setAccessible(true);
			UNIFORMS.put(key, (s, i, n)->constuct.newInstance(s, i, n));
		}catch(NoSuchMethodException|SecurityException e){
			throw new RuntimeException(e);
		}
	}
	
	static{
		registerUniform(GL20.GL_FLOAT_MAT4, UniformMat4.class);
		registerUniform(GL11.GL_FLOAT, UniformFloat1.class);
		registerUniform(GL20.GL_FLOAT_VEC2, UniformFloat2.class);
		registerUniform(GL20.GL_FLOAT_VEC3, UniformFloat3.class);
		registerUniform(GL20.GL_FLOAT_VEC4, UniformFloat4.class);
		registerUniform(GL20.GL_BOOL, UniformBoolean.class);
		
	}
	
	public int			program,vertex=NOT_LOADED,geometry=NOT_LOADED,fragment=NOT_LOADED;
	private boolean		bound,loaded;
	public final String	name;
	public ShaderLoader	loader;
	
	protected UniformMat4	transformMat;
	protected UniformMat4	projectionMat;
	protected UniformMat4	viewMat;
	protected UniformFloat1	shineDamper;
	protected UniformFloat1	reflectivity;
	protected UniformFloat1	lightTroughput;
	
	protected List<ShaderModule>			modules				=new ArrayList<>();
	protected List<ShaderModule.Global>		modulesGlobal		=new ArrayList<>();
	protected List<ShaderModule.Instance>	modulesInstance		=new ArrayList<>();
	protected List<ShaderModule.ModelMdl>	modulesModelUniforms=new ArrayList<>();
	
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
		
		loader.setShader(this);
		
		loadShader(loader.getVertex(), GL20.GL_VERTEX_SHADER, ".vs", id->vertex=id);
		loadShader(loader.getGeometry(), GL32.GL_GEOMETRY_SHADER, ".gs", id->geometry=id);
		loadShader(loader.getFragment(), GL20.GL_FRAGMENT_SHADER, ".fs", id->fragment=id);
		glCtx(()->{
			
			if(fragment<0) throw new OpenGLException("Could not load shader: "+name+" (vs="+vertex+", gs="+geometry+", fs="+fragment+")");
			
			modules.forEach(module->{
				if(module instanceof ShaderModule.Global) modulesGlobal.add((ShaderModule.Global)module);
				if(module instanceof ShaderModule.Instance) modulesInstance.add((ShaderModule.Instance)module);
				if(module instanceof ShaderModule.ModelMdl) modulesModelUniforms.add((ShaderModule.ModelMdl)module);
			});
			
			program=GL20.glCreateProgram();
			
			GLUtil.attachShader(program, vertex);
			GLUtil.attachShader(program, geometry);
			GLUtil.attachShader(program, fragment);
			
			bindAttributes();
			for(ShaderModule shaderModule:modules){
				LogUtil.println(shaderModule);
			}
			modules.forEach(md->{
				md.bindAttributes();
			});
			
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
		if(data==null){
			set.accept(NOT_FOUND);
			return;
		}
		
		if(data.obj2!=null) data.obj2.stream().filter(m->!modules.stream().noneMatch(m1->m1.getClass().equals(m.getClass()))).forEach(modules::add);
		
		data.obj1=data.obj1.replaceAll("\r\n", "\n");
		
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
			String errTxt;
			if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS)==GL11.GL_FALSE&&!(errTxt=GL20.glGetShaderInfoLog(shaderID, 2048)).isEmpty()){
				GL20.glDeleteShader(shaderID);
				shaderID=FAILED;
				
				LogUtil.printlnEr("Could not compile", name+ext);
				String[] errors=errTxt.split("\n");
				
				MapOfLists<String,String> errs=new MapOfLists<>();
				MapOfLists<String,String> wans=new MapOfLists<>();
				
				for(int i=0;i<errors.length;i++){
					String error=errors[i];
					if(error.isEmpty()) continue;
					String lineNum=error.substring(error.indexOf('(')+1, error.indexOf(')'));
					
					Matcher errType=ERROR_TYPE.matcher(error);
					errType.find();
					
					String[] erType=errType.group(0).split(" ");
					
					(erType[0].equals("error")?errs:wans).add(lineNum, "["+erType[1]+"]: "+error.substring(errType.end()+2));
				}
				
				String[] lines=data.obj1.split("\n|\n\r");
				
				Consumer<MapOfLists<String,String>> printMsgs=d->d.forEach((line, msgs)->{
					LogUtil.printEr("    Line", line+": ");
					
					if(msgs.size()==1){
						LogUtil.printlnEr(msgs.get(0));
						LogUtil.printlnEr("    src -> "+lines[Integer.parseInt(line)-1]);
					}else{
						LogUtil.printlnEr("\n    src -> "+lines[Integer.parseInt(line)-1]);
						msgs.forEach(msg->LogUtil.printlnEr("        "+msg));
					}
					
				});
				
				if(!errs.isEmpty()){
					LogUtil.printlnEr("ERRORS:");
					printMsgs.accept(errs);
				}
				if(!wans.isEmpty()){
					LogUtil.printlnEr("WARNINGS:");
					printMsgs.accept(wans);
				}
				System.exit(-1);
			}
			set.accept(shaderID);
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
		transformMat=getUniform("transformMat");
		projectionMat=getUniform("projectionMat");
		viewMat=getUniform("viewMat");
	}
	
	protected int getUniformId(String name){
		return GL20.glGetUniformLocation(program, name);
	}
	
	public <T extends AbstractUniform> T[] getUniformArray(String name){
		return getUniformArray(i->name+"["+i+"]");
	}
	
	public <T extends AbstractUniform> T[] getUniformArray(Function<Integer,String> name){
		
		List<T> unis=new ArrayList<>();
		T unif;
		
		while((unif=getUniform(name.apply(unis.size())))!=null){
			unis.add(unif);
		}
		
		return UtilL.array(unis);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends AbstractUniform> T getUniform(String name){
		GLUtil.checkError();
		int id=getUniformId(name);
		GLUtil.checkError();
		if(id==-1) return null;
		
		int type=GL20.glGetActiveUniformType(program, id);
		UniformFactory fac=UNIFORMS.get(type);
		if(fac==null) throw new RuntimeException("Unknown uniform type "+type);
		
		return (T)fac.get(this, id, name);
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
		
		vertex=fragment=geometry=program=NOT_LOADED;
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
