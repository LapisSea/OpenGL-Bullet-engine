package com.lapissea.opengl.program.util;

import com.lapissea.opengl.window.api.util.color.ColorM;
import com.lapissea.opengl.window.api.util.color.ColorMFinal;

public class BlackBody{
	
	private static final ColorMFinal[]	COLORS;
	private static final int[]			KELVINS;
	static{
		int[] data={
				155,188,255,40000,
				155,188,255,39500,
				155,188,255,39000,
				155,188,255,38500,
				156,188,255,38000,
				156,188,255,37500,
				156,189,255,37000,
				156,189,255,36500,
				156,189,255,36000,
				157,189,255,35500,
				157,189,255,35000,
				157,189,255,34500,
				157,189,255,34000,
				157,189,255,33500,
				158,190,255,33000,
				158,190,255,32500,
				158,190,255,32000,
				158,190,255,31500,
				159,190,255,31000,
				159,190,255,30500,
				159,191,255,30000,
				159,191,255,29500,
				160,191,255,29000,
				160,191,255,28500,
				160,191,255,28000,
				161,192,255,27500,
				161,192,255,27000,
				161,192,255,26500,
				162,192,255,26000,
				162,193,255,25500,
				163,193,255,25000,
				163,193,255,24500,
				163,194,255,24000,
				164,194,255,23500,
				164,194,255,23000,
				165,195,255,22500,
				166,195,255,22000,
				166,195,255,21500,
				167,196,255,21000,
				168,196,255,20500,
				168,197,255,20000,
				169,197,255,19500,
				170,198,255,19000,
				171,198,255,18500,
				172,199,255,18000,
				173,200,255,17500,
				174,200,255,17000,
				175,201,255,16500,
				176,202,255,16000,
				177,203,255,15500,
				179,204,255,15000,
				180,205,255,14500,
				182,206,255,14000,
				184,207,255,13500,
				186,208,255,13000,
				188,210,255,12500,
				191,211,255,12000,
				193,213,255,11500,
				196,215,255,11000,
				200,217,255,10500,
				204,219,255,10000,
				208,222,255,9500,
				214,225,255,9000,
				220,229,255,8500,
				227,233,255,8000,
				235,238,255,7500,
				245,243,255,7000,
				255,249,253,6500,
				255,243,239,6000,
				255,236,224,5500,
				255,228,206,5000,
				255,219,186,4500,
				255,209,163,4000,
				255,196,137,3500,
				255,180,107,3000,
				255,161,72,2500,
				255,137,18,2000,
				255,109,0,1500,
				255,51,0,1000
		};
		//40000 - 1000
		
		KELVINS=new int[data.length/4];
		COLORS=new ColorMFinal[data.length/4];
		
		for(int i=0;i<data.length;){
			COLORS[data.length/4-1-i/4]=new ColorMFinal(data[i++]/256F, data[i++]/256F, data[i++]/256F);
			KELVINS[data.length/4-1-i/4]=data[i++];
		}
		
	}
	
	public static ColorM fromKelvin(ColorM dest, float kelvins){
		if(dest==null) dest=new ColorM();
		if(kelvins<=KELVINS[0]) return dest.set(COLORS[0]);
		if(kelvins>=KELVINS[KELVINS.length-1]) return dest.set(COLORS[COLORS.length-1]);
		int id=find((int)kelvins),id1=id+1;
		int val=KELVINS[id];
		return dest.interpolate(COLORS[id], COLORS[id1], (kelvins-val)/(KELVINS[id1]-val));
	}
	
	private static int find(int wut){
		int low=0;
		int high=KELVINS.length-1;
		
		while(low<=high){
			int mid=low+high>>>1;
		int midVal=KELVINS[mid];
		
		if(midVal<wut){
			int next=KELVINS[mid+1];
			if(next>wut) return mid;
			
			low=mid+1;
		}else if(midVal>wut) high=mid-1;
		else return mid;
		}
		return -1;
	}
	
}
