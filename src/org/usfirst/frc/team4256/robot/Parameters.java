package org.usfirst.frc.team4256.robot;

public abstract class Parameters {
	public static final byte Gyrometer_updateHz = 30;
	
	public static final int Swerve_module1rotator = 0;//CAN
	public static final int Swerve_module2rotator = 1;//CAN
	public static final int Swerve_module3rotator = 2;//CAN
	public static final int Swerve_module4rotator = 3;//CAN
	
	public static final int Swerve_module1drive1 = 4;//PDP
	public static final int Swerve_module1drive2 = 5;//PDP
	
	public static final int Swerve_module2drive1 = 6;//PDP
	public static final int Swerve_module2drive2 = 7;//PDP
	
	public static final int Swerve_module3drive1 = 8;//PDP
	public static final int Swerve_module3drive2 = 9;//PDP
	
	public static final int Swerve_module4drive1 = 10;//PDP
	public static final int Swerve_module4drive2 = 11;//PDP
}
