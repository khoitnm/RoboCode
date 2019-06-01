package org.tnmk.robocode.main;

import java.io.Serializable;

public class Config implements Serializable {
    private static final long serialVersionUID = 7002652936917259639L;
	private boolean fire = true;
	private boolean moveCloseToTarget = true;
	private boolean changeDirectionWhenFinishMove = true;
	private boolean changeDirectionWhenBulletHit = true;
	private boolean changeDirectionWhenRobotHit = true;
	private boolean paintTargetPrediction = true;
	private boolean moveOnlyInSafeZone = false;
	
	public static Config createConfigStandStillNoFire(){
		Config config = new Config();
		config.fire = false;
		config.moveCloseToTarget = false;
		config.changeDirectionWhenBulletHit = false;
		config.changeDirectionWhenFinishMove = false;
		config.changeDirectionWhenRobotHit = false;
		return config;
	}
	
	public boolean isFire() {
		return fire;
	}
	public void setFire(boolean fire) {
		this.fire = fire;
	}
	public boolean isMoveCloseToTarget() {
		return moveCloseToTarget;
	}
	public void setMoveCloseToTarget(boolean moveCloseToTarget) {
		this.moveCloseToTarget = moveCloseToTarget;
	}
	public boolean isChangeDirectionWhenFinishMove() {
		return changeDirectionWhenFinishMove;
	}
	public void setChangeDirectionWhenFinishMove(boolean changeDirectionWhenFinishMove) {
		this.changeDirectionWhenFinishMove = changeDirectionWhenFinishMove;
	}
	public boolean isChangeDirectionWhenBulletHit() {
		return changeDirectionWhenBulletHit;
	}
	public void setChangeDirectionWhenBulletHit(boolean changeDirectionWhenBulletHit) {
		this.changeDirectionWhenBulletHit = changeDirectionWhenBulletHit;
	}
	public boolean isChangeDirectionWhenRobotHit() {
		return changeDirectionWhenRobotHit;
	}
	public void setChangeDirectionWhenRobotHit(boolean changeDirectionWhenRobotHit) {
		this.changeDirectionWhenRobotHit = changeDirectionWhenRobotHit;
	}
	public boolean isPaintTargetPrediction() {
		return paintTargetPrediction;
	}
	public void setPaintTargetPrediction(boolean paintTargetPrediction) {
		this.paintTargetPrediction = paintTargetPrediction;
	}
	public boolean isMoveOnlyInSafeZone() {
		return moveOnlyInSafeZone;
	}
	public void setMoveOnlyInSafeZone(boolean moveOnlyInSafeZone) {
		this.moveOnlyInSafeZone = moveOnlyInSafeZone;
	}
	
	
}
