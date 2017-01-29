package com.cyborgcats.reusable;
//package org.usfirst.frc.team4256.robot;//COMPLETE

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

public class R_Gyrometer extends AHRS {
	private static final float floatiness = 1;//the tolerance when checking for equivalence of floats
	private float lastMeasuredAngle = 0;
	private double lastLegalDirection = 1.0;
	public V_Compass compass;
	
	public R_Gyrometer(final byte updateHz, final float protectedZoneStart, final float protectedZoneSize) {
		super(SerialPort.Port.kMXP, SerialDataType.kProcessedData, updateHz);
		reset();
		compass = new V_Compass(protectedZoneStart, protectedZoneSize);
	}
	/**
	 * This function returns the current angle based on the tare angle.
	**/
	public float getCurrentAngle() {
		if (isCalibrating()) {
			return lastMeasuredAngle;
		}float currentAngle;
		if (0 <= V_Compass.validateAngle(getFusedHeading()) && V_Compass.validateAngle(getFusedHeading()) <= compass.getTareAngle()) {
			currentAngle = 360 - compass.getTareAngle() + V_Compass.validateAngle(getFusedHeading());//follows order of operations
		}else {
			currentAngle = V_Compass.validateAngle(getFusedHeading()) - compass.getTareAngle();
		}lastMeasuredAngle = V_Compass.validateAngle(currentAngle);
		return lastMeasuredAngle;
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public float findNewPath(float endAngle) {
		endAngle = compass.legalizeAngle(endAngle);
		final float currentAngle = getCurrentAngle();
		float currentPathVector = V_Compass.findPath(currentAngle, endAngle);
		boolean legal = Math.abs(compass.legalizeAngle(currentAngle) - currentAngle) <= floatiness;
		if (legal) {
			currentPathVector = compass.findLegalPath(currentAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
}