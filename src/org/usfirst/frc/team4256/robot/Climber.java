package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Subsystem;
import com.cyborgcats.reusable.Phoenix.Talon;

public class Climber implements Subsystem {
	private static final double fastSpeed = 1.0, slowSpeed = 0.6;
	private final Talon motor;
	private double direction = 1.0;
	public Climber(final int climberID) {this.motor = new Talon(climberID, Talon.percent);}
	
	public void ascend(final boolean fast) {
		if (fast) motor.quickSet(direction*fastSpeed, false);
		else motor.quickSet(direction*slowSpeed, false);
	}
	public void stop() {motor.quickSet(0.0, false);}
	public void reverse(final boolean reverse) {direction = reverse ? -1.0 : 1.0;}
	
	@Override
	public void init() {motor.init();}
	@Override
	public void completeLoopUpdate() {motor.completeLoopUpdate(); direction = 1.0;}
	@Override
	public boolean perform(final String action, final double[] data) {
		switch(Abilities.valueOf(action)) {
		case ASCEND: ascend(false); return true;
		case REVERSE: reverse(true); return true;
		case STOP: stop(); return true;
		default: throw new IllegalStateException("The climber cannot " + action);
		}
	}
	
	public static enum Abilities {ASCEND, REVERSE, STOP}
}
