package com.cyborgcats.reusable;//COMPLETE

public class V_Compass {
	private double tareAngle = 0;
	private double lastLegalDirection = 1;
	private double protectedZoneStart; //Angles increase as the numbers on a clock increase. This value should be the first protected angle encountered by a minute hand which starts at 12:00.
	private double protectedZoneSize; //This value should be the number of degrees the minute hand must travel before reaching the end of the protected section.
	
	public V_Compass(final double protectedZoneStart, final double protectedZoneSize) {
		this.protectedZoneStart = protectedZoneStart;
		this.protectedZoneSize = Math.abs(protectedZoneSize)%360;
	}
	/**
	 * This function tares the compass at the specified angle, relative to the current 0. It accepts both -'s and +'s.
	**/
	public void setTareAngle(final double tareAngle, final boolean relativeReference) {
		this.tareAngle = relativeReference ? this.tareAngle + tareAngle : tareAngle;
		protectedZoneStart = validateAngle(protectedZoneStart - this.tareAngle);//TODO I don't think this still works with tares outside of 0 to 360
	}
	/**
	 * This function returns the current tare angle, relative to the initialized 0.
	**/
	public double getTareAngle() {
		return tareAngle;
	}
	/**
	 * This function returns the last legal direction.
	**/
	public double getLastLegalDirection() {
		return lastLegalDirection;
	}
	/**
	 * This function returns the starting angle of the protected zone.
	**/
	public double getProtectedZoneStart() {
		return protectedZoneStart;
	}
	/**
	 * This function returns the size of the protected zone in degrees.
	**/
	public double getProtectedZoneSize() {
		return protectedZoneSize;
	}
	/**
	 * This function modifies the input to create a value between 0 and 359.999...
	**/
	public static double validateAngle(final double angle) {
		if (angle < 0) {
			return (360 - (Math.abs(angle)%360) < 360) ? 360 - (Math.abs(angle)%360) : 0;
		}else {
			return (angle%360 < 360) ? angle%360 : 0;
		}
	}
	/**
	 * This function returns a valid and legal version of the input.
	**/
	public double legalizeAngle(double angle) {
		angle = validateAngle(angle);
		protectedZoneStart = validateAngle(protectedZoneStart);
		double protectedZoneEnd = validateAngle(protectedZoneStart + protectedZoneSize);
		if (findPath(protectedZoneStart, angle) >= 0 && findPath(protectedZoneStart, angle) <= protectedZoneSize) {
			angle = Math.abs(findPath(protectedZoneStart, angle)) <= Math.abs(findPath(angle, protectedZoneEnd)) ? protectedZoneStart : protectedZoneEnd;
		}return angle;
	}
	/**
	 * This function finds the shortest path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	public static double findPath(double startAngle, double endAngle) {
		startAngle = validateAngle(startAngle);
		endAngle = validateAngle(endAngle);
		double pathVector = endAngle - startAngle;
		if (Math.abs(pathVector) > 180) {
			pathVector = Math.abs(pathVector) - 360;
		}if (endAngle - startAngle < -180) {
			pathVector = -pathVector;
		}return pathVector;
	}
	/**
	 * This function returns the path to the border that is nearest to the specified angle.
	**/
	public double findBorderPath(final double startAngle) {
		double borderPath = findPath(startAngle, protectedZoneStart);
		if (Math.abs(borderPath) > Math.abs(findPath(startAngle, protectedZoneStart + protectedZoneSize))) {
			borderPath = findPath(startAngle, protectedZoneStart + protectedZoneSize);
		}return borderPath;
	}
	/**
	 * This function finds the shortest legal path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	public double findLegalPath(double startAngle, double endAngle) {
		startAngle = legalizeAngle(startAngle);
		endAngle = legalizeAngle(endAngle);
		double legalPathVector = findPath(startAngle, endAngle);
		if (protectedZoneSize != 0) {
			final double borderPath = findBorderPath(startAngle);
			if ((Math.abs(borderPath) < Math.abs(legalPathVector) && Math.signum(legalPathVector) == Math.signum(borderPath))
			|| (borderPath == 0 && Math.signum(legalPathVector) == Math.signum(findPath(startAngle, protectedZoneStart + protectedZoneSize/2)))) {
				legalPathVector = 360*Math.signum(-legalPathVector) + legalPathVector;
			}
		}return legalPathVector;
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public double findNewPath(final double startAngle, double endAngle) {
		endAngle = legalizeAngle(endAngle);
		double currentPathVector = V_Compass.findPath(startAngle, endAngle);
		boolean legal = legalizeAngle(startAngle) == startAngle;
		if (legal) {
			currentPathVector = findLegalPath(startAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
	/**
	 * This function finds the angle between the Y axis and any Cartesian coordinate.
	**/
	public static double convertToAngle(final double x, final double y) {
		return x == 0 && y == 0 ? 0 : validateAngle(Math.toDegrees(-1*(Math.atan2(-y, x) - Math.PI/2)));
	}
}