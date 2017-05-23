package com.lapissea.opengl.launch;

import java.net.URLClassLoader;

import com.lapissea.opengl.program.core.asm.LapisClassLoader;

public class GameStart{
	
	public static LapisClassLoader GAME_LOADER;
	
	public static void main(String[] args) throws Exception{
		
		NativeSetUp.haxNatives();
		
		GAME_LOADER=new LapisClassLoader(((URLClassLoader)GameStart.class.getClassLoader()).getURLs());
		Thread.currentThread().setContextClassLoader(GAME_LOADER);
		
		Class.forName(GameStart.class.getPackage().getName()+".Launch", true, GAME_LOADER).getDeclaredMethod("main", String[].class).invoke(null, new Object[]{args});
	}
	
	
}
