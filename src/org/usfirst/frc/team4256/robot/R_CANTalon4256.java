package org.usfirst.frc.team4256.robot;

import com.ctre.CANTalon;

public class R_CANTalon4256 extends CANTalon {
	//CONSTRUCTION VALUES USED FOR PREPARATION OF PID MAY BE CHANGED LATER
	public R_CANTalon4256(final int deviceNumber, final boolean reverseSensor) {//can also have update rate
		super(deviceNumber);
		setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Absolute);
		reverseSensor(reverseSensor);//sensor must count positively as motor spins with positive speed
		configNominalOutputVoltage(+0f, -0f);//minimum voltage draw
		configPeakOutputVoltage(+12f, -12f);//maximum voltage draw
		setAllowableClosedLoopErr(0);
		setProfile(0);//choose between PID loop parameter stores
		if (isSensorPresent(FeedbackDevice.CtreMagEncoder_Absolute) != FeedbackDeviceStatus.FeedbackStatusPresent) {
			throw new IllegalStateException("CANTalon4256 could not find an integrated versaplanetary encoder.");
		}
	}
	
	public double decapitateAngle(final double endAngle) {
		return V_Compass4256.findPath((float)getPulseWidthPosition(), (float)endAngle) > 90 ? (double)V_Compass4256.validateAngle((float)(endAngle + 180)) : endAngle;
	}
}