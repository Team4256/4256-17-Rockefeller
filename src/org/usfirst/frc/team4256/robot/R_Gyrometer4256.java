package org.usfirst.frc.team4256.robot;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SerialPort;

public class R_Gyrometer4256 extends AHRS {
	private V_Compass4256 compass;//TODO can this be final
	
	public R_Gyrometer4256(final float protectedZoneStart, final float protectedZoneSize) {
		super(SerialPort.Port.kMXP, SerialDataType.kProcessedData, (byte)46);
		reset();
		compass = new V_Compass4256(protectedZoneStart, protectedZoneSize);
	}
	private double lastLegalDirection = 1.0;
	private double lastMeasuredAngle = 0.0;
	private double lastMeasuredRate = 0.0;
	private long lastMeasuredRateTime = System.currentTimeMillis();
	/**
	 * This function returns the current angle based on the tare angle.
	**/
	public float getCurrentAngle() {
		if (isCalibrating()) {
			return (float)lastMeasuredAngle;
		}lastMeasuredAngle = getAngle();
		float currentAngle;
		if (0 <= V_Compass4256.validateAngle(getFusedHeading()) && V_Compass4256.validateAngle(getFusedHeading()) <= compass.tareAngle) {
			currentAngle = 360 - compass.tareAngle + V_Compass4256.validateAngle(getFusedHeading());
		}else {
			currentAngle = V_Compass4256.validateAngle(getFusedHeading()) - compass.tareAngle;
		}return V_Compass4256.validateAngle(currentAngle);
	}
	/**
	 * This function finds the shortest legal path from the current angle to the end angle and returns the size of that path in degrees.
	 * Positive means clockwise and negative means counter-clockwise.
	 * If the current angle is inside the protected zone, the path goes through the previously breached border.
	**/
	public float getCurrentPath(float endAngle) {
		endAngle = compass.legalizeAngle(endAngle);
		final float currentAngle = getCurrentAngle();
		float currentPathVector = V_Compass4256.findPath(currentAngle, endAngle);
		boolean legal = compass.legalizeAngle(currentAngle) == currentAngle;
		if (legal) {
			currentPathVector = compass.findLegalPath(currentAngle, endAngle);
			lastLegalDirection = Math.signum(currentPathVector);
		}else if (!legal && Math.signum(currentPathVector) != -lastLegalDirection) {
			currentPathVector = 360*Math.signum(-currentPathVector) + currentPathVector;
		}return currentPathVector;
	}
	/**
	 * This function returns true if the distance between the current angle and the last measured angle is greater than the tolerance.
	 * Otherwise, it returns false.
	**/
	public boolean isRotating(final double tolerance) {//TODO could re-implement using getRate()
		if (Math.abs(lastMeasuredAngle - getAngle()) >= tolerance && !isCalibrating()) {
			lastMeasuredAngle = getAngle();
			return true;
		}else if (!isCalibrating()) {
			lastMeasuredAngle = getAngle();
		}
		return false;
	}
	
	public double getAcceleration() {//TODO may have to change for AHRS
		double a = (getRate()*1000.0 - lastMeasuredRate)/(System.currentTimeMillis() - lastMeasuredRateTime);
		lastMeasuredRate = getRate()*1000.0;
		lastMeasuredRateTime = System.currentTimeMillis();
		return a;
	}
}