package org.tnmk.robocode.common.gun.predictor.self;

import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.BattleField;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.gun.predictor.albert.MoveSim;
import org.tnmk.robocode.common.gun.predictor.albert.MoveSimStat;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictStateResult;

public class PredictWrapper {
	public static PredictStateResult toPredictResult(MoveSimStat moveSimStat){
		PredictStateResult rs = new PredictStateResult();
		rs.setHeading(Math.toDegrees(moveSimStat.heading));
		rs.setDifferentHeadingToPreviousStep(Math.toDegrees(moveSimStat.differentHeading));
		rs.setVelocity(moveSimStat.velocity);
		rs.setX(moveSimStat.x);
		rs.setY(moveSimStat.y);
		return rs;
	}
	public static PredictStateResult[] toPredictResults(MoveSimStat[] source){
		PredictStateResult[] rs = new PredictStateResult[source.length];
		for (int i = 0; i < rs.length; i++) {
	        rs[i] = toPredictResult(source[i]);
        }
		return rs;
	}
	/**
	 * Note: this method cannot predict target position correctly (because of remaining distance?).
	 * @param steps
	 * @param robotState
	 * @param battleField
	 * @return
	 */
	public static PredictStateResult predict(int steps, FullRobotState robotState, BattleField battleField) {
		if (steps == 0){
			PredictStateResult result = new PredictStateResult();
			result.setDifferentHeadingToPreviousStep(0);
			result.setHeading(robotState.getHeading());
			result.setPosition(robotState.getPosition());
			result.setVelocity(robotState.getVelocity());
			return result;
		}
		PredictStateResult[] rs = predictSteps(steps, robotState, battleField.getWidth(), battleField.getHeight());
		return rs[rs.length-1];
	}
	public static PredictStateResult[] predictSteps(int steps, FullRobotState robotState, double battleWidth, double battleHeight) {
		MoveSim moveSim = new MoveSim();
		double headingRadian = Math.toRadians(robotState.getHeading());
		double turnRemainingRadian = Math.toRadians(robotState.getTurnRemaining());
		MoveSimStat[] result = moveSim.futurePos(
				steps, robotState.getX(), robotState.getY(), robotState.getVelocity(),robotState.getMaxVelocity(), headingRadian, robotState.getDistanceRemaining(), turnRemainingRadian, robotState.getMaxTurnRate(), battleWidth, battleHeight);
		return toPredictResults(result);
	}
	
    /**
     * Estimates the coordinate of the target after time t.
     * @param start - The position of the target at t = 0
     * @param heading - The direction vector of the target at t = 0
     * @param time - The time elapsed since last scan.
     * @param velocity - The velocity of the target along it's vector.
     * @return - The estimated new position of the target.
     */
    public static PredictStateResult predictTargetPosition(int steps, BaseRobotState robotState){
    	PredictStateResult result = new PredictStateResult();
    	double distance = robotState.getSpeed() * steps;
    	double moveRadians = Math.toRadians(robotState.getMoveAngle());
    	result.setDifferentHeadingToPreviousStep(0);
    	result.setHeading(robotState.getHeading());
    	result.setVelocity(robotState.getVelocity());
    	result.setX(robotState.getX()+distance*Math.sin(moveRadians));
    	result.setY(robotState.getY()+distance*Math.cos(moveRadians));
    	return result;
    }
}
