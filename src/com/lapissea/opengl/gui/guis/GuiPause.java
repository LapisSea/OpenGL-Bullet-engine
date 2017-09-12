package com.lapissea.opengl.gui.guis;

import com.lapissea.opengl.gui.Gui;
import com.lapissea.opengl.gui.GuiElementMaterial;
import com.lapissea.opengl.gui.GuiFlow.SizeCalcStatic;
import com.lapissea.opengl.gui.elements.GuiButton;
import com.lapissea.opengl.gui.elements.GuiLabel;
import com.lapissea.opengl.window.api.util.color.ColorM;

public class GuiPause extends Gui{
	
	public GuiPause(){
		background.color.set(0F, 0F, 0F, 0.3F);
		
		GuiLabel l=new GuiLabel("Paused", 100);
		l.preferedX=Align.CENTER;
		l.margin.top=20;
		addChild(l);
		
		GuiButton e=new GuiButton();
		e.preferedX=e.preferedY=Align.CENTER;
		e.preferedWidth=new SizeCalcStatic(300);
		e.preferedHeight=new SizeCalcStatic(100);
		
		e.backgroundNormal=new GuiElementMaterial(7, new ColorM(0, 0.2, 0.3, 0.3), -1);
		e.borderNormal=new GuiElementMaterial(15, new ColorM(0.6, 0.8, 1, 1), 400);
		
		e.borderHighlight.color=null;
		e.backgroundHighlight.color=new ColorM(0.2, 0.3, 1, 0.4);
		e.backgroundHighlight.blurRad=0;
		
		e.borderActive.color=new ColorM(0.8, 1, 1, 1);
		e.borderActive.mouseRad=700;
		e.backgroundActive.color=new ColorM(0.1, 0.2, 1, 0.6);
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
		return true;
	}
}
