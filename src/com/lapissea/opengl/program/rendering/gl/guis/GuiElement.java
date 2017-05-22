package com.lapissea.opengl.program.rendering.gl.guis;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.api.frustrum.IFrustrumShape;
import com.lapissea.opengl.window.api.util.IRotation;
import com.lapissea.opengl.window.api.util.IVec3f;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.opengl.window.impl.assets.Model;

public class GuiElement implements ModelTransformed{
	
	private static class Mdl extends Model{
		
		public Mdl(String name){
			super(name);
		}
		
		@Override
		public IModel load(int vao, int vertexCount, boolean usesIndicies, boolean usesQuads, int[] vbos, ModelAttribute[] attributeIds, IFrustrumShape<? extends IVec3f,? extends IRotation> shape){
			super.load(vao, vertexCount, usesIndicies, usesQuads, vbos, attributeIds, shape);
			glDrawId=GL11.GL_TRIANGLE_STRIP;
			return this;
		}
		
	}
	
	@SuppressWarnings("serial")
	private static class V3NoZ extends Vec3f{
		
		@Override
		public float z(){
			return 0;
		}
	}
	
	protected static IModel UNIT_QUAD=ModelLoader.buildModel(Mdl.class, "UNIT_QUAD", false, "genNormals", false, "vertices", new float[]{
			0,0,0,
			0,1,0,
			1,0,0,
			1,1,0,
	});
	
	public Vec2f	pos		=new Vec2f(),size=new Vec2f(100, 100);
	public IModel	model	=UNIT_QUAD;
	public int		z;
	
	@Override
	public IModel getModel(){
		return model;
	}
	
	public Vec2f getPos(){
		return pos;
	}
	
	public int getZ(){
		return z;
	}
	
	public Vec2f getSize(){
		return size;
	}
	
	static Matrix4f _MAT=new Matrix4f();
	
	
	Vec3f _POS=new V3NoZ(),_ROT=new Vec3f();
	
	Vec2f _NO_SCALE=new Vec2f(){
		
		@Override
		public float x(){
			return 1;
		}
		
		@Override
		public float y(){
			return 1;
		}
	};
	
	@Override
	public Matrix4f getTransform(){
		_MAT.setIdentity();
		Vec2f pos=getPos();
		
		float rot=0*(float)((Game.get().world.time()+Game.getPartialTicks())/100D);
		_POS.set(pos.x(), pos.y());
		_MAT.translate(_POS);
		
		if(rot!=0) MatrixUtil.rotate(_MAT, _ROT.z(rot));
		return _MAT;
	}
}
