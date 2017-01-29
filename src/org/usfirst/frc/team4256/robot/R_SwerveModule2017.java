package org.usfirst.frc.team4256.robot;

import edu.wpi.first.wpilibj.Talon;

public class R_SwerveModule2017 {//style: any custom real class can be passed into another during construction
	private R_CANTalon4256 rotator;
	private Talon drive1;
	private Talon drive2;
	
	public R_SwerveModule2017(final R_CANTalon4256 rotator, final int drive1Port, final int drive2Port) {
		this.rotator = rotator;
		drive1 = new Talon(drive1Port);
		drive2 = new Talon(drive2Port);
	}
	public static double findWheelToField(final double wheelAngle, final double robotAngle) {
		return (double)V_Compass4256.validateAngle((float)(wheelAngle - robotAngle));
	}
	
	public void rotateTo(final double wheelAngle) {
		
	}
	
	public void set(final double speed) {
		drive1.set(speed);
		drive2.set(speed);
	}
	
	public double decapitateAngle(final double endAngle) {
		return V_Compass4256.findPath((float)getPulseWidthPosition(), (float)endAngle) > 90 ? (double)V_Compass4256.validateAngle((float)(endAngle + 180)) : endAngle;
	}
	
}