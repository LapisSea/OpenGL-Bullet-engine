package com.lapissea.opengl.program.rendering.gl.gui.guis;

import com.lapissea.opengl.program.rendering.gl.gui.Gui;
import com.lapissea.opengl.program.rendering.gl.gui.GuiElementMaterial;
import com.lapissea.opengl.program.rendering.gl.gui.GuiFlow.SizeCalcStatic;
import com.lapissea.opengl.program.rendering.gl.gui.elements.GuiButton;
import com.lapissea.opengl.program.rendering.gl.gui.elements.GuiLabel;
import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiPause extends Gui{
	
	public GuiPause(){
		background.color.set(0F, 0F, 0F, 0.4F);
		
		GuiLabel l=new GuiLabel("Paused", 100);
		l.preferedX=Align.CENTER;
		l.margin.top=20;
		addChild(l);
		
		GuiButton e=new GuiButton();
		e.preferedX=e.preferedY=Align.CENTER;
		e.preferedWidth=new SizeCalcStatic(300);
		e.preferedHeight=new SizeCalcStatic(100);
		
		e.backgroundNormal=new GuiElementMaterial(7, new ColorM(0, 0.2, 0.3, 0.3), -1);
		e.borderNormal=new GuiElementMaterial(15, new ColorM(0.6, 0.8, 1, 1), 200);
		
		e.borderHighlight.color=new ColorM(1,1,0,0.6);
		//e.backgroundHighlight.color=new ColorM(0,0,1,0.4);
		e.backgroundHighlight.blurRad=0;
		
		e.borderActive.color=new ColorM(1,0,0,1);
		e.backgroundActive.color=new ColorM(0,1,1,0.2);
		e.backgroundActive.blurRad=0;
		
		e.borderWidth=4;
		addChild(e); 
		e.onClick=this::close;
		
		l=new GuiLabel("Continue", 50){
			@Override
			public void update(){
				super.update();
			}
		};
		l.preferedX=Align.CENTER;
		l.preferedY=Align.CENTER;
		e.addChild(l);
	}
	
	@Override
	public boolean pausesGame(){
//		background.color.set(1F, 0F, 0F, 0.4F);
		return true;
	}
}
