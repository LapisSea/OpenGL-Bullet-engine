package com.lapissea.opengl.program.rendering.gl.gui.elements;

import static org.lwjgl.opengl.GL11.*;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.frustrum.FrustrumCube;
import com.lapissea.opengl.program.rendering.gl.gui.GuiElement;
import com.lapissea.opengl.program.rendering.gl.gui.GuiFlow.SizeCalcStatic;
import com.lapissea.opengl.program.rendering.gl.model.DynamicModel;
import com.lapissea.opengl.program.resources.model.ModelLoader;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

public class GuiLabel extends GuiElement{
	
	String	text;
	int		fontSize;
	
	public static IModel model0=ModelLoader.EMPTY_MODEL;
	
	public GuiLabel(String text, int fontSize){
		this(text, fontSize, false);
	}
	
	public GuiLabel(String text, int fontSize, boolean changing){
		this.text=text;
		this.fontSize=fontSize;
		if(!changing){
			model=Game.get().renderer.fontComfortaa.buildAsModel(fontSize, text, false);
			FrustrumCube c=(FrustrumCube)model.getFrustrumShape();
			preferedWidth=new SizeCalcStatic(c.getSizeX()*2);
			preferedHeight=new SizeCalcStatic(c.getSizeY()*2);
		}else{
			model=ModelLoader.buildModel(DynamicModel.class, "changing label", GL_TRIANGLES, "vertices", new float[6], "uvs", new float[6], "genNormals", false, "vertexType", ModelAttribute.VERTEX_ATTR_2D).culface(false);
			setText(text);
		}
		size.set(1, -1);
		model.getTextures().add(null);
		model.getTextures().add(Game.get().renderer.fontComfortaa.letters);
		
	}
	
	public void setText(String text){
		if(!(model instanceof DynamicModel)||text.equals(this.text)) return;
		DynamicModel m=(DynamicModel)model;
		m.clear();
		Game.get().renderer.fontComfortaa.build(0, 0, fontSize, text, (x, y)->m.add(m.getVertexType(), x, y), (u, v)->m.add(ModelAttribute.UV_ATTR, u, v));
		FrustrumCube c=(FrustrumCube)m.getFrustrumShape();
		((SizeCalcStatic)preferedWidth).value=c.getSizeX()*2;
		((SizeCalcStatic)preferedHeight).value=c.getSizeY()*2;
		if(hasParent()) parent.updateFlow();
	}
	
	@Override
	protected void finalize() throws Throwable{
		Game.glCtx(model::delete);
	}
	
	@Override
	public void updateFlow(){
		super.updateFlow();
	}
}
