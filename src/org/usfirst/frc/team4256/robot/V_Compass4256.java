package org.usfirst.frc.team4256.robot;

public class V_Compass4256 {
	public float tareAngle = 0;
	public float protectedZoneStart; //Angles increase as the numbers on a clock increase. This value should be the first protected angle encountered by a minute hand which starts at 12:00.
	public float protectedZoneSize; //This value should be the number of degrees the minute hand must travel before reaching the end of the protected section.
	
	public V_Compass4256(final float protectedZoneStart, final float protectedZoneSize) {
		this.protectedZoneStart = protectedZoneStart;
		this.protectedZoneSize = Math.abs(protectedZoneSize)%360;//TODO can this be public final
	}
	/**
	 * This function modifies the input to create a value between 0 and 359.999...
	**/
	public static float validateAngle(final float angle) {
		if (angle < 0) {
			return (360 - (Math.abs(angle)%360) < 360) ? 360 - (Math.abs(angle)%360) : 0;
		}else {
			return (angle%360 < 360) ? angle%360 : 0;
		}
	}
	/**
	 * This function finds the shortest path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	public static float findPath(float startAngle, float endAngle) {
		startAngle = validateAngle(startAngle);
		endAngle = validateAngle(endAngle);
		float pathVector = endAngle - startAngle;
		if (Math.abs(pathVector) > 180) {
			pathVector = Math.abs(pathVector) - 360;
		}if (endAngle - startAngle < -180) {
			pathVector = -pathVector;
		}return pathVector;
	}
	/**
	 * This function returns a valid and legal version of the input.
	**/
	public float legalizeAngle(float angle) {
		angle = validateAngle(angle);
		protectedZoneStart = validateAngle(protectedZoneStart);
		float protectedZoneEnd = validateAngle(protectedZoneStart + protectedZoneSize);
		if (findPath(protectedZoneStart, angle) >= 0.0 && findPath(protectedZoneStart, angle) <= protectedZoneSize) {
			angle = Math.abs(findPath(angle, protectedZoneStart)) <= Math.abs(findPath(angle, protectedZoneEnd)) ? protectedZoneStart : protectedZoneEnd;
		}return angle;
	}
	/**
	 * This function tares the compass at the specified angle. It accepts both -'s and +'s.
	**/
	public void setTareAngle(final float tareAngle) {
		this.tareAngle = validateAngle(this.tareAngle + tareAngle);
		this.protectedZoneStart = validateAngle(this.protectedZoneStart + tareAngle);
	}
	/**
	 * This function returns the path to the border that is nearest to the specified angle.
	**/
	public float findBorderPath(final float startAngle) {
		float borderPath = findPath(startAngle, protectedZoneStart);
		if (Math.abs(borderPath) > Math.abs(findPath(startAngle, protectedZoneStart + protectedZoneSize))) {
			borderPath = findPath(startAngle, protectedZoneStart + protectedZoneSize);
		}return borderPath;
	}
	/**
	 * This function finds the shortest legal path from the start angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	**/
	public float findLegalPath(float startAngle, float endAngle) {
		startAngle = legalizeAngle(startAngle);
		endAngle = legalizeAngle(endAngle);
		float legalPathVector = findPath(startAngle, endAngle);
		float borderPath = findBorderPath(startAngle);
		if ((Math.abs(borderPath) < Math.abs(legalPathVector) && Math.signum(legalPathVector) == Math.signum(borderPath))
		|| (borderPath == 0 && Math.signum(legalPathVector) == Math.signum(findPath(startAngle, protectedZoneStart + protectedZoneSize/2)))) {
			legalPathVector = 360*Math.signum(-legalPathVector) + legalPathVector;
		}return legalPathVector;
	}
}