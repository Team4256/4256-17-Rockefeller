package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.R_Gyro;
import com.cyborgcats.reusable.V_PID;

public abstract class V_Instructions {
	private static int previousStep = -1;//should ensure it always starts fresh
	private static Long stepStart = System.currentTimeMillis();
	private static double[] currentInstructions;
	private static boolean canMoveOn = true;
	
	public static void follow(final double[][] instructions, final int autoStep, final R_DriveTrain swerve, final R_Gyro gyro) {
		if (autoStep != previousStep) {
			stepStart = System.currentTimeMillis();
			if (canMoveOn) {
				currentInstructions = instructions[autoStep];
			}canMoveOn = autoStep + 1 < instructions.length;
			previousStep = autoStep;
			V_PID.clear("spin");
		}
		swerve.holonomic(currentInstructions[1], currentInstructions[2], V_PID.get("spin", gyro.wornPath(currentInstructions[3])));
	}
	
	public static boolean readyToMoveOn() {
		return System.currentTimeMillis() - stepStart >= currentInstructions[0];
	}
	
	public static boolean canMoveOn() {
		return canMoveOn;
	}
}