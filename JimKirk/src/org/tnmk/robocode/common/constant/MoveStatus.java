package org.tnmk.robocode.common.constant;

public enum MoveStatus {
	HIT_ROBOT(-1), HIT_WALL(-1), STAND_STILL(0), RUNNING(1),
	
	HIT_BULLET(2) //It hit bullet, but still moving
	;
	private final int numValue;
	
	MoveStatus(int numValue){
		this.numValue = numValue;
	}

	public int getNumValue() {
	    return numValue;
    }
}