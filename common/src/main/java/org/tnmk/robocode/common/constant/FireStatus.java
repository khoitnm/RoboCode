package org.tnmk.robocode.common.constant;

public enum FireStatus {
	STAND_STILL(0), STARTED_AIM(1), AIMED(2), STARTED_FIRE(3), FIRED(4);
	private final int numValue;
	
	FireStatus(int numValue){
		this.numValue = numValue;
	}

	public int getNumValue() {
	    return numValue;
    }
}