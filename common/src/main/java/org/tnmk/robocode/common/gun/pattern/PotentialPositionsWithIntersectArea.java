package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Optional;
import org.tnmk.robocode.common.log.LogHelper;

public class PotentialPositionsWithIntersectArea {
    private final Optional<Rectangle2D> intersectArea;
    private final List<BotBody> potentialBotBodies;

    public PotentialPositionsWithIntersectArea(Optional<Rectangle2D> intersectArea, List<BotBody> potentialBotBodies) {
        this.intersectArea = intersectArea;
        this.potentialBotBodies = potentialBotBodies;
    }

    @Override
    public String toString() {
        return "PotentialPositionsWithIntersectArea{" +
                "intersectArea=" + LogHelper.toString(intersectArea.orElse(null)) +
                ", potentialBotBodies=" + potentialBotBodies +
                '}';
    }

    public Optional<Rectangle2D> getIntersectArea() {
        return intersectArea;
    }

    public List<BotBody> getPotentialBotBodies() {
        return potentialBotBodies;
    }
}
