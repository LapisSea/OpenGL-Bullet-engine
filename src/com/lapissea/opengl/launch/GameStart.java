package com.lapissea.opengl.launch;

import java.net.URLClassLoader;

import com.lapissea.opengl.program.core.asm.LapisClassLoader;

public class GameStart{
	
	public static LapisClassLoader GAME_LOADER;
	
	public static final long START_TIME=System.nanoTime();
	static{
		System.out.println(START_TIME);
	}
	public static void main(String[] args) throws Exception{
		SingleInstanceProgram.check();

		GAME_LOADER=new LapisClassLoader((URLClassLoader)ClassLoader.getSystemClassLoader());
		Thread.currentThread().setContextClassLoader(GAME_LOADER);
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Class.forName(GameStart.class.getPackage().getName()+".Launch", true, GAME_LOADER).getDeclaredMethod("start", String[].class).invoke(null, new Object[]{args});
	}
	
}
