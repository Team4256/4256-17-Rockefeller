package org.usfirst.frc.team4256.robot;

public abstract class V_Instructions {//things that are used regardless of the driver, yet are not specific to a physical object, just copied over some interesting code from last year
	
	public static void shotAlignment(final double theoreticalTimeS, final double tolerance, final boolean enable) {
		//final float goalAngle = OPERATE2016.gyro.getCurrentAngle() + (float)OPERATE2016.visionTable.getNumber("AngleDifferential", 0.0);
		//OPERATE2016.base.arcadeDrive(0, FILTER4256.rotate(enable, goalAngle, theoreticalTimeS, tolerance), false);
	}
}
//TODO current based limit switches (ask for currentAmps and maxAmps, or maybe average amps, and return boolean for whether currentAmps are normal -- could also do changeIN thing)
//TODO documentation
//TODO change encoder sampling rate based on motor speed
//TODO emergency shutdown based on temperature