package org.roboticsapi.runtime.world.primitives;

import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Communication Module to propagate value into net
 */
public class RotationNetcommIn extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::RotationNetcommIn";

	/**  */
	private final OutPort outLastUpdated = new OutPort("outLastUpdated");

	/**  */
	private final OutPort outValue = new OutPort("outValue");

	/** Key (unique name) of the property */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIstring> paramKey = new Parameter<org.roboticsapi.runtime.core.types.RPIstring>(
			"Key", new org.roboticsapi.runtime.core.types.RPIstring(""));

	/** Initial value to write to net */
	private final Parameter<org.roboticsapi.runtime.world.types.RPIRotation> paramValue = new Parameter<org.roboticsapi.runtime.world.types.RPIRotation>(
			"Value", new org.roboticsapi.runtime.world.types.RPIRotation("{a:0,b:-0,c:0}"));

	public RotationNetcommIn() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outLastUpdated);
		add(outValue);

		// Add all parameters
		add(paramKey);
		add(paramValue);
	}

	/**
	 * Creates communication Module to propagate value into net
	 *
	 * @param key   Key (unique name) of the property
	 * @param value Initial value to write to net
	 */
	public RotationNetcommIn(org.roboticsapi.runtime.core.types.RPIstring paramKey,
			org.roboticsapi.runtime.world.types.RPIRotation paramValue) {
		this();

		// Set the parameters
		setKey(paramKey);
		setValue(paramValue);
	}

	/**
	 * Creates communication Module to propagate value into net
	 *
	 * @param key   Key (unique name) of the property
	 * @param value Initial value to write to net
	 */
	public RotationNetcommIn(String paramKey, String paramValue) {
		this(new org.roboticsapi.runtime.core.types.RPIstring(paramKey),
				new org.roboticsapi.runtime.world.types.RPIRotation(paramValue));
	}

	/**
	 * 
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutLastUpdated() {
		return this.outLastUpdated;
	}

	/**
	 * 
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutValue() {
		return this.outValue;
	}

	/**
	 * Key (unique name) of the property
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIstring> getKey() {
		return this.paramKey;
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(org.roboticsapi.runtime.core.types.RPIstring value) {
		this.paramKey.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(String value) {
		this.setKey(new org.roboticsapi.runtime.core.types.RPIstring(value));
	}

	/**
	 * Initial value to write to net
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.world.types.RPIRotation> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.runtime.world.types.RPIRotation value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new org.roboticsapi.runtime.world.types.RPIRotation(value));
	}

}
