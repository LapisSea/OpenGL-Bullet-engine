package com.lapissea.opengl.util;

import com.lapissea.opengl.window.api.util.MathUtil;

public class Performance{
	
	private static final Config CFG=Config.getConfig("Performance");
	
	public static final int CORE_COUNT=Runtime.getRuntime().availableProcessors();
	
	public static int getMaxThread(){
		return MathUtil.snap(CFG.fillInt("max-threads", CORE_COUNT), 1, CORE_COUNT);
	}
	
}
