package org.tnmk.robocode.common.gun.predictor.self.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PredictedFireResult implements Serializable {
    private static final long serialVersionUID = -7941579678670729608L;
	private FindingBestFirePointResult findingBestPointResult = new FindingBestFirePointResult();
	/**
	 * This list is never null. Sometimes, the fields inside {@link #findingBestPointResult} has value but this field is still empty because they were calculated without needing to calculate this field.
	 */
	private List<PredictedFirePoint> availableFirePoints = new ArrayList<>();

	public void setFindingBestPointResult(FindingBestFirePointResult findingBestPointResult) {
		this.findingBestPointResult = findingBestPointResult;
	}

	public FindingBestFirePointResult getFindingBestPointResult() {
		return findingBestPointResult;
	}

	public List<PredictedFirePoint> getAvailableFirePoints() {
		return availableFirePoints;
	}

	public void setAvailableFirePoints(List<PredictedFirePoint> possibleFirePoints) {
		if (possibleFirePoints == null){
			this.availableFirePoints = new ArrayList<>();
		}else{
			this.availableFirePoints = possibleFirePoints;
		}
	}

}
