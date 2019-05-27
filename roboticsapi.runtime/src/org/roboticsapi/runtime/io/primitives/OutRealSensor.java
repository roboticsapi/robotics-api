package org.roboticsapi.runtime.io.primitives;

import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.Parameter;
import org.roboticsapi.runtime.rpi.Primitive;

/**
 * Analog output sensor
 */
public class OutRealSensor extends Primitive {
	/** Type name of the primitive */
	public static final String PRIMITIVE_TYPE = "IO::OutRealSensor";

	/** value currently set on output */
	private final OutPort outIO = new OutPort("outIO");

	/** Device name IO is connected to */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIstring> paramDeviceID = new Parameter<org.roboticsapi.runtime.core.types.RPIstring>(
			"DeviceID", new org.roboticsapi.runtime.core.types.RPIstring(""));

	/** Port to be used */
	private final Parameter<org.roboticsapi.runtime.core.types.RPIint> paramPort = new Parameter<org.roboticsapi.runtime.core.types.RPIint>(
			"Port", new org.roboticsapi.runtime.core.types.RPIint("0"));

	public OutRealSensor() {
		super(PRIMITIVE_TYPE);

		// Add all ports
		add(outIO);

		// Add all parameters
		add(paramDeviceID);
		add(paramPort);
	}

	/**
	 * Creates analog output sensor
	 *
	 * @param deviceID Device name IO is connected to
	 * @param port     Port to be used
	 */
	public OutRealSensor(org.roboticsapi.runtime.core.types.RPIstring paramDeviceID,
			org.roboticsapi.runtime.core.types.RPIint paramPort) {
		this();

		// Set the parameters
		setDeviceID(paramDeviceID);
		setPort(paramPort);
	}

	/**
	 * Creates analog output sensor
	 *
	 * @param deviceID Device name IO is connected to
	 * @param port     Port to be used
	 */
	public OutRealSensor(String paramDeviceID, Integer paramPort) {
		this(new org.roboticsapi.runtime.core.types.RPIstring(paramDeviceID),
				new org.roboticsapi.runtime.core.types.RPIint(paramPort));
	}

	/**
	 * value currently set on output
	 * 
	 * @return the output port of the block
	 */
	public final OutPort getOutIO() {
		return this.outIO;
	}

	/**
	 * Device name IO is connected to
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIstring> getDeviceID() {
		return this.paramDeviceID;
	}

	/**
	 * Sets a parameter of the block: Device name IO is connected to
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDeviceID(org.roboticsapi.runtime.core.types.RPIstring value) {
		this.paramDeviceID.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Device name IO is connected to
	 * 
	 * @param value new value of the parameter
	 */
	public final void setDeviceID(String value) {
		this.setDeviceID(new org.roboticsapi.runtime.core.types.RPIstring(value));
	}

	/**
	 * Port to be used
	 * 
	 * @return the parameter of the block
	 */
	public final Parameter<org.roboticsapi.runtime.core.types.RPIint> getPort() {
		return this.paramPort;
	}

	/**
	 * Sets a parameter of the block: Port to be used
	 * 
	 * @param value new value of the parameter
	 */
	public final void setPort(org.roboticsapi.runtime.core.types.RPIint value) {
		this.paramPort.setValue(value);
	}

	/**
	 * Sets a parameter of the block: Port to be used
	 * 
	 * @param value new value of the parameter
	 */
	public final void setPort(Integer value) {
		this.setPort(new org.roboticsapi.runtime.core.types.RPIint(value));
	}

}
