package org.tnmk.robocode.common.gun.predictor.self;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.common.math.MathUtils;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.BattleField;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.gun.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictStateResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.gun.predictor.self.model.RawEstimateAimResult;

import robocode.Robot;
import robocode.Rules;

public class PredictManager  implements Serializable{
	private final List<PredictStrategy> predictStrategies = new ArrayList<>();


	private BattleField battleField;
	private Robot robot;
	public PredictManager(Robot robot) {
		this.robot = robot;
		this.battleField = MoveHelper.createBattleField(robot);
		
		predictStrategies.add(new VeryClosePredictStrategy(robot));
		predictStrategies.add(new FacingPredictStrategy(robot));
		predictStrategies.add(new LinearPredictStrategy(robot)); 
		
	}

	public PredictedAimAndFireResult predictBestStepsToAimAndFire(long time, double gunCoolRate, double sourceGunHeat, int maxPower, double sourceGunHeading, FullRobotState sourceState, BaseRobotState targetState) {
		// Robot will aim while waiting for the gun cool down. So the steps to
		// aim is equals to the steps to cool gun down.
		int gunCoolTime = (int) Math.ceil(sourceGunHeat / gunCoolRate) + 1;
		double estimatedGunTurnRightAngle = MathUtils.calculateTurnRightDirectionToTarget(sourceGunHeading, sourceState.getPosition(), targetState.getPosition());
		int estimatedAimSteps = (int)Math.ceil(Math.abs(estimatedGunTurnRightAngle)/Rules.GUN_TURN_RATE)+1;
		// if the steps to cool gun down is too short, robot won't have enough steps to aim, so we must calculate aimSteps base on the estimated gunTurnAngle.
		int aimSteps = Math.max(gunCoolTime, estimatedAimSteps);

		PredictStateResult predictedAimedSource = PredictWrapper.predict(aimSteps, sourceState, battleField);
		PredictStateResult predictedAimedTarget = PredictWrapper.predictTargetPosition(aimSteps, targetState);
	
		RawEstimateAimResult firstAimEstimation = new RawEstimateAimResult();
		firstAimEstimation.setGunTurnRightDirection(estimatedGunTurnRightAngle);
		firstAimEstimation.setAimSteps(aimSteps);
		firstAimEstimation.setAimedSource(predictedAimedSource);
		firstAimEstimation.setAimedTarget(predictedAimedTarget);
		
		FirePredictRequest firePredictRequest = new FirePredictRequest();
		firePredictRequest.setBeginSource(sourceState);
		firePredictRequest.setBeginTarget(targetState);
		firePredictRequest.setBeginTime(time);
		firePredictRequest.setMaxPower(maxPower);
		firePredictRequest.setBeginSourceGunHeading(sourceGunHeading);
		firePredictRequest.setAimEstimateResult(firstAimEstimation);
		
		
		PredictedAimAndFireResult result = null;
		for (PredictStrategy predictStrategy : predictStrategies) {
			result = predictStrategy.predictBestFirePoint(firePredictRequest);
			if (result.isFoundBestPoint()){
				break;
			}
        }
		
		return result;
	}


	public Robot getRobot() {
	    return robot;
    }

	public void setRobot(Robot robot) {
	    this.robot = robot;
    }
}
