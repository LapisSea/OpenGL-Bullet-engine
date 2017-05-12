package com.lapissea.opengl.program.util;

public class NanoTimer{
	
	private long	start;
	private long[]	data	=new long[100];
	private int		pos		=99,count;
	private boolean	started	=false;
	
	public void start(){
		started=true;
		start=now();
	}
	
	public void end(){
		if(!started) throw new IllegalStateException("Not started");
		pos=(pos+1)%100;
		data[pos]=now()-start;
		if(count<100) count++;
		
	}
	
	public double msAvrg100(){
		long sum=0;
		for(int i=0;i<count;i++){
			sum+=data[i];
		}
		return toMs(sum/count);
	}
	
	public double ms(){
		return toMs(data[pos]);
	}
	
	private static long now(){
		return System.nanoTime();
	}
	
	private static double toMs(long nano){
		return Math.round(nano/10000D)/100D;
	}
	
}
