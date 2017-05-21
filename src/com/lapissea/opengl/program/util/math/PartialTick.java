package com.lapissea.opengl.program.util.math;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.util.Quat4M;
import com.lapissea.opengl.window.api.util.Calculateable;

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
	
	public static Quat4M calc(Quat4M dest, Quat4M prevValue, Quat4M value){
		return Quat4M.interpolate(dest, prevValue, value, pt());
	}
	
	public static <T extends Calculateable<T>> T calc(T dest, T prevValue, T value){
		if(value.equals(prevValue))return dest.set(value);
		return calculate(dest, prevValue, value);
	}
	private static <T extends Calculateable<T>> T calculate(T dest, T prevValue, T value){
		return dest.set(value).sub(prevValue).mul(pt()).add(prevValue);
	}
}
