package com.lapissea.opengl.program.core;

import java.util.Objects;

import com.lapissea.opengl.program.util.UtilM;

public class Timer implements Runnable{
	
	private static final int SECOND=1000_000_000;
	
	private long		lastTimeUpdated;
	private long		lastTimeRendered;
	/**update period*/
	private long		up;
	/**frame period*/
	private long		fp;
	private long		lastSec;
	private int			updateQueue;
	private int			updatesCount;
	private int			framesCount;
	private int			ups;
	private int			fps;
	private int			ctxUpdates;
	private int			ctxFrames;
	private boolean		render;
	private boolean		running		=true;
	private boolean		restrictFps	=true;
	private float		partialUpdate;
	private Runnable	updateHook;
	private Runnable	renderHook;
	
	public Timer(int ups, int fps, Runnable updateHook, Runnable renderHook){
		setUPS(ups);
		setFPS(fps);
		setUpdateHook(updateHook);
		setRenderHook(renderHook);
	}
	
	private long time(){
		return System.nanoTime();
	}
	
	private void update(){
		
		long time=time();
		if(time-lastTimeUpdated>=up){
			lastTimeUpdated=time;
			updateQueue++;
		}
		time=time();
		if(!restrictFps||time-lastTimeRendered>=fp){
			render=true;
		}
		time=time();
		if(time-lastSec>=SECOND){
			lastSec=time;
			ctxUpdates=updatesCount;
			ctxFrames=framesCount;
			//LogUtil.println("fps", getFps(), "ups", getUps());
			updatesCount=0;
			framesCount=0;
		}
		//UtilM.sleep(0, 500000);
	}
	
	public void runUpdateRender(){
		while(updateQueue>0){
			updateHook.run();
			updateQueue--;
			updatesCount++;
		}
		
		if(!restrictFps) render();
		else if(render){
			render=false;
			render();
			lastTimeRendered=time();
			throttleLooping();
		}
		UtilM.sleep(0, 500000);
	}
	
	private void render(){
		partialUpdate=(float)(((double)(time())-lastTimeUpdated)/SECOND*ups);
		renderHook.run();
		framesCount++;
	}
	
	@Override
	public void run(){
		running=true;
		while(running){
			update();
			throttleLooping();
		}
	}
	
	/**
	 * Save CPU from checking if anything should update many times in a millisecond
	 */
	private void throttleLooping(){
		if(restrictFps&&fps<ctxFrames*1.2&&ups<ctxUpdates*1.2) UtilM.sleep(0, 500000);
	}
	
	public void end(){
		running=false;
	}
	
	public void setUPS(int ups){
		this.ups=ups;
		up=SECOND/ups;
	}
	
	public void setFPS(int fps){
		this.fps=fps;
		fp=SECOND/fps;
	}
	
	public int getFps(){
		return ctxFrames;
	}
	
	public int getUps(){
		return ctxUpdates;
	}
	
	public float getPartialTick(){
		return partialUpdate;
	}
	
	public int getTargetedFps(){
		return restrictFps?fps:(int)Double.POSITIVE_INFINITY;
	}
	
	public int getTargetedUps(){
		return ups;
	}
	
	public void setFpsRestriction(boolean restrict){
		restrictFps=restrict;
	}
	
	public boolean isRunning(){
		return running;
	}

	
	public void setUpdateHook(Runnable updateHook){
		this.updateHook=Objects.requireNonNull(updateHook);
	}

	
	public void setRenderHook(Runnable renderHook){
		this.renderHook=Objects.requireNonNull(renderHook);
	}
	
}
