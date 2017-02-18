package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_Gyro;

public class R_DriveTrain {
	private static final double Side = 21.85;//inches, wheel tip to wheel tip
	private static final double Front = 25.85;
	private static final double Radius = Math.sqrt(Side*Side + Front*Front);
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
		if (Math.abs(speed) > 1) {speed = Math.signum(speed);}//TODO is this necessary with the normalizer below?
		if (Math.abs(spin) > 1) {spin = Math.signum(spin);}
		double chassis_fieldAngle = gyro.getCurrentAngle();
		double forward = Math.sin(Math.toRadians(R_SwerveModule.convertToRobot(direction, chassis_fieldAngle)))*speed;
		double strafe = Math.cos(Math.toRadians(R_SwerveModule.convertToRobot(direction, chassis_fieldAngle)))*speed;
		double a = strafe - spin*(Side/Radius),b = strafe + spin*(Side/Radius),c = forward - spin*(Front/Radius),d = forward + spin*(Front/Radius);
		double angle1 = Math.toDegrees(Math.atan2(b,d)),
				angle2 = Math.toDegrees(Math.atan2(b,c)),
				angle3 = Math.toDegrees(Math.atan2(a,d)),
				angle4 = Math.toDegrees(Math.atan2(a,c));
		module1.swivelTo(angle1);
		module2.swivelTo(angle2);
		module3.swivelTo(angle3);
		module4.swivelTo(angle4);
		
		if (isThere(5)) {
			double speed1 = Math.sqrt(b*b + d*d),
					speed2 = Math.sqrt(b*b + c*c),
					speed3 = Math.sqrt(a*a + d*d),
					speed4 = Math.sqrt(a*a + c*c);
			double max = speed1;
			if (speed2 > max) {
				max = speed2;
			}if (speed3 > max) {
				max = speed3;
			}if (speed4 > max) {
				max = speed4;
			}
			if (max > 1) {
				speed1 /= max;	speed2 /= max;	speed3 /= max;	speed4 /= max;
			}
			module1.set(speed1);
			module2.set(speed2);
			module3.set(speed3);
			module4.set(speed4);
		}
	}
	
	public boolean isThere(final double threshold) {//TODO if the change in my pid error has leveled out, then do..., or if speed has gone below threshold (change here and in SwerveModule)
		return module1.isThere(threshold) && module2.isThere(threshold) && module3.isThere(threshold) && module4.isThere(threshold);
	}
}