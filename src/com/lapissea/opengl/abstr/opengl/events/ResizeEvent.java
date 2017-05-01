package com.lapissea.opengl.abstr.opengl.events;

import com.lapissea.opengl.abstr.opengl.IGLWindow;

public class ResizeEvent extends WindowEvent{
	
	public static interface IResizeHook{
		
		void onResizeEvent(ResizeEvent e);
	}
	
	public final int oldWidth,oldHeight,newWidth,newHeight;
	
	public ResizeEvent(IGLWindow source, int oldWidth, int oldHeight, int newWidth, int newHeight){
		super(source);
		this.oldWidth=oldWidth;
		this.oldHeight=oldHeight;
		this.newWidth=newWidth;
		this.newHeight=newHeight;
	}
	
}
