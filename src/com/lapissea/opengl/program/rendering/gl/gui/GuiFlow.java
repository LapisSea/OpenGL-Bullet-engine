package com.lapissea.opengl.program.rendering.gl.gui;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lapissea.opengl.program.rendering.gl.gui.GuiElement.Margin;
import com.lapissea.opengl.program.util.math.vec.Vec2f;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public class GuiFlow{
	
	public static enum GuiAligment{
		NEGATIVE(-1),MIDDLE(0),POSITIVE(1);
		
		public final int num;
		
		private GuiAligment(int num){
			this.num=num;
		}
	}
	
	public static interface ISizeCalc{
		
		float calc(float viewPort, boolean axis);
		
		public static ISizeCalc parse(String str){// 10% - 5px
			str=str.replaceAll(" +", "");
			if(str.isEmpty()) return new SizeCalcStatic(0);
			Pattern p=Pattern.compile("\\+|\\-");
			Function<String,ISizeCalc> parse=s->{
				try{
					if(s.endsWith("%")){
						float percent=Float.parseFloat(s.substring(0, s.length()-1))/100;
						if(percent==0) return (v, b)->0;
						if(percent==1) return (v, b)->v;
						return new SizeCalcPercent(percent);
					}
					if(s.endsWith("px")) return new SizeCalcStatic(Float.parseFloat(s.substring(0, s.length()-2)));
					throw new Exception("Unknown value!");
				}catch(Exception e){
					throw new RuntimeException("Cant parse \""+s+"\"!", e);
				}
			};
			
			Matcher m=p.matcher(str);
			IntList ids=new IntArrayList();
			while(m.find()){
				ids.add(m.start());
			}
			
			if(ids.size()==0) return parse.apply(str);
			
			ISizeCalc[] calcs=new ISizeCalc[ids.size()+1];
			byte[] relations=new byte[ids.size()];
			
			calcs[0]=parse(str.substring(0, ids.getInt(0)));
			for(int i=0;i<ids.size();i++){
				int pos=ids.getInt(i);
				switch(str.charAt(pos)){
				case '-':
					relations[i]=-1;
				break;
				case '+':
					relations[i]=1;
				break;
				default:
					throw new RuntimeException("Unknown/unsupported operator '"+str.charAt(pos)+"' at pos "+pos);
				}
				int i1=i+1;
				calcs[i1]=parse.apply(str.substring(pos+1, i1==ids.size()?str.length():ids.getInt(i1)));
			}
			
			return new SizeCalcMixed(calcs, relations);
		}
		
	}
	
	public static class SizeCalcStatic implements ISizeCalc{
		
		public float value;
		
		public SizeCalcStatic(float value){
			this.value=value;
		}
		
		@Override
		public float calc(float viewPort, boolean axis){
			return value;
		}
		
		@Override
		public String toString(){
			return "size="+value+"px";
		}
		
	}
	
	public static class ChildrenContain implements ISizeCalc{
		
		GuiElement		that;
		Vec2f			max	=new Vec2f();
		static Vec2f	VEC	=new Vec2f();
		
		public ChildrenContain(GuiElement that){
			this.that=that;
		}
		
		@Override
		public float calc(float viewPort, boolean axis){
			max.set(0, 0);
			that.children.forEach(child->{
				Margin m=child.margin;
				VEC.set(child.getPos()).add(child.getElementSize()).add(m.left+m.right, m.top+m.bottom);
				max.set(Math.max(VEC.x(), max.x()), Math.max(VEC.y(), max.y()));
			});
			
			return 0;
		}
		
	}
	
	public static class SizeCalcPercent implements ISizeCalc{
		
		public float percent;
		
		public SizeCalcPercent(float percent){
			this.percent=percent;
		}
		
		@Override
		public float calc(float viewPort, boolean axis){
			return viewPort*percent;
		}
		
		@Override
		public String toString(){
			return "size="+percent+"%";
		}
	}
	
	public static class SizeCalcMixed implements ISizeCalc{
		
		private final byte[]		relations;
		private final ISizeCalc[]	calcs;
		
		public SizeCalcMixed(ISizeCalc[] calcs, byte[] relations){
			this.relations=relations;
			this.calcs=calcs;
		}
		
		@Override
		public float calc(float viewPort, boolean axis){
			float value=calcs[0].calc(viewPort, axis);
			
			for(int i=0;i<relations.length;i++){
				value+=relations[i]*calcs[i+1].calc(viewPort, axis);
			}
			
			return value;
		}
		
	}
	
	public GuiAligment	vertical=GuiAligment.NEGATIVE,horizontal=GuiAligment.NEGATIVE;
	public ISizeCalc	preferredWidth,preferredHeight;
	
}
