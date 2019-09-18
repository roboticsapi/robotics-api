package org.roboticsapi.facet.runtime.rpi.world.types;

import org.roboticsapi.facet.runtime.rpi.ComplexType;

/**
 * Composed type RPITwist a.k.a World::Twist
 */
public class RPITwist extends ComplexType {

	private org.roboticsapi.facet.runtime.rpi.world.types.RPIVector vel = new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector();
	private org.roboticsapi.facet.runtime.rpi.world.types.RPIVector rot = new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector();

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
	public RPITwist(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector vel,
			org.roboticsapi.facet.runtime.rpi.world.types.RPIVector rot) {
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
	public void setVel(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector value) {
		this.vel = value;
	}

	/**
	 * Retrieves the Translational velocity [m/s]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.facet.runtime.rpi.world.types.RPIVector getVel() {
		return vel;
	}

	/**
	 * Sets the Rotational velocity (direction = rotation axis, length = velocity)
	 * [rad/s]
	 * 
	 * @param value The new value
	 */
	public void setRot(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector value) {
		this.rot = value;
	}

	/**
	 * Retrieves the Rotational velocity (direction = rotation axis, length =
	 * velocity) [rad/s]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.facet.runtime.rpi.world.types.RPIVector getRot() {
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
