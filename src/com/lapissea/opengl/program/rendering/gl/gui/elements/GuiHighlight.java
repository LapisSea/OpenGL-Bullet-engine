package com.lapissea.opengl.program.rendering.gl.gui.elements;

import com.lapissea.opengl.program.rendering.gl.gui.GuiElement;
import com.lapissea.opengl.window.api.util.color.ColorM;


public class GuiHighlight extends GuiElement{
	
	protected float highlightProgress;
	
	public GuiHighlight(GuiElement parent){
		super(parent);
	}
	
	@Override
	public void update(){
		super.update();
		
		if(parent!=null){
			if(isMouseOver()) new ColorM(1, 1, 0, 0.3).mix(background.color, 1, 0.1F);
			else new ColorM(0, 1, 1, 0.3).mix(background.color, 1, 0.1F);
		}
	}
	
}
