package com.lapissea.opengl.rendering.shader.modules;

import java.util.function.Function;

import com.lapissea.opengl.rendering.shader.Shader;

public class ShaderModuleArrayList extends ShaderModule{
	
	public static class Loader extends ShaderModuleSrcLoader{
		
		public Loader(){
			super("Texture");
		}
		
		@Override
		public String load(String extension, String[] args){
			StringBuilder src=new StringBuilder();
			
			for(String arg:args){
				String[] arr=arg.split(",");
				String type=arr[0].trim();
				String size=arr[1].trim();
				
				src
						.append("\nstruct List").append(type).append("{")
						.append("\n\t").append(type).append(" data[").append(size).append("];")
						.append("\n\tint size;")
						.append("\n\t").append(type).append(" get(int id){\n\t\treturn data[id];")
						.append("\n\t}")
						.append("\n};")
						.append("\n");
			}
			
			return src.toString();
		}
		
	}
	
	public ShaderModuleArrayList(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){}
	
	public static String arrayListSize(String name){
		return name+".size";
	}
	
	public static Function<Integer,String> arrayList(String name, String inData){
		return arrayList(name, i->inData);
	}
	
	public static Function<Integer,String> arrayList(String name, Function<Integer,String> inData){
		return i->name+".data["+i+"]."+inData.apply(i);
	}
	
}
