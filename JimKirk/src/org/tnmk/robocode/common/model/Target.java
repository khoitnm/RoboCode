package org.tnmk.robocode.common.model;

import java.io.Serializable;

import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;


public class Target implements Serializable{
    private static final long serialVersionUID = -944104881676726239L;

	private final BaseRobotState state;
	
	private PredictedAimAndFireResult predicted;

	public Target(BaseRobotState state){
		this.state = state;
	}
	
	public boolean isSameTarget(Target target){
		return this.getState().getName().equals(target.getState().getName());
	}
	
	public PredictedAimAndFireResult getPredicted() {
	    return predicted;
    }

	public void setPredicted(PredictedAimAndFireResult predicted) {
	    this.predicted = predicted;
    }

	public BaseRobotState getState() {
	    return state;
    }

//	public void setState(BaseRobotState state) {
//	    this.state = state;
//    }
}
