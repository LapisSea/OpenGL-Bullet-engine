package com.lapissea.opengl.rendering.shader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.lwjgl.opengl.OpenGLException;

import com.lapissea.opengl.rendering.shader.modules.ShaderModule;
import com.lapissea.opengl.rendering.shader.modules.ShaderModule.ShaderModuleSrcLoader;
import com.lapissea.opengl.util.PairM;
import com.lapissea.opengl.util.UtilM;
import com.lapissea.util.LogUtil;

public class ImportShaderLoader extends ShaderLoader{
	
	protected static final Pattern IMPORT_MARK=Pattern.compile("#include *\\\".*\\\"");
	
	@Override
	public PairM<String,Collection<ShaderModule>> getVertex(){
		Map<String,ShaderModule> modules=new HashMap<>();
		String src=resolve(UtilM.getTxtResource("shaders/"+shader.name+".vs"), modules);
		if(src==null) return null;
		return new PairM<>(src, modules.values());
	}
	
	@Override
	public PairM<String,Collection<ShaderModule>> getGeometry(){
		Map<String,ShaderModule> modules=new HashMap<>();
		String src=resolve(UtilM.getTxtResource("shaders/"+shader.name+".gs"), modules);
		if(src==null) return null;
		return new PairM<>(src, modules.values());
	}
	
	@Override
	public PairM<String,Collection<ShaderModule>> getFragment(){
		Map<String,ShaderModule> modules=new HashMap<>();
		String src=resolve(UtilM.getTxtResource("shaders/"+shader.name+".fs"), modules);
		if(src==null) return null;
		return new PairM<>(src, modules.values());
	}
	
	protected String resolve(String src, Map<String,ShaderModule> modules){
		if(src==null) return null;
		src=src.trim();
		if(src.isEmpty()) return null;
		
		Map<String,String> values=new HashMap<>();
		
		values(values, shader.getCompileValues());
		modules.values().forEach(m->values(values, m.getCompileValues()));
		
		src=values(values, src);
		
		Matcher macher;
		while((macher=IMPORT_MARK.matcher(src)).find()){
			
			String name=cropImportMark(macher.group(0));
			
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
			
			int pos=name.lastIndexOf('.');
			if(pos==-1){
				LogUtil.printlnEr("WARNING: Shader module extension name not included! ("+name+"@"+shader.name+") Assuming \".smd\"!");
				pos=name.length();
				name+=".smd";
			}
			String extension=name.substring(pos);
			name=name.substring(0, pos);
			if(modules.containsKey(name)){
				src=insertReplace(src, macher, "/*SKIPPED DUPLICATE \""+name+extension+"\" */");
				continue;
			}
			
			ShaderModule m=ShaderModule.getNew(name, shader);
			if(m!=null) modules.put(name, m);
			
			ShaderModuleSrcLoader loader=ShaderModule.getLoader(name);
			String importSrc;
			
			if(loader==null) importSrc=UtilM.getTxtResource("shaders/modules/"+name+extension);
			else importSrc=loader.load(extension, args);
			
			if(importSrc==null) throw new OpenGLException("Missing moddule source: "+name+extension+"@"+shader.name);
			
			src=insertReplace(src, macher, "/*MODULE_START: "+name+extension+"*/\n"+resolve(importSrc, modules)+"\n/*MODULE_END: "+name+extension+"*/\n");
			
		}
		
		return src;
	}
	
	protected String values(Map<String,String> values, String src){
		for(Entry<String,String> e:values.entrySet()){
			src=src.replaceAll("<"+e.getKey()+">", e.getValue());
		}
		return src;
	}
	
	protected void values(Map<String,String> data, Map<String,String> add){
		if(add==null) return;
		data.putAll(add);
	}
	
	protected String cropImportMark(String imp0rt){
		return imp0rt.substring(imp0rt.indexOf('"')+1, imp0rt.lastIndexOf('"')).trim();
	}
	
	protected String insertReplace(String src, Matcher macher, String replace){
		return src.substring(0, macher.start())+replace+"\n"+src.substring(macher.end());
	}
	
}
