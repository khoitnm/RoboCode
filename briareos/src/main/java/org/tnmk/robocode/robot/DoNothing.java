package org.tnmk.robocode.robot;

import org.tnmk.robocode.common.robot.deprecated.ModernRobot;
import robocode.ScannedRobotEvent;


public class DoNothing extends ModernRobot {

	public DoNothing() {
		super();
	}


	public void run() {

		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
		
		while (true) {
			long begin = System.currentTimeMillis();
			System.out.println("Normal Bgin \t"+getTime()+"");
			setAhead(100);
			setTurnGunLeft(360);
			setTurnLeft(360);
			setTurnRadarRight(360);
			setFire(1);
			setAhead(-200);
			setTurnGunRight(360);
			setTurnRadarRight(-360);
			setTurnRight(360);
			long end = System.currentTimeMillis();
			System.out.println("Normal End \t"+getTime()+" runtime: "+(end - begin)+" ms");
			execute();
		}
	}
	
	public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
		System.out.println("\t Scaned Robot Begin \t"+getTime());
		setTurnGunRight(100);
		execute();
		System.out.println("\t Scaned Robot End \t"+getTime());
//		System.out.println("\t Scaned Robot "+getTime());
	}
}