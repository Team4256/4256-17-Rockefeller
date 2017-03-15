package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_CANTalon;
import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.V_PID;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public abstract class V_Instructions {//things that are used regardless of the driver, yet are not specific to a physical object
	/**
	 * Ian's auto code
	 */
	static double spinError = 0;
	static double spinOut = 0;
	
	static R_Gyro gyro = new R_Gyro(Parameters.Gyrometer_updateHz, 0, 0);
	private static boolean timedMovementOneDone = false;
	private static boolean timedMovementTwoDone = false;
	private static boolean timedMovementThreeDone = false;
	
	//moves a certain direction at a certain speed with a certain spin for a certain time
	public static void timedMovementOne(R_DriveTrain swerve, double directionOne, double speedOne, long timeinMillisOne) {
		long startTimeOne = System.currentTimeMillis();
		long expectedTime = System.currentTimeMillis();
		while(System.currentTimeMillis()-startTimeOne < timeinMillisOne) {
			while(System.currentTimeMillis() < expectedTime) {
			}
			swerve.holonomic(directionOne, speedOne, 0);
			expectedTime += 20;
		}
		stop(swerve);
		timedMovementOneDone = true;
	}
	public static boolean timedMovementOneDone() {
		return timedMovementOneDone;
	}
	public static void timedMovementTwo(R_DriveTrain swerve, double directionOne, double speedOne, long timeinMillisOne, double directionTwo, double speedTwo, long timeMillisTwo, int gear) {
		long startTimeOne = System.currentTimeMillis();		
		long expectedTimeOne = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTimeOne < timeinMillisOne) {
			while(System.currentTimeMillis() < expectedTimeOne) {
			}
			swerve.holonomic(directionOne, speedOne, 0);
			expectedTimeOne += 20;
		}
		stop(swerve);
		long startTimeTwo = System.currentTimeMillis();
		long expectedTimeTwo = System.currentTimeMillis();
		while(Math.abs(gyro.wornPath(gear)) < 2 && System.currentTimeMillis()-startTimeTwo < timeMillisTwo) {
			while(System.currentTimeMillis() < expectedTimeTwo) {
			}
			spinError = gyro.wornPath(gear);
			spinOut = V_PID.get("spin", spinError);
			swerve.holonomic(directionTwo, speedTwo, spinOut);
			expectedTimeTwo += 20;
		}
		stop(swerve);
		timedMovementTwoDone = true;
	}
	public static boolean timedMovementTwoDone() {
		return timedMovementTwoDone;
	}
	public static void timedMovementThree(R_DriveTrain swerve, double directionOne, double speedOne, long timeinMillisOne, double directionTwo, long timeinMillisTwo, double directionThree, double speedThree, long timeinMillisThree, int gear) {
		long startTimeOne = System.currentTimeMillis();
		long expectedTimeOne = System.currentTimeMillis();
		double angle = gyro.getCurrentAngle();
		while(System.currentTimeMillis()-startTimeOne < timeinMillisOne) {
			while(System.currentTimeMillis() < expectedTimeOne) {
			}
			spinError = gyro.wornPath(angle);
			spinOut = V_PID.get("spin", spinError);
			swerve.holonomic(directionOne, speedOne, spinOut);
			expectedTimeOne += 20;
		}
		V_PID.clear("spin");
		stop(swerve);
		long startTimeTwo = System.currentTimeMillis();
		long expectedTimeTwo = System.currentTimeMillis();
		while(Math.abs(gyro.wornPath(gear)) > 2 && System.currentTimeMillis()-startTimeTwo < timeinMillisTwo) {
			while(System.currentTimeMillis() < expectedTimeTwo) {
			}
			spinError = gyro.wornPath(gear);
			spinOut = V_PID.get("spin", spinError);
			swerve.holonomic(directionTwo, 0, spinOut);
			expectedTimeTwo += 20;
		}
		stop(swerve);
		long startTimeThree = System.currentTimeMillis();
		long expectedTimeThree = System.currentTimeMillis();
		while(System.currentTimeMillis()-startTimeThree < timeinMillisThree) {
			while(System.currentTimeMillis() < expectedTimeThree) {
			}
			spinError = gyro.wornPath(gear);
			spinOut = V_PID.get("spin", spinError);
			swerve.holonomic(directionThree, speedThree, spinOut);
			expectedTimeThree += 20;
		}
		stop(swerve);
		timedMovementThreeDone = true;
	}
	public static boolean timedMovementThreeDone() {
		return timedMovementThreeDone;
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