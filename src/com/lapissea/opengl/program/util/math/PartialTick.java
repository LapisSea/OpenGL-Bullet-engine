package com.lapissea.opengl.program.util.math;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.interfaces.Interpolateble;

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
