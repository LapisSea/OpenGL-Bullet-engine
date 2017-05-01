package com.lapissea.opengl.program.core;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.abstr.opengl.ILWJGLCtx;
import com.lapissea.opengl.abstr.opengl.events.FocusEvent;
import com.lapissea.opengl.abstr.opengl.events.FocusEvent.IFocusHook;
import com.lapissea.opengl.abstr.opengl.events.Initable;
import com.lapissea.opengl.abstr.opengl.events.InputEvents;
import com.lapissea.opengl.abstr.opengl.events.KeyEvent;
import com.lapissea.opengl.abstr.opengl.events.KeyEvent.IKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent;
import com.lapissea.opengl.abstr.opengl.events.MouseKeyEvent.IMouseKeyEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseMoveEvent;
import com.lapissea.opengl.abstr.opengl.events.MouseMoveEvent.IMouseMoveEventHook;
import com.lapissea.opengl.abstr.opengl.events.MouseScrollEvent;
import com.lapissea.opengl.abstr.opengl.events.MouseScrollEvent.IMouseScrollEventHook;
import com.lapissea.opengl.abstr.opengl.events.Renderable;
import com.lapissea.opengl.abstr.opengl.events.ResizeEvent;
import com.lapissea.opengl.abstr.opengl.events.ResizeEvent.IResizeHook;
import com.lapissea.opengl.abstr.opengl.events.Updateable;
import com.lapissea.opengl.abstr.opengl.events.WindowEvents;

public class Registry implements Updateable,Initable,WindowEvents,InputEvents{
	
	private final List<Updateable>				updateModules		=new ArrayList<>();
	private final List<Renderable>				renderModules		=new ArrayList<>();
	private final List<Initable>				initModules			=new ArrayList<>();
	private final List<WindowEvents>			windowEvents		=new ArrayList<>();
	private final List<IKeyEventHook>			keyEvents			=new ArrayList<>();
	private final List<IMouseKeyEventHook>		mouseKeyEvents		=new ArrayList<>();
	private final List<IMouseMoveEventHook>		mousemoveEvents		=new ArrayList<>();
	private final List<IMouseScrollEventHook>	mouseScrollEvents	=new ArrayList<>();
	private final List<IFocusHook>				focusEvents			=new ArrayList<>();
	private final List<IResizeHook>				resizeEvents		=new ArrayList<>();
	
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
		
		if(listener instanceof IKeyEventHook) add(keyEvents, (IKeyEventHook)listener);
		if(listener instanceof IMouseKeyEventHook) add(mouseKeyEvents, (IMouseKeyEventHook)listener);
		if(listener instanceof IMouseMoveEventHook) add(mousemoveEvents, (IMouseMoveEventHook)listener);
		if(listener instanceof IMouseScrollEventHook) add(mouseScrollEvents, (IMouseScrollEventHook)listener);
		if(listener instanceof IFocusHook) add(focusEvents, (IFocusHook)listener);
		if(listener instanceof IResizeHook) add(resizeEvents, (IResizeHook)listener);
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
