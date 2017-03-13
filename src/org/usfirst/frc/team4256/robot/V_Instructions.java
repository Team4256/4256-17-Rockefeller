package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.V_PID;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public abstract class V_Instructions {//things that are used regardless of the driver, yet are not specific to a physical object, just copied over some interesting code from last year
	/**
	 * Ian's auto code
	 */
	static double spinError = 0;
	static double spinOut = 0;
	
	static R_Gyro gyro = new R_Gyro(Parameters.Gyrometer_updateHz, 0, 0);
	
	//moves a certain direction at a certain speed with a certain spin for a certain time
	private static void timedMovementOne(R_DriveTrain swerve, double directionOne, double speedOne, long timeinMillisOne) {
		long startTimeOne = System.currentTimeMillis();		
		while(System.currentTimeMillis()-startTimeOne < timeinMillisOne) {
			swerve.holonomic(directionOne, speedOne, 0);
		}
		stop(swerve);
	}
	private static void timedMovementTwo(R_DriveTrain swerve, double directionOne, double speedOne, long timeinMillisOne, double directionTwo, double speedTwo, long timeMillisTwo, int gear) {
		long startTimeOne = System.currentTimeMillis();		
		while(System.currentTimeMillis()-startTimeOne < timeinMillisOne) {
			swerve.holonomic(directionOne, speedOne, 0);
		}
		stop(swerve);
		long startTimeTwo = System.currentTimeMillis();
		while(System.currentTimeMillis()-startTimeTwo < timeMillisTwo) {
			spinError = gyro.wornPath(gear);
			spinOut = V_PID.get("spin", spinError);
//			if(spinOut > 0.25) {
//				spinOut = 0.25;
//			}
			swerve.holonomic(directionTwo, speedTwo, spinOut);
		}
		stop(swerve);
	}
	private static void timedMovementThree(R_DriveTrain swerve, double directionOne, double speedOne, long timeinMillisOne, double directionTwo, long timeinMillisTwo, double directionThree, double speedThree, long timeinMillisThree, int gear) {
		long startTimeOne = System.currentTimeMillis();
		double angle = gyro.getCurrentAngle();
		while(System.currentTimeMillis()-startTimeOne < timeinMillisOne) {
			spinError = gyro.wornPath(angle);
			spinOut = V_PID.get("spin", spinError);
			swerve.holonomic(directionOne, speedOne, spinOut);
		}
		V_PID.clear("spin");
		stop(swerve);
		long startTimeTwo = System.currentTimeMillis();
		while(Math.abs(gyro.wornPath(gear)) > 2 && System.currentTimeMillis()-startTimeTwo < timeinMillisTwo) {
			spinError = gyro.wornPath(gear);
			spinOut = V_PID.get("spin", spinError);
//			if(spinOut > 0.25) {
//				spinOut = 0.25;
//			}
			swerve.holonomic(directionTwo, 0, spinOut);
		}
		stop(swerve);
		long startTimeThree = System.currentTimeMillis();
		while(System.currentTimeMillis()-startTimeThree < timeinMillisThree) {
			spinError = gyro.wornPath(gear);
			spinOut = V_PID.get("spin", spinError);
			swerve.holonomic(directionThree, speedThree, spinOut);
		}
		stop(swerve);
	}
	//stops swerve
	private static void stop(R_DriveTrain swerve) {
		swerve.holonomic(0, 0, 0);
	}
	//turns shooter on to a certain RPM
	private static void shooterOn(R_CANTalon flywheel, int shooterSpeed) {
		flywheel.set(shooterSpeed);
	}
	//turns shooter off
	private static void shooterOff(R_CANTalon flywheel) {
		flywheel.set(0);
	}
	public static void placeLeftGear(R_DriveTrain swerve, DoubleSolenoid gearer) {
		gearer.set(Value.kForward);
		swerve.align(.004);
		timedMovementThree(swerve, -20, 0.2, 2500, 60, 3000, 60, 0.15, 5000, -21);
//		timedMovementOne(swerve, -20, 0.2, 3000);
	}
	public static void placeMiddleGear(R_DriveTrain swerve, DoubleSolenoid gearer) {
		gearer.set(Value.kForward);
		swerve.align(.002);
		timedMovementTwo(swerve, -5, 0.25, 1000, -5, 0.1, 2000, 0);
	}
	public static void driveForward(R_DriveTrain swerve, DoubleSolenoid gearer) {
		gearer.set(Value.kForward);
		swerve.align(.002);
		timedMovementOne(swerve, 0, 0, 0);
		
	}
	
	public static void shotAlignment(final double theoreticalTimeS, final double tolerance, final boolean enable) {
		//final float goalAngle = OPERATE2016.gyro.getCurrentAngle() + (float)OPERATE2016.visionTable.getNumber("AngleDifferential", 0.0);
		//OPERATE2016.base.arcadeDrive(0, FILTER4256.rotate(enable, goalAngle, theoreticalTimeS, tolerance), false);
	}
}
//TODO current based limit switches (ask for currentAmps and maxAmps, or maybe average amps, and return boolean for whether currentAmps are normal -- could also do changeIN thing)
//TODO documentation