package com.lapissea.opengl.program.gui;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.gui.GuiFlow.ISizeCalc;
import com.lapissea.opengl.program.gui.GuiFlow.SizeCalcStatic;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.resources.model.ModelLoader;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.events.KeyEvent;
import com.lapissea.opengl.window.api.events.MouseButtonEvent;
import com.lapissea.opengl.window.api.events.MouseMoveEvent;
import com.lapissea.opengl.window.api.events.MouseScrollEvent;
import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.api.util.vec.IVec2iR;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

public class GuiElement implements ModelTransformed{
	
	protected static final IModel	UNIT_QUAD	=ModelLoader.buildModel("UNIT_QUAD", GL_TRIANGLE_STRIP, "genNormals", false, "vertices", new float[]{
			0,0,
			0,1,
			1,0,
			1,1}, "vertexType", ModelAttribute.VERTEX_ATTR_2D);
	protected static final Matrix4f	_MAT		=new Matrix4f();
	protected static final Vec3f	_POS		=new Vec3f(),_ROT=new Vec3f();
	protected static final Vec2f	VEC2		=new Vec2f();
	
	protected static final IVec2iR MOUSE=new IVec2iR(){
		
		@Override
		public int x(){
			return Mouse.getX();
		}
		
		@Override
		public int y(){
			return Game.win().getSize().y()-Mouse.getY();
		}
	};
	
	public static class Margin{
		
		public float top,bottom,left,right;
	}
	
	public static enum Align{
		STATIC((e, axis)->axis?e.pos.x():e.pos.y()),NEGATIVE((e, axis)->{
			if(e.parent==null) return axis?e.pos.x():e.pos.y();
			return axis?e.margin.left:e.margin.top;
		}),CENTER((e, axis)->{
			if(e.parent==null) return axis?e.pos.x():e.pos.y();
			return (axis?e.parent.getElementSize().x()-e.getElementSize().x():e.parent.getElementSize().y()-e.getElementSize().y())/2;
		}),POSITIVE((e, axis)->{
			if(e.parent==null) return axis?e.pos.x():e.pos.y();
			return axis?e.parent.getElementSize().x()-e.getElementSize().x()-e.margin.right:e.parent.getElementSize().y()-e.getElementSize().y()-e.margin.bottom;
		});
		
		public static interface Aligner{
			
			float calc(GuiElement e, boolean axis);
		}
		
		private final Aligner aligner;
		
		public float calc(GuiElement e, boolean axis){
			return aligner.calc(e, axis);
		}
		
		private Align(Aligner aligner){
			this.aligner=aligner;
		}
		
	}
	
	protected Vec2f				pos				=new Vec2f();
	protected Vec2f				size			=new Vec2f();
	protected Vec2f				elementSize		=new Vec2f();
	protected IModel			model			=UNIT_QUAD;
	public GuiElementMaterial	border			=new GuiElementMaterial();
	public GuiElementMaterial	background		=new GuiElementMaterial();
	public float				borderWidth		=0;
	public ISizeCalc			preferedWidth	=new SizeCalcStatic(100);
	public ISizeCalc			preferedHeight	=new SizeCalcStatic(100);
	public Align				preferedX		=Align.NEGATIVE;
	public Align				preferedY		=Align.NEGATIVE;
	public Margin				margin			=new Margin();
	
	public GuiElement			parent;
	protected List<GuiElement>	children=new ArrayList<>();
	
	public GuiElement(){}
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	public Vec2f getAbsolutePos(Vec2f dest){
		return parent==null?dest.set(pos):parent.getAbsolutePos(dest).add(pos);
	}
	
	public Vec2f getPos(){
		return pos;
	}
	
	public int getZ(){
		return parent==null?0:parent.getZ()+1;
	}
	
	public Vec2f getSize(){
		return size;
	}
	
	public Vec2f getElementSize(){
		return elementSize;
	}
	
	public void update(){
		children.forEach(GuiElement::update);
	}
	
	public boolean isMouseOver(){
		getAbsolutePos(VEC2);
		return intersectsMouse(VEC2.x(), VEC2.y(), VEC2.x()+size.x(), VEC2.y()+size.y());
	}
	
	public static boolean intersectsMouse(float minX, float minY, float maxX, float maxY){
		return MOUSE.x()>=minX&&
				MOUSE.y()>=minY&&
				MOUSE.x()<=maxX&&
				MOUSE.y()<=maxY;
	}
	
	@Override
	public Matrix4f getTransform(){
		_MAT.setIdentity();
		Vec2f pos=getAbsolutePos(new Vec2f());
		
		float rot=0;//*(float)((Game.get().world.time()+Game.getPartialTicks())/100D);
		
		_POS.add(pos.x(), pos.y(), 0);
		_MAT.translate(_POS);
		_POS.sub(pos.x(), pos.y(), 0);
		
		if(rot!=0) MatrixUtil.rotate(_MAT, _ROT.z(rot));
		return _MAT;
	}
	
	public IColorM getParentBg(){
		return hasParent()?parent.getParentBg():background.color;
	}
	
	public boolean hasParent(){
		return parent!=null;
	}
	
	protected void deepChildForEach(Consumer<GuiElement> consumer){
		children.forEach(ch->ch.deepChildForEach(ch, consumer));
	}
	
	protected void deepChildForEach(GuiElement e, Consumer<GuiElement> consumer){
		consumer.accept(this);
		children.forEach(ch->ch.deepChildForEach(ch, consumer));
	}
	
	public void onKey(KeyEvent e){}
	
	public void onMouseButton(MouseButtonEvent e){}
	
	public void onMouseMove(MouseMoveEvent e){}
	
	public void onMouseScroll(MouseScrollEvent e){}
	
	public GuiElementMaterial getRenderBackground(){
		return background;
	}
	
	public GuiElementMaterial getRenderBorder(){
		return border;
	}
	
	public void updateFlow(){
		//		this.elementSize.set(preferedWidth!=null?preferedWidth.calc(parent!=null?parent.size.x():Game.win().getSize().x()):elementSize.x(), preferedHeight!=null?preferedHeight.calc(parent!=null?parent.size.y():Game.win().getSize().y()):elementSize.y());
		pos.set(preferedX.calc(this, true), preferedY.calc(this, false));
		children.forEach(GuiElement::updateFlow);
	}
	
	public GuiElement addChild(GuiElement child){
		child.parent=this;
		children.add(child);
		return child;
	}
}
