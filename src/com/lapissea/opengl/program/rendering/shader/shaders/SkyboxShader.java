package com.lapissea.opengl.program.rendering.shader.shaders;

import static org.lwjgl.opengl.GL11.*;

import java.util.Collection;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.game.world.World;
import com.lapissea.opengl.program.rendering.ModelTransformed;
import com.lapissea.opengl.program.rendering.shader.ShaderRenderer;
import com.lapissea.opengl.program.rendering.shader.Shaders;
import com.lapissea.opengl.program.rendering.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.program.resources.model.ModelLoader;
import com.lapissea.opengl.program.resources.texture.TextureLoader;
import com.lapissea.opengl.program.util.math.PartialTick;
import com.lapissea.opengl.program.util.math.vec.Vec3f;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;
import com.lapissea.opengl.window.impl.assets.BasicTextureCube;

public class SkyboxShader extends ShaderRenderer<ModelTransformed>{
	
	private IModel cube=ModelLoader.buildModel("Skybox", GL_TRIANGLES, "vertices", Shaders.VERTEX_BOX, "genNormals", false, "textures", TextureLoader.loadTexture("skybox/ame_nebula", BasicTextureCube.class));
	
	UniformFloat3	sunPos;
	UniformFloat1	eyeHeight;
	UniformFloat1	viewFarPlane;
	Vec3f			dir	=new Vec3f();
	
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
		World world=Game.get().world;
		float sunPos0=(float)(world.getSunPos(Game.getPartialTicks())*Math.PI*2+Math.PI);
		dir.set(sunPos0, 0, 0);
		
		dir.eulerToVector();
		
		//dir.set(getRenderer().getCamera().rot).eulerToVector();
		
		sunPos.upload(dir);
		if(viewFarPlane!=null) viewFarPlane.upload((float)world.fog.getMaxDistance());
		
		if(eyeHeight!=null) eyeHeight.upload(PartialTick.calc(getRenderer().getCamera().prevPos.y, getRenderer().getCamera().pos.y)/2);
	}
	
	@Override
	protected synchronized void bindAttributes(){
		bindAttribute(ModelAttribute.VERTEX_ATTR_3D);
	}
	
	@Override
	protected void setUpUniforms(){
		projectionMat=getUniform("projectionMat");
		viewMat=getUniform("viewMat");
		sunPos=getUniform("sunPos");
		eyeHeight=getUniform("eyeHeight");
		viewFarPlane=getUniform("viewFarPlane");
	}
	
	Matrix4f viewCopy=new Matrix4f();
	
	@Override
	public void uploadProjectionAndViewMat(Matrix4f project, Matrix4f view){
		if(viewMat==null) return;
		viewCopy.load(view);
		viewCopy.m30=0;
		viewCopy.m31=0;
		viewCopy.m32=0;
		viewMat.upload(viewCopy);
		projectionMat.upload(project);
	}
	
	private static final int	iSteps	=16;
	private static final int	jSteps	=16;
	
	private static float rsi(Vec3f r0, Vec3f rd, float sr){
		// Simplified ray-sphere intersection that assumes
		// the ray starts inside the sphere and that the
		// sphere is centered at the origin. Always intersects.
		float a=Vector3f.dot(rd, rd);
		float b=2*Vector3f.dot(rd, r0);
		float c=Vector3f.dot(r0, r0)-sr*sr;
		return (float)((-b+Math.sqrt(b*b-4.0*a*c))/(2.0*a));
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
		float mu=Vector3f.dot(r, pSun);
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
				Vec3f jPos=iPos.clone().add(pSun.clone().mul(jTime+jStepSize*0.5F));
				
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
