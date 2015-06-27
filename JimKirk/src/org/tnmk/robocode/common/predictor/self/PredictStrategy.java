package org.tnmk.robocode.common.predictor.self;

import org.tnmk.robocode.common.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;

public interface PredictStrategy {
	PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest);
}
