package org.tnmk.robocode.common.gun.predictor.self;

import java.awt.Color;
import java.io.Serializable;

import org.tnmk.robocode.common.gun.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;

public interface PredictStrategy extends Serializable{
	PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest);

	Color getPredictBulletColor();
}
