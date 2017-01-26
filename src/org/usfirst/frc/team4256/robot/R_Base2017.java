package org.usfirst.frc.team4256.robot;

public class R_Base2017 {
	final private V_Compass4256 compass = new V_Compass4256(0, 0);
	private R_Gyrometer4256 gyro;
	private R_SwerveModule2017 module1;
	private R_SwerveModule2017 module2;
	private R_SwerveModule2017 module3;
	private R_SwerveModule2017 module4;
	
	public R_Base2017(final R_Gyrometer4256 gyro, final R_SwerveModule2017 module1, final R_SwerveModule2017 module2, final R_SwerveModule2017 module3, final R_SwerveModule2017 module4) {
		this.gyro = gyro;
		this.module1 = module1;
		this.module2 = module2;
		this.module3 = module3;
		this.module4 = module4;
	}
	
	public void swerveDrive(final double direction, final double speed, final double spin) {
		module1.rotateTo(R_SwerveModule2017.findWheelToField(direction, gyro.getCurrentAngle()));
		module2.rotateTo(R_SwerveModule2017.findWheelToField(direction, gyro.getCurrentAngle()));
		module3.rotateTo(R_SwerveModule2017.findWheelToField(direction, gyro.getCurrentAngle()));
		module4.rotateTo(R_SwerveModule2017.findWheelToField(direction, gyro.getCurrentAngle()));
		scalingFactor1 = proportional to the distance along the rim of the elipse formed by the rotating drive train
				or proportional to the angle relative to direction of travel
		speed1 = speed*scalingFactor1;
	}
}