package org.usfirst.frc.team4256.robot;

import com.cyborgcats.reusable.Subsystem;

import edu.wpi.first.wpilibj.DoubleSolenoid;

public final class Gearer implements Subsystem {
	private final DoubleSolenoid actuator;
	public Gearer(final DoubleSolenoid actuator) {this.actuator = actuator;}
	
	public boolean isExtended() {return actuator.get().equals(DoubleSolenoid.Value.kForward);}
	public void extend() {actuator.set(DoubleSolenoid.Value.kForward);}
	public void retract() {actuator.set(DoubleSolenoid.Value.kReverse);}

	@Override
	public void init() {}
	@Override
	public void completeLoopUpdate() {}
	@Override
	public boolean perform(final String action, final double[] data) {
		switch(Abilities.valueOf(action)) {
		case EXTEND: extend(); return isExtended();
		case RETRACT: retract(); return !isExtended();
		default: throw new IllegalStateException("The gearer cannot " + action);
		}
	}
	
	public static enum Abilities {EXTEND, RETRACT}
}
