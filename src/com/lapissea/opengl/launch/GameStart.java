package com.lapissea.opengl.launch;

import java.net.URLClassLoader;

import com.lapissea.opengl.program.core.asm.LapisClassLoader;
import com.lapissea.splashscreen.SplashScreenHost;

public class GameStart{
	
	public static LapisClassLoader GAME_LOADER;
	
	public static final long START_TIME=System.nanoTime();
	static{
		System.err.println(START_TIME);
	}
	
	public static void main(String[] args) throws Exception{
		//		JSONObject o=UtilM.jsonObj(new WindowConfig("lel"));
		//		System.out.println(UtilM.compressTypes(o).toString(4));
		
		SplashScreenHost.open("Splash.jar", "LWJGL 2 game");
		Thread.sleep(1);
		SplashScreenHost.sendMsg("Starting...");
		NativeSetUp.haxNatives();
		SplashScreenHost.sendMsg("Injected natves!");
		SplashScreenHost.sendPercent(0.1F);
		
		GAME_LOADER=new LapisClassLoader((URLClassLoader)GameStart.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(GAME_LOADER);
		SplashScreenHost.sendMsg("Init loader!");
		SplashScreenHost.sendPercent(0.2F);
		
		Class.forName(GameStart.class.getPackage().getName()+".Launch", true, GAME_LOADER).getDeclaredMethod("start", String[].class).invoke(null, new Object[]{args});
	}
	
}
