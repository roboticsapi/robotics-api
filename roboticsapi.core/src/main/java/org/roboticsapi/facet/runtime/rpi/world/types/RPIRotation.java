package org.roboticsapi.facet.runtime.rpi.world.types;

import org.roboticsapi.facet.runtime.rpi.ComplexType;

/**
 * Composed type RPIRotation a.k.a World::Rotation
 */
public class RPIRotation extends ComplexType {

	private org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble a = new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble();
	private org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble b = new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble();
	private org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble c = new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble();

	/**
	 * Creates an empty RPIRotation
	 */
	public RPIRotation() {
	}

	/**
	 * Creates an RPIRotation
	 * 
	 * @param a A rotation (rad, around Z)
	 * @param b B rotation (rad, around Y)
	 * @param c C rotation (rad, around X)
	 */
	public RPIRotation(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble a,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble b,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	public RPIRotation(String value) {
		this();
		consumeString(value);
	}

	/**
	 * Sets the A rotation (rad, around Z)
	 * 
	 * @param value The new value
	 */
	public void setA(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.a = value;
	}

	/**
	 * Retrieves the A rotation (rad, around Z)
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble getA() {
		return a;
	}

	/**
	 * Sets the B rotation (rad, around Y)
	 * 
	 * @param value The new value
	 */
	public void setB(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.b = value;
	}

	/**
	 * Retrieves the B rotation (rad, around Y)
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble getB() {
		return b;
	}

	/**
	 * Sets the C rotation (rad, around X)
	 * 
	 * @param value The new value
	 */
	public void setC(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.c = value;
	}

	/**
	 * Retrieves the C rotation (rad, around X)
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble getC() {
		return c;
	}

	@Override
	protected void appendComponents(StringBuilder buf) {
		appendComponent(buf, "a", a);
		buf.append(",");
		appendComponent(buf, "b", b);
		buf.append(",");
		appendComponent(buf, "c", c);
	}

	@Override
	protected String consumeComponent(String key, String value) {
		if (key.equals("a")) {
			return a.consumeString(value);
		}
		if (key.equals("b")) {
			return b.consumeString(value);
		}
		if (key.equals("c")) {
			return c.consumeString(value);
		}
		throw new IllegalArgumentException("key");
	}
}
