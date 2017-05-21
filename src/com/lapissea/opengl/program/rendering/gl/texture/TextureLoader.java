package com.lapissea.opengl.program.rendering.gl.texture;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

import com.lapissea.opengl.program.core.Game;
import com.lapissea.opengl.program.rendering.GLUtil;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.window.assets.ITexture;
import com.lapissea.opengl.window.assets.ITextureCube;
import com.lapissea.opengl.window.impl.assets.BasicTexture;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class TextureLoader{
	
	public static final ITexture		NO_TEXTURE;
	public static final Int2IntMap		DEFAULT_PARAMS	=new Int2IntOpenHashMap(6);
	private static final Int2IntMap		CUSTOM_PARAMS	=new Int2IntOpenHashMap(6);
	private static final List<ITexture>	CHASE			=Collections.synchronizedList(new ArrayList<>());
	
	private static class TextureData{
		
		final int			width,height;
		final ByteBuffer	data;
		
		public TextureData(int width, int height, ByteBuffer data){
			this.width=width;
			this.height=height;
			this.data=data;
		}
		
		public TextureData(BufferedImage image){
			this(image.getWidth(), image.getHeight(), imgToBuff(image));
		}
		
	}
	
	static{
		//		DEFAULT_PARAMS.put(GL11.GL_TEXTURE_WRAP_S, GL13.GL_CLAMP_TO_BORDER);
		//		DEFAULT_PARAMS.put(GL11.GL_TEXTURE_WRAP_T, GL13.GL_CLAMP_TO_BORDER);
		
		//		DEFAULT_PARAMS.put(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		DEFAULT_PARAMS.put(GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		DEFAULT_PARAMS.put(GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		DEFAULT_PARAMS.put(GL14.GL_TEXTURE_LOD_BIAS, -1);
		
		BufferedImage badImg=new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
		badImg.setRGB(0, 0, 0xFF02FF41);
		badImg.setRGB(2, 0, 0xFF02FF41);
		badImg.setRGB(0, 2, 0xFF02FF41);
		badImg.setRGB(2, 2, 0xFF02FF41);
		
		badImg.setRGB(1, 0, 0xFFFF00CB);
		badImg.setRGB(1, 2, 0xFFFF00CB);
		badImg.setRGB(0, 1, 0xFFFF00CB);
		badImg.setRGB(2, 1, 0xFFFF00CB);
		
		badImg.setRGB(1, 1, 0xFF2200FF);
		
		NO_TEXTURE=loadTexture("NO_TEXTURE", badImg);
	}
	
	public static void reloadTexture(ITexture image){
		reloadTexture(image.getPath());
	}
	
	public static void reloadTexture(ITexture image, BufferedImage data){
		reloadTexture(image.getPath(), data);
	}
	
	public static void reloadTexture(String path){
		ITexture texture=getExisting(path);
		if(texture==null){
			LogUtil.printlnEr("Failed to reload texture:", path);
			return;
		}
		
		TextureData image=loadData(path);
		if(image==null) failTexture(texture);
		else reloadTexture0(texture, image);
	}
	
	public static void reloadTexture(String path, BufferedImage data){
		ITexture texture=getExisting(path);
		if(texture==null){
			LogUtil.printlnEr("Texture ", path, "does not exist!");
			return;
		}
		reloadTexture0(texture, new TextureData(data));
	}
	
	private static void reloadTexture0(ITexture texture, TextureData image){
		if(!Game.get().glCtx.isGlThread()){
			Game.glCtx(()->reloadTexture0(texture, image));
			return;
		}
		if(!texture.isLoaded()){
			if(texture.isLoading()){
				Game.glCtxLatter(()->reloadTexture0(texture, image));
				return;
			}
			throw new IllegalStateException("Texture can not be reloaded if not loaded!");
		}
		
		GLUtil.checkError();
		if(image.width!=texture.getWidth()||image.width!=texture.getWidth()){
			
			texture.delete();
			writeToNewObj(texture, image);
			
			GLUtil.checkError();
		}
		else{
			texture.bind();
			GLUtil.checkError();
			GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, texture.getWidth(), texture.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image.data);
			GLUtil.checkError();
		}
		
	}
	
	public synchronized static ITexture loadTexture(String path, int...params){
		ITexture result=getExisting(path);
		if(result!=null) return result;
		
		return loadTexture0(path, BasicTexture.class, params);
	}
	
	public synchronized static <T extends ITexture> T loadTexture(String path, Class<T> type, int...params){
		T result=getExisting(path, type);
		if(result!=null) return result;
		
		return loadTexture0(path, type, params);
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends ITexture> T loadTexture0(String path, Class<T> type, int...params){
		if(UtilM.instanceOf(type, ITextureCube.class)){
			String[] names={
					"/right",
					"/left",
					"/top",
					"/bottom",
					"/back",
					"/front"
			};
			
			TextureData[] data=new TextureData[6];
			for(int i=0;i<names.length;i++){
				data[i]=loadData(path+names[i]);
				if(data[i]==null) return failTexture(alocate(path, type));
			}
			
			return (T)alocateAndWrite(path, data, (Class<ITextureCube>)type, params);
		}
		
		TextureData data=loadData(path);
		
		if(data==null) return failTexture(alocate(path, type));
		return alocateAndWrite(path, data, type, params);
		
	}
	
	public static ITexture loadTexture(String path, BufferedImage image, int...params){
		ITexture result=getExisting(path);
		return result!=null?result:alocateAndWrite(path, new TextureData(image), BasicTexture.class, params);
	}
	
	public static <T extends ITexture> T loadTexture(String path, BufferedImage image, Class<T> type, int...params){
		T result=getExisting(path, type);
		return result!=null?result:alocateAndWrite(path, new TextureData(image), type, params);
	}
	
	private static TextureData loadData(String path){
		LogUtil.println("Loading texture:", path);
		try{
			try(InputStream str=UtilM.getResource("textures/"+path+".png", "textures/"+path+".jpg")){
				
				if(str==null){
					LogUtil.printlnEr("res/textures/"+path+".png does not exist!");
					return null;
				}
				BufferedImage image=ImageIO.read(str);
				if(image==null){
					LogUtil.printlnEr("res/textures/"+path+".png failed to be read!");
					return null;
				}
				return new TextureData(image);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static <T extends ITexture> T alocateAndWrite(String path, TextureData image, Class<T> type, int...params){
		T result=alocate(path, type);
		for(int i=0;i<params.length;i+=2){
			result.params(params[i], params[i+1]);
		}
		writeToNewObj(result, image);
		return result;
	}
	
	private static void writeToNewObj(ITexture texture, TextureData image){
		Object code=texture.notifyLoading();
		
		Game.glCtx(()->{
			if(code!=texture.loadingKey()) return;//another loading process was called, this will have no effect hence it is pointless 
			
			int id=GL11.glGenTextures();
			
			///////////////////////////////////////////
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
			
			GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.width, image.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image.data);
			
			if(mergeParams(texture.params()).get(GL11.GL_TEXTURE_MIN_FILTER)!=GL11.GL_NEAREST){
				GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
				if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic){
					float ammount=Math.min(4, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, ammount);
					GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
				}
			}
			CUSTOM_PARAMS.forEach((k, v)->GL11.glTexParameteri(GL11.GL_TEXTURE_2D, k, v));
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			///////////////////////////////////////////
			
			texture.load(id, image.width, image.height);
			LogUtil.println("Loaded texture:", texture.getPath());
		});
	}
	
	private static <T extends ITextureCube> T alocateAndWrite(String path, TextureData[] image, Class<T> type, int...params){
		T result=alocate(path, type);
		for(int i=0;i<params.length;i+=2){
			result.params(params[i], params[i+1]);
		}
		writeToNewObj(result, image);
		return result;
	}
	
	private static void writeToNewObj(ITextureCube texture, TextureData[] data){
		Object code=texture.notifyLoading();
		
		Game.glCtx(()->{
			if(code!=texture.loadingKey()) return;//another loading process was called, this will have no effect hence it is pointless 
			
			int id=GL11.glGenTextures();
			
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, id);
			
			for(int i=0;i<data.length;i++){
				TextureData image=data[i];
				GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X+i, 0, GL11.GL_RGBA,
						image.width, image.height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE,
						image.data);
			}
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			//			mergeParams(texture.params()).forEach((k, v)->GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, k, v));
			
			texture.load(id, -1, -1);
			LogUtil.println("Loaded cube texture:", texture.getPath());
		});
	}
	
	private static <T extends ITexture> T failTexture(T texture){
		if(texture.isLoaded()) return texture;
		texture.load(NO_TEXTURE.getId(), NO_TEXTURE.getWidth(), NO_TEXTURE.getHeight());
		LogUtil.printlnEr("Failed to load texture:", texture.getPath());
		return texture;
	}
	
	public static <T extends ITexture> T alocate(String path, Class<T> type){
		
		try{
			Constructor<T> constr=type.getConstructor(String.class);
			try{
				T result=constr.newInstance(path);
				CHASE.add(result);
				return result;
			}catch(Exception e){
				throw e;
			}
		}catch(Exception e){
			LogUtil.printlnEr("No", type.getSimpleName()+"(String) constructor!");
			throw new RuntimeException(e);
		}
	}
	
	public synchronized static void deleteAll(){
		UtilM.doAndClear(CHASE, ITexture::delete);
	}
	
	public synchronized static void cleanChaseUp(){
		CHASE.removeIf(tx->!tx.isLoaded());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends ITexture> T getExisting(String path, Class<T> type){
		ITexture ex=getExisting(path);
		if(ex==null) return null;
		
		if(UtilM.instanceOf(ex, type)) return (T)ex;
		
		throw new ClassCastException("Found existing texture with path "+path+" with type "+ex.getClass().getSimpleName()+" that is ont compatabile with "+type.getSimpleName());
	}
	
	public static ITexture getExisting(String path){
		ITexture result=CHASE.stream().filter(tx->tx.getPath().equals(path)).findFirst().orElse(null);
		if(result==null) return null;
		if(!result.isLoaded()&&!result.isLoading()){
			CHASE.remove(path);
			return null;
		}
		return result;
	}
	
	private static Int2IntMap mergeParams(int[] params){
		if(params==null||params.length==0){
			CUSTOM_PARAMS.clear();
			CUSTOM_PARAMS.putAll(DEFAULT_PARAMS);
		}
		else{
			if(params.length%2!=0) throw new IllegalArgumentException("Params need to have x*2 elements!");
			CUSTOM_PARAMS.clear();
			CUSTOM_PARAMS.putAll(DEFAULT_PARAMS);
			for(int i=0, j=params.length/2;i<j;i++){
				int i1=i*2;
				CUSTOM_PARAMS.put(params[i1], params[i1+1]);
			}
		}
		
		return CUSTOM_PARAMS;
	}
	
	private static ByteBuffer imgToBuff(BufferedImage img){
		BufferedImage image=img;
		
		int[] pixels=new int[image.getWidth()*image.getHeight()];
		image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
		
		ByteBuffer buffer=BufferUtils.createByteBuffer(image.getWidth()*image.getHeight()*4); //4 for RGBA, 3 for RGB
		
		for(int y=0;y<image.getHeight();y++){
			for(int x=0;x<image.getWidth();x++){
				int pixel=pixels[y*image.getWidth()+x];
				buffer.put((byte)((pixel>>16)&0xFF)); // Red component
				buffer.put((byte)((pixel>>8)&0xFF)); // Green component
				buffer.put((byte)(pixel&0xFF)); // Blue component
				buffer.put((byte)((pixel>>24)&0xFF)); // Alpha component. Only for RGBA
			}
		}
		
		buffer.flip();
		return buffer;
	}
	
}
