package org.usfirst.frc.team4256.robot;

public abstract class Parameters {
	//ELECTRONICS
	public static final int Climber = 18;//CAN
	
	public static final int Intake = 15;//CAN
	
	public static final int Shooter_flywheel = 16;//CAN
	public static final int Shooter_rotator = 17;//CAN
	public static final int Shooter_linearServos = 5;//PWM
	public static final int Shooter_calibrator = 6;//DIO
	
	public static final int Swerve_rotatorA = 11;//CAN, front left
	public static final int Swerve_rotatorB = 12;//CAN, front right
	public static final int Swerve_rotatorC = 13;//CAN, aft left
	public static final int Swerve_rotatorD = 14;//CAN, aft right
	public static final int Swerve_driveAA = 21;//CAN, front left
	public static final int Swerve_driveAB = 22;//CAN, front left
	public static final int Swerve_driveBA = 23;//CAN, front right
	public static final int Swerve_driveBB = 24;//CAN, front right
	public static final int Swerve_driveCA = 25;//CAN, aft left
	public static final int Swerve_driveCB = 26;//CAN, aft left
	public static final int Swerve_driveDA = 27;//CAN, aft right
	public static final int Swerve_driveDB = 28;//CAN, aft right
	public static final int Swerve_calibratorA = 0;//AIO, front left
	public static final int Swerve_calibratorB = 1;//AIO, front right
	public static final int Swerve_calibratorC = 2;//AIO, aft left
	public static final int Swerve_calibratorD = 3;//AIO, aft right
	
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
