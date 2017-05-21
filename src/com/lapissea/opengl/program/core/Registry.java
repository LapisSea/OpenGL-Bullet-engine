package com.lapissea.opengl.program.core;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.program.game.events.Initable;
import com.lapissea.opengl.program.game.events.Renderable;
import com.lapissea.opengl.program.game.events.Updateable;
import com.lapissea.opengl.window.api.ILWJGLCtx;
import com.lapissea.opengl.window.api.events.FocusEvent;
import com.lapissea.opengl.window.api.events.KeyEvent;
import com.lapissea.opengl.window.api.events.MouseKeyEvent;
import com.lapissea.opengl.window.api.events.MouseMoveEvent;
import com.lapissea.opengl.window.api.events.MouseScrollEvent;
import com.lapissea.opengl.window.api.events.ResizeEvent;
import com.lapissea.opengl.window.api.events.FocusEvent.IFocusEventListener;
import com.lapissea.opengl.window.api.events.KeyEvent.IKeyEventListener;
import com.lapissea.opengl.window.api.events.MouseKeyEvent.IMouseButtonEventListener;
import com.lapissea.opengl.window.api.events.MouseMoveEvent.IMouseMoveEventListener;
import com.lapissea.opengl.window.api.events.MouseScrollEvent.IMouseScrollEventListener;
import com.lapissea.opengl.window.api.events.ResizeEvent.IResizeEventListener;
import com.lapissea.opengl.window.api.events.util.InputEvents;
import com.lapissea.opengl.window.api.events.util.WindowEvents;

public class Registry implements Updateable,Initable,WindowEvents,InputEvents{
	
	private final List<Updateable>				updateModules		=new ArrayList<>();
	private final List<Renderable>				renderModules		=new ArrayList<>();
	private final List<Initable>				initModules			=new ArrayList<>();
	private final List<WindowEvents>			windowEvents		=new ArrayList<>();
	private final List<IKeyEventListener>			keyEvents			=new ArrayList<>();
	private final List<IMouseButtonEventListener>		mouseKeyEvents		=new ArrayList<>();
	private final List<IMouseMoveEventListener>		mousemoveEvents		=new ArrayList<>();
	private final List<IMouseScrollEventListener>	mouseScrollEvents	=new ArrayList<>();
	private final List<IFocusEventListener>				focusEvents			=new ArrayList<>();
	private final List<IResizeEventListener>				resizeEvents		=new ArrayList<>();
	
	public void bindWindow(ILWJGLCtx ctx){
		ctx.getCtxWindow()
				.setEventHook(this::onKeyEvent)
				.setEventHook(this::onMouseKeyEvent)
				.setEventHook(this::onMouseMoveEvent)
				.setEventHook(this::onMouseScrollEvent)
				.setEventHook(this::onFocusEvent)
				.setEventHook(this::onResizeEvent);
	}
	
	private <T> void add(List<T> modules, T module){
		if(modules.contains(module)) throw new IllegalArgumentException("This module is already registered!");
		modules.add(module);
	}
	
	public void register(Object listener){
		if(listener instanceof Updateable) add(updateModules, (Updateable)listener);
		if(listener instanceof Renderable) add(renderModules, (Renderable)listener);
		if(listener instanceof Initable) add(initModules, (Initable)listener);
		
		if(listener instanceof IKeyEventListener) add(keyEvents, (IKeyEventListener)listener);
		if(listener instanceof IMouseButtonEventListener) add(mouseKeyEvents, (IMouseButtonEventListener)listener);
		if(listener instanceof IMouseMoveEventListener) add(mousemoveEvents, (IMouseMoveEventListener)listener);
		if(listener instanceof IMouseScrollEventListener) add(mouseScrollEvents, (IMouseScrollEventListener)listener);
		if(listener instanceof IFocusEventListener) add(focusEvents, (IFocusEventListener)listener);
		if(listener instanceof IResizeEventListener) add(resizeEvents, (IResizeEventListener)listener);
	}
	
	@Override
	public void onResizeEvent(ResizeEvent e){
		windowEvents.forEach(e0->e0.onResizeEvent(e));
	}
	
	@Override
	public void preInit(){
		initModules.forEach(Initable::preInit);
	}
	
	@Override
	public void init(){
		initModules.forEach(Initable::init);
	}
	
	@Override
	public void postInit(){
		initModules.forEach(Initable::postInit);
	}
	
	@Override
	public void update(){
		updateModules.forEach(Updateable::update);
	}
	
	@Override
	public void onKeyEvent(KeyEvent e){
		keyEvents.forEach(e0->e0.onKeyEvent(e));
	}
	
	@Override
	public void onMouseKeyEvent(MouseKeyEvent e){
		mouseKeyEvents.forEach(e0->e0.onMouseKeyEvent(e));
	}
	
	@Override
	public void onMouseMoveEvent(MouseMoveEvent e){
		mousemoveEvents.forEach(e0->e0.onMouseMoveEvent(e));
	}
	
	@Override
	public void onMouseScrollEvent(MouseScrollEvent e){
		mouseScrollEvents.forEach(e0->e0.onMouseScrollEvent(e));
	}
	
	@Override
	public void onFocusEvent(FocusEvent e){
		focusEvents.forEach(e0->e0.onFocusEvent(e));
	}
	
}
