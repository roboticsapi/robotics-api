package org.roboticsapi.facet.runtime.rpi.world.primitives;

import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Communication Module to propagate value into net
 */
public class FrameNetcommIn extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "World::FrameNetcommIn";

	/**  */
	private final OutPort outLastUpdated = new OutPort("outLastUpdated");

	/**  */
	private final OutPort outValue = new OutPort("outValue");

	/** Key (unique name) of the property */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramKey = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"Key", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	/** Initial value to write to net */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame> paramValue = new Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame>(
			"Value",
			new org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame("{pos:{x:0,y:0,z:0},rot:{a:0,b:-0,c:0}}"));

	public FrameNetcommIn() {
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
	public FrameNetcommIn(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramKey,
			org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame paramValue) {
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
	public FrameNetcommIn(String paramKey, String paramValue) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramKey),
				new org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame(paramValue));
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
	public final Parameter<org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame> getValue() {
		return this.paramValue;
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame value) {
		this.paramValue.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Initial value to write to net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setValue(String value) {
		this.setValue(new org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame(value));
	}

}
