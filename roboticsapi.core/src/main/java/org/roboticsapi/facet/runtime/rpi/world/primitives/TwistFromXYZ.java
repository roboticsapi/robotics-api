package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * A twist from XYZ module (combines values for X, Y, Z velocities and A, B, C
 * velocities into a twist datatype) [deprecated]
 */
@Deprecated
public class TwistFromXYZ extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::TwistFromXYZ";

	/** A velocity */
	private final InPort inA = new InPort("inA");

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** B velocity */
	private final InPort inB = new InPort("inB");

	/** C velocity */
	private final InPort inC = new InPort("inC");

	/** X velocity */
	private final InPort inX = new InPort("inX");

	/** Y velocity */
	private final InPort inY = new InPort("inY");

	/** Z velocity */
	private final InPort inZ = new InPort("inZ");

	/** Combined twist */
	private final OutPort outValue = new OutPort("outValue");

	/** X velocity */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"X", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Y velocity */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Y", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** Z velocity */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramZ = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"Z", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** A velocity */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramA = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"A", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** B velocity */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramB = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"B", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	/** C velocity */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramC = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"C", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public TwistFromXYZ() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inA);
		add(inActive);
		add(inB);
		add(inC);
		add(inX);
		add(inY);
		add(inZ);
		add(outValue);

		// Add all parameters
		add(paramX);
		add(paramY);
		add(paramZ);
		add(paramA);
		add(paramB);
		add(paramC);
	}

	/**
	 * Creates a twist from XYZ module (combines values for X, Y, Z velocities and
	 * A, B, C velocities into a twist datatype) [deprecated]
	 *
	 * @param paramX X velocity
	 * @param paramY Y velocity
	 * @param paramZ Z velocity
	 * @param paramA A velocity
	 * @param paramB B velocity
	 * @param paramC C velocity
	 */
	public TwistFromXYZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramX,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramY,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramZ,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramA,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramB,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramC) {
		this();

		// Set the parameters
		setX(paramX);
		setY(paramY);
		setZ(paramZ);
		setA(paramA);
		setB(paramB);
		setC(paramC);
	}

	/**
	 * Creates a twist from XYZ module (combines values for X, Y, Z velocities and
	 * A, B, C velocities into a twist datatype) [deprecated]
	 *
	 * @param paramX X velocity
	 * @param paramY Y velocity
	 * @param paramZ Z velocity
	 * @param paramA A velocity
	 * @param paramB B velocity
	 * @param paramC C velocity
	 */
	public TwistFromXYZ(Double paramX, Double paramY, Double paramZ, Double paramA, Double paramB, Double paramC) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramX),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramY),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramZ),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramA),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramB),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramC));
	}

	/**
	 * A velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInA() {
		return this.inA;
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
	 * B velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInB() {
		return this.inB;
	}

	/**
	 * C velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInC() {
		return this.inC;
	}

	/**
	 * X velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInX() {
		return this.inX;
	}

	/**
	 * Y velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInY() {
		return this.inY;
	}

	/**
	 * Z velocity
	 *
	 * @return the input port of the block
	 */
	public final InPort getInZ() {
		return this.inZ;
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: X velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: X velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Y velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Y velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Y velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * Z velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getZ() {
		return this.paramZ;
	}

	/**
	 * Sets a parameter of the block: Z velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Z velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(Double value) {
		this.setZ(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * A velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getA() {
		return this.paramA;
	}

	/**
	 * Sets a parameter of the block: A velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setA(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramA.setValue(value);
	}

	/**
	 * Sets a parameter of the block: A velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setA(Double value) {
		this.setA(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * B velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getB() {
		return this.paramB;
	}

	/**
	 * Sets a parameter of the block: B velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setB(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramB.setValue(value);
	}

	/**
	 * Sets a parameter of the block: B velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setB(Double value) {
		this.setB(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

	/**
	 * C velocity
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getC() {
		return this.paramC;
	}

	/**
	 * Sets a parameter of the block: C velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setC(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramC.setValue(value);
	}

	/**
	 * Sets a parameter of the block: C velocity
	 * 
	 * @param value new value of the parameter
	 */
	public final void setC(Double value) {
		this.setC(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
