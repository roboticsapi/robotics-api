package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Communication Module to propagate value into net
 */
public class IntNetcommIn extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::IntNetcommIn";

	/**  */
	private final OutPort outLastUpdated = new OutPort("outLastUpdated");

	/**  */
	private final OutPort outValue = new OutPort("outValue");

	/** Key (unique name) of the property */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramKey = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"Key", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	/** Initial value to write to net */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint>(
			"Value", new org.roboticsapi.facet.runtime.rpi.core.types.RPIint("0"));

	public IntNetcommIn() {
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
	public IntNetcommIn(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramKey,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramValue) {
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
	public IntNetcommIn(String paramKey, Integer paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramKey),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramValue));
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getKey() {
		return this.paramKey;
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramKey.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setKey(String value) {
		this.setKey(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

	/**
	 * Initial value to write to net
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.core.types.RPIint value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(Integer value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(value));
	}

}
