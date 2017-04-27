package com.lapissea.opengl.program.util.color;

import java.awt.Color;

import com.lapissea.opengl.program.interfaces.Calculateable;
import com.lapissea.opengl.program.util.MathUtil;

public class ColorM extends ColorMRead implements Calculateable<ColorM>{
	
	public ColorM(){
		super();
	}
	
	public ColorM(IColorM color){
		this(color.r(), color.g(), color.b(), color.a());
	}
	
	public ColorM(float[] data){
		load(data);
	}
	
	public ColorM(double r, double g, double b){
		super(r, g, b);
	}
	
	public ColorM(double r, double g, double b, double a){
		super(r, g, b, a);
	}
	
	public ColorM(float r, float g, float b, float a){
		super(r, g, b, a);
	}
	
	public ColorM(float r, float g, float b){
		super(r, g, b);
	}
	
	public ColorM load(float[] data){
		return load(0, data);
	}
	
	public ColorM load(int offset, float[] data){
		int dataLeft=data.length-offset;
		
		if(dataLeft>=4) return r(data[offset+0]).g(data[offset+1]).b(data[offset+2]).a(data[offset+3]);
		if(dataLeft==3) return r(data[offset+0]).g(data[offset+1]).b(data[offset+2]);
		
		throw new IllegalArgumentException("Not enough data!");
	}
	
	public ColorM r(float r){
		this.r=MathUtil.snap(r, 0, 1);
		return this;
	}
	
	public ColorM g(float g){
		this.g=MathUtil.snap(g, 0, 1);
		return this;
	}
	
	public ColorM b(float b){
		this.b=MathUtil.snap(b, 0, 1);
		return this;
	}
	
	public ColorM a(float a){
		this.a=MathUtil.snap(a, 0, 1);
		return this;
	}
	
	public ColorM mix(Color color){
		return mix(IColorM.convert(color));
	}
	
	public ColorM mix(Color color, float scale1, float scale2){
		return mix(IColorM.convert(color), scale1, scale2);
	}
	
	public ColorM negative(){
		return this;
	}
	
	public ColorM set(float modifier, char c){
		modifier=MathUtil.snap(modifier, 0, 1);
		return new ColorM(c=='r'?modifier:r, c=='g'?modifier:g, c=='b'?modifier:b, c=='a'?modifier:a);
	}
	
	public ColorM add(float var){
		r(r()+var);
		g(g()+var);
		b(b()+var);
		a(a()+var);
		return this;
	}
	
	public ColorM addR(float r){
		r(r()+r);
		return this;
	}
	
	public ColorM addG(float r){
		g(g()+g);
		return this;
	}
	
	public ColorM addB(float b){
		b(b()+b);
		return this;
	}
	
	public ColorM addA(float b){
		a(a()+a);
		return this;
	}
	
	@Override
	public ColorM add(ColorM var){
		r(r()+var.r());
		g(g()+var.g());
		b(b()+var.b());
		a(a()+var.a());
		return this;
	}
	
	public ColorM div(float var){
		r(r()/var);
		g(g()/var);
		b(b()/var);
		a(a()/var);
		return this;
	}
	
	public ColorM divR(float r){
		r(r()/r);
		return this;
	}
	
	public ColorM divG(float r){
		g(g()/g);
		return this;
	}
	
	public ColorM divB(float b){
		b(b()/b);
		return this;
	}
	
	public ColorM divA(float b){
		a(a()/a);
		return this;
	}
	
	@Override
	public ColorM div(ColorM var){
		r(r()/var.r());
		g(g()/var.g());
		b(b()/var.b());
		a(a()/var.a());
		return this;
	}
	
	@Override
	public ColorM mul(float var){
		r(r()*var);
		g(g()*var);
		b(b()*var);
		a(a()*var);
		return this;
	}
	
	public ColorM mulR(float r){
		r(r()*r);
		return this;
	}
	
	public ColorM mulG(float r){
		g(g()*g);
		return this;
	}
	
	public ColorM mulB(float b){
		b(b()*b);
		return this;
	}
	
	public ColorM mulA(float b){
		a(a()*a);
		return this;
	}
	
	@Override
	public ColorM mul(ColorM var){
		r(r()*var.r());
		g(g()*var.g());
		b(b()*var.b());
		a(a()*var.a());
		return this;
	}
	
	public ColorM sub(float var){
		r(r()-var);
		g(g()-var);
		b(b()-var);
		a(a()-var);
		return this;
	}
	
	public ColorM subR(float r){
		r(r()-r);
		return this;
	}
	
	public ColorM subG(float r){
		g(g()-g);
		return this;
	}
	
	public ColorM subB(float b){
		b(b()-b);
		return this;
	}
	
	public ColorM subA(float b){
		a(a()-a);
		return this;
	}
	
	@Override
	public ColorM sub(ColorM var){
		r(r()-var.r());
		g(g()-var.g());
		b(b()-var.b());
		a(a()-var.a());
		return this;
	}
	
	@Override
	public ColorM clone(){
		return new ColorM(r(), g(), b(), a);
	}
	
	public static ColorM toColorM(IColorM color){
		return color instanceof ColorM?(ColorM)color:new ColorM(color);
	}
	
	@Override
	public ColorM subRev(ColorM c){
		return c.sub(this);
	}
	
	@Override
	public ColorM abs(){
		return this;
	}
	
	@Override
	public ColorM sqrt(){
		r(MathUtil.sqrt(r()));
		g(MathUtil.sqrt(g()));
		b(MathUtil.sqrt(b()));
		a(MathUtil.sqrt(a()));
		return this;
	}
	
	@Override
	public ColorM sq(){
		r(MathUtil.sq(r()));
		g(MathUtil.sq(g()));
		b(MathUtil.sq(b()));
		a(MathUtil.sq(a()));
		return this;
	}
	
	public ColorM set(int rgb){
		r(((rgb>>16)&0xFF)/256F);
		g(((rgb>>8)&0xFF)/256F);
		b(((rgb>>0)&0xFF)/256F);
		return this;
	}
	
	@Override
	public ColorM set(ColorM src){
		r(src.r());
		g(src.g());
		b(src.b());
		a(src.a());
		return this;
	}
}
