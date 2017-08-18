package com.lapissea.opengl.program.rendering.gl.gui.elements;

import com.lapissea.opengl.program.rendering.gl.gui.GuiElement;
import com.lapissea.opengl.program.util.math.vec.FloatSmooth;
import com.lapissea.opengl.window.api.events.MouseButtonEvent;
import com.lapissea.opengl.window.api.events.MouseButtonEvent.Action;
import com.lapissea.opengl.window.api.events.MouseButtonEvent.Button;
import com.lapissea.opengl.window.api.events.MouseMoveEvent;

public class GuiMouseReact extends GuiElement{
	
	protected FloatSmooth	highlightProgress	=new FloatSmooth(),activateProgress=new FloatSmooth();
	private boolean			mouseOver,mouseDown;
	
	protected float selectSpeed=5,unselectSpeed=6,activateSpeed=2,unactivateSpeed=3;
	
	@Override
	public void update(){
		super.update();
		upd(highlightProgress, isMouseOver(), selectSpeed, unselectSpeed);
		upd(activateProgress, isMouseDown(), activateSpeed, unactivateSpeed);
	}
	
	protected void upd(FloatSmooth value, boolean flag, float add, float sub){
		value.update();
		if(flag){
			if(value.getValue()<1){
				value.setValue(value.getValue()+1/add);
				if(value.getValue()>1) value.setValue(1);
			}
		}else{
			if(value.getValue()>0){
				value.setValue(value.getValue()-1/sub);
				if(value.getValue()<0) value.setValue(0);
			}
		}
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent e){
		getAbsolutePos(VEC2);
		
		boolean n6w=VEC2.x()<=MOUSE.x()&&
				VEC2.y()<=MOUSE.y()&&
				VEC2.x()+size.x()>=MOUSE.x()&&
				VEC2.y()+size.y()>=MOUSE.y();
		
		if(mouseOver!=n6w){
			mouseOver=n6w;
			if(n6w) onMouseEnter();
			else onMouseExit();
		}
	}
	
	@Override
	public void onMouseButton(MouseButtonEvent e){
		if(!isMouseOver()) return;
		if(e.button==Button.RIGHT){
			mouseDown=e.action==Action.DOWN;
			if(!mouseDown&&isMouseOver()) onClick();
		}
	}
	
	protected void onMouseEnter(){}
	
	protected void onMouseExit(){
		mouseDown=false;
	}
	
	protected void onClick(){}
	
	public boolean isMouseDown(){
		return mouseDown;
	}
	
	@Override
	public boolean isMouseOver(){
		return mouseOver;
	}
}
