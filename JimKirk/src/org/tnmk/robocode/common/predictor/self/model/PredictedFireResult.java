package org.tnmk.robocode.common.predictor.self.model;

import java.util.ArrayList;
import java.util.List;

public class PredictedFireResult {
	private FindingBestFirePointResult findingBestPointResult = new FindingBestFirePointResult();
	/**
	 * This list is never null.
	 * Sometimes, the fields inside {@link #findingBestPointResult} has value but this field is still empty because they were calculated without needing to calculate this field.
	 */
	private List<PredictedFirePoint> AvailableFirePoints = new ArrayList<>();
	
	public void setFindingBestPointResult(FindingBestFirePointResult findingBestPointResult) {
		this.findingBestPointResult = findingBestPointResult;
	}
	public FindingBestFirePointResult getFindingBestPointResult() {
		return findingBestPointResult;
	}
	public List<PredictedFirePoint> getAvailableFirePoints() {
		return AvailableFirePoints;
	}
	public void setAvailableFirePoints(List<PredictedFirePoint> possibleFirePoints) {
		if (possibleFirePoints == null) this.AvailableFirePoints = new ArrayList<>(); 
		this.AvailableFirePoints = possibleFirePoints;
	}
	
}
