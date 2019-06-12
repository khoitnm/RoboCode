package org.tnmk.robocode.common.movement;

/**
 * This class help us to know which movement strategy is using.
 */
public class MovementContext {
    private SpecialMovementType specialMovementType = SpecialMovementType.NONE;

    public boolean isNone(){
        return specialMovementType == null || specialMovementType == SpecialMovementType.NONE;
    }

    public boolean is(SpecialMovementType specialMovementType){
        return this.specialMovementType == specialMovementType;
    }

    public SpecialMovementType getSpecialMovementType() {
        return specialMovementType;
    }

    public void setSpecialMovementType(SpecialMovementType specialMovementType) {
        this.specialMovementType = specialMovementType;
    }
}
