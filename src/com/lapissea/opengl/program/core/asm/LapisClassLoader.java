package com.lapissea.opengl.program.core.asm;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;

import com.lapissea.opengl.program.core.asm.poll.TransformerAsmPoll;
import com.lapissea.opengl.program.util.data.PrefixTree;

public class LapisClassLoader extends URLClassLoader{
	
	private final PrefixTree<ClassTransformer>	transformers=new PrefixTree<>();
	private final ArrayList<ClassTransformer>	searchResult=new ArrayList<>();
	
	public LapisClassLoader(URLClassLoader parent){
		super(parent.getURLs(), parent);
		TransformerAsmPoll.register(this);
	}
	
	public void registerTransformer(String domain, ClassTransformer transformer){
		transformers.put(domain, transformer);
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
	
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException{
		synchronized(getClassLoadingLock(name)){
			Class<?> c=findLoadedClass(name);
			
			if(c==null){
				if(name.startsWith("com.lapissea")) return findClass(name);
				return super.loadClass(name);
			}
			return c;
		}
	}
	
	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException{
		String file=name.replace('.', File.separatorChar)+".class";
		try{
			byte[] classData=loadClassData(file);
			searchResult.clear();
			for(ClassTransformer classTransformer:transformers.getStartMatchesReverse(name, searchResult)){
				try{
					classData=classTransformer.transform(name, classData);
				}catch(Throwable e){
					e.printStackTrace();
					System.exit(2);
				}
			}
			Class<?> c=defineClass(name, classData, 0, classData.length);
			classData=null;
			resolveClass(c);
			return c;
		}catch(Exception e){
			return super.findClass(name);
		}
	}
	
}
