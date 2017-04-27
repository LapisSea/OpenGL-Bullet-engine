package com.lapissea.opengl.program.rendering.gl;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

import com.lapissea.opengl.program.core.Registry;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.WindowInput;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public class Window{
	
	private static int		MAX_OPENGL	=-1;
	private static boolean	fullScreen;
	
	public static final Vec2i POS=new Vec2i(){
		
		@Override
		public int x(){
			return Display.getX();
		}
		
		@Override
		public int y(){
			return Display.getY();
		}
	};
	
	public static final Vec2i SIZE=new Vec2i(){
		
		@Override
		public int x(){
			return Display.getWidth();
		}
		
		@Override
		public int y(){
			return Display.getHeight();
		}
	};
	
	public static Registry EVENT_HOOK;
	
	public static void init(Vec2i pos, Vec2i size, String title, boolean fullScreen) throws LWJGLException{
		LogUtil.println("Creating window...");
		//System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		Display.create(new PixelFormat(), new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true));
		Display.setDisplayMode(new DisplayMode(Math.max(size.x(), 100), Math.max(size.y(), 100)));
		Display.setTitle(title);
		Display.setResizable(true);
		setFullScreen(fullScreen);
		setPos(pos);
		
		Mouse.create();
		Keyboard.create();

		LogUtil.println("Window created!");
		LogUtil.printWrapped("OPEN_GL_INFO\nLWJGL version:"+Sys.getVersion()+"\nOpenGL version:"+GL11.glGetString(GL11.GL_VERSION)+"\nOPEN_GL_INFO");
		
		ContextCapabilities caps=GLContext.getCapabilities();
		
		if(caps.OpenGL11) MAX_OPENGL=11;
		if(caps.OpenGL12) MAX_OPENGL=12;
		if(caps.OpenGL13) MAX_OPENGL=13;
		if(caps.OpenGL14) MAX_OPENGL=14;
		if(caps.OpenGL15) MAX_OPENGL=15;
		
		if(caps.OpenGL20) MAX_OPENGL=20;
		if(caps.OpenGL21) MAX_OPENGL=21;
		
		if(caps.OpenGL30) MAX_OPENGL=30;
		if(caps.OpenGL31) MAX_OPENGL=31;
		if(caps.OpenGL32) MAX_OPENGL=32;
		if(caps.OpenGL33) MAX_OPENGL=33;
		
		if(caps.OpenGL40) MAX_OPENGL=40;
		if(caps.OpenGL41) MAX_OPENGL=41;
		if(caps.OpenGL42) MAX_OPENGL=42;
		if(caps.OpenGL43) MAX_OPENGL=43;
		if(caps.OpenGL44) MAX_OPENGL=44;
		if(caps.OpenGL45) MAX_OPENGL=45;
		
		LogUtil.println("OpenGL version marked as:",MAX_OPENGL);
		Display.setVSyncEnabled(true);
		setupWindowInput();
		GL11.glClearColor(0, 0, 0, 0);
	}
	
	public static void setFullScreen(boolean fullScreen0){
		fullScreen=fullScreen0;
		try{
			Display.setFullscreen(fullScreen0);
		}catch(LWJGLException e){
			e.printStackTrace();
		}
	}
	
	public static boolean isFullscreen(){
		return fullScreen;
	}
	
	public static void setPos(Vec2i pos){
		setPos(pos.x(), pos.y());
	}
	
	public static void setPos(int x, int y){
		if(!isFullscreen()) Display.setLocation(x, y);
	}
	
	private static void setupWindowInput(){
		WindowInput.windowSizeCallback(()->{
			if(Mouse.isGrabbed())Window.centerMouse();
			
			int w=Display.getWidth();
			int h=Display.getHeight();
			GL11.glViewport(0, 0, w, h);
			if(EVENT_HOOK!=null) EVENT_HOOK.onResize(w, h);
		});
		
		WindowInput.keyboardKeyCallback(e->{
			if(EVENT_HOOK!=null) EVENT_HOOK.onKey(e);
		});
		
		WindowInput.mouseKeyCallback(e->{
			if(EVENT_HOOK!=null) EVENT_HOOK.onClick(e);
		});
		
		WindowInput.mouseMoveCallback(e->{
			
		});
		WindowInput.windowFocusCallback(()->{//focus event
		});
	}
	
	public static void swapBuffers(int fps){
		Display.update();
//		Display.setVSyncEnabled(false);
		Display.sync(fps);
	}
	
	public static void closeWindow(){
		Display.destroy();
	}
	
	public static void centerMouse(){
		Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
	}
	
	public static boolean isClosed(){
		return Display.isCloseRequested();
	}
	
	public static boolean isFocused(){
		return Display.isActive();
	}
	
	public static boolean active3d(){
		return isFocused()&&Mouse.isGrabbed();
	}
}
