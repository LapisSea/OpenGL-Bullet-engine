package com.lapissea.opengl.rendering.shader.modules;

import java.util.HashMap;
import java.util.Map;

import com.lapissea.opengl.core.Game;
import com.lapissea.opengl.rendering.shader.Shader;
import com.lapissea.opengl.rendering.shader.modules.ShaderModule.ModelMdl;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat1;
import com.lapissea.opengl.rendering.shader.uniforms.floats.UniformFloat3;
import com.lapissea.opengl.window.assets.IMaterial;
import com.lapissea.opengl.window.assets.IModel;
import com.lapissea.opengl.window.assets.ModelAttribute;

public class ShaderModuleMaterial extends ShaderModule implements ModelMdl{
	
	public static final int MATERIAL_MAX_COUNT=20;
	
	UniformFloat3[]	ambient;
	UniformFloat3[]	diffuse;
	UniformFloat3[]	specular;
	UniformFloat3[]	emission;
	UniformFloat1[]	jelly;
	UniformFloat1[]	shineDamper;
	UniformFloat1[]	lightTroughput;
	UniformFloat1	tim;
	
	public ShaderModuleMaterial(Shader parent){
		super(parent);
	}
	
	@Override
	public void setUpUniforms(){
		ambient=getUniformArray(i->"materials["+i+"].ambient");
		diffuse=getUniformArray(i->"materials["+i+"].diffuse");
		specular=getUniformArray(i->"materials["+i+"].specular");
		emission=getUniformArray(i->"materials["+i+"].emission");
		jelly=getUniformArray(i->"materials["+i+"].jelly");
		shineDamper=getUniformArray(i->"materials["+i+"].shineDamper");
		lightTroughput=getUniformArray(i->"materials["+i+"].lightTroughput");
		tim=getUniform("tim");
	}
	
	@Override
	public void bindAttributes(){
		bindAttribute(ModelAttribute.MATERIAL_ID_ATTR);
	}
	
	@Override
	public void uploadUniformsModel(IModel model){
		if(tim!=null) tim.upload((float)((Game.get().world.time()+(double)Game.getPartialTicks())/20D%(Math.PI*2)));
		for(int i=0, j=Math.min(model.getMaterialCount(), ambient.length-1);i<j;i++){
			IMaterial mat=model.getMaterial(i);
			uploadMaterial(mat.getId(), mat);
		}
	}
	
	protected void uploadMaterial(int i, IMaterial material){
		if(ambient!=null)ambient[i].upload(material.getAmbient());
		diffuse[i].upload(material.getDiffuse());
		specular[i].upload(material.getSpecular());
		emission[i].upload(material.getEmission());
		jelly[i].upload(material.getJelly());
		shineDamper[i].upload(material.getShineDamper());
		lightTroughput[i].upload(material.getLightTroughput());
	}
	
	@Override
	public Map<String,String> getCompileValues(){
		Map<String,String> map=new HashMap<>();
		map.put("MATERIAL_MAX_COUNT", ""+MATERIAL_MAX_COUNT);
		return map;
	}
}
