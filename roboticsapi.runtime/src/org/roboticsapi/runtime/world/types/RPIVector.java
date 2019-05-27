package org.roboticsapi.runtime.world.types;

import org.roboticsapi.runtime.rpi.types.ComplexType;

/**
 * Composed type RPIVector a.k.a World::Vector
 */
public class RPIVector extends ComplexType {

	private org.roboticsapi.runtime.core.types.RPIdouble x = new org.roboticsapi.runtime.core.types.RPIdouble();
	private org.roboticsapi.runtime.core.types.RPIdouble y = new org.roboticsapi.runtime.core.types.RPIdouble();
	private org.roboticsapi.runtime.core.types.RPIdouble z = new org.roboticsapi.runtime.core.types.RPIdouble();

	/**
	 * Creates an empty RPIVector
	 */
	public RPIVector() {
	}

	/**
	 * Creates an RPIVector
	 * 
	 * @param x X position [m]
	 * @param y Y position [m]
	 * @param z Z position [m]
	 */
	public RPIVector(org.roboticsapi.runtime.core.types.RPIdouble x, org.roboticsapi.runtime.core.types.RPIdouble y,
			org.roboticsapi.runtime.core.types.RPIdouble z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public RPIVector(String value) {
		this();
		consumeString(value);
	}

	/**
	 * Sets the X position [m]
	 * 
	 * @param value The new value
	 */
	public void setX(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.x = value;
	}

	/**
	 * Retrieves the X position [m]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.runtime.core.types.RPIdouble getX() {
		return x;
	}

	/**
	 * Sets the Y position [m]
	 * 
	 * @param value The new value
	 */
	public void setY(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.y = value;
	}

	/**
	 * Retrieves the Y position [m]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.runtime.core.types.RPIdouble getY() {
		return y;
	}

	/**
	 * Sets the Z position [m]
	 * 
	 * @param value The new value
	 */
	public void setZ(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.z = value;
	}

	/**
	 * Retrieves the Z position [m]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.runtime.core.types.RPIdouble getZ() {
		return z;
	}

	@Override
	protected void appendComponents(StringBuilder buf) {
		appendComponent(buf, "x", x);
		buf.append(",");
		appendComponent(buf, "y", y);
		buf.append(",");
		appendComponent(buf, "z", z);
	}

	@Override
	protected String consumeComponent(String key, String value) {
		if (key.equals("x")) {
			return x.consumeString(value);
		}
		if (key.equals("y")) {
			return y.consumeString(value);
		}
		if (key.equals("z")) {
			return z.consumeString(value);
		}
		throw new IllegalArgumentException("key");
	}
}
