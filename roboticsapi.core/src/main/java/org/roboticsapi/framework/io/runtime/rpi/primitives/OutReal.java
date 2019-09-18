package org.roboticsapi.framework.io.runtime.rpi.primitives;

import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.Parameter;
import org.roboticsapi.facet.runtime.rpi.Primitive;

/**
 * Analog output
 */
public class OutReal extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "IO::OutReal";

	/** Activation port */
	private final InPort inActive = new InPort("inActive");

	/** Value to set on output */
	private final InPort inIO = new InPort("inIO");

	/** Device name IO is connected to */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> paramDeviceID = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring>(
			"DeviceID", new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(""));

	/** Port to be used */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> paramPort = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint>(
			"Port", new org.roboticsapi.facet.runtime.rpi.core.types.RPIint("0"));

	/** Value to set to output when inIO is not connected */
	private final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> paramIO = new Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble>(
			"IO", new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble("0"));

	public OutReal() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(inActive);
		add(inIO);

		// Add all parameters
		add(paramDeviceID);
		add(paramPort);
		add(paramIO);
	}

	/**
	 * Creates analog output
	 *
	 * @param deviceID Device name IO is connected to
	 * @param port     Port to be used
	 */
	public OutReal(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramDeviceID,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramPort) {
		this();

		// Set the parameters
		setDeviceID(paramDeviceID);
		setPort(paramPort);
	}

	/**
	 * Creates analog output
	 *
	 * @param deviceID Device name IO is connected to
	 * @param port     Port to be used
	 */
	public OutReal(String paramDeviceID, Integer paramPort) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramDeviceID),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramPort));
	}

	/**
	 * Creates analog output
	 *
	 * @param paramDeviceID Device name IO is connected to
	 * @param paramPort     Port to be used
	 * @param paramIO       Value to set to output when inIO is not connected
	 */
	public OutReal(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring paramDeviceID,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIint paramPort,
			org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble paramIO) {
		this();

		// Set the parameters
		setDeviceID(paramDeviceID);
		setPort(paramPort);
		setIO(paramIO);
	}

	/**
	 * Creates analog output
	 *
	 * @param paramDeviceID Device name IO is connected to
	 * @param paramPort     Port to be used
	 * @param paramIO       Value to set to output when inIO is not connected
	 */
	public OutReal(String paramDeviceID, Integer paramPort, Double paramIO) {
		this(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(paramDeviceID),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(paramPort),
				new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(paramIO));
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
	 * Value to set on output
	 *
	 * @return the input port of the block
	 */
	public final InPort getInIO() {
		return this.inIO;
	}

	/**
	 * Device name IO is connected to
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIstring> getDeviceID() {
		return this.paramDeviceID;
	}

	/**
	 * Sets a parameter of the block: Device name IO is connected to
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDeviceID(org.roboticsapi.facet.runtime.rpi.core.types.RPIstring value) {
		this.paramDeviceID.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Device name IO is connected to
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDeviceID(String value) {
		this.setDeviceID(new org.roboticsapi.facet.runtime.rpi.core.types.RPIstring(value));
	}

	/**
	 * Port to be used
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIint> getPort() {
		return this.paramPort;
	}

	/**
	 * Sets a parameter of the block: Port to be used
	 * 
	 * @param value new value of the parameter
	 */
	public final void setPort(org.roboticsapi.facet.runtime.rpi.core.types.RPIint value) {
		this.paramPort.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Port to be used
	 * 
	 * @param value new value of the parameter
	 */
	public final void setPort(Integer value) {
		this.setPort(new org.roboticsapi.facet.runtime.rpi.core.types.RPIint(value));
	}

	/**
	 * Value to set to output when inIO is not connected
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble> getIO() {
		return this.paramIO;
	}

	/**
	 * Sets a parameter of the block: Value to set to output when inIO is not
	 * connected
	 * 
	 * @param value new value of the parameter
	 */
	public final void setIO(org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble value) {
		this.paramIO.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Value to set to output when inIO is not
	 * connected
	 * 
	 * @param value new value of the parameter
	 */
	public final void setIO(Double value) {
		this.setIO(new org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble(value));
	}

}
