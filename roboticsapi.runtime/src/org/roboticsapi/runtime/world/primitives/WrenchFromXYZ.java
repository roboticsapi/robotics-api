package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * A wrench from XYZ module (combines values for X, Y, Z forces and A, B, C
 * torques into a wrench datatype)
 */
public class WrenchFromXYZ extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::WrenchFromXYZ";

	/** A torque */
	private final InPort inA = new InPort("inA");

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** B torque */
	private final InPort inB = new InPort("inB");

	/** C torque */
	private final InPort inC = new InPort("inC");

	/** X force */
	private final InPort inX = new InPort("inX");

	/** Y force */
	private final InPort inY = new InPort("inY");

	/** Z force */
	private final InPort inZ = new InPort("inZ");

	/** Combined wrench */
	private final OutPort outValue = new OutPort("outValue");

	/** X force */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramX = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"X", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Y force */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramY = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Y", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** Z force */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramZ = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"Z", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** A torque */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramA = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"A", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** B torque */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramB = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"B", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	/** C torque */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> paramC = new Parameter<org.roboticsapi.runtime.core.types.RPIdouble>(
			"C", new org.roboticsapi.runtime.core.types.RPIdouble("0"));

	public WrenchFromXYZ() {
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
	 * Creates a wrench from XYZ module (combines values for X, Y, Z forces and A,
	 * B, C torques into a wrench datatype)
	 *
	 * @param paramX X force
	 * @param paramY Y force
	 * @param paramZ Z force
	 * @param paramA A torque
	 * @param paramB B torque
	 * @param paramC C torque
	 */
	public WrenchFromXYZ(org.roboticsapi.runtime.core.types.RPIdouble paramX,
			org.roboticsapi.runtime.core.types.RPIdouble paramY, org.roboticsapi.runtime.core.types.RPIdouble paramZ,
			org.roboticsapi.runtime.core.types.RPIdouble paramA, org.roboticsapi.runtime.core.types.RPIdouble paramB,
			org.roboticsapi.runtime.core.types.RPIdouble paramC) {
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
	 * Creates a wrench from XYZ module (combines values for X, Y, Z forces and A,
	 * B, C torques into a wrench datatype)
	 *
	 * @param paramX X force
	 * @param paramY Y force
	 * @param paramZ Z force
	 * @param paramA A torque
	 * @param paramB B torque
	 * @param paramC C torque
	 */
	public WrenchFromXYZ(Double paramX, Double paramY, Double paramZ, Double paramA, Double paramB, Double paramC) {
		this(new org.roboticsapi.runtime.core.types.RPIdouble(paramX), new org.roboticsapi.runtime.core.types.RPIdouble(paramY),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramZ),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramA),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramB),
				new org.roboticsapi.runtime.core.types.RPIdouble(paramC));
	}

	/**
	 * A torque
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
	 * B torque
	 *
	 * @return the input port of the block
	 */
	public final InPort getInB() {
		return this.inB;
	}

	/**
	 * C torque
	 *
	 * @return the input port of the block
	 */
	public final InPort getInC() {
		return this.inC;
	}

	/**
	 * X force
	 *
	 * @return the input port of the block
	 */
	public final InPort getInX() {
		return this.inX;
	}

	/**
	 * Y force
	 *
	 * @return the input port of the block
	 */
	public final InPort getInY() {
		return this.inY;
	}

	/**
	 * Z force
	 *
	 * @return the input port of the block
	 */
	public final InPort getInZ() {
		return this.inZ;
	}

	/**
	 * Combined wrench
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * X force
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getX() {
		return this.paramX;
	}

	/**
	 * Sets a parameter of the block: X force
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramX.setValue(value);
	}

	/**
	 * Sets a parameter of the block: X force
	 * 
	 * @param value new value of the parameter
	 */
	public final void setX(Double value) {
		this.setX(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Y force
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getY() {
		return this.paramY;
	}

	/**
	 * Sets a parameter of the block: Y force
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramY.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Y force
	 * 
	 * @param value new value of the parameter
	 */
	public final void setY(Double value) {
		this.setY(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * Z force
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getZ() {
		return this.paramZ;
	}

	/**
	 * Sets a parameter of the block: Z force
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramZ.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Z force
	 * 
	 * @param value new value of the parameter
	 */
	public final void setZ(Double value) {
		this.setZ(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * A torque
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getA() {
		return this.paramA;
	}

	/**
	 * Sets a parameter of the block: A torque
	 * 
	 * @param value new value of the parameter
	 */
	public final void setA(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramA.setValue(value);
	}

	/**
	 * Sets a parameter of the block: A torque
	 * 
	 * @param value new value of the parameter
	 */
	public final void setA(Double value) {
		this.setA(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * B torque
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getB() {
		return this.paramB;
	}

	/**
	 * Sets a parameter of the block: B torque
	 * 
	 * @param value new value of the parameter
	 */
	public final void setB(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramB.setValue(value);
	}

	/**
	 * Sets a parameter of the block: B torque
	 * 
	 * @param value new value of the parameter
	 */
	public final void setB(Double value) {
		this.setB(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

	/**
	 * C torque
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIdouble> getC() {
		return this.paramC;
	}

	/**
	 * Sets a parameter of the block: C torque
	 * 
	 * @param value new value of the parameter
	 */
	public final void setC(org.roboticsapi.runtime.core.types.RPIdouble value) {
		this.paramC.setValue(value);
	}

	/**
	 * Sets a parameter of the block: C torque
	 * 
	 * @param value new value of the parameter
	 */
	public final void setC(Double value) {
		this.setC(new org.roboticsapi.runtime.core.types.RPIdouble(value));
	}

}
