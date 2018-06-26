//DRIVER
//start + back: align
//left stick, both axis: raw speed and direction
//right stick, x axis: raw spin
//left stick, press: snail mode drive
//right stick, press: snail mode spin
//LB: boolean climber
//LT: toggle clamp
//RB: turbo mode (drive and climber)
//RT: toggle lift
//X: swerve x mode
//dpad down: toggle gearer

//GUNNER
//start + back: gyro reset
//LT: reverse driver's climbing commands

package org.usfirst.frc.team4256.robot;

import java.util.HashMap;
import java.util.Map;

import com.cyborgcats.reusable.Fridge;
import com.cyborgcats.reusable.Gyro;
import com.cyborgcats.reusable.PID;
import com.cyborgcats.reusable.Subsystem;
import com.cyborgcats.reusable.Xbox;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends IterativeRobot {
	//{Human Input}
	private static final Xbox driver = new Xbox(0), gunner = new Xbox(1);
	//{Robot Input}
	private static final Gyro gyro = new Gyro(Parameters.Gyrometer_updateHz);
	public static double gyroHeading = 0.0;
	
	private static NetworkTableInstance nt;
	private static NetworkTable rockefeller, edison;
	
	//{Robot Output}
	private static final SwerveModule
	moduleA = new SwerveModule(Parameters.rotationAID, true, Parameters.tractionAID, Parameters.magnetAID),
	moduleB = new SwerveModule(Parameters.rotationBID, true, Parameters.tractionBID, Parameters.magnetBID),
	moduleC = new SwerveModule(Parameters.rotationCID, true, Parameters.tractionCID, Parameters.magnetCID),
	moduleD = new SwerveModule(Parameters.rotationDID, true, Parameters.tractionDID, Parameters.magnetDID);
	private static final D_Swerve swerve = new D_Swerve(moduleA, moduleB, moduleC, moduleD);
	
	private static final Gearer gearer = new Gearer(new DoubleSolenoid(Parameters.Gearer_module, Parameters.Gearer_forward, Parameters.Gearer_reverse));
	private static final Climber climber = new Climber(Parameters.Climber);
	
	private static final Map<String, Subsystem> subsystems = new HashMap<String, Subsystem>();
	
	@Override
	public void robotInit() {
		//{Robot Input}
		nt = NetworkTableInstance.getDefault();
		rockefeller = nt.getTable("Rockefeller");
		edison = nt.getTable("Edison");
		//{Robot Output}
		swerve.init();
		subsystems.put("Gearer", gearer);
		subsystems.put("Climber", climber);
		for (Subsystem subsystem : subsystems.values()) subsystem.init();
	}

	@Override
	public void autonomousInit() {
		if (DriverStation.getInstance().getAlliance() != DriverStation.Alliance.Red) Parameters.loadingStation += 90;
		
		swerve.autoMode(true);
		
		PID.clear("forward");
		PID.clear("strafe");
		PID.clear("spin");
	}
	
	@Override
	public void teleopInit() {
		swerve.autoMode(false);
	}
	
	@Override
	public void testInit() {
	}
	
	@Override
	public void disabledInit() {
	}
	
	@Override
	public void robotPeriodic() {
		gyroHeading = gyro.getCurrentAngle();
		rockefeller.getEntry("Gyro").setNumber(gyroHeading);
		rockefeller.getEntry("Gearer Extended").setBoolean(gearer.isExtended());
		rockefeller.getEntry("Match Timer").setNumber(DriverStation.getInstance().getMatchTime());
	}
	
	@Override
	public void autonomousPeriodic() {
	}
	
	@Override
	public void teleopPeriodic() {		
		//{speed multipliers}
		final boolean turbo = driver.getRawButton(Xbox.BUTTON_STICK_LEFT);
		final boolean snail = driver.getRawButton(Xbox.BUTTON_STICK_RIGHT);
		
		//{calculating speed}
		double speed = driver.getCurrentRadius(Xbox.STICK_LEFT, true);//turbo mode
		if (!turbo) speed *= 0.7;//-------------------------------------normal mode
		if (snail)  speed *= 0.5;//-------------------------------------snail mode
		speed *= speed;
		
		//{calculating spin}
		double spin = 0.7*driver.getDeadbandedAxis(Xbox.AXIS_RIGHT_X);//normal mode
		if (snail) spin  *= 0.7;//--------------------------------------snail mode
		spin *= spin*Math.signum(spin);
		
		if (driver.getRawButton(Xbox.BUTTON_X)) swerve.formX();//X lock
		else {//SWERVE DRIVE
			swerve.travelTowards(driver.getCurrentAngle(Xbox.STICK_LEFT, true));
			swerve.setSpeed(speed);
			swerve.setSpin(spin);
		}
		
		
		if (driver.getRawButton(Xbox.BUTTON_START)) rockefeller.getEntry("Aligned").setBoolean(swerve.align());//SWERVE ALIGNMENT
		if (Fridge.becomesTrue("gyro reset", gunner.getRawButton(Xbox.BUTTON_BACK))) gyro.setTareAngle(gyroHeading, true);//GYRO RESET
		
		climber.reverse(gunner.getAxisPress(Xbox.AXIS_LT, .5));
		if (driver.getRawButton(Xbox.BUTTON_LB)) climber.ascend(driver.getRawButton(Xbox.BUTTON_RB));//CLIMBER
		else climber.stop();
		
		if (Fridge.freeze("POVSOUTH", driver.getPOV(0) == Xbox.POV_SOUTH)) gearer.extend();//GEARER
		else gearer.retract();
		
		if (gyro.netAcceleration() >= 1) driver.setRumble(RumbleType.kLeftRumble, 1);//DANGER RUMBLE
		else driver.setRumble(RumbleType.kLeftRumble, 0);
		
		//{completing motor controller updates}
		swerve.completeLoopUpdate();
		for (Subsystem subsystem : subsystems.values()) subsystem.completeLoopUpdate();
	}
	
	@Override
	public void testPeriodic() {
		moduleA.swivelTo(0);
		moduleB.swivelTo(0);
		moduleC.swivelTo(0);
		moduleD.swivelTo(0);
	}
	
	@Override
	public void disabledPeriodic() {
	}
}
