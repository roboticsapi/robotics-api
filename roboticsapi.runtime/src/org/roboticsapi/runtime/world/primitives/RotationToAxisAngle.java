package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A rotation splitter module (calculates normalized axis and angle from a
 * rotation data flow)
 */
public class RotationToAxisAngle extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationToAxisAngle";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Rotation */
	private final InPort inValue = new InPort("inValue");

	/** Angle to rotate */
	private final OutPort outAngle = new OutPort("outAngle");

	/** Rotation axis (normalized) */
	private final OutPort outAxis = new OutPort("outAxis");

	public RotationToAxisAngle() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inValue);
		add(outAngle);
		add(outAxis);

		// Add all parameters
	}

	/**
	 * Activation port
	 *
	 * @return the input port of the block
	 */
	public final InPort getInActive() {
		return this.inActive;
	}

	/**
	 * Rotation
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Angle to rotate
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutAngle() {
		return this.outAngle;
	}

	/**
	 * Rotation axis (normalized)
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutAxis() {
		return this.outAxis;
	}

}
