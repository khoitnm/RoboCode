package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.robot.deprecated.Config;
import org.tnmk.robocode.common.robot.deprecated.ModernRobot;

public class OutlanderStandStill extends ModernRobot {

	public OutlanderStandStill() {
		super();
	}


	public void run() {
		super.init();
		super.setConfig(Config.createConfigStandStillNoFire());
		
		// ahead(5);
		super.preparePos(100, 200);
		super.preparePos(0, 100);
		

		while (true) {
			System.out.println("Normal "+getTime());
			execute();
		}
	}
}