package org.roboticsapi.runtime.world.types;

import org.roboticsapi.runtime.rpi.types.ComplexType;

/**
 * Composed type RPITwist a.k.a World::Twist
 */
public class RPITwist extends ComplexType {

	private org.roboticsapi.runtime.world.types.RPIVector vel = new org.roboticsapi.runtime.world.types.RPIVector();
	private org.roboticsapi.runtime.world.types.RPIVector rot = new org.roboticsapi.runtime.world.types.RPIVector();

	/**
	 * Creates an empty RPITwist
	 */
	public RPITwist() {
	}

	/**
	 * Creates an RPITwist
	 * 
	 * @param vel Translational velocity [m/s]
	 * @param rot Rotational velocity (direction = rotation axis, length = velocity)
	 *            [rad/s]
	 */
	public RPITwist(org.roboticsapi.runtime.world.types.RPIVector vel, org.roboticsapi.runtime.world.types.RPIVector rot) {
		this.vel = vel;
		this.rot = rot;
	}

	public RPITwist(String value) {
		this();
		consumeString(value);
	}

	/**
	 * Sets the Translational velocity [m/s]
	 * 
	 * @param value The new value
	 */
	public void setVel(org.roboticsapi.runtime.world.types.RPIVector value) {
		this.vel = value;
	}

	/**
	 * Retrieves the Translational velocity [m/s]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.runtime.world.types.RPIVector getVel() {
		return vel;
	}

	/**
	 * Sets the Rotational velocity (direction = rotation axis, length = velocity)
	 * [rad/s]
	 * 
	 * @param value The new value
	 */
	public void setRot(org.roboticsapi.runtime.world.types.RPIVector value) {
		this.rot = value;
	}

	/**
	 * Retrieves the Rotational velocity (direction = rotation axis, length =
	 * velocity) [rad/s]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.runtime.world.types.RPIVector getRot() {
		return rot;
	}

	@Override
	protected void appendComponents(StringBuilder buf) {
		appendComponent(buf, "vel", vel);
		buf.append(",");
		appendComponent(buf, "rot", rot);
	}

	@Override
	protected String consumeComponent(String key, String value) {
		if (key.equals("vel")) {
			return vel.consumeString(value);
		}
		if (key.equals("rot")) {
			return rot.consumeString(value);
		}
		throw new IllegalArgumentException("key");
	}
}
