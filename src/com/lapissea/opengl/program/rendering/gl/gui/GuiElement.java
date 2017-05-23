package com.lapissea.opengl.program.rendering.gl.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.events.KeyEvent;
import com.lapissea.opengl.window.api.events.MouseButtonEvent;
import com.lapissea.opengl.window.api.events.MouseMoveEvent;
import com.lapissea.opengl.window.api.events.MouseScrollEvent;
import com.lapissea.opengl.window.api.util.IVec2i;
import com.lapissea.opengl.window.api.util.color.IColorM;
import com.lapissea.opengl.window.assets.IModel;

public class GuiElement implements ModelTransformed{
	
	protected static final IModel	UNIT_QUAD	=ModelLoader.buildModel("UNIT_QUAD", GL11.GL_TRIANGLE_STRIP, "genNormals", false, "vertices", new float[]{0,0,0,0,1,0,1,0,0,1,1,0});
	protected static final Matrix4f	_MAT		=new Matrix4f();
	protected static final Vec3f	_POS		=new Vec3f(),_ROT=new Vec3f();
	protected static final Vec2f	VEC2		=new Vec2f();
	
	protected static final IVec2i MOUSE=new IVec2i(){
		
		@Override
		public int x(){
			return Mouse.getX();
		}
		
		@Override
		public int y(){
			return Game.win().getSize().y()-Mouse.getY();
		}
		
		@Override
		public IVec2i x(int x){
			return null;
		}
		
		@Override
		public IVec2i y(int y){
			return null;
		}
	};
	
	protected Vec2f				pos			=new Vec2f(),size=new Vec2f(100, 100);
	protected IModel			model		=UNIT_QUAD;
	public GuiElementMaterial	border		=new GuiElementMaterial();
	public GuiElementMaterial	background	=new GuiElementMaterial();
	public float				borderWidth;
	
	public GuiElement		parent;
	public List<GuiElement>	children=new ArrayList<>();
	
	public GuiElement(GuiElement parent){
		this.parent=parent;
	}
	
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
	
	
	public void update(){
		children.forEach(GuiElement::update);
	}
	
	public boolean isMouseOver(){
		getAbsolutePos(VEC2);
		return VEC2.x()<=MOUSE.x()&&
				VEC2.y()<=MOUSE.y()&&
				VEC2.x()+size.x()>=MOUSE.x()&&
				VEC2.y()+size.y()>=MOUSE.y();
	}
	
	@Override
	public Matrix4f getTransform(){
		_MAT.setIdentity();
		Vec2f pos=getPos();
		
		float rot=0*(float)((Game.get().world.time()+Game.getPartialTicks())/100D);
		
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
	
	public void onKey(KeyEvent e){}
	
	public void onMouseButton(MouseButtonEvent e){}
	
	public void onMouseMove(MouseMoveEvent e){}
	
	public void onMouseScroll(MouseScrollEvent e){}
}
