package org.roboticsapi.facet.runtime.rpi.world.types;

import org.roboticsapi.facet.runtime.rpi.ComplexType;

/**
 * Composed type RPIFrame a.k.a World::Frame
 */
public class RPIFrame extends ComplexType {

	private org.roboticsapi.facet.runtime.rpi.world.types.RPIVector pos = new org.roboticsapi.facet.runtime.rpi.world.types.RPIVector();
	private org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation rot = new org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation();

	/**
	 * Creates an empty RPIFrame
	 */
	public RPIFrame() {
	}

	/**
	 * Creates an RPIFrame
	 * 
	 * @param pos Position [m]
	 * @param rot Rotation
	 */
	public RPIFrame(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector pos,
			org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation rot) {
		this.pos = pos;
		this.rot = rot;
	}

	public RPIFrame(String value) {
		this();
		consumeString(value);
	}

	/**
	 * Sets the Position [m]
	 * 
	 * @param value The new value
	 */
	public void setPos(org.roboticsapi.facet.runtime.rpi.world.types.RPIVector value) {
		this.pos = value;
	}

	/**
	 * Retrieves the Position [m]
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.facet.runtime.rpi.world.types.RPIVector getPos() {
		return pos;
	}

	/**
	 * Sets the Rotation
	 * 
	 * @param value The new value
	 */
	public void setRot(org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation value) {
		this.rot = value;
	}

	/**
	 * Retrieves the Rotation
	 * 
	 * @return The current value
	 */
	public org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation getRot() {
		return rot;
	}

	@Override
	protected void appendComponents(StringBuilder buf) {
		appendComponent(buf, "pos", pos);
		buf.append(",");
		appendComponent(buf, "rot", rot);
	}

	@Override
	protected String consumeComponent(String key, String value) {
		if (key.equals("pos")) {
			return pos.consumeString(value);
		}
		if (key.equals("rot")) {
			return rot.consumeString(value);
		}
		throw new IllegalArgumentException("key");
	}
}
