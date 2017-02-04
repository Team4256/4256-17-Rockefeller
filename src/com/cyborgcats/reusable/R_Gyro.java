package com.cyborgcats.reusable;//COMPLETE

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

public class R_Gyro extends AHRS {
	private double lastMeasuredAngle = 0;
	private double lastLegalDirection = 1;
	public V_Compass compass;
	
	public R_Gyro(final byte updateHz, final double protectedZoneStart, final double protectedZoneSize) {
		super(SerialPort.Port.kMXP, SerialDataType.kProcessedData, updateHz);
		reset();
		compass = new V_Compass(protectedZoneStart, protectedZoneSize);
	}
	/**
	 * This function returns the current angle based on the tare angle.
	 * It will ignore changes to the tare angle until hardware calibration is complete.
	**/
	public double getCurrentAngle() {
		double currentAngle = V_Compass.validateAngle((double)getFusedHeading());
		if (!isCalibrating()) {
			if (0 <= currentAngle && currentAngle <= compass.getTareAngle()) {
				currentAngle += 360 - compass.getTareAngle();//follows order of operations
			}else {
				currentAngle -= compass.getTareAngle();
			}lastMeasuredAngle = V_Compass.validateAngle(currentAngle);
		}return lastMeasuredAngle;
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public double findNewPath(double endAngle) {
		endAngle = compass.legalizeAngle(endAngle);
		final double currentAngle = getCurrentAngle();
		double currentPathVector = V_Compass.findPath(currentAngle, endAngle);
		boolean legal = compass.legalizeAngle(currentAngle) == currentAngle;
		if (legal) {
			currentPathVector = compass.findLegalPath(currentAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
}