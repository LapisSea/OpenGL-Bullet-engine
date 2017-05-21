package com.lapissea.opengl.program.util;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;

public enum OperatingSystem{
	
	WINDOWS(s->s.contains("win")),
	LINUX(s->s.contains("linux")||s.contains("unix")),
	OSX(s->s.contains("mac")||s.contains("osx"));
	
	public static final String			OS_NAME		=System.getProperty("os.name").toLowerCase(Locale.ROOT);
	public static final OperatingSystem	ACTIVE_OS	=Arrays.stream(OperatingSystem.values()).filter(os->os.detector.apply(OS_NAME)).findFirst().orElseThrow(()->new UnsupportedOperationException("Sorry, os with name "+OS_NAME+" is not supported or could not be identified. :("));
	public static final String			APP_DATA	=new File((ACTIVE_OS==WINDOWS?System.getenv("AppData"):System.getProperty("user.home")+"/Library/Application Support")+"/LapisSea/").getPath();
	
	
	public final Function<String,Boolean> detector;
	
	private OperatingSystem(Function<String,Boolean> detector){
		this.detector=detector;
	}
	
}
