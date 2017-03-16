//DRIVER
//start + back: align
//left stick, both axis: raw speed and direction
//right stick, x axis: raw spin
//right stick, press: snail mode
//LB: toggle gearer
//LT: boolean climber
//RB: turbo mode (drive and climber)
//RT: raw intake in
//dpad down: boolean intake out
//X: left gear orientation
//A: center gear orientation
//B: right gear orientation
//Y: loading station orientation

//GUNNER
//start + back: gyro reset
//left stick, y axis: delta gimbal y
//right stick, x axis: delta gimbal x
//LT: reverse driver's climbing commands

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
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	//{Human Input}
	private static final R_Xbox driver = new R_Xbox(0);
	private static final R_Xbox gunner = new R_Xbox(1);
	private static final Map<Integer, Double> buttons2angle = new HashMap<Integer, Double>();
	private static final int[] gearButtons = new int[] {R_Xbox.BUTTON_X, R_Xbox.BUTTON_A, R_Xbox.BUTTON_B, R_Xbox.BUTTON_Y};
	private static Long handsOffTime = System.currentTimeMillis();
	private static double lockedAngle = 0;
	//{Robot Input}
	private static final R_Gimbal gimbal = new R_Gimbal(Parameters.Camera_servoX, Parameters.Camera_servoY, 6);
	private static final R_Gyro gyro = new R_Gyro(Parameters.Gyrometer_updateHz, 0, 0);
	private static NetworkTable edison;
	private static NetworkTable tesla;
	private static double metersX = 0;
	private static double metersY = 0;
	//{Robot Output}
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
	public void robotInit() {
		//{Robot Input}
		gyro.setTareAngle(270, false);//gearer should become forward
		edison = NetworkTable.getTable("edison");
		tesla = NetworkTable.getTable("tesla");
		//{Robot Output}
		compressor.clearAllPCMStickyFaults();
		swerve.init();
		V_PID.set("forward", Parameters.forwardP, Parameters.forwardI, Parameters.forwardD);
		V_PID.set("strafe", Parameters.strafeP, Parameters.strafeI, Parameters.strafeD);
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
		V_PID.clear("forward");
		V_PID.clear("strafe");
		V_PID.clear("spin");
	}
	
	@Override
	public void teleopInit() {
		if (DriverStation.getInstance().getAlliance() != DriverStation.Alliance.Red) {//TODO override brake modes
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
		tesla.putNumber("x", 0);
		tesla.putNumber("y", 0);
		tesla.putNumber("expected x", 0);
		tesla.putNumber("expected y", 0);
		tesla.putNumber("expected angle", 0);
	}
	
	@Override
	public void disabledInit() {
		//TODO
	}
	
	@Override
	public void robotPeriodic() {
		SmartDashboard.putBoolean("gear out", gearer.get().equals(DoubleSolenoid.Value.kForward));
		SmartDashboard.putBoolean("aligning", swerve.isAligning());
		SmartDashboard.putBoolean("aligned", swerve.isAligned());
	}
	
	@Override
	public void autonomousPeriodic() {
		gearer.set(DoubleSolenoid.Value.kReverse);
		if (!swerve.isAligned()) {
			swerve.align(.004);
			moduleA.setTareAngle(1, true);	moduleB.setTareAngle(1, true);	moduleC.setTareAngle(5, true);	moduleD.setTareAngle(6, true);
		}
		if (!V_Instructions.timedMovementOneDone()) {
			V_Instructions.timedMovementOne(swerve, 90, .15, 4300);
		}
//		metersX = tesla.getNumber("x", metersX);
//		metersY = tesla.getNumber("y", metersY);
//		double expectedX = tesla.getNumber("expected x", metersX);
//		double expectedY = tesla.getNumber("expected y", metersY);
//		double expectedAngle = tesla.getNumber("expected angle", gyro.getCurrentAngle());
//		double xError = expectedX - metersX;
//		double yError = expectedY - metersY;
//		double spinError = gyro.wornPath(expectedAngle);
//		swerve.holonomic2(V_PID.get("forward", yError), V_PID.get("strafe", xError), V_PID.get("spin", spinError));
	}
	
	@Override
	public void teleopPeriodic() {
		if (driver.getRawButton(R_Xbox.BUTTON_START) && driver.getRawButton(R_Xbox.BUTTON_BACK)) {//SWERVE ALIGNMENT
			swerve.align(.004);//TODO limit how long this can take
			moduleA.setTareAngle(1, true);	moduleB.setTareAngle(1, true);	moduleC.setTareAngle(5, true);	moduleD.setTareAngle(6, true);
		}
		
		if (gunner.getRawButton(R_Xbox.BUTTON_START) && gunner.getRawButton(R_Xbox.BUTTON_BACK)) {//GYRO RESET
			gyro.reset();
			lockedAngle = gyro.getCurrentAngle();
			V_PID.clear("spin");
		}
		
		//{calculating speed}
		double speed = driver.getCurrentRadius(R_Xbox.STICK_LEFT, true);//--turbo mode
		if (!driver.getRawButton(R_Xbox.BUTTON_RB)) {speed *= .6;}//--normal mode
		//{calculating raw spin}
		double spin = driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X);
		spin *= spin*Math.signum(spin)*.5;//--normal mode
		if (driver.getRawButton(R_Xbox.BUTTON_STICK_RIGHT)) {
			spin *= .5;//--snail mode
			if (speed == 0) {speed = .01;}//.01 restrains coast after spinning by hacking holonomic
		}
		//{adding driver aids}
		if (V_Fridge.becomesTrue("hands off", driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X, .1) == 0)) {
			handsOffTime = System.currentTimeMillis();
			lockedAngle = gyro.getCurrentAngle();//remember angle when driver stops rotating
			V_PID.clear("spin");
		}if (driver.getDeadbandedAxis(R_Xbox.AXIS_RIGHT_X, .1) == 0) {
			double spinError = 0;
			if (speed >= .3) {spinError = gyro.wornPath(lockedAngle);}//stop rotation drift at high speeds
			int gearButton = driver.mostRecentButton(gearButtons);
			if (driver.lastPress(gearButton) > handsOffTime) {spinError = gyro.wornPath(buttons2angle.get(gearButton));}
			if (Math.abs(spinError) > 3) {spin = V_PID.get("spin", spinError);}
		}
		
		swerve.holonomic(driver.getCurrentAngle(R_Xbox.STICK_LEFT, true), speed*speed, spin);//SWERVE DRIVE
		
		if (driver.getAxisPress(R_Xbox.AXIS_LT, .5)) {//CLIMBER
			double climbSpeed = driver.getRawButton(R_Xbox.BUTTON_RB) ? -1 : -.6;
			if (gunner.getAxisPress(R_Xbox.AXIS_LT, .5)) {climbSpeed *= -1;}
			climber.set(climbSpeed);
		}else {
			climber.set(0);
		}
		if (V_Fridge.freeze("LB", driver.getRawButton(R_Xbox.BUTTON_LB))) {//GEARER
				gearer.set(DoubleSolenoid.Value.kForward);
		}else {
				gearer.set(DoubleSolenoid.Value.kReverse);
		}
		
		if (driver.getPOV(0) == R_Xbox.POV_SOUTH) {
			intake.set(-.2);
		}else {
			intake.set(driver.getRawAxis(R_Xbox.AXIS_RT));//INTAKE
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
		
		//{completing Talon updates}
		moduleA.completeLoopUpdate();
		moduleB.completeLoopUpdate();
		moduleC.completeLoopUpdate();
		moduleD.completeLoopUpdate();
		climber.completeLoopUpdate();
		intake.completeLoopUpdate();
	}
	
	@Override
	public void testPeriodic() {//TODO must test this extensively
		metersX = tesla.getNumber("x", metersX);
		metersY = tesla.getNumber("y", metersY);
		double expectedX = tesla.getNumber("expected x", metersX);
		double expectedY = tesla.getNumber("expected y", metersY);
		double expectedAngle = tesla.getNumber("expected angle", gyro.getCurrentAngle());
		double xError = expectedX - metersX;
		double yError = expectedY - metersY;
		double spinError = gyro.wornPath(expectedAngle);
		swerve.holonomic2(V_PID.get("forward", yError), V_PID.get("strafe", xError), V_PID.get("spin", spinError));
	}
	
	@Override
	public void disabledPeriodic() {
		//TODO
	}
}
