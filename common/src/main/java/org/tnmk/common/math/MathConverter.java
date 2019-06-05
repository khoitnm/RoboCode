package org.tnmk.common.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.geom2d.Point2D;
import math.geom2d.conic.Circle2D;
import math.geom2d.line.Line2D;

public final class MathConverter {
	private MathConverter() {
	}
	public static Point toPoint(Point2D point2D){
		return new Point(point2D.getX(), point2D.getY());
	}
	public static Point2D toPoint2D(Point point){
		return new Point2D(point.getX(), point.getY());
	}
	public static List<Point> toPoints(Collection<? extends Point2D> point2Ds){
		List<Point> result = new ArrayList<>();
		for (Point2D point2D : point2Ds) {
	        result.add(toPoint(point2D));
        }
		return result;
	}
	public static Circle toCircle(Circle2D c){
		return new Circle(c.center().x(), c.center().y(), c.radius());
	}
	public static Circle2D toCircle2D(Circle c){
		return new Circle2D(c.getCenterX(), c.getCenterY(), c.getRadius());
	}
	public static Line2D toLine2D(LineSegment lineSegment){
		return new Line2D(toPoint2D(lineSegment.getPointA()), toPoint2D(lineSegment.getPointB()));
	}
}
