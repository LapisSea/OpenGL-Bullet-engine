package com.lapissea.opengl.util.math;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.window.api.util.Interpolateble;

public class PartialTick{
	
	private static float pt(){
		return Game.getPartialTicks();
	}
	
	public static double calc(double prevValue, double value){
		return prevValue+(value-prevValue)*pt();
	}
	
	public static float calc(float prevValue, float value){
		return prevValue+(value-prevValue)*pt();
	}
	
	public static <T extends Interpolateble<T>> T calc(T dest, T prevValue, T value){
		return dest.interpolate(prevValue, value, pt());
	}
}
