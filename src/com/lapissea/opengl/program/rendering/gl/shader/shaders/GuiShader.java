package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule;
import com.lapissea.opengl.program.rendering.gl.shader.shaders.GuiShader.IGuiElement;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat2;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.vec.Vec2f;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;

@SuppressWarnings("serial")
public class GuiShader extends ShaderRenderer.Basic3D<IGuiElement>{
	
	public interface IGuiElement extends ModelTransformed{
		
		static Matrix4f _MAT=new Matrix4f();
		
		Vec3f _POS=new Vec3f(){
			
			@Override
			public float z(){
				return 0;
			}
		};
		
		Vec3f _NO_SCALE=new Vec3f(){
			
			@Override
			public float x(){
				return 1;
			}
			
			@Override
			public float y(){
				return 1;
			}
			
			@Override
			public float z(){
				return 1;
			}
		};
		
		default Vec3f getModelScale(){
			return _NO_SCALE;
		}
		
		Vec2f getModelPos();
		
		int getZ();
		
		@Override
		default Matrix4f getTransform(){
			_MAT.setIdentity();
			Vec2f pos=getModelPos();
			_POS.set(pos.x(), pos.y());
			return MatrixUtil.createTransformMat(_MAT, _POS, getModelScale());
		}
		
	}
	
	static IModel UNIT_QUAD=ModelLoader.buildModel("UNIT_QUAD", false, "genNormals", false, "vertices", new float[]{
			0,0, 0,
			1,0,0,
			1,1,0,
			
			0,0,0,
			1,1,0,
			0,1,0,
	});
	
	static class Foo implements IGuiElement{
		
		public Vec2f	pos		=new Vec2f();
		public IModel	model	=UNIT_QUAD;
		public int		z;
		
		@Override
		public IModel getModel(){
			return model;
		}
		
		@Override
		public Vec2f getModelPos(){
			return pos;
		}
		
		@Override
		public int getZ(){
			return z;
		}
		
		@Override
		public Vec3f getModelScale(){
			return new Vec3f(200, 100, 0);
		}
	}
	
	Foo foo=new Foo();
	
	UniformFloat2	screenSize;
	UniformFloat1	z;
	
	public GuiShader(){
		super("gui");
	}
	
	@Override
	public void render(){
		foo.model.culface(false);
		renderSingle(foo);
		super.render();
	}
	
	@Override
	protected void setUpUniforms(){
		transformMat=getUniform(UniformMat4.class, "transformMat");
		screenSize=getUniform(UniformFloat2.class, "screenSize");
		z=getUniform(UniformFloat1.class, "z");
	}
	
	@Override
	protected void prepareGlobal(){
		bind();
		if(screenSize!=null)screenSize.upload(Game.win().getSize());
		modulesGlobal.forEach(ShaderModule.Global::uploadUniformsGlobal);
	}
	
	@Override
	protected void prepareInstance(IGuiElement renderable){
		super.prepareInstance(renderable);
		if(z!=null)z.upload(renderable.getZ());
		
	}
}
