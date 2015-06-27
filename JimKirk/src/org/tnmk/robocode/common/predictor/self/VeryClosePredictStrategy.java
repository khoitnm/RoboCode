package org.tnmk.robocode.common.predictor.self;

import org.tnmk.robocode.common.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;

import robocode.Robot;

public class VeryClosePredictStrategy extends BasePredictStrategy{

	public VeryClosePredictStrategy(Robot robot) {
	    super(robot);
    }

	@Override
	 public PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest){
		PredictedAimAndFireResult result = super.initResult(firePredictRequest);
	    // TODO Auto-generated method stub
		super.setPredictAimResult(firePredictRequest, null, result);
	    return result;
    }

}
