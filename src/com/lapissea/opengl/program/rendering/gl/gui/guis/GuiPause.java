package com.lapissea.opengl.program.rendering.gl.gui.guis;

import com.lapissea.opengl.program.rendering.gl.gui.Gui;
import com.lapissea.opengl.program.rendering.gl.gui.GuiElement;
import com.lapissea.opengl.program.rendering.gl.gui.GuiElementMaterial;
import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiPause extends Gui{
	
	public GuiPause(){
		background.color.set(0F, 0F, 0F, 0.4F);
		GuiElement e;
		for(int x=0;x<5;x++){
			for(int y=0;y<5;y++){
				children.add(e=new GuiElement(this));
				e.getSize().set(100, 100);
				e.getPos().x(x*105).y(y*105);
				e.background=new GuiElementMaterial(6, new ColorM(0, 0.2, 0.3, 0.3), -1);
				e.border=new GuiElementMaterial(15, new ColorM(0.6, 0.8, 1, 1), 150);
				e.borderWidth=4;
			}
		}
	}
	
	@Override
	public boolean pausesGame(){
		return true;
	}
}
