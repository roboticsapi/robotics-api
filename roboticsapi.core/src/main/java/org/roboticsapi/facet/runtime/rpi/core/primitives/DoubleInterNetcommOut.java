package org.roboticsapi.facet.runtime.rpi.core.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Communication module to write value from net to remote net
 */
public class DoubleInterNetcommOut extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "Core::DoubleInterNetcommOut";

	/**  */
	private final InPort inValue = new InPort("inValue");

	/** Name of the remote RCC the communicate with */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramRemoteRCC = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"RemoteRCC", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	/** Name of the remote net */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramRemoteNet = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"RemoteNet", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	/** Key (unique name) of the remote property */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramRemoteKey = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"RemoteKey", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	public DoubleInterNetcommOut() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inValue);

		// Add all parameters
		add(paramRemoteRCC);
		add(paramRemoteNet);
		add(paramRemoteKey);
	}

	/**
	 * Creates communication module to write value from net to remote net
	 *
	 * @param remoteRCC Name of the remote RCC the communicate with
	 * @param remoteNet Name of the remote net
	 * @param remoteKey Key (unique name) of the remote property
	 */
	public DoubleInterNetcommOut(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramRemoteRCC,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramRemoteNet,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramRemoteKey) {
		this();

		// Set the parameters
		setRemoteRCC(paramRemoteRCC);
		setRemoteNet(paramRemoteNet);
		setRemoteKey(paramRemoteKey);
	}

	/**
	 * Creates communication module to write value from net to remote net
	 *
	 * @param remoteRCC Name of the remote RCC the communicate with
	 * @param remoteNet Name of the remote net
	 * @param remoteKey Key (unique name) of the remote property
	 */
	public DoubleInterNetcommOut(String paramRemoteRCC, String paramRemoteNet, String paramRemoteKey) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramRemoteRCC),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramRemoteNet),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramRemoteKey));
	}

	/**
	 * 
	 *
	 * @return the input port of the block
	 */
	public final InPort getInValue() {
		return this.inValue;
	}

	/**
	 * Name of the remote RCC the communicate with
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getRemoteRCC() {
		return this.paramRemoteRCC;
	}

	/**
	 * Sets a parameter of the block: Name of the remote RCC the communicate with
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRemoteRCC(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramRemoteRCC.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Name of the remote RCC the communicate with
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRemoteRCC(String value) {
		this.setRemoteRCC(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

	/**
	 * Name of the remote net
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getRemoteNet() {
		return this.paramRemoteNet;
	}

	/**
	 * Sets a parameter of the block: Name of the remote net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRemoteNet(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramRemoteNet.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Name of the remote net
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRemoteNet(String value) {
		this.setRemoteNet(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

	/**
	 * Key (unique name) of the remote property
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getRemoteKey() {
		return this.paramRemoteKey;
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the remote property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRemoteKey(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramRemoteKey.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Key (unique name) of the remote property
	 * 
	 * @param value new value of the parameter
	 */
	public final void setRemoteKey(String value) {
		this.setRemoteKey(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

}
