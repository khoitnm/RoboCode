package org.tnmk.robocode.jimkirk;


import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.PredictWrapper;
import org.tnmk.robocode.common.predictor.self.model.PredictStateResult;

import robocode.Event;
import robocode.StatusEvent;

/**
 * @author Khoi
 * With AdvancedRobot, the shooting time is different from basic Robot class.
 * 
 * Term:
 * + Bearing: the angle (degree) from pointA to pointB (or vectorA to vectorB).
 * It can be an absolute bearing (compare to North axis) or relative bearing (compare to vectorA)
 */
public class JimKirkStraight extends JimKirkBase {
	public static int MOVE_DISTANCE = 100000;
	private int direction = 1;
	
	long predictTime = -1;
	PredictStateResult predictState;
    public void run() {
    	super.init();
    	
    	turnLeft(getHeading() - 90);
        while (true) {
        	if (getDistanceRemaining() == 0 || getVelocity() == 0){
        		direction = -direction;
        	}
        	setAhead(direction * MOVE_DISTANCE);
        	long time = getTime();
        	if (time > predictTime){
        		int predictSteps = 30;
            	FullRobotState thisState = RobotStateConverter.toRobotState(this);
            	predictState = PredictWrapper.predict(predictSteps, thisState, battleField);
            	predictTime = predictSteps + getTime();
            	String msg = String.format("PREDICT: %s - (%.2f, %.2f)", 
            			predictTime, predictState.getX(), predictState.getY()
            			);
    			System.out.println(msg);
        	}
        	execute();
        }
    }
	public void onStatus(StatusEvent e) {
		long time = getTime();
		FullRobotState robotState = RobotStateConverter.toRobotState(this);
		String msg = String.format("%s - THIS(%.2f, %.2f)", time, robotState.getX(), robotState.getY());
		System.out.println(msg);
		if (predictState != null && time == predictTime) {
			if (Math.abs(getX() - predictState.getX()) > 5){
				msg = String.format("\t\t predicted! WRONG (%.2f, %.2f)", predictState.getX(), predictState.getY());
				System.out.println(msg);
			}
		}
	}
    
    @Override
	protected void moveAwayFromTarget(Event e, double bearing) {
//		setTurnLeft(90 - bearing);
	}
}