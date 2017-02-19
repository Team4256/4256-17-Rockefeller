package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_Gyro;

public class R_DriveTrain {
	private static final double Side = 21.85;//inches, wheel tip to wheel tip
	private static final double Front = 25.85;
	private static final double Radius = Math.sqrt(Side*Side + Front*Front);
	private double lastSpeed = 0;
	private R_Gyro gyro;
	private R_SwerveModule module1;//arranged clockwise
	private R_SwerveModule module2;
	private R_SwerveModule module3;
	private R_SwerveModule module4;
	
	public R_DriveTrain(final R_Gyro gyro, final R_SwerveModule module1, final R_SwerveModule module2, final R_SwerveModule module3, final R_SwerveModule module4) {
		this.gyro = gyro;
		this.module1 = module1;
		this.module2 = module2;
		this.module3 = module3;
		this.module4 = module4;
	}
	/**
	 * Set some PID defaults.
	**/
	public void init() {
		module1.init();
		module2.init();
		module3.init();
		module4.init();
	}
	
	public void holonomic(final double direction, double speed, double spin) {
		double chassis_fieldAngle = gyro.getCurrentAngle();
		double forward = Math.cos(Math.toRadians(R_SwerveModule.convertToRobot(direction, chassis_fieldAngle)));
		double strafe = - Math.sin(Math.toRadians(R_SwerveModule.convertToRobot(direction, chassis_fieldAngle)));
		boolean bad = speed == 0 && spin == 0;
		if (bad) {
			forward *= lastSpeed;strafe *= lastSpeed;
		}else {
			forward *= speed;strafe *= speed;
			lastSpeed = speed;
		}
		double a = strafe - spin*(Side/Radius),b = strafe + spin*(Side/Radius),c = forward - spin*(Front/Radius),d = forward + spin*(Front/Radius);
		module1.swivelTo(Math.toDegrees(Math.atan2(b,d)));
		module2.swivelTo(Math.toDegrees(Math.atan2(b,c)));
		module3.swivelTo(Math.toDegrees(Math.atan2(a,d)));
		module4.swivelTo(Math.toDegrees(Math.atan2(a,c)));
		
		if (isThere(5)) {
			double speed1 = Math.sqrt(b*b + d*d),speed2 = Math.sqrt(b*b + c*c),speed3 = Math.sqrt(a*a + d*d),speed4 = Math.sqrt(a*a + c*c);
			if (bad) {
				module1.set(0);module2.set(0);module3.set(0);module4.set(0);
			}else {
				double max = Math.max(speed1, Math.max(speed2, Math.max(speed3, speed4)));
				if (max > 1) {
					speed1 /= max;	speed2 /= max;	speed3 /= max;	speed4 /= max;
				}
				module1.set(speed1);module2.set(0);module3.set(0);module4.set(0);
			}
		}
	}
	
	public boolean isThere(final double threshold) {//TODO if the change in my pid error has leveled out, then do..., or if speed has gone below threshold (change here and in SwerveModule)
		return module1.isThere(threshold) && module2.isThere(threshold) && module3.isThere(threshold) && module4.isThere(threshold);
	}
}