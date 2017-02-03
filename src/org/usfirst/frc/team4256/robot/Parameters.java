package org.usfirst.frc.team4256.robot;

public abstract class Parameters {
	public static final byte Gyrometer_updateHz = 30;
	
	public static final int Swerve_module1rotator = 1;//CAN
	public static final int Swerve_module2rotator = 2;//CAN
	public static final int Swerve_module3rotator = 3;//CAN
	public static final int Swerve_module4rotator = 4;//CAN
	
	public static final int Swerve_module1drive1 = 0;//PWM
	public static final int Swerve_module1drive2 = 1;//PWM
	
	public static final int Swerve_module2drive1 = 2;//PWM
	public static final int Swerve_module2drive2 = 3;//PWM
	
	public static final int Swerve_module3drive1 = 4;//PWM
	public static final int Swerve_module3drive2 = 5;//PWM
	
	public static final int Swerve_module4drive1 = 6;//PWM
	public static final int Swerve_module4drive2 = 7;//PWM
}
