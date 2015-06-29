package org.tnmk.robocode.common.predictor.self;

import java.awt.Color;
import java.io.Serializable;

import org.tnmk.robocode.common.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;

public interface PredictStrategy extends Serializable{
	PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest);

	Color getPredictBulletColor();
}
