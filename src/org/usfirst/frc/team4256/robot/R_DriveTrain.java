package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_Gyro;

public class R_DriveTrain {
	private static final double Side = 21.85;//inches, wheel tip to wheel tip
	private static final double Front = 25.85;
	private static final double Radius = Math.sqrt(Side*Side + Front*Front);
	private double lastSpeed = 0;
	private R_Gyro gyro;
	private R_SwerveModule moduleA;//arranged clockwise//TODO
	private R_SwerveModule moduleB;
	private R_SwerveModule moduleC;
	private R_SwerveModule moduleD;
	
	public R_DriveTrain(final R_Gyro gyro, final boolean flippedA, final boolean flippedB, final boolean flippedC, final boolean flippedD) {
		this.gyro = gyro;
		this.moduleA = new R_SwerveModule(Parameters.Swerve_rotatorA, flippedA, Parameters.Swerve_driveAA, Parameters.Swerve_driveAB, Parameters.Swerve_calibratorA);
		this.moduleB = new R_SwerveModule(Parameters.Swerve_rotatorB, flippedB, Parameters.Swerve_driveBA, Parameters.Swerve_driveBB, Parameters.Swerve_calibratorB);
		this.moduleC = new R_SwerveModule(Parameters.Swerve_rotatorC, flippedC, Parameters.Swerve_driveCA, Parameters.Swerve_driveCB, Parameters.Swerve_calibratorC);
		this.moduleD = new R_SwerveModule(Parameters.Swerve_rotatorD, flippedD, Parameters.Swerve_driveDA, Parameters.Swerve_driveDB, Parameters.Swerve_calibratorD);
	}
	/**
	 * Set some PID defaults.
	**/
	public void init() {
		moduleA.init();
		moduleB.init();
		moduleC.init();
		moduleD.init();
	}
	/**
	 * 
	**/
	public void align(final double increment) {
		while ((!moduleA.isAligned() || !moduleB.isAligned() || !moduleC.isAligned() || !moduleD.isAligned())) {
			moduleA.align(increment);
			moduleB.align(increment);
			moduleC.align(increment);
			moduleD.align(increment);
			
//			if (!moduleA.isAligned()) {
//				moduleA.align(increment);
//			}
//			if (!moduleB.isAligned()) {
//				moduleB.align(increment);
//			}
//			if (!moduleC.isAligned()) {
//				moduleC.align(increment);
//			}
//			if (!moduleD.isAligned()) {
//				moduleD.align(increment);
//			}
		}
	}
	
	public void holonomic(final double direction, double speed, double spin) {
		double chassis_fieldAngle = gyro.getCurrentAngle();
		double forward = Math.cos(Math.toRadians(R_SwerveModule.convertToRobot(direction, chassis_fieldAngle)));
		double strafe = Math.sin(Math.toRadians(R_SwerveModule.convertToRobot(direction, chassis_fieldAngle)));
		boolean bad = speed == 0 && spin == 0;
		if (bad) {
			forward *= lastSpeed;strafe *= lastSpeed;
		}else {
			forward *= speed;strafe *= speed;
			lastSpeed = speed;
		}
		double a = strafe - spin*(Side/Radius),b = strafe + spin*(Side/Radius),c = forward - spin*(Front/Radius),d = forward + spin*(Front/Radius);
		moduleA.swivelTo(Math.toDegrees(Math.atan2(b,d)));
		moduleB.swivelTo(Math.toDegrees(Math.atan2(b,c)));
		moduleC.swivelTo(Math.toDegrees(Math.atan2(a,d)));
		moduleD.swivelTo(Math.toDegrees(Math.atan2(a,c)));
		
		if (isThere(5)) {
			double speedA = Math.sqrt(b*b + d*d),speedB = Math.sqrt(b*b + c*c),speedC = Math.sqrt(a*a + d*d),speedD = Math.sqrt(a*a + c*c);
			if (bad) {
				moduleA.set(0);	moduleB.set(0);	moduleC.set(0);	moduleD.set(0);
			}else {
				double max = Math.max(speedA, Math.max(speedB, Math.max(speedC, speedD)));
				if (max > 1) {
					speedA /= max;	speedB /= max;	speedC /= max;	speedD /= max;
				}
				moduleA.set(speedA);	moduleB.set(speedB);	moduleC.set(speedC);	moduleD.set(speedD);
			}
		}
	}
	
	public boolean isThere(final double threshold) {//TODO if the change in my pid error has leveled out, then do..., or if speed has gone below threshold (change here and in SwerveModule)
		return moduleA.isThere(threshold) && moduleB.isThere(threshold) && moduleC.isThere(threshold) && moduleD.isThere(threshold);
	}
}