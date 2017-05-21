package com.lapissea.opengl.program.util.config.configs;

import java.awt.Dimension;
import java.awt.Point;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lapissea.opengl.program.util.config.Config;

public class SwingWindowConfig extends Config{
	
	public Point		position;
	public Dimension	size;
	
	public SwingWindowConfig(String name){
		super(name);
		position=new Point();
		size=new Dimension(500, 250);
	}
	
	@JsonCreator
	public SwingWindowConfig(@JsonProperty("name") String name, @JsonProperty("position") Point position, @JsonProperty("size") Dimension size){
		this(name);
		this.position=position;
		this.size=size;
	}
	
}
