package com.lapissea.opengl.program.rendering.gl.shader.shaders;

import java.util.Collection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.gl.model.ModelLoader;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.gl.shader.Shaders;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.UniformMat4;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat4;
import com.lapissea.opengl.program.util.math.MatrixUtil;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

public class SkyboxShader extends ShaderRenderer<ModelTransformed>{
	
	private static class UniformMat40 extends UniformMat4{
		
		public UniformMat40(Shader shader, int id, String name){
			super(shader, id, name);
		}
		
		@Override
		public void upload(Matrix4f mat){
			shader.bindingProttect();
			
			if(MatrixUtil.equals(mat, prev)) return;
			prev.load(mat);
			
			BUFF.put(prev.m00);
			BUFF.put(prev.m01);
			BUFF.put(prev.m02);
			BUFF.put(prev.m03);
			BUFF.put(prev.m10);
			BUFF.put(prev.m11);
			BUFF.put(prev.m12);
			BUFF.put(prev.m13);
			BUFF.put(prev.m20);
			BUFF.put(prev.m21);
			BUFF.put(prev.m22);
			BUFF.put(prev.m23);
			BUFF.put(0);//prev.m30);
			BUFF.put(0);//prev.m31);
			BUFF.put(0);//prev.m32);
			BUFF.put(prev.m33);
			BUFF.flip();
			GL20.glUniformMatrix4(id(), false, BUFF);
			checkError(this::retryUpload);
		}
		
	}
	
	private IModel cube=ModelLoader.buildModel("Skybox", GL11.GL_TRIANGLES, "vertices", Shaders.VERTEX_BOX, "genNormals", false/*, "textures", TextureLoader.loadTexture("skybox/test", BasicTextureCube.class)*/);
	
	UniformFloat3	sunPos;
	UniformFloat4	sunColor;
	UniformFloat1	eyeHeight;
	
	@Override
	@Deprecated
	public void renderBatched(ModelTransformed entity){}
	
	@Override
	@Deprecated
	public void renderSingle(ModelTransformed renderable){}

	@Override
	@Deprecated
	public void renderBatch(Collection<? extends ModelTransformed> entitysWithSameModel){}
	
	@Override
	public void render(){
		onRendered();
		prepareGlobal();
		prepareModel(cube);
		cube.drawCall();
		unbindModel(cube);
		unbind();
	}
	
	@Override
	public void prepareGlobal(){
		super.prepareGlobal();
		double sunPos0=Game.get().world.getSunPos(Game.getPartialTicks())*Math.PI*2;
		float cos=(float)Math.cos(sunPos0);
		Vec3f dir=new Vec3f(cos/3, (float)Math.sin(sunPos0), cos);
		dir.normalise();
		sunPos.upload(dir);
		
		if(eyeHeight!=null) eyeHeight.upload(PartialTick.calc(getRenderer().getCamera().prevPos.y, getRenderer().getCamera().pos.y));
	}
	
	@Override
	protected synchronized void bindAttributes(){
		bindAttribute(ModelAttribute.VERTEX_ATTR_3D);
	}
	
	@Override
	protected void setUpUniforms(){
		projectionMat=getUniform(UniformMat4.class, "projectionMat");
		viewMat=getUniform(UniformMat40.class, "viewMat");
		sunPos=getUniform(UniformFloat3.class, "sunPos");
		sunColor=getUniform(UniformFloat4.class, "sunColor");
		eyeHeight=getUniform(UniformFloat1.class, "eyeHeight");
	}
	
	
	private static final int	iSteps	=16;
	private static final int	jSteps	=16;
	
	private static float rsi(Vec3f r0, Vec3f rd, float sr){
		// Simplified ray-sphere intersection that assumes
		// the ray starts inside the sphere and that the
		// sphere is centered at the origin. Always intersects.
		float a=Vec3f.dot(rd, rd);
		float b=2*Vec3f.dot(rd, r0);
		float c=Vec3f.dot(r0, r0)-(sr*sr);
		return (float)((-b+Math.sqrt((b*b)-4.0*a*c))/(2.0*a));
	}
	
	public static Vec3f atmosphere(Vec3f r, Vec3f r0, Vec3f pSun, float iSun, float rPlanet, float rAtmos, Vec3f kRlh, float kMie, float shRlh, float shMie, float g){
		// Normalize the sun and view directions.
		pSun=pSun.clone();
		r=r.clone();
		pSun.normalise();
		r.normalise();
//		LogUtil.println(r);
		
		// Calculate the step size of the primary ray.
		float iStepSize=rsi(r0, r, rAtmos)/iSteps;
		
		// Initialize the primary ray time.
		float iTime=0;
		
		// Initialize accumulators for Rayleight and Mie scattering.
		Vec3f totalMie=new Vec3f(0, 0, 0);
		
		// Initialize optical depth accumulators for the primary ray.
		float iOdRlh=0;
		float iOdMie=0;
		
		// Calculate the Rayleigh and Mie phases.
		float mu=Vec3f.dot(r, pSun);
		float mumu=mu*mu;
		float gg=g*g;
		float pMie=(float)(3.0/(8.0*Math.PI)*((1.0-gg)*(mumu+1.0))/(Math.pow(Math.abs(1.0+gg-2.0*mu*g), 1.5)*(2.0+gg)));
		
		// Sample the primary ray.
		for(int i=0;i<iSteps;i++){
			
			// Calculate the primary ray sample position.
			Vec3f iPos=r0.clone().add(r.clone().mul(iTime+iStepSize*0.5F));
			
			// Calculate the height of the sample.
			float iHeight=iPos.length()-rPlanet;
			
			// Calculate the optical depth of the Rayleigh and Mie scattering for this step.
			float odStepRlh=(float)(Math.exp(-iHeight/shRlh)*iStepSize);
			float odStepMie=(float)(Math.exp(-iHeight/shMie)*iStepSize);
			
			// Accumulate optical depth.
			iOdRlh+=odStepRlh;
			iOdMie+=odStepMie;
			
			// Calculate the step size of the secondary ray.
			float jStepSize=rsi(iPos, pSun, rAtmos)/jSteps;
			
			// Initialize the secondary ray time.
			float jTime=0;
			
			// Initialize optical depth accumulators for the secondary ray.
			float jOdRlh=0;
			float jOdMie=0;
			
			// Sample the secondary ray.
			for(int j=0;j<jSteps;j++){
				
				// Calculate the secondary ray sample position.
				Vec3f jPos=iPos.clone().add(pSun.clone().mul((jTime+jStepSize*0.5F)));
				
				// Calculate the height of the sample.
				float jHeight=jPos.length()-rPlanet;
				
				// Accumulate the optical depth.
				jOdRlh+=Math.exp(-jHeight/shRlh)*jStepSize;
				jOdMie+=Math.exp(-jHeight/shMie)*jStepSize;
				
				// Increment the secondary ray time.
				jTime+=jStepSize;
			}
			
			// Calculate attenuation.
			Vec3f attn=kRlh.clone().mul(iOdRlh+jOdRlh).add(kMie*(iOdMie+jOdMie)).mul(-1);
			
			attn.x((float)Math.exp(attn.x()));
			attn.y((float)Math.exp(attn.y()));
			attn.z((float)Math.exp(attn.z()));
			
			// Accumulate scattering.
			totalMie.add(attn.clone().mul(odStepMie));
			
			// Increment the primary ray time.
			iTime+=iStepSize;
			
		}
		// Calculate and return the final color.
		Vec3f vec=totalMie.clone().mul(pMie*kMie*iSun);

		vec.x(1-(float)Math.exp(-vec.x()));
		vec.y(1-(float)Math.exp(-vec.y()));
		vec.z(1-(float)Math.exp(-vec.z()));
//		1.0 - exp(-1.0 * color);
		return vec;
	}
	
}
