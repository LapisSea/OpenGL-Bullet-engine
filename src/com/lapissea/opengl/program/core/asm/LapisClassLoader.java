package com.lapissea.opengl.program.core.asm;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.PrefixTree;

public class LapisClassLoader extends URLClassLoader{
	
	private final PrefixTree<ClassTransformer> transformers=new PrefixTree<>();
	
	public LapisClassLoader(URL[] urls){
		super(((URLClassLoader)LapisClassLoader.class.getClassLoader()).getURLs(), null);
	}
	
	public void registerTransformer(String domain, ClassTransformer transformer){
		transformers.put(domain, transformer);
		LogUtil.println(transformers);
	}
	
	private byte[] loadClassData(String name) throws IOException{
		// Opening the file
		InputStream stream=ClassLoader.getSystemClassLoader().getResourceAsStream(name);
		int size=stream.available();
		byte buff[]=new byte[size];
		DataInputStream in=new DataInputStream(stream);
		// Reading the binary data
		in.readFully(buff);
		in.close();
		return buff;
	}
	
	private byte[] classData;
	
	
//	@Override
//	public Class<?> loadClass(String name) throws ClassNotFoundException{
//		synchronized(getClassLoadingLock(name)){
//			
//			Class<?> c=findLoadedClass(name);
//			
//			if(c==null){
//				if(name.startsWith("com.lapissea")) return getClass(name);
//				return super.loadClass(name);
//			}
//			return c;
//		}
//	}
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException{
		String file=name.replace('.', File.separatorChar)+".class";
		try{
			classData=loadClassData(file);
			//			if(name.startsWith("com.lapissea.opengl.launch.Test")){
			//				LogUtil.println(transformers);
			//				List<ClassTransformer> t=new ArrayList<>();
			//				transformers.getStartMatchesReverse(name, t);
			//				System.out.println(t);
			//			}
			Class<?> c=defineClass(name, classData, 0, classData.length);
			classData=null;
			resolveClass(c);
			return c;
		}catch(Exception e){
			return super.findClass(name);
		}
	}
	
}
