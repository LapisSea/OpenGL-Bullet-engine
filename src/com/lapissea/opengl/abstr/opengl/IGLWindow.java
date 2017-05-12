package com.lapissea.opengl.abstr.opengl;

import org.lwjgl.LWJGLException;

import com.lapissea.opengl.abstr.opengl.events.FocusEvent.IFocusHook;
import com.lapissea.opengl.abstr.opengl.events.KeyEvent.IKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent.IMouseKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseMoveEvent.IMouseMoveEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseScrollEvent.IMouseScrollEventHook;
import com.lapissea.opengl.abstr.opengl.events.ResizeEvent.IResizeHook;
import com.lapissea.opengl.program.util.math.vec.Vec2i;

public interface IGLWindow{
	
	IGLWindow create() throws LWJGLException;
	
	default IGLWindow setPos(Vec2i pos){
		return setPos(pos.x(), pos.y());
	}
	
	IGLWindow setPos(int x, int y);
	
	Vec2i getPos();
	
	default IGLWindow setSize(Vec2i size){
		return setSize(size.x(), size.y());
	}
	
	IGLWindow setSize(int x, int y);
	
	Vec2i getSize();
	
	IGLWindow setTitle(String title);
	
	IGLWindow setResizable(boolean flag);
	
	IGLWindow setFullScreen(boolean flag);
	
	IGLWindow setVSync(boolean flag);
	
	boolean isClosed();
	
	IGLWindow setEventHook(IKeyEventHook hook);
	
	IGLWindow setEventHook(IMouseKeyEventHook hook);
	
	IGLWindow setEventHook(IMouseMoveEventHook hook);
	
	IGLWindow setEventHook(IMouseScrollEventHook hook);
	
	IGLWindow setEventHook(IFocusHook hook);
	
	IGLWindow setEventHook(IResizeHook hook);
	
	default IGLWindow setEventHooks(Object hook){
		if(hook instanceof IKeyEventHook) setEventHook((IKeyEventHook)hook);
		if(hook instanceof IMouseKeyEventHook) setEventHook((IMouseKeyEventHook)hook);
		if(hook instanceof IMouseMoveEventHook) setEventHook((IMouseMoveEventHook)hook);
		if(hook instanceof IMouseScrollEventHook) setEventHook((IMouseScrollEventHook)hook);
		if(hook instanceof IFocusHook) setEventHook((IFocusHook)hook);
		if(hook instanceof IResizeHook) setEventHook((IResizeHook)hook);
		return this;
	}
	
	void updateInput();
	
	void swapBuffers(int targetedFps);
	
	boolean isFocused();
	
	void centerMouse();
	
	ILWJGLCtx getCtx();
	
	void destroy();
}
