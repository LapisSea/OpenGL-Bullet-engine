package com.lapissea.opengl.program.core;

import java.util.ArrayList;
import java.util.List;

import com.lapissea.opengl.program.events.KeyEvent;
import com.lapissea.opengl.program.events.MouseKeyEvent;
import com.lapissea.opengl.program.interfaces.FullGameHook;
import com.lapissea.opengl.program.interfaces.Initable;
import com.lapissea.opengl.program.interfaces.InputEvents;
import com.lapissea.opengl.program.interfaces.Renderable;
import com.lapissea.opengl.program.interfaces.Updateable;
import com.lapissea.opengl.program.interfaces.WindowEvents;

public class Registry implements Updateable,Initable,WindowEvents,InputEvents{
	
	private final List<FullGameHook>	fullModules		=new ArrayList<>();
	private final List<Updateable>		updateModules	=new ArrayList<>();
	private final List<Renderable>		renderModules	=new ArrayList<>();
	private final List<Initable>		initModules		=new ArrayList<>();
	private final List<WindowEvents>	windowEvents	=new ArrayList<>();
	private final List<InputEvents>		inputEvents		=new ArrayList<>();
	
	private <T> void add(List<T> modules, T module){
		if(modules.contains(module)) throw new IllegalArgumentException("This module is already registered!");
		modules.add(module);
	}
	
	public void register(Object listener){
		if(listener instanceof FullGameHook) add(fullModules, (FullGameHook)listener);
		else{
			if(listener instanceof Updateable) add(updateModules, (Updateable)listener);
			if(listener instanceof Renderable) add(renderModules, (Renderable)listener);
			if(listener instanceof Initable) add(initModules, (Initable)listener);
		}
		if(listener instanceof WindowEvents) add(windowEvents, (WindowEvents)listener);
		if(listener instanceof InputEvents) add(inputEvents, (InputEvents)listener);
	}
	
	@Override
	public void onResize(int width, int height){
		windowEvents.forEach(e->e.onResize(width, height));
	}
	
	@Override
	public void onKey(KeyEvent event){
		inputEvents.forEach(e->e.onKey(event));
	}
	
	@Override
	public void onClick(MouseKeyEvent event){
		inputEvents.forEach(e->e.onClick(event));
	}
	
	@Override
	public void preInit(){
		initModules.forEach(Initable::preInit);
		fullModules.forEach(Initable::preInit);
	}
	
	@Override
	public void init(){
		initModules.forEach(Initable::init);
		fullModules.forEach(Initable::init);
	}
	
	@Override
	public void postInit(){
		initModules.forEach(Initable::postInit);
		fullModules.forEach(Initable::postInit);
	}
	
	@Override
	public void update(){
		updateModules.forEach(Updateable::update);
		fullModules.forEach(Updateable::update);
	}

}
