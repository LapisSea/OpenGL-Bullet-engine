package com.lapissea.opengl.program.util.color;

import java.awt.Color;

import com.lapissea.opengl.program.util.RandUtil;

public interface IColorM{
	
	final IColorM BLACK     =ColorMFinal.convert(Color.BLACK);
	final IColorM BLUE      =ColorMFinal.convert(Color.BLUE);
	final IColorM CYAN      =ColorMFinal.convert(Color.CYAN);
	final IColorM DARK_GRAY =ColorMFinal.convert(Color.DARK_GRAY);
	final IColorM GRAY      =ColorMFinal.convert(Color.GRAY);
	final IColorM GREEN     =ColorMFinal.convert(Color.GREEN);
	final IColorM LIGHT_GRAY=ColorMFinal.convert(Color.LIGHT_GRAY);
	final IColorM MAGENTA   =ColorMFinal.convert(Color.MAGENTA);
	final IColorM ORANGE    =ColorMFinal.convert(Color.ORANGE);
	final IColorM PINK      =ColorMFinal.convert(Color.PINK);
	final IColorM RED       =ColorMFinal.convert(Color.RED);
	final IColorM WHITE     =ColorMFinal.convert(Color.WHITE);
	final IColorM YELLOW    =ColorMFinal.convert(Color.YELLOW);
	final IColorM ZERO    =new ColorMFinal(Color.YELLOW);
	
	float r();
	
	float g();
	
	float b();
	
	float a();
	
	default int rInt(){
		return (int)(r()*255+0.5F);
	}
	
	default int gInt(){
		return (int)(g()*255+0.5F);
	}
	
	default int bInt(){
		return (int)(b()*255+0.5F);
	}
	
	default int aInt(){
		return (int)(a()*255+0.5F);
	}
	
	default <T extends ColorM> T blackNWhite(T target){
		
		return target;
	}
	
	static ColorM randomRGB(){
		return randomRGB(new ColorM());
	}
	
	static <T extends ColorM> T randomRGB(T target){
		target.r(RandUtil.RF());
		target.g(RandUtil.RF());
		target.b(RandUtil.RF());
		return target;
	}
	
	static ColorM randomRGBA(){
		return randomRGBA();
	}
	
	static <T extends ColorM> T randomRGBA(T target){
		target.r(RandUtil.RF());
		target.g(RandUtil.RF());
		target.b(RandUtil.RF());
		target.a(RandUtil.RF());
		return target;
	}
	
	public static ColorM convert(Color color){
		return convert(new ColorM(), color);
	}
	
	static <T extends ColorM> T convert(T target, Color color){
		target.r(color.getRed()/255F);
		target.g(color.getGreen()/255F);
		target.b(color.getBlue()/255F);
		target.a(color.getAlpha()/255F);
		return target;
	}
	
	default <T extends ColorM> T mix(T target){
		return mix(target, 1, 1);
	}
	
	static <T extends ColorM> T mix(T target, IColorM color){
		return mix(target, color, 1, 1);
	}
	
	default <T extends ColorM> T mix(T target, float scale1, float scale2){
		return mix(target, this, scale1, scale2);
	}
	
	static <T extends ColorM> T mix(T target, IColorM color, float scale1, float scale2){
		target.r((target.r()*scale1+color.r()*scale2)/(scale1+scale2));
		target.g((target.g()*scale1+color.g()*scale2)/(scale1+scale2));
		target.b((target.b()*scale1+color.b()*scale2)/(scale1+scale2));
		target.a((target.a()*scale1+color.a()*scale2)/(scale1+scale2));
		return target;
	}
	
}
