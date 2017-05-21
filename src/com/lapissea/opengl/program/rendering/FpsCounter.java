package com.lapissea.opengl.program.rendering;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;

public class FpsCounter{
	
	private boolean			active;
	private LongList		frames	=new LongArrayList(30);
	private final int		second;
	private final boolean	useNano;
	
	public FpsCounter(boolean useNano){
		this.useNano=useNano;
		second=useNano?1000_000_000:1000;
	}
	
	public void newFrame(){
		if(!active) return;
		
		long time=useNano?System.nanoTime():System.currentTimeMillis();
		
		frames.removeIf(f->time-f>second);
		frames.add(frames.size(),time);
	}
	
	public int getFps(){
		return frames.size();
	}
	
	public void activate(){
		active=true;
	}
	
	public void deactivate(){
		active=false;
		frames.clear();
	}
	@Override
	public String toString(){
		return "FPS: "+getFps();
	}
	
}
