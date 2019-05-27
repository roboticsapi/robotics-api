package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A twist from velocity vector module (combines values for translational and
 * rotational velocity into a twist datatype)
 */
public class TwistFromVelocities extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistFromVelocities";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Rotational velocity */
	private final InPort inRotVel = new InPort("inRotVel");

	/** Translational velocity */
	private final InPort inTransVel = new InPort("inTransVel");

	/** Combined twist */
	private final OutPort outValue = new OutPort("outValue");

	/** X velocity */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"X", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Y velocity */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Y", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Z velocity */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramZ = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Z", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** X rotation velocity */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramRX = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"RX", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Y rotation velocity */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramRY = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"RY", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Z rotation velocity */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramRZ = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"RZ", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	public TwistFromVelocities() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inRotVel);
		add(inTransVel);
		add(outValue);

		// Add all parameters
		add(paramX);
		add(paramY);
		add(paramZ);
		add(paramRX);
		add(paramRY);
		add(paramRZ);
	}

	/**
	 * Creates a twist from velocity vector module (combines values for
	 * translational and rotational velocity into a twist datatype)
	 *
	 * @param x  X velocity
	 * @param y  Y velocity
	 * @param z  Z velocity
	 * @param rX X rotation velocity
	 * @param rY Y rotation velocity
	 * @param rZ Z rotation velocity
	 */
	public TwistFromVelocities(org.roboticsapi.runtime.core.types.RPIdouble paramX,
			org.roboticsapi.runtime.core.types.RPIdouble paramY, org.roboticsapi.runtime.core.types.RPIdouble paramZ,
			org.roboticsapi.runtime.core.types.RPIdouble paramRX, org.roboticsapi.runtime.core.types.RPIdouble paramRY,
			org.roboticsapi.runtime.core.types.RPIdouble paramRZ) {
		this();

		// Set the parameters
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
		setRX(paramRX);
		setRY(paramRY);
		setRZ(paramRZ);
	}

	/**
	 * Creates a twist from velocity vector module (combines values for
	 * translational and rotational velocity into a twist datatype)
	 *
	 * @param x  X velocity
	 * @param y  Y velocity
	 * @param z  Z velocity
	 * @param rX X rotation velocity
	 * @param rY Y rotation velocity
	 * @param rZ Z rotation velocity
	 */
	public TwistFromVelocities(Double paramX, Double paramY, Double paramZ, Double paramRX, Double paramRY,
			Double paramRZ) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramX), new org.roboticsapi.runtime.core.types.RPIdouble(paramY),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramZ),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramRX),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramRY),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramRZ));
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
	 * Rotational velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInRotVel() {
		return this.inRotVel;
	}

	/**
	 * Translational velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInTransVel() {
		return this.inTransVel;
	}

	/**
	 * Combined twist
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * X velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: X velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: X velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Y velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Y velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Y velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Z velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getZ() {
		return this.paramZ;
	}

	/**
	 * Sets a parameter of the block: Z velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Z velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(Double value) {
		this.setZ(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * X rotation velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getRX() {
		return this.paramRX;
	}

	/**
	 * Sets a parameter of the block: X rotation velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRX(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramRX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: X rotation velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRX(Double value) {
		this.setRX(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Y rotation velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getRY() {
		return this.paramRY;
	}

	/**
	 * Sets a parameter of the block: Y rotation velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRY(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramRY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Y rotation velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRY(Double value) {
		this.setRY(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Z rotation velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getRZ() {
		return this.paramRZ;
	}

	/**
	 * Sets a parameter of the block: Z rotation velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRZ(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramRZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Z rotation velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRZ(Double value) {
		this.setRZ(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
