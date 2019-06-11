package org.tnmk.robocode.common.gun.predictor.self.model;

import org.tnmk.robocode.common.model.BaseRobotState;

public class PredictStateResult extends BaseRobotState {
	private double differentHeadingToPreviousStep;

	public double getDifferentHeadingToPreviousStep() {
		return differentHeadingToPreviousStep;
	}

	public void setDifferentHeadingToPreviousStep(double differentHeadingToPreviousStep) {
		this.differentHeadingToPreviousStep = differentHeadingToPreviousStep;
	}

}
