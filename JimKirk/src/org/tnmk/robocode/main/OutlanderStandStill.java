package org.tnmk.robocode.main;

import java.sql.PreparedStatement;

import org.tnmk.robocode.common.constant.AimStatus;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.WallSmoothHelper;
import org.tnmk.robocode.common.helper.MoveHelper.BattlePosition;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedFirePoint;

import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

public class OutlanderStandStill extends OutlanderBase {

	public OutlanderStandStill() {
		super();
	}


	public void run() {
		super.init();
		super.setConfig(Config.createConfigStandStillNoFire());
		
		// ahead(5);
		super.preparePos(this.battleField.getWidth() - 300, 100);
		

		while (true) {
			execute();
		}
	}
}