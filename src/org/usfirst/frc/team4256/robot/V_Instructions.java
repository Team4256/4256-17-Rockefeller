package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.V_PID;

public abstract class V_Instructions {
	private static int previousStep = -1;//should ensure it always starts fresh
	private static Long stepStart = System.currentTimeMillis();
	private static double[] currentInstructions;
	private static boolean canMoveOn = true;
	private static Long autoTimer;
	private static boolean startedTimer;
	
	public static void follow(final double[][] instructions, final int autoStep, final R_DriveTrain swerve, final R_Gyro gyro) {
		if (autoStep != previousStep) {
			stepStart = System.currentTimeMillis();
			if (canMoveOn) {
				currentInstructions = instructions[autoStep];
			}canMoveOn = autoStep + 1 < instructions.length;
			previousStep = autoStep;
			V_PID.clear("spin");
		}
		if (System.currentTimeMillis() - stepStart < currentInstructions[0]) {
			double spinError = gyro.wornPath(currentInstructions[3]);
			if (Math.abs(spinError) < 3) {spinError = 0;}
			swerve.holonomic(currentInstructions[1], currentInstructions[2], V_PID.get("spin", spinError));
		}
	}
	
	public static boolean readyToMoveOn() {
		return System.currentTimeMillis() - stepStart >= currentInstructions[0];
	}
	
	public static boolean canMoveOn() {
		return canMoveOn;
	}
	
	public static void startTimer() {
		autoTimer = System.currentTimeMillis();
		startedTimer = true;
	}
	
	public static void resetTimer() {
		startedTimer = false;
	}
	
	public static boolean startedTimer() {
		return startedTimer;
	}
	
	public static double getSeconds() {
		return (System.currentTimeMillis() - autoTimer)/1000;
	}
}