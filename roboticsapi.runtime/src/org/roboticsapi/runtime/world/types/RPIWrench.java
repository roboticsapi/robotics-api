package org.roboticsapi.runtime.world.types;

import org.roboticsapi.runtime.rpi.types.ComplexType;

/**
 * Composed type RPIWrench a.k.a World::Wrench
 */
public class RPIWrench extends ComplexType {

	private org.roboticsapi.runtime.world.types.RPIVector force = new org.roboticsapi.runtime.world.types.RPIVector();
	private org.roboticsapi.runtime.world.types.RPIVector torque = new org.roboticsapi.runtime.world.types.RPIVector();

	/**
	 * Creates an empty RPIWrench
	 */
	public RPIWrench() {
	}

	/**
	 * Creates an RPIWrench
	 * 
	 * @param force  Force [N]
	 * @param torque Torque (direction = rotation axis, length = amount) [Nm]
	 */
	public RPIWrench(org.roboticsapi.runtime.world.types.RPIVector force,
			org.roboticsapi.runtime.world.types.RPIVector torque) {
		this.force = force;
		this.torque = torque;
	}

	public RPIWrench(String value) {
		this();
		consumeString(value);
	}

	/**
	 * Sets the Force [N]
	 * 
	 * @param value The new value
	 */
	public void setForce(org.roboticsapi.runtime.world.types.RPIVector value) {
		this.force = value;
	}

	/**
	 * Retrieves the Force [N]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.runtime.world.types.RPIVector getForce() {
		return force;
	}

	/**
	 * Sets the Torque (direction = rotation axis, length = amount) [Nm]
	 * 
	 * @param value The new value
	 */
	public void setTorque(org.roboticsapi.runtime.world.types.RPIVector value) {
		this.torque = value;
	}

	/**
	 * Retrieves the Torque (direction = rotation axis, length = amount) [Nm]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.runtime.world.types.RPIVector getTorque() {
		return torque;
	}

	@Override
	protected void appendComponents(StringBuilder buf) {
		appendComponent(buf, "force", force);
		buf.append(",");
		appendComponent(buf, "torque", torque);
	}

	@Override
	protected String consumeComponent(String key, String value) {
		if (key.equals("force")) {
			return force.consumeString(value);
		}
		if (key.equals("torque")) {
			return torque.consumeString(value);
		}
		throw new IllegalArgumentException("key");
	}
}
