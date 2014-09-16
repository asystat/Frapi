package com.pichula.frapi.api;

public class MutableFloat implements Comparable{

	float value = 1f; // note that we start at 1 since we're counting

	public MutableFloat(float f){
		value=f;
	}
	 
	public MutableFloat(){
		
	}
	
	public void increment() {
		++value;
	}

	public float get() {
		return value;
	}

	@Override
	public int compareTo(Object arg0) {
		float externValue=((MutableFloat)arg0).value;
		return value > externValue ? +1 : value < externValue ? -1 : 0;
	}

	public void increment(float increment) {
		value+=increment;
	}
}
