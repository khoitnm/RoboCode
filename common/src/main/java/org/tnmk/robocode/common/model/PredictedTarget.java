package org.tnmk.robocode.common.model;

import java.io.Serializable;

import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;


public class PredictedTarget implements Serializable{
    private static final long serialVersionUID = -944104881676726239L;

	private final BaseRobotState state;
	
	private PredictedAimAndFireResult predicted;

	public PredictedTarget(BaseRobotState state){
		this.state = state;
	}
	
	public boolean isSameTarget(PredictedTarget target){
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
