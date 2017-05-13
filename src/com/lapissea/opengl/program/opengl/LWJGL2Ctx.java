package com.lapissea.opengl.program.opengl;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;

import com.lapissea.opengl.abstr.opengl.IGLLoader;
import com.lapissea.opengl.abstr.opengl.IGLWindow;
import com.lapissea.opengl.abstr.opengl.ILWJGLCtx;

public class LWJGL2Ctx implements ILWJGLCtx{
	
	private final IGLWindow		window		=new LWJGL2Window(this);
	private final IGLLoader		loader		=new LWJGL2Loader(this);
	private final List<Thread>	glThreads	=new ArrayList<>();
	
	@Override
	public IGLWindow getCtxWindow(){
		return window;
	}
	
	@Override
	public IGLLoader getGLLoader(){
		return loader;
	}
	
	@Override
	public boolean isGlThread(Thread thread){
		return glThreads.contains(thread);
	}
	
	@Override
	public void init(){
		glThreads.clear();
		glThreads.add(Thread.currentThread());
		try{
			window.create();
		}catch(LWJGLException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public void destroy(){
		glThreads.clear();
		window.destroy();
		loader.destroy();
	}
	
}
