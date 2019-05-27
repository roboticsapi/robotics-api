package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A rotation combiner module (creates a rotation data flow from an axis and an
 * angle to rotate around)
 */
public class RotationFromAxisAngle extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationFromAxisAngle";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Angle to rotate */
	private final InPort inAngle = new InPort("inAngle");

	/** Rotation axis (normalized) */
	private final InPort inAxis = new InPort("inAxis");

	/** Combined rotation */
	private final OutPort outValue = new OutPort("outValue");

	/** Axis x value */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"X", new org.roboticsapi.runtime.core.types.RPIdouble("1"));

	/** Axis y value */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Y", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Axis z value */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramZ = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Z", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Rotation angle */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramAngle = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Angle", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	public RotationFromAxisAngle() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inAngle);
		add(inAxis);
		add(outValue);

		// Add all parameters
		add(paramX);
		add(paramY);
		add(paramZ);
		add(paramAngle);
	}

	/**
	 * Creates a rotation combiner module (creates a rotation data flow from an axis
	 * and an angle to rotate around)
	 *
	 * @param x Axis x value
	 * @param y Axis y value
	 * @param z Axis z value
	 */
	public RotationFromAxisAngle(org.roboticsapi.runtime.core.types.RPIdouble paramX,
			org.roboticsapi.runtime.core.types.RPIdouble paramY, org.roboticsapi.runtime.core.types.RPIdouble paramZ) {
		this();

		// Set the parameters
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
	}

	/**
	 * Creates a rotation combiner module (creates a rotation data flow from an axis
	 * and an angle to rotate around)
	 *
	 * @param x Axis x value
	 * @param y Axis y value
	 * @param z Axis z value
	 */
	public RotationFromAxisAngle(Double paramX, Double paramY, Double paramZ) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramX), new org.roboticsapi.runtime.core.types.RPIdouble(paramY),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramZ));
	}

	/**
	 * Creates a rotation combiner module (creates a rotation data flow from an axis
	 * and an angle to rotate around)
	 *
	 * @param paramX     Axis x value
	 * @param paramY     Axis y value
	 * @param paramZ     Axis z value
	 * @param paramAngle Rotation angle
	 */
	public RotationFromAxisAngle(org.roboticsapi.runtime.core.types.RPIdouble paramX,
			org.roboticsapi.runtime.core.types.RPIdouble paramY, org.roboticsapi.runtime.core.types.RPIdouble paramZ,
			org.roboticsapi.runtime.core.types.RPIdouble paramAngle) {
		this();

		// Set the parameters
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
		setAngle(paramAngle);
	}

	/**
	 * Creates a rotation combiner module (creates a rotation data flow from an axis
	 * and an angle to rotate around)
	 *
	 * @param paramX     Axis x value
	 * @param paramY     Axis y value
	 * @param paramZ     Axis z value
	 * @param paramAngle Rotation angle
	 */
	public RotationFromAxisAngle(Double paramX, Double paramY, Double paramZ, Double paramAngle) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramX), new org.roboticsapi.runtime.core.types.RPIdouble(paramY),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramZ),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramAngle));
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
	 * Angle to rotate
	 *
	 * @return the input port of the block
	 */
	public final InPort getInAngle() {
		return this.inAngle;
	}

	/**
	 * Rotation axis (normalized)
	 *
	 * @return the input port of the block
	 */
	public final InPort getInAxis() {
		return this.inAxis;
	}

	/**
	 * Combined rotation
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Axis x value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: Axis x value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Axis x value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Axis y value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Axis y value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Axis y value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Axis z value
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getZ() {
		return this.paramZ;
	}

	/**
	 * Sets a parameter of the block: Axis z value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Axis z value
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(Double value) {
		this.setZ(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Rotation angle
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getAngle() {
		return this.paramAngle;
	}

	/**
	 * Sets a parameter of the block: Rotation angle
	 * 
	 * @param value new value of the parameter
	 */
	public final void setAngle(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramAngle.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Rotation angle
	 * 
	 * @param value new value of the parameter
	 */
	public final void setAngle(Double value) {
		this.setAngle(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
