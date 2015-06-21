package org.tnmk.robocode.common.predictor.self.model;

import java.util.ArrayList;
import java.util.List;

import org.tnmk.robocode.common.math.LineSegment;

public class FindingBestFirePointResult{
	private List<PredictedFiredPoint> nearestPoints = new ArrayList<>();
	private List<PredictedFiredPoint> impossiblePoints = new ArrayList<>();
	private PredictedFiredPoint bestPoint;
	private LineSegment targetCurrentMoveLine;
	private double targetCurrentMoveAngle;
	
	public List<PredictedFiredPoint> getNearestPoints() {
		return nearestPoints;
	}
	public void setNearestPoints(List<PredictedFiredPoint> nearestPoints) {
		this.nearestPoints = nearestPoints;
	}
	public PredictedFiredPoint getBestPoint() {
		return bestPoint;
	}
	public void setBestPoint(PredictedFiredPoint bestPoint) {
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

	public List<PredictedFiredPoint> getImpossiblePoints() {
        return impossiblePoints;
    }
	public void setImpossiblePoints(List<PredictedFiredPoint> impossiblePoints) {
        this.impossiblePoints = impossiblePoints;
    }
}