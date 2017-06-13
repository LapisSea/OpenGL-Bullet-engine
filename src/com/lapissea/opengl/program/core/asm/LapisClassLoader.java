package com.lapissea.opengl.program.core.asm;

import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

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
		try{
			InputStream stream=ClassLoader.getSystemClassLoader().getResourceAsStream(name.replace('.', File.separatorChar)+".class");
			
			int size=stream.available();
			byte[] classData=new byte[size];
			DataInputStream in=new DataInputStream(stream);
			in.readFully(classData);
			stream.close();
			
			searchResult.clear();
			transformers.getStartMatchesReverse(name, searchResult);
			if(searchResult.size()>0){
				ClassReader reader=new ClassReader(classData);
				ClassNode node=new ClassNode();
				reader.accept(node, ClassReader.EXPAND_FRAMES);
				
				if(AsmUtil.hasAnnotation(node, Asmfied.class)){
					boolean dirty=false;
					
					for(ClassTransformer classTransformer:searchResult){
						try{
							if(classTransformer.transform(name, node)) dirty=true;
						}catch(Throwable e){
							e.printStackTrace();
							System.exit(2);
						}
					}
					if(dirty){
						ClassWriter writer=new ClassWriter(ClassWriter.COMPUTE_FRAMES|ClassWriter.COMPUTE_MAXS);
						node.accept(writer);
						classData=writer.toByteArray();
						
					}
				}
			}
			Class<?> c=defineClass(name, classData, 0, classData.length);
			resolveClass(c);
			return c;
		}catch(Exception e){
			return super.findClass(name);
		}
	}
	
}
