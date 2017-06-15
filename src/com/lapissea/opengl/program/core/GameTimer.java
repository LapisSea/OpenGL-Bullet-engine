package com.lapissea.opengl.program.core;

public abstract class GameTimer{
	
	protected static final int	SECOND	=1000_000_000;
	protected boolean			running	=true;
	
	public GameTimer(int ups, int fps){
		setUps(ups);
		setFps(fps);
	}
	
	public GameTimer(int ups, int fps, Runnable update, Runnable render){
		this(ups, fps);
		setUpdate(update);
		setRender(render);
	}
	
	public void end(){
		running=false;
	}
	
	public boolean isRunning(){
		return running;
	}
	
	
	protected long time(){
		return System.nanoTime();
	}
	
	
	public abstract int getFps();
	
	public abstract GameTimer setFps(int fps);
	
	
	public abstract int getUps();
	
	public abstract GameTimer setUps(int ups);
	
	
	public abstract float getSpeed();
	
	public abstract GameTimer setSpeed(int speed);
	
	
	public abstract GameTimer setInfiniteFps(boolean flag);
	
	public abstract boolean getInfiniteFps();
	
	
	public abstract float getPartialTick();
	
	
	public abstract GameTimer setRender(Runnable hook);
	
	public abstract GameTimer runRender();
	
	
	public abstract GameTimer setUpdate(Runnable hook);
	
	public abstract GameTimer runUpdate();
}
