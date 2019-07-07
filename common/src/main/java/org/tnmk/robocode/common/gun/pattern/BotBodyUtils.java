package org.tnmk.robocode.common.gun.pattern;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class BotBodyUtils {

    /**
     * @param botBodies must be NOT empty
     * @return
     */
    public static Optional<Rectangle2D> reckonIntersectArea(List<BotBody> botBodies) {
        if (botBodies.isEmpty()) {
            throw new IllegalArgumentException("The list of botBodies must be not empty.");
        }
        Iterator<BotBody> botBodyIterator = botBodies.iterator();
        BotBody firstItem = botBodyIterator.next();
        Rectangle2D intersectArea = firstItem.getBotShape();
        while (botBodyIterator.hasNext() && intersectArea != null) {
            BotBody otherBotBody = botBodyIterator.next();
            if (intersectArea.intersects(otherBotBody.getBotShape())) {
                intersectArea = intersectArea.createIntersection(otherBotBody.getBotShape());
            } else {
                intersectArea = null;
            }
        }
        return Optional.ofNullable(intersectArea);
    }
}
