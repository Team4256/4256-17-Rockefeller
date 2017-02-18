package com.cyborgcats.reusable;

import com.ctre.CANTalon;

public class R_CANTalon extends CANTalon {//TODO still may be a few default params to set. see cantalon user guide
	public static final FeedbackDevice absolute = FeedbackDevice.CtreMagEncoder_Absolute;
	public static final FeedbackDevice relative = FeedbackDevice.CtreMagEncoder_Relative;
	public static final TalonControlMode position = TalonControlMode.Position;
	public static final TalonControlMode speed = TalonControlMode.Speed;
	public static final TalonControlMode current = TalonControlMode.Current;
	public static final TalonControlMode voltage = TalonControlMode.Voltage;
	public static final TalonControlMode percent = TalonControlMode.PercentVbus;
	private double gearRatio;
	
	public R_CANTalon(final int deviceNumber, final FeedbackDevice deviceType, final boolean reverseSensor, final TalonControlMode controlMode, final double gearRatio) {
		super(deviceNumber);
		setFeedbackDevice(deviceType);
		reverseOutput(reverseSensor);//sensor must count positively as motor spins with positive speed
		changeControlMode(controlMode);
		if (isSensorPresent(deviceType) != FeedbackDeviceStatus.FeedbackStatusPresent) {
			throw new IllegalStateException("A CANTalon4256 could not find its integrated versaplanetary encoder.");
		}
		this.gearRatio = gearRatio;
	}
	/**
	 * Set some PID defaults.
	**/
	public void defaults() {
		setProfile(0);//choose between PID loop parameter stores
		setAllowableClosedLoopErr(0);
		configNominalOutputVoltage(+0f, -0f);//minimum voltage draw
		configPeakOutputVoltage(+12f, -12f);//maximum voltage draw
	}
	/**
	 * This function returns the current angle. If wraparound is true, the output will be between 0 and 359.999...
	**/
	public double getCurrentAngle(final boolean wraparound) {//ANGLE
		if (getControlMode() != position) {return -1;}
		return wraparound ? V_Compass.validateAngle(getPosition()*360/4.2) : getPosition()*360/4.2;
	}
	/**
	 * This function updates the PID loop's target position such that the motor will rotate to the specified angle in the best way possible.
	**/
	public void setAngle(final double endAngle, final V_Compass compass) {//ANGLE
		if (getControlMode() == position) {
			set((getCurrentAngle(false) + compass.findNewPath(getCurrentAngle(true), endAngle))*gearRatio/360);
		}
	}
	/**
	 * This function updates the PID loop's target speed.
	**/
	public void setRPM(final double rpm) {//SPEED
		if (getControlMode() == speed) {
			set(rpm);
		}
	}
	/**
	 * This function updates the PID loop's target current.
	**/
	public void setAmps(final double amps) {//CURRENT
		if (getControlMode() == current) {
			set(amps);
		}
	}
	/**
	 * This function scales the input to a voltage between 0 and 12, and then uses voltage compensation mode to maintain it.
	 * setVoltageCompensationRate(voltsPerSecondOverTen) must be run before calling this function.
	**/
	public void setVC(double speed) {//VOLTAGE
		if (getControlMode() == voltage) {
			if (Math.abs(speed) > 1) {speed = Math.signum(speed);}
			speed *= 12;
			set(speed);
		}
	}
	/**
	 * This function returns the PID error for the current control mode.
	 * Position: Degrees
	 * Speed: RPM
	 * Current: Amperes
	**/
	public double getCurrentError() {//ANGLE, SPEED, CURRENT
		TalonControlMode mode = getControlMode();
		if (mode == position) {
			return getError()*360/(4096*gearRatio);
		}else if (mode == speed) {
			return getError()*600/(4096*gearRatio);
		}else if (mode == current){
			return getError();
		}else {
			return -1;
		}
	}
}