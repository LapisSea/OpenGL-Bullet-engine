package com.lapissea.opengl.program.rendering.gl.shader.modules;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.ModelUniforms;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

public class ShaderModuleMaterial extends ShaderModule implements ModelUniforms{
	
	public static final int MATERIAL_MAX_COUNT=20;
	
	UniformFloat3[]	ambient;
	UniformFloat3[]	diffuse;
	UniformFloat3[]	specular;
	UniformFloat1[]	jelly;
	UniformFloat1[]	shineDamper;
	UniformFloat1[]	reflectivity;
	UniformFloat1[]	lightTroughput;
	UniformFloat1	tim;
	
	public ShaderModuleMaterial(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		ambient=getUniformArray(UniformFloat3.class, i->"materials["+i+"].ambient");
		diffuse=getUniformArray(UniformFloat3.class, i->"materials["+i+"].diffuse");
		specular=getUniformArray(UniformFloat3.class, i->"materials["+i+"].specular");
		jelly=getUniformArray(UniformFloat1.class, i->"materials["+i+"].jelly");
		shineDamper=getUniformArray(UniformFloat1.class, i->"materials["+i+"].shineDamper");
		reflectivity=getUniformArray(UniformFloat1.class, i->"materials["+i+"].reflectivity");
		lightTroughput=getUniformArray(UniformFloat1.class, i->"materials["+i+"].lightTroughput");
		tim=getUniform(UniformFloat1.class, "tim");
	}
	
	@Override
	public void bindAttributes(){
		bindAttribute(ModelAttribute.MAERIAL_ID_ATTR);
	}
	
	@Override
	public void uploadUniformsModel(IModel model){
		if(tim!=null)tim.upload((float)(((Game.get().world.time()+(double)Game.getPartialTicks())/20D)%(Math.PI*2)));
		for(int i=0, j=Math.min(model.getMaterialCount(), ambient.length-1);i<j;i++){
			IMaterial mat=model.getMaterial(i);
			uploadMaterial(mat.getId(), mat);
		}
	}
	
	protected void uploadMaterial(int i, IMaterial material){
		ambient[i].upload(material.getAmbient());
		diffuse[i].upload(material.getDiffuse());
		specular[i].upload(material.getSpecular());
		jelly[i].upload(material.getJelly());
		shineDamper[i].upload(material.getShineDamper());
		reflectivity[i].upload(material.getReflectivity());
		lightTroughput[i].upload(material.getLightTroughput());
	}
	
}
