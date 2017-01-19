package org.usfirst.frc.team4256.robot;

public class R_Swerve2017 {//just copied over some interesting code from last year
	
	private static float rotateAngle = 0;
	private static double rotateValue = 0.0;
	private static double rotateIncrement = 0.0;
	private static boolean previousStateR = false;
	
	/**
	 * This should take the place of arcadeDrive. Therefore, the move and rotate values should match what would otherwise be used for that.
	 * When enable becomes true, it will set the locked heading to the current heading.
	 * While enable remains true, it will adjust rotation speed to maintain the locked heading.
	 * Do not run this in the same loop under the same conditions as other functions that affect rotation speed.
	**/
	public static void headingCorrection(final double moveValue, final double rotateValue, final boolean enable) {
		if (enable) {
			OPERATE2016.base.arcadeDrive(Math.pow(moveValue, 2.0), rotate(enable, OPERATE2016.gyro.getCurrentAngle(), 0.5, 2.0), false);//TODO adjust these values experimentally
		}else {
			OPERATE2016.base.arcadeDrive(moveValue, rotateValue, true);
		}
	}
	/**
	 * This returns the rotation value necessary to turn to the goalAngle based on acceleration calculations and data from the gyrometer.
	 * theoreticalTimeS is the time in seconds which is used in calculations, and tolerance is the accuracy of the turn in degrees.
	 * Do not run this in the same loop under the same conditions as other functions that affect rotation speed.
	 * Make sure to use the unsquared version of arcadeDrive.
	**/
	public static double rotate(final boolean enable, final float goalAngle, final double theoreticalTimeS, final double tolerance) {
		if (enable) {
			if (!previousStateR) {
				rotateAngle = goalAngle;
				rotateValue = 0.0;//TODO could be Math.signum(a or path)*minimumRotationSpeed
				rotateIncrement = 0.0;
			}
			if (Math.abs((double)OPERATE2016.gyro.getCurrentPath(rotateAngle)) > Math.abs(tolerance)) {
				double path = (double)OPERATE2016.gyro.getCurrentPath(rotateAngle);
				double a = 4.0*path/Math.pow(theoreticalTimeS, 2.0);//TODO do calculations that take into account my current speed rather than Vi of 0
				if (Math.abs((double)OPERATE2016.gyro.getCurrentPath(rotateAngle)) <= Math.abs(path)/2.0) {
					a = -a;
					rotateIncrement = -rotateIncrement;
				}
				if (Math.abs(OPERATE2016.gyro.getAcceleration()) - Math.abs(a) < -5.0) {
					rotateIncrement += Math.signum(a)*0.05;//TODO adjust this value experimentally
				}else if (Math.abs(OPERATE2016.gyro.getAcceleration()) - Math.abs(a) > 5.0) {
					rotateIncrement += Math.signum(-a)*0.05;
				}rotateValue += rotateIncrement;
			}
		}
		previousStateR = enable;
		return rotateValue;
	}
	
	
	
	
}
