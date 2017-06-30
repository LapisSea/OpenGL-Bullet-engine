package com.lapissea.opengl.program.rendering.gl.shader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.ShaderModuleSrcLoader;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;

public class ImportShaderLoader extends ShaderLoader{
	
	protected static final Pattern IMPORT_MARK=Pattern.compile("#include *\\\".*\\\"");
	
	@Override
	public PairM<String,Collection<ShaderModule>> getVertex(){
		Map<String,ShaderModule> modules=new HashMap<>();
		return new PairM<>(resolve(UtilM.getTxtResource("shaders/"+shader.name+".vs"), modules), modules.values());
	}
	
	@Override
	public PairM<String,Collection<ShaderModule>> getGeometry(){
		Map<String,ShaderModule> modules=new HashMap<>();
		return new PairM<>(resolve(UtilM.getTxtResource("shaders/"+shader.name+".gs"), modules), modules.values());
	}
	
	@Override
	public PairM<String,Collection<ShaderModule>> getFragment(){
		Map<String,ShaderModule> modules=new HashMap<>();
		return new PairM<>(resolve(UtilM.getTxtResource("shaders/"+shader.name+".fs"), modules), modules.values());
	}
	
	protected String resolve(String src, Map<String,ShaderModule> modules){
		if(src==null) return null;
		src=src.trim();
		if(src.isEmpty()) return null;
		
		Map<String,String> m=shader.getCompileValues();
		if(m!=null){
			for(Entry<String,String> e:m.entrySet()){
				src=src.replaceAll(e.getKey(), e.getValue());
			}
		}
		
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
			String extension=pos==-1?".smd":name.substring(pos);
			
			if(modules.containsKey(name)){
				src=insertReplace(src, macher, "//SKIPPED DUPLICATE \""+name+'"');
				continue;
			}
			
			ShaderModuleSrcLoader loader=ShaderModule.getLoader(name);
			
			modules.put(name, ShaderModule.getNew(name, shader));
			
			String importSrc;
			
			if(loader==null) importSrc=UtilM.getTxtResource("shaders/modules/"+name);
			else importSrc=loader.load(extension, args);
			
			if(importSrc==null) return null;
			
			src=insertReplace(src, macher, "////_:"+name+":_\\\\\\\\\\\n"+resolve(importSrc, modules));
			
		}
		
		return src;
	}
	
	protected String cropImportMark(String imp0rt){
		return imp0rt.substring(imp0rt.indexOf('"')+1, imp0rt.lastIndexOf('"')).trim();
	}
	
	protected String insertReplace(String src, Matcher macher, String replace){
		return src.substring(0, macher.start())+replace+"\n"+src.substring(macher.end());
	}
	
}
