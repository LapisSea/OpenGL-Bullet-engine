package com.lapissea.opengl.program.util.config.configs;

import java.awt.Dimension;
import java.awt.Point;

import com.lapissea.opengl.program.util.config.Config;

public class SwingWindowConfig extends Config{
	
	public Point		position;
	public Dimension	size;
	
	public SwingWindowConfig(String name){
		super(name);
		position=new Point();
		size=new Dimension(500, 250);
	}
	
	public SwingWindowConfig(String name, Point position, Dimension size){
		this(name);
		this.position=position;
		this.size=size;
	}
	
}
