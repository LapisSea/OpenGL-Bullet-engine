package com.lapissea.opengl.program.gui.elements;

import com.lapissea.opengl.program.gui.GuiElementMaterial;

public class GuiButton extends GuiMouseReact{
	
	public GuiElementMaterial	borderNormal	=new GuiElementMaterial(),backgroundNormal=new GuiElementMaterial();
	public GuiElementMaterial	borderHighlight	=new GuiElementMaterial(true),backgroundHighlight=new GuiElementMaterial(true);
	public GuiElementMaterial	borderActive	=new GuiElementMaterial(true),backgroundActive=new GuiElementMaterial(true);
	
	public Runnable onClick;
	
	@Override
	protected void onClick(){
		if(onClick!=null) onClick.run();
	}
	
	@Override
	public GuiElementMaterial getRenderBackground(){
		float h=highlightProgress.get(),a=activateProgress.get();
		return background.interpolate(backgroundNormal, backgroundHighlight, h).interpolate(backgroundActive, a);
	}
	
	@Override
	public GuiElementMaterial getRenderBorder(){
//		float h=highlightProgress.get(),a=activateProgress.get();
		float h=highlightProgress.get(),a=activateProgress.get();
		return border.interpolate(borderNormal, borderHighlight, h).interpolate(borderActive, a);
	}
	
	@Override
	public void updateFlow(){
		super.updateFlow();
		size.set(elementSize);
	}
}
