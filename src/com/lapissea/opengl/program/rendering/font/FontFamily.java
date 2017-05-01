package com.lapissea.opengl.program.rendering.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;

import com.lapissea.opengl.program.interfaces.FloatConsumer;
import com.lapissea.opengl.program.rendering.gl.texture.TextureLoader;
import com.lapissea.opengl.program.rendering.gl.texture.TextureTiled;
import com.lapissea.opengl.program.rendering.gl.texture.UvArea;
import com.lapissea.opengl.program.util.IntTree;
import com.lapissea.opengl.program.util.LogUtil;
import com.lapissea.opengl.program.util.PairM;
import com.lapissea.opengl.program.util.UtilM;
import com.lapissea.opengl.program.util.math.vec.Vec2f;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;

public class FontFamily{
	
	private static final GraphicsEnvironment	GE		=GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static final Font					DEFAULT	=new Font(Font.SANS_SERIF, Font.PLAIN, 1);
	
	private Font		font;
	private FontMetrics	metrics;
	
	@SuppressWarnings("unused")
	private float	xOrigin,yOrigin,x,y,xMargin,yMargin;
	private boolean	quad=false;
	
	private IntTree<LetterUv>	data	=new IntTree<>();
	public final TextureFont	letters	=TextureLoader.loadTexture("letters", new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), TextureFont.class/*, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST*/);
	
	public static class TextureFont extends TextureTiled<LetterUv>{
		
		public TextureFont(String fontName){
			super("font-"+fontName);
		}
		
	}
	
	public static class LetterUv extends UvArea{
		
		public final char	c;
		public final int	height,width,topMar,bottomMar,leftMar,rightMar;
		
		public LetterUv(char c, Vec2f min, Vec2f max, int height, int width, int topMar, int bottomMar, int leftMar, int rightMar){
			super(min, max);
			this.c=c;
			this.height=height;
			this.width=width;
			this.topMar=topMar;
			this.bottomMar=bottomMar;
			this.leftMar=leftMar;
			this.rightMar=rightMar;
		}
		
	}
	
	private class LetterImg implements Comparable<LetterImg>{
		
		char				c;
		int					id,topMar,bottomMar,leftMar,rightMar;
		BufferedImage		img;
		Rectangle2D.Float	pos;
		
		public LetterImg(char c, BufferedImage img, int topMar, int bottomMar, int leftMar, int rightMar){
			this.c=c;
			this.img=img;
			pos=new Rectangle2D.Float(0, 0, img.getWidth(), img.getHeight());
			this.topMar=topMar;
			this.bottomMar=bottomMar;
			this.leftMar=leftMar;
			this.rightMar=rightMar;
		}
		
		@Override
		public int compareTo(LetterImg o){
			int h=Float.compare(o.pos.height, pos.height);
			if(h!=0) return h;
			int w=Float.compare(o.pos.width, pos.width);
			if(w!=0) return w;
			return Integer.compare(c, o.c);
		}
	}
	
	public FontFamily(String name){
		font=Arrays.stream(GE.getAllFonts()).filter(f->f.getFamily().equals(name)).findFirst().orElseGet(()->{
			
			try(InputStream fontSrc=UtilM.getResource("fonts/"+name+".zip")){
				if(fontSrc==null){
					LogUtil.printlnEr("Font at \"fonts/"+name+".zip\" does not exist!");
					return new Font(name, Font.PLAIN, 1);
				}
				ZipInputStream zip=new ZipInputStream(fontSrc);
				for(ZipEntry e;(e=zip.getNextEntry())!=null;){
					String fileName=e.getName();
					if(fileName.startsWith("regular")||fileName.startsWith("bold")||fileName.startsWith("light")){
						Font f=Font.createFont(Font.TRUETYPE_FONT, zip);
						LogUtil.println(f);
						GE.registerFont(f);
						return f;
					}
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return new Font(name, Font.PLAIN, 1);
		});
		fontSize(100);
		build();
	}
	
	private void build(){
		StringBuilder toBuild=new StringBuilder();
		//		LogUtil.println("generating");
		//		int j=(int)(Math.pow(2, 8));
		//		for(int i=0;i<j;i++){
		//			toBuild.append((char)i);
		//		}
		toBuild.append("?0123456789 ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-._,;:!(){}[]|7\\%'\"/\\");
		
		//		long start=System.currentTimeMillis();
		
		int xSize=2,ySize=2;
		final ArrayList<LetterImg> tiles;
		PairM<LetterImg,?> wat=new PairM<>();
		
		if(toBuild.length()>0){
			tiles=new ArrayList<>(toBuild.length());
			//			LogUtil.println("rendering letters");
			toBuild.chars().parallel().forEach(i->{
				char c=(char)i;
				
				if(!font.canDisplay(c)){
					synchronized(tiles){
						if(wat.obj1==null) tiles.add(wat.obj1=charToTile(c));
					}
					return;
				}
				
				LetterImg t=charToTile(c);
				if(t!=null){
					synchronized(tiles){
						tiles.add(t);
					}
				}
				
			});
			
			//			LogUtil.println("Rendered", tiles.size(), "tiles\nsorting");
			
			int siz=tiles.stream().mapToInt(t->(int)(t.pos.width*t.pos.height)).sum();
			while(xSize*ySize<siz){
				xSize*=2;
				if(xSize*ySize<siz) ySize*=2;
			}
			
			tiles.sort(null);
			List<LetterImg> placed=new ArrayList<>(tiles.size());
			Rectangle2D.Float imgR=new Rectangle2D.Float(0, 0, xSize, ySize);
			for(LetterImg t:tiles){
				//Find place
				while(true){
					LetterImg colided=placed.stream().filter(t1->t1.pos.intersects(t.pos)).findFirst().orElse(null);
					
					if(colided==null){
						//found it!
						
						//snuggle in
						int xPrev,yPrev;
						do{
							xPrev=(int)t.pos.x;
							yPrev=(int)t.pos.y;
							
							t.pos.x--;
							if(t.pos.x<0) t.pos.x++;
							else if(placed.stream().anyMatch(t1->t1.pos.intersects(t.pos))){
								t.pos.x++;
							}
							t.pos.y--;
							if(t.pos.y<0) t.pos.y++;
							if(placed.stream().anyMatch(t1->t1.pos.intersects(t.pos))){
								t.pos.y++;
							}
						}while(xPrev!=(int)t.pos.x||yPrev!=(int)t.pos.y);
						
						break;
					}
					else{
						double pos=colided.pos.getMaxX();
						LetterImg nextPossible=null;
						while(colided.id+1<placed.size()){
							nextPossible=placed.get(colided.id+1);
							if(nextPossible.pos.getMinX()!=pos||colided.pos.y!=nextPossible.pos.y) break;
							colided=nextPossible;
							pos=colided.pos.getMaxX();
						}
						t.pos.x=(float)pos;
					}
					
					//reached end?
					if(t.pos.getMaxX()>imgR.width){
						//reset to next line
						t.pos.x=0;
						t.pos.y+=t.pos.height/4F;
						
					}
					if(t.pos.getMaxY()>imgR.height){
						if(ySize<=xSize){
							ySize*=2;
							imgR.height*=2;
						}
						else{
							xSize*=2;
							imgR.width*=2;
						}
					}
				}
				t.id=placed.size();
				placed.add(t);
				//LogUtil.println(Math.round((placed.size()/(float)tiles.size())*1000)/10F);
			}
		}
		else tiles=null;
		
		BufferedImage img=new BufferedImage(xSize, ySize, BufferedImage.TYPE_4BYTE_ABGR);
		if(toBuild.length()>0){
			//			LogUtil.println("rendering 2");
			Graphics2D g=img.createGraphics();
			//			g.setColor(Color.CYAN);
			//			g.fillRect(0, 0, xSize, ySize);
			tiles.forEach(t->{
				g.drawImage(t.img, (int)t.pos.x, (int)t.pos.y, null);
			});
			g.dispose();
		}
		letters.tiles.clear();
		data.clear();
		
		for(LetterImg l:tiles){
			LetterUv newUv=new LetterUv(l.c,
					new Vec2f(l.pos.x/xSize, l.pos.y/ySize), new Vec2f((l.pos.x+l.pos.width-2)/xSize, (l.pos.y+l.pos.height-2)/ySize),
					l.img.getHeight(), l.img.getWidth(),
					l.topMar, l.bottomMar, l.leftMar, l.rightMar);
			
			letters.tiles.add(newUv);
			data.set(l.c, newUv);
		}
		
		//LogUtil.println("done in", System.currentTimeMillis()-start);
		
		TextureLoader.reloadTexture(letters, img);
		
		File outputfile=new File("image.png");
		try{
			ImageIO.write(img, "png", outputfile);
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}
	
	private LetterImg charToTile(char c){
		int w=getWidth(c);
		if(w<=0) return null;
		int h=metrics.getHeight();
		
		int margin=Math.max(w, h)/10;
		w+=margin*2;
		h+=margin*2;
		
		BufferedImage img=new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g=img.createGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		g.setColor(Color.WHITE);
		g.setFont(font);
		g.drawString(Character.toString(c), margin, margin+metrics.getAscent());
		g.dispose();
		
		int topEmpty=0;
		int bottomEmpty=img.getHeight()-1;
		int leftEmpty=0;
		int rightEmpty=w-1;
		
		whil:while(topEmpty<h-1){
			for(int x=0;x<w;x++){
				if(((img.getRGB(x, topEmpty)>>24)&0xff)>0) break whil;
			}
			topEmpty++;
		}
		whil:while(bottomEmpty>topEmpty){
			for(int x=0;x<w;x++){
				if(((img.getRGB(x, bottomEmpty-1)>>24)&0xff)>0) break whil;
			}
			bottomEmpty--;
		}
		whil:while(leftEmpty<w-1){
			for(int y=topEmpty;y<bottomEmpty;y++){
				if(((img.getRGB(leftEmpty, y)>>24)&0xff)>0) break whil;
			}
			leftEmpty++;
		}
		whil:while(rightEmpty>leftEmpty){
			for(int y=0;y<h;y++){
				if(((img.getRGB(rightEmpty-1, y)>>24)&0xff)>0) break whil;
			}
			rightEmpty--;
		}
		int w1=rightEmpty-leftEmpty+2;
		int h1=bottomEmpty-topEmpty+2;
		if(w==0||h==0) return null;
		
		BufferedImage img0=img;
		img=new BufferedImage(w1, h1, BufferedImage.TYPE_4BYTE_ABGR);
		g=img.createGraphics();
		
		//		g.setColor(new Color(rand.nextFloat()*0.8F+0.2F, rand.nextFloat()*0.8F+0.2F, rand.nextFloat()*0.8F+0.2F));
		//		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		
		g.drawImage(img0, -leftEmpty, -topEmpty, null);
		
		g.dispose();
		
		w-=margin*2;
		h-=margin*2;
		
		return new LetterImg(c, img, topEmpty, h-bottomEmpty, leftEmpty, w-rightEmpty);
	}
	
	private int getWidth(char c){
		return metrics.charWidth(c);
	}
	
	public void setyMargin(float yMargin){
		this.yMargin=yMargin;
	}
	
	public void setxMargin(float xMargin){
		this.xMargin=xMargin;
	}
	
	public void setQuad(boolean quad){
		this.quad=quad;
	}
	
	private void fontSize(float size){
		if(font.getSize2D()!=size){
			font=font.deriveFont(size);
			metrics=new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics(font);
		}
	}
	
	public void build(float size, String toBuild, FloatConsumer vert, FloatConsumer uv){
		build(0, 0, size, toBuild, vert, uv);
	}
	
	public PairM<FloatList,FloatList> build(float size, String toBuild){
		return build(0, 0, size, toBuild);
	}
	
	public void build(float x, float y, float size, char toBuild, FloatConsumer vert, FloatConsumer uv){
		this.xOrigin=this.x=x;
		this.yOrigin=this.y=y;
		addChar(toBuild, vert, uv);
	}
	
	public PairM<FloatList,FloatList> build(float x, float y, float size, char toBuild){
		FloatList vert=new FloatArrayList(),uv=new FloatArrayList();
		build(x, y, size, toBuild, vert::add, uv::add);
		return new PairM<FloatList,FloatList>(vert, uv);
	}
	
	public void build(float x, float y, float size, String toBuild, FloatConsumer vert, FloatConsumer uv){
		this.xOrigin=this.x=x;
		this.yOrigin=this.y=y;
		for(char c:toBuild.toCharArray()){
			addChar(c, vert, uv);
		}
	}
	
	public PairM<FloatList,FloatList> build(float x, float y, float size, String toBuild){
		FloatList vert=new FloatArrayList(),uv=new FloatArrayList();
		build(x, y, size, toBuild, vert::add, uv::add);
		return new PairM<FloatList,FloatList>(vert, uv);
	}
	
	private void addChar(char toBuild, FloatConsumer vert, FloatConsumer uv){
		
		if(toBuild=='\n'){
			x=xOrigin;
			y-=metrics.getHeight()+yMargin;
			return;
		}
		LetterUv tile=data.get(toBuild);
		if(tile==null) LogUtil.printlnEr("Cant find", toBuild);
		float w=tile.width,h=tile.height;
		
		if(!Character.isWhitespace(toBuild)){
			float x=this.x+tile.leftMar,y=this.y+tile.bottomMar;
			if(quad){
				add(vert, uv, x, y, tile.topLeftX(), tile.topLeftY());
				add(vert, uv, x, y+h, tile.bottomLeftX(), tile.bottomLeftY());
				add(vert, uv, x+w, y+h, tile.bottomRightX(), tile.bottomRightY());
				add(vert, uv, x+w, y, tile.topRightX(), tile.topRightY());
			}
			else{
				add(vert, uv, x+w, y+h, tile.topRightX(), tile.topRightY());
				add(vert, uv, x, y+h, tile.topLeftX(), tile.topLeftY());
				add(vert, uv, x, y, tile.bottomLeftX(), tile.bottomLeftY());
				
				add(vert, uv, x+w, y, tile.bottomRightX(), tile.bottomRightY());
				add(vert, uv, x+w, y+h, tile.topRightX(), tile.topRightY());
				add(vert, uv, x, y, tile.bottomLeftX(), tile.bottomLeftY());
			}
		}
		
		this.x+=w+xMargin+tile.leftMar+tile.rightMar;
	}
	
	private void add(FloatConsumer vert, FloatConsumer uv, float x, float y, float u, float v){
		vert.accept(x);
		vert.accept(y);
		uv.accept(u);
		uv.accept(v);
	}
}
