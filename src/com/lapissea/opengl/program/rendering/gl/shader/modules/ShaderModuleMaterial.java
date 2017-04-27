package com.lapissea.opengl.program.rendering.gl.shader.modules;

import com.lapissea.opengl.program.rendering.gl.model.Model;
import com.lapissea.opengl.program.rendering.gl.model.ModelAttribute;
import com.lapissea.opengl.program.rendering.gl.shader.Material;
import com.lapissea.opengl.program.rendering.gl.shader.Shader;
import com.lapissea.opengl.program.rendering.gl.shader.modules.ShaderModule.ModelUniforms;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.program.rendering.gl.shader.uniforms.floats.UniformFloat3;

public class ShaderModuleMaterial extends ShaderModule implements ModelUniforms{
	
	public static final int MATERIAL_MAX_COUNT=20;
	
	UniformFloat3[]	ambient;
	UniformFloat3[]	diffuse;
	UniformFloat3[]	specular;
	UniformFloat1[]	jelly;
	UniformFloat1[]	shineDamper;
	UniformFloat1[]	reflectivity;
	UniformFloat1[]	lightTroughput;
	
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
	}
	
	@Override
	public void bindAttributes(){
		bindAttribute(ModelAttribute.MAERIAL_ID_ATTR);
	}

	@Override
	public void uploadUniformsModel(Model model){
		
		uploadMaterial(0, model.defaultMaterial);
		
		for(int i=0, j=Math.min(model.materials.size(), ambient.length-1);i<j;i++){
			uploadMaterial(i+1, model.materials.get(i));
		}
	}
	
	protected void uploadMaterial(int i, Material material){
		ambient[i].upload(material.ambient);
		diffuse[i].upload(material.diffuse);
		specular[i].upload(material.specular);
		jelly[i].upload(material.jelly);
		shineDamper[i].upload(material.shineDamper);
		reflectivity[i].upload(material.reflectivity);
		lightTroughput[i].upload(material.lightTroughput);
	}
	
}
