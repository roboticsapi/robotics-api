package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A vector from XYZ module (combines values for X, Y, Z into a vector datatype)
 */
public class VectorFromXYZ extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::VectorFromXYZ";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** X position */
	private final InPort inX = new InPort("inX");

	/** Y position */
	private final InPort inY = new InPort("inY");

	/** Z position */
	private final InPort inZ = new InPort("inZ");

	/** Combined vector */
	private final OutPort outValue = new OutPort("outValue");

	/** X position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"X", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Y position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Y", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Z position */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramZ = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Z", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public VectorFromXYZ() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inX);
		add(inY);
		add(inZ);
		add(outValue);

		// Add all parameters
		add(paramX);
		add(paramY);
		add(paramZ);
	}

	/**
	 * Creates a vector from XYZ module (combines values for X, Y, Z into a vector
	 * datatype)
	 *
	 * @param paramX X position
	 * @param paramY Y position
	 * @param paramZ Z position
	 */
	public VectorFromXYZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramX,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramY,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramZ) {
		this();

		// Set the parameters
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
	}

	/**
	 * Creates a vector from XYZ module (combines values for X, Y, Z into a vector
	 * datatype)
	 *
	 * @param paramX X position
	 * @param paramY Y position
	 * @param paramZ Z position
	 */
	public VectorFromXYZ(Double paramX, Double paramY, Double paramZ) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramX),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramY),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramZ));
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
	 * X position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInX() {
		return this.inX;
	}

	/**
	 * Y position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInY() {
		return this.inY;
	}

	/**
	 * Z position
	 *
	 * @return the input port of the block
	 */
	public final InPort getInZ() {
		return this.inZ;
	}

	/**
	 * Combined vector
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * X position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: X position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: X position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Y position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Y position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Y position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Z position
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getZ() {
		return this.paramZ;
	}

	/**
	 * Sets a parameter of the block: Z position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Z position
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(Double value) {
		this.setZ(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
