package org.usfirst.frc.team4256.robot;

public abstract class Parameters {
	//ELECTRONICS
	public static final int Climber = 18;//CAN
	
	public static final int Intake = 15;//CAN
	
	public static final int Shooter_flyWheel = 16;//CAN
	public static final int Shooter_rotator = 17;//CAN
	public static final int Shooter_leftServo = 6;//PWM
	public static final int Shooter_rightServo = 7;//PWM
	public static final int Shooter_calibrator = 6;//DIO
	
	public static final int Swerve_rotator1 = 11;//CAN, front left
	public static final int Swerve_rotator2 = 12;//CAN, front right
	public static final int Swerve_rotator3 = 13;//CAN, aft left
	public static final int Swerve_rotator4 = 14;//CAN, aft right
	public static final int Swerve_drive1 = 0;//PWM, front left
	public static final int Swerve_drive2 = 1;//PWM, front right
	public static final int Swerve_drive3 = 2;//PWM, aft left
	public static final int Swerve_drive4 = 3;//PWM, aft right
	public static final int Swerve_calibrator1 = 0;//DIO, front left
	public static final int Swerve_calibrator2 = 1;//DIO, front right
	public static final int Swerve_calibrator3 = 2;//DIO, aft left
	public static final int Swerve_calibrator4 = 3;//DIO, aft right
	
	//VALUES
	public static final byte Gyrometer_updateHz = 30;
	
	public static final double leftGear = -60;
	public static final double centerGear = -90;
	public static final double rightGear = -150;
	
	public static final double swerveP = .6;
	public static final double swerveI = 0;
	public static final double swerveD = 6;
	
	public static final int spinP = 0;
	public static final int spinI = 0;
	public static final int spinD = 0;
}
