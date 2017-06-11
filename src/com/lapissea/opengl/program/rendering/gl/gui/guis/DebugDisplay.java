package com.lapissea.opengl.program.rendering.gl.gui.guis;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.gui.GuiFlow.SizeCalcStatic;
import com.lapissea.opengl.program.rendering.gl.gui.IngameDisplay;
import com.lapissea.opengl.program.rendering.gl.gui.elements.GuiLabel;


public class DebugDisplay extends IngameDisplay{
	
	GuiLabel l;
	
	public DebugDisplay(){
		l=new GuiLabel(Game.get().renderer.getDebugInfo(), 30, true);
		l.margin.top=10;
		l.margin.left=10;
		addChild(l);
	}
	
	@Override
	public void update(){
		super.update();
		l.setText(Game.get().renderer.getDebugInfo()+Math.random());
		l.updateFlow();
		l.margin.top=10;
		l.margin.left=10;
		
		((SizeCalcStatic)preferedWidth).value=l.getElementSize().x()+20;
		((SizeCalcStatic)preferedHeight).value=l.getElementSize().y()+20;
		updateFlow();
		pos.set(-5, -5);
		size.set(elementSize);
		l.preferedX=Align.NEGATIVE;
		l.preferedY=Align.NEGATIVE;
//		size.set(Game.win().getSize());
		background.color.set(0,0,0,0.2F);
		borderWidth=5;
		border.blurRad=10;
		border.color.set(0,0,0,0.25F);
	}
}
