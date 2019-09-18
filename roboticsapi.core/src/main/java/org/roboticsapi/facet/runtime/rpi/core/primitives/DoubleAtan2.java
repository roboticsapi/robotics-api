package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Calculates the arc tangent of y/x
 */
public class DoubleAtan2 extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleAtan2";

	/** x */
	private final InPort inX = new InPort("inX");

	/** y */
	private final InPort inY = new InPort("inY");

	/** Result (atan2(y,x)) */
	private final OutPort outValue = new OutPort("outValue");

	/** Y */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Y", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("1"));

	/** X */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"X", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("1"));

	public DoubleAtan2() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inX);
		add(inY);
		add(outValue);

		// Add all parameters
		add(paramY);
		add(paramX);
	}

	/**
	 * Creates calculates the arc tangent of y/x
	 *
	 * @param paramY Y
	 * @param paramX X
	 */
	public DoubleAtan2(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramY,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramX) {
		this();

		// Set the parameters
		setY(paramY);
		setX(paramX);
	}

	/**
	 * Creates calculates the arc tangent of y/x
	 *
	 * @param paramY Y
	 * @param paramX X
	 */
	public DoubleAtan2(Double paramY, Double paramX) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramY),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramX));
	}

	/**
	 * x
	 *
	 * @return the input port of the block
	 */
	public final InPort getInX() {
		return this.inX;
	}

	/**
	 * y
	 *
	 * @return the input port of the block
	 */
	public final InPort getInY() {
		return this.inY;
	}

	/**
	 * Result (atan2(y,x))
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Y
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Y
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Y
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * X
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: X
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: X
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
