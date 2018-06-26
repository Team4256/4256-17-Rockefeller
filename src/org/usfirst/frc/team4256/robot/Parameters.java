package org.usfirst.frc.team4256.robot;

public final class Parameters {
	private Parameters() {}
	
	public static final int Climber = 18;//CAN, master
	
	public static final int Shooter_flywheel = 16;//CAN
	public static final int Shooter_rotator = 17;//CAN
	public static final int Shooter_linearServos = 5;//PWM
	public static final int Shooter_calibrator = 6;//DIO
	
	public static final int
	rotationAID = 11,//CAN, front left
	rotationBID = 12,//CAN, front right
	rotationCID = 13,//CAN, aft left
	rotationDID = 14,//CAN, aft right
	tractionAID = 21,//CAN, front left
	tractionBID = 23,//CAN, front right
	tractionCID = 25,//CAN, aft left
	tractionDID = 27,//CAN, aft right
	magnetAID = 0,//AIO, front left
	magnetBID = 1,//AIO, front right
	magnetCID = 2,//AIO, aft left
	magnetDID = 3;//AIO, aft right
	
	//PNEUMATICS
	public static final int Shooter_flapModule = 0;//PCM
	public static final int Shooter_flapForward = 2;//PCM
	public static final int Shooter_flapReverse = 3;//PCM
	
	public static final int Gearer_module = 0;//PCM
	public static final int Gearer_forward = 0;//PCM
	public static final int Gearer_reverse = 1;//PCM
	
	//VALUES
	public static final byte Gyrometer_updateHz = 50;
	
	public static final double SPIN_P = 0.05;
	public static final double SPIN_I = 0.000015;
	public static final double SPIN_D = 4.25;
	
	//.1838 works well for a leash length of 3, doubling that works for length of 1.5
	public static final double LEASH_P = 0.1838*2.0;
	public static final double LEASH_I = 0.0;
	public static final double LEASH_D = 1.2;
}
