package org.tnmk.robocode.common.helper;

import java.io.Serializable;

import org.tnmk.robocode.common.constant.Distance;

import robocode.Robot;
import robocode.ScannedRobotEvent;

public class FireByDistance implements Serializable{
    private static final long serialVersionUID = -1506827075368013673L;
	private Robot robot;
	public FireByDistance(Robot robot){
		this.robot = robot;
	}
	
	public RobotAction fire(ScannedRobotEvent e){
		double distance = e.getDistance();
		if (distance <= Distance.SO_FAR){
			if (distance <= Distance.CLOSE){
	    		robot.fire(3);
	    	}else if (distance <= Distance.MEDIUM){
	    		robot.fire(2);
	    	}else{
	    		robot.fire(1);
	    	}	
			return RobotAction.FIRE;
		}else{
			return RobotAction.HOLDON;
		}
	}
}
