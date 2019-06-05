package org.tnmk.robocode.common.predictor.self.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.tnmk.common.math.LineSegment;

public class FindingBestFirePointResult implements Serializable {
    private static final long serialVersionUID = -7193042179679613688L;
	/**
	 * This list is never null. Sometimes, the field {@link #bestPoint} has value but this field is still empty because bestPoint was calculated without needing to calculate this field.
	 */
	private List<PredictedFirePoint> nearestPoints = new ArrayList<>();
	private List<PredictedFirePoint> possiblePoints = new ArrayList<>();
	private List<PredictedFirePoint> availablePoints = new ArrayList<>();
	/**
	 * This list is never null. Sometimes, the field {@link #bestPoint} has value but this field is still empty because bestPoint was calculated without needing to calculate this field.
	 */
	private List<PredictedFirePoint> impossiblePoints = new ArrayList<>();
	private List<PredictedFirePoint> tooFarPoints = new ArrayList<>();
	private List<PredictedFirePoint> outsideBattlePoints = new ArrayList<>();
	private List<PredictedFirePoint> impossibleAnglePoints = new ArrayList<>();

	private PredictedFirePoint bestPoint;
	private LineSegment targetCurrentMoveLine;
	private double targetCurrentMoveAngle;

	// SEPECIAL SET-GET
	// ------------------------------------------------------------------------------------------
	public void setImpossiblePoints(List<PredictedFirePoint> impossiblePoints) {
		if (impossiblePoints == null){
			this.impossiblePoints = new ArrayList<>();
		}else{
			this.impossiblePoints = impossiblePoints;
		}
	}

	public void setNearestPoints(List<PredictedFirePoint> nearestPoints) {
		if (nearestPoints == null){
			this.nearestPoints = new ArrayList<>();
		}else{
			this.nearestPoints = nearestPoints;
		}
	}

	// SET-GET
	// ------------------------------------------------------------------------------------------
	public List<PredictedFirePoint> getNearestPoints() {
		return nearestPoints;
	}

	public PredictedFirePoint getBestPoint() {
		return bestPoint;
	}

	public void setBestPoint(PredictedFirePoint bestPoint) {
		this.bestPoint = bestPoint;
	}

	public double getTargetCurrentMoveAngle() {
		return targetCurrentMoveAngle;
	}

	public void setTargetCurrentMoveAngle(double targetCurrentMoveAngle) {
		this.targetCurrentMoveAngle = targetCurrentMoveAngle;
	}

	public LineSegment getTargetCurrentMoveLine() {
		return targetCurrentMoveLine;
	}

	public void setTargetCurrentMoveLine(LineSegment targetCurrentMoveLine) {
		this.targetCurrentMoveLine = targetCurrentMoveLine;
	}

	public List<PredictedFirePoint> getImpossiblePoints() {
		return impossiblePoints;
	}

	public List<PredictedFirePoint> getImpossibleAnglePoints() {
		return impossibleAnglePoints;
	}

	public void setImpossibleAnglePoints(List<PredictedFirePoint> impossibleAnglePoints) {
		this.impossibleAnglePoints = impossibleAnglePoints;
	}

	public List<PredictedFirePoint> getTooFarPoints() {
		return tooFarPoints;
	}

	public void setTooFarPoints(List<PredictedFirePoint> tooFarPoints) {
		this.tooFarPoints = tooFarPoints;
	}

	public List<PredictedFirePoint> getOutsideBattlePoints() {
		return outsideBattlePoints;
	}

	public void setOutsideBattlePoints(List<PredictedFirePoint> outsideBattlePoints) {
		this.outsideBattlePoints = outsideBattlePoints;
	}

	public List<PredictedFirePoint> getPossiblePoints() {
		return possiblePoints;
	}

	public void setPossiblePoints(List<PredictedFirePoint> possiblePoints) {
		this.possiblePoints = possiblePoints;
	}

	public List<PredictedFirePoint> getAvailablePoints() {
		return availablePoints;
	}

	public void setAvailablePoints(List<PredictedFirePoint> availablePoints) {
		this.availablePoints = availablePoints;
	}

}