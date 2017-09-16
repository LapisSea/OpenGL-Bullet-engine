package com.lapissea.opengl.util;

import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import com.lapissea.opengl.window.api.util.MathUtil;

public class Rand{
	
	private static final Random RAND=new Random();
	
	public static int ci(int scale){
		return scale-i(scale*2);
	}
	
	public static boolean b(){
		return RAND.nextBoolean();
	}
	
	/**
	 * this method returns a random boolean with a custom chance of getting
	 * true. The higher the number is the higher chance will be for getting a
	 * true return. Type in a number higher or equal to 0 and lower or equal to
	 * 1.
	 */
	public static boolean b(double percentage){
		percentage=MathUtil.snap(percentage, 0, 1);
		return percentage!=0&&(percentage==1||f()<percentage);
	}
	
	/**
	 * this method returns a random boolean with a custom chance of getting
	 * true. The higher the number is the lower chance will be for getting a
	 * true return. Type in a number higher or equal to 1.
	 * s
	 */
	public static boolean b(int percentage){
		percentage=Math.max(percentage, 1);
		if(percentage==1) return true;
		return i(percentage)==0;
	}
	
	public static double d(){
		return RAND.nextDouble();
	}
	
	public static double d(double scale){
		return d()*scale;
	}
	
	public static double cd(double scale){
		return (0.5-d())*scale;
	}
	
	public static double d(double start, double range){
		return start+d(range);
	}
	
	public static float f(){
		return RAND.nextFloat();
	}
	
	public static float f(double scale){
		return (float)(f()*scale);
	}
	
	public static float cf(float scale){
		return (float)((0.5-f())*scale);
	}
	
	public static float f(float start, float range){
		return start+f(range);
	}
	
	
	public static int i(int scale){
		return RAND.nextInt(scale);
	}
	
	public static int i(int start, int range){
		return start+i(range);
	}
	public static IntStream is(long streamSize){
		return RAND.ints(streamSize);
	}
	
	public static long l(){
		return RAND.nextLong();
	}
	
	public static LongStream ls(long streamSize){
		return RAND.longs(streamSize);
	}
	
	public static <T> T pick(T[] data){
		return data[i(data.length)];
	}
}
