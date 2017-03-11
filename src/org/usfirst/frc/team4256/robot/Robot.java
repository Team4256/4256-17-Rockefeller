package org.usfirst.frc.team4256.robot;

import java.util.HashMap;
import java.util.Map;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gimbal;
import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.R_Xbox;
import com.cyborgcats.reusable.V_Fridge;
import com.cyborgcats.reusable.V_PID;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();
	//Human Input
	private static final R_Xbox driver = new R_Xbox(0);
	private static final R_Xbox gunner = new R_Xbox(1);
	private static final Map<Integer, Double> buttons2angle = new HashMap<Integer, Double>();
	private static final int[] mappedButtons = new int[] {R_Xbox.BUTTON_X, R_Xbox.BUTTON_A, R_Xbox.BUTTON_B, R_Xbox.BUTTON_Y};
	private static double lockedAngle = 0;
	private static boolean override = false;
	//Robot Input
	private static final R_Gimbal gimbal = new R_Gimbal(Parameters.Camera_servoX, Parameters.Camera_servoY, 6);
	private static final R_Gyro gyro = new R_Gyro(Parameters.Gyrometer_updateHz, 0, 0);
	//Robot Output
	private static final Compressor compressor = new Compressor(0);
	
	private static final R_SwerveModule moduleA = new R_SwerveModule(Parameters.Swerve_rotatorA, true, Parameters.Swerve_driveAA, Parameters.Swerve_driveAB, Parameters.Swerve_calibratorA);
	private static final R_SwerveModule moduleB = new R_SwerveModule(Parameters.Swerve_rotatorB, true, Parameters.Swerve_driveBA, Parameters.Swerve_driveBB, Parameters.Swerve_calibratorB);
	private static final R_SwerveModule moduleC = new R_SwerveModule(Parameters.Swerve_rotatorC, true, Parameters.Swerve_driveCA, Parameters.Swerve_driveCB, Parameters.Swerve_calibratorC);
	private static final R_SwerveModule moduleD = new R_SwerveModule(Parameters.Swerve_rotatorD, true, Parameters.Swerve_driveDA, Parameters.Swerve_driveDB, Parameters.Swerve_calibratorD);
	private static final R_DriveTrain swerve = new R_DriveTrain(gyro, moduleA, moduleB, moduleC, moduleD);
	
	private static final R_CANTalon climber = new R_CANTalon(Parameters.Climber, 17, R_CANTalon.voltage);
	
	private static final DoubleSolenoid gearer = new DoubleSolenoid(Parameters.Gearer_module, Parameters.Gearer_forward, Parameters.Gearer_reverse);
	
	private static final R_CANTalon intake = new R_CANTalon(Parameters.Intake, 1, R_CANTalon.percent);
	
//	private static final R_CANTalon flywheel = new R_CANTalon(Parameters.Shooter_flywheel, 1, R_CANTalon.speed, true, R_CANTalon.relative);
//	private static final R_CANTalon turret = new R_CANTalon(Parameters.Shooter_rotator, 12, R_CANTalon.position, false, R_CANTalon.absolute, 135, 90);
//	private static final Servo linearServo = new Servo(Parameters.Shooter_linearServo);
//	private static final DoubleSolenoid flap = new DoubleSolenoid(Parameters.Shooter_flapModule, Parameters.Shooter_flapForward, Parameters.Shooter_flapReverse);
	@Override
	public void robotInit() {//TODO align swerve here?
		SmartDashboard.putNumber("sideColor", 0);
		compressor.clearAllPCMStickyFaults();
		swerve.init();
		V_PID.set("spin", Parameters.spinP, Parameters.spinI, Parameters.spinD);
		climber.init();
		climber.setVoltageCompensationRampRate(24);
		intake.init();
//		flywheel.init();
//		flywheel.setPID(.025, 0, .25, .01025, 0, 24, 0);
//		turret.init();
//		turret.setPID(Parameters.swerveP, Parameters.swerveI, Parameters.swerveD);
	}

	@Override
	public void autonomousInit() {
		V_PID.clear("spin");
//		int sideMultiplier = DriverStation.getInstance().getAlliance() != DriverStation.Alliance.Red ? 1 : -1;
		int sideMultiplier = SmartDashboard.getNumber("sideColor", 0) == 0 ? 1 : -1;//0 is red, 1 is blue
		V_Instructions.placeLeftGear(swerve, gearer, sideMultiplier);//TODO ony allow this to go for 15 seconds
	}
	
	@Override
	public void teleopInit() {
		if (DriverStation.getInstance().getAlliance() != DriverStation.Alliance.Red) {
			Parameters.loadingStation += 90;
		}
		buttons2angle.put(R_Xbox.BUTTON_X, Parameters.leftGear);
		buttons2angle.put(R_Xbox.BUTTON_A, Parameters.centerGear);
		buttons2angle.put(R_Xbox.BUTTON_B, Parameters.rightGear);
		buttons2angle.put(R_Xbox.BUTTON_Y, Parameters.loadingStation);
		V_PID.clear("spin");
		lockedAngle = gyro.getCurrentAngle();
	}
	
	@Override
	public void testInit() {
		//TODO
	}
	
	@Override
	public void disabledInit() {
		//TODO
	}
	
	@Override
	public void robotPeriodic() {
		SmartDashboard.putBoolean("gear out", gearer.get().equals(DoubleSolenoid.Value.kForward));
		SmartDashboard.putBoolean("A aligned", moduleA.isAligned());
		SmartDashboard.putBoolean("B aligned", moduleB.isAligned());
		SmartDashboard.putBoolean("C aligned", moduleC.isAligned());
		SmartDashboard.putBoolean("D aligned", moduleD.isAligned());
	}
	
	@Override
	public void autonomousPeriodic() {
	}
	
	@Override
	public void teleopPeriodic() {
		//DRIVER
		//start + back: align
		//X: left gear orientation
		//A: center gear orientation
		//B: right gear orientation
		//Y: loading station orientation
		//right axis: raw spin
		//left axis: speed and direction
		//RB: turbo
		//LB: gearer
		//LT: boolean climber
		//RT: raw intake in
		//dpad down: boolean intake out
		
		//GUNNER
		//start + back: gyro reset
		//right axis: gimbal x
		//left axis: gimbal y
		
		if (driver.getRawButton(R_Xbox.BUTTON_START) && driver.getRawButton(R_Xbox.BUTTON_BACK)) {//SWERVE ALIGNMENT
			swerve.align(.004);//TODO limit how long this can take
		}
		
		if (gunner.getRawButton(R_Xbox.BUTTON_START) && gunner.getRawButton(R_Xbox.BUTTON_BACK)) {//GYRO RESET
			gyro.reset();
//			lockedAngle = gyro.getCurrentAngle();
		}
		
		//{calculating speed}
		double speed = driver.getCurrentRadius(R_Xbox.STICK_LEFT, true);
		if (!driver.getRawButton(R_Xbox.BUTTON_RB)) {speed *= .6;}
		//{calculating spin}
//		double spin = 0;
//		if (V_Fridge.becomesTrue("hands off", driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X, .1) == 0)) {
//			lockedAngle = gyro.getCurrentAngle();//TODO gyro doesn't update as quickly as Luke does
//			V_PID.clear("spin");
//		}if (driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X, .1) == 0) {
//			buttons2angle.forEach((k, v) -> {
//				driver.getRawButton((int)k);
//				if (!override) {override = driver.getRawButton((int)k);}
//			});
//			if (override) {
//				lockedAngle = buttons2angle.get(driver.getYoungestButton(mappedButtons));
//			}double spinError = Math.abs(gyro.wornPath(lockedAngle)) > 3 ? gyro.wornPath(lockedAngle) : 0;
//			spin = V_PID.get("spin", spinError);
//		}else {
//			override = false;
		double spin = driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X);
		spin *= .5*spin*Math.signum(spin);
		if (driver.getRawButton(R_Xbox.BUTTON_STICK_RIGHT)) {
			spin *= .5;
			if (speed == 0) {
				speed = .01;//lock everything down
			}
		}
		
//		}
//		SmartDashboard.putNumber("lockedAngle", lockedAngle);
		
		swerve.holonomic(driver.getCurrentAngle(R_Xbox.STICK_LEFT, true), speed*speed, spin);//SWERVE DRIVE
		
		if (driver.getAxisPress(R_Xbox.AXIS_LT, .5)) {//CLIMBER
			if (driver.getRawButton(R_Xbox.BUTTON_RB)) {
				climber.set(-1);
			}else {
				climber.set(-.6);
			}
		}else if (gunner.getAxisPress(R_Xbox.AXIS_LT, .5)) {
			climber.set(.6);
		}else {
			climber.set(0);
		}
		
		if (V_Fridge.freeze("LB", driver.getRawButton(R_Xbox.BUTTON_LB))) {//GEARER
				gearer.set(DoubleSolenoid.Value.kForward);
		}else {
				gearer.set(DoubleSolenoid.Value.kReverse);
		}
		
		if (driver.getAxisPress(R_Xbox.AXIS_RT, .05)) {
			intake.set(driver.getRawAxis(R_Xbox.AXIS_RT));//INTAKE
		}else if (driver.getPOV(0) == R_Xbox.POV_SOUTH) {
			intake.set(-.2);
		}else {
			intake.set(0);
		}
		
//		if (gunner.getRawButton(R_XboxV2.BUTTON_RB)) {//FLYWHEEL
//			flywheel.set(6000);
//		}else {
//			flywheel.set(0);
//		}
		
		gimbal.moveCamera(-gunner.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X), gunner.getDeadbandedAxis(R_Xbox.AXIS_LEFT_Y));//CAMERA GIMBLE
		
		if (gyro.netAcceleration() >= 1) {
			driver.setRumble(RumbleType.kLeftRumble, 1);//DANGER RUMBLE
		}else {
			driver.setRumble(RumbleType.kLeftRumble, 0);
		}
		//{finishing loop}
		moduleA.completeLoopUpdate();
		moduleB.completeLoopUpdate();
		moduleC.completeLoopUpdate();
		moduleD.completeLoopUpdate();
		climber.completeLoopUpdate();
		intake.completeLoopUpdate();
	}
	
	@Override
	public void testPeriodic() {
	}
	
	@Override
	public void disabledPeriodic() {
		//TODO
	}
}
