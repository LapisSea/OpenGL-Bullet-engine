package com.lapissea.opengl.program.rendering.gl;

import java.util.function.IntSupplier;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import com.lapissea.opengl.abstr.opengl.IGLWindow;
import com.lapissea.opengl.abstr.opengl.ILWJGLCtx;
import com.lapissea.opengl.abstr.opengl.events.FocusEvent;
import com.lapissea.opengl.abstr.opengl.events.FocusEvent.IFocusHook;
import com.lapissea.opengl.abstr.opengl.events.KeyEvent;
import com.lapissea.opengl.abstr.opengl.events.KeyEvent.IKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.KeyEvent.KeyAction;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent.Action;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent.Button;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent.IMouseKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseMoveEvent;
import com.lapissea.opengl.abstr.opengl.events.MouseMoveEvent.IMouseMoveEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseScrollEvent;
import com.lapissea.opengl.abstr.opengl.events.MouseScrollEvent.IMouseScrollEventHook;
import com.lapissea.opengl.abstr.opengl.events.ResizeEvent;
import com.lapissea.opengl.abstr.opengl.events.ResizeEvent.IResizeHook;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;

public class LWJGL2Window implements IGLWindow{
	
	private final BooleanList prevKbKeys=new BooleanArrayList(),prevMsKeys=new BooleanArrayList();
	
	private class Vec2iVal extends Vec2i{
		
		final IntSupplier x,y;
		
		public Vec2iVal(IntSupplier x, IntSupplier y){
			
			this.x=x;
			this.y=y;
		}
		
		@Override
		public int x(){
			return x.getAsInt();
		}
		
		@Override
		public int y(){
			return y.getAsInt();
		}
	}
	
	private final Vec2i		pos	=new Vec2iVal(Display::getX, Display::getY);
	private final Vec2i		size=new Vec2iVal(Display::getWidth, Display::getHeight);
	private final ILWJGLCtx	ctx;
	
	public LWJGL2Window(ILWJGLCtx ctx){
		this.ctx=ctx;
	}
	
	@Override
	public ILWJGLCtx getCtx(){
		return ctx;
	}
	
	@Override
	public IGLWindow setSize(Vec2i size){
		try{
			Display.setDisplayMode(new DisplayMode(Math.max(size.x(), 100), Math.max(size.y(), 100)));
		}catch(LWJGLException e){
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public IGLWindow setFullScreen(boolean fullScreen0){
		try{
			Display.setFullscreen(fullScreen0);
		}catch(LWJGLException e){
			e.printStackTrace();
		}
		return this;
	}
	
	public static boolean isFullscreen(){
		return Display.isFullscreen();
	}
	
	@Override
	public IGLWindow setPos(Vec2i pos){
		setPos(pos.x(), pos.y());
		return this;
	}
	
	public void setPos(int x, int y){
		if(!isFullscreen()) Display.setLocation(x, y);
	}
	
	@Override
	public void swapBuffers(int fps){
		Display.update();
		//		Display.setVSyncEnabled(false);
		Display.sync(fps);
	}
	
	public void closeWindow(){
		Display.destroy();
	}
	
	@Override
	public void centerMouse(){
		Mouse.setCursorPosition(Display.getWidth()/2, Display.getHeight()/2);
	}
	
	@Override
	public boolean isClosed(){
		return Display.isCloseRequested();
	}
	
	@Override
	public boolean isFocused(){
		return Display.isActive();
	}
	
	public boolean active3d(){
		return isFocused()&&Mouse.isGrabbed();
	}
	
	@Override
	public IGLWindow create() throws LWJGLException{
		
		LogUtil.println("Creating window...");
		
		setPos(pos);
		setSize(size);
		PixelFormat format=new PixelFormat();
		ContextAttribs ctx=new ContextAttribs(3, 2).withForwardCompatible(true).withProfileCore(true);
		Display.create(format, null, ctx);
		
		GL11.glClearColor(1, 1, 1, 1);
		
		Mouse.create();
		Keyboard.create();
		
		LogUtil.println("Window created!");
		LogUtil.printWrapped("OPEN_GL_INFO\nLWJGL version:"+Sys.getVersion()+"\nOpenGL version:"+GL11.glGetString(GL11.GL_VERSION)+"\nOPEN_GL_INFO");
		
		return this;
	}
	
	@Override
	public IGLWindow setTitle(String title){
		Display.setTitle(title);
		return this;
	}
	
	@Override
	public IGLWindow setResizable(boolean flag){
		Display.setResizable(flag);
		return this;
	}
	
	@Override
	public IGLWindow setVSync(boolean flag){
		Display.setVSyncEnabled(flag);
		return this;
	}
	
	@Override
	public Vec2i getPos(){
		return pos;
	}
	
	@Override
	public Vec2i getSize(){
		return size;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////
	
	private int prevMouseX=-1,prevMouseY=-1,prevSizeW,prevSizeH;
	
	private boolean PREV_FOCUS;
	
	protected IKeyEventHook			keyKb;
	protected IMouseKeyEventHook	keyMs;
	protected IMouseMoveEventHook	moveMs;
	protected IMouseScrollEventHook	scrollMs;
	protected IFocusHook			focus;
	protected IResizeHook			resize;
	
	@Override
	public IGLWindow setEventHook(IKeyEventHook hook){
		keyKb=hook;
		return this;
	}
	
	@Override
	public IGLWindow setEventHook(IMouseKeyEventHook hook){
		keyMs=hook;
		return this;
	}
	
	@Override
	public IGLWindow setEventHook(IMouseMoveEventHook hook){
		moveMs=hook;
		return this;
	}
	
	@Override
	public IGLWindow setEventHook(IMouseScrollEventHook hook){
		scrollMs=hook;
		return this;
	}
	
	@Override
	public IGLWindow setEventHook(IFocusHook hook){
		focus=hook;
		return this;
	}
	
	@Override
	public IGLWindow setEventHook(IResizeHook hook){
		resize=hook;
		return this;
	}
	
	@Override
	public void updateInput(){
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		if(moveMs!=null){
			int x=Mouse.getX();
			int y=Mouse.getY();
			if(prevMouseX!=x||prevMouseY!=y){
				moveMs.onMouseMoveEvent(new MouseMoveEvent(this, prevMouseX, prevMouseY, Mouse.getEventDX(), Mouse.getEventDY()));
				prevMouseX=x;
				prevMouseY=y;
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
		if(keyMs!=null){
			while(Mouse.next()){
				int key=Mouse.getEventButton();
				if(key!=-1){
					boolean state=Mouse.getEventButtonState();
					set(prevMsKeys, key, state);
					Button b=MouseKeyEvent.buttonFromInt(key);
					if(b!=null) keyMs.onMouseKeyEvent(new MouseKeyEvent(this, state?Action.PRESS:Action.RELEASE, b));
				}
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
		if(keyKb!=null){
			while(Keyboard.next()){
				int key=Keyboard.getEventKey();
				if(key!=-1){
					boolean state=Keyboard.getEventKeyState();
					set(prevKbKeys, key, state);
					
					keyKb.onKeyEvent(new KeyEvent(this, state?Keyboard.isRepeatEvent()?KeyAction.HOLD:KeyAction.PRESS:KeyAction.RELEASE, key));
				}
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
		if(resize!=null){
			int w=Display.getWidth();
			int h=Display.getHeight();
			
			if(prevSizeW!=w||prevSizeH!=h){
				int prevW=prevSizeW;
				int prevH=prevSizeH;
				prevSizeW=w;
				prevSizeH=h;
				
				//-----------
				if(Mouse.isGrabbed()) centerMouse();
				GL11.glViewport(0, 0, w, h);
				//-----------
				
				resize.onResizeEvent(new ResizeEvent(this, prevW, prevH, w, h));
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
		if(focus!=null){
			boolean focusBol=Display.isActive();
			if(PREV_FOCUS!=focusBol){
				PREV_FOCUS=focusBol;
				focus.onFocusEvent(new FocusEvent());
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
		if(scrollMs!=null){
			if(Mouse.hasWheel()){
				int moved=Mouse.getDWheel();
				if(moved!=0) scrollMs.onMouseScrollEvent(new MouseScrollEvent(this, moved));
			}
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////
	}
	
	private void set(BooleanList list, int id, boolean value){
		while(list.size()<=id)
			list.add(false);
		list.set(id, value);
	}
	
	@Override
	public void destroy(){
		Display.destroy();
	}
	
}
