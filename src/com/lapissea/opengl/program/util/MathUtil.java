package com.lapissea.opengl.program.util;

public class MathUtil{
	
	public static double snap(double value, double min, double max){
		if(min>max) return value;
		if(value<min) return min;
		if(value>max) return max;
		return value;
	}
	
	public static float snap(float value, float min, float max){
		if(min>max) return value;
		if(value<min) return min;
		if(value>max) return max;
		return value;
	}
	
	public static int snap(int value, int min, int max){
		if(min>max) return value;
		if(value<min) return min;
		if(value>max) return max;
		return value;
	}
	
	public static int snapToArray(int value, Object[] arr){
		return snap(value, 0, arr.length-1);
	}
	
	public static double sq(double var){
		return var*var;
	}
	
	public static int sq(int var){
		return var*var;
	}
	
	public static float sq(float var){
		return var*var;
	}
	
	public static float sqrt(float value){
		return (float)Math.sqrt(value);
	}

	public static int max(int i1,int i2,int i3){
		return Math.max(i1, Math.max(i2, i3));
	}
	public static int max(int i1,int i2,int i3,int i4){
		return Math.max(i1, max(i2, i3, i4));
	}
	public static float max(float i1,float i2,float i3){
		return Math.max(i1, Math.max(i2, i3));
	}
	public static float max(float i1,float i2,float i3,float i4){
		return Math.max(i1, max(i2, i3, i4));
	}
	
}
