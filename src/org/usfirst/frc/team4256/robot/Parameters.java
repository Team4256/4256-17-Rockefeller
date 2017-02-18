package org.usfirst.frc.team4256.robot;

public abstract class Parameters {
	//ELECTRONICS
	public static final int Climber = 18;//CAN
	
	public static final int Intake = 15;//CAN
	
	public static final int Shooter_flywheel = 16;//CAN
	public static final int Shooter_rotator = 17;//CAN
	public static final int Shooter_servos = 5;//PWM
	public static final int Shooter_calibrator = 6;//DIO
	
	public static final int Swerve_rotator1 = 11;//CAN, front left
	public static final int Swerve_rotator2 = 12;//CAN, front right
	public static final int Swerve_rotator3 = 13;//CAN, aft left
	public static final int Swerve_rotator4 = 14;//CAN, aft right
	public static final int Swerve_drive1 = 0;//PWM, front left
	public static final int Swerve_drive2 = 1;//PWM, front right
	public static final int Swerve_drive3 = 2;//PWM, aft left
	public static final int Swerve_drive4 = 3;//PWM, aft right
	public static final int Swerve_calibrator1 = 0;//AIO, front left
	public static final int Swerve_calibrator2 = 1;//AIO, front right
	public static final int Swerve_calibrator3 = 2;//AIO, aft left
	public static final int Swerve_calibrator4 = 3;//AIO, aft right
	
	//VALUES
	public static final byte Gyrometer_updateHz = 30;
	
	public static final double leftGear = -60;
	public static final double centerGear = -90;
	public static final double rightGear = -150;
	public static final double loadingStation = 26.7;//TODO will change depending on side of field
	
	public static final double swerveP = .6;
	public static final double swerveI = 0;
	public static final double swerveD = 6;
	
	public static final double spinP = 0.0005;
	public static final double spinI = 0;
	public static final double spinD = 0;
}
