package org.roboticsapi.runtime.fieldbus.canopen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.runtime.AbstractSoftRobotDeviceDriver;
import org.roboticsapi.runtime.driver.GenericLoadable;
import org.roboticsapi.runtime.fieldbus.can.CanFieldbusDriver;

public class CanOpenFieldbusDriver extends AbstractSoftRobotDeviceDriver implements GenericLoadable {

	private CanFieldbusDriver canDriver;

	protected CanOpenFieldbusDriver() {
		super();
	}

	public final CanFieldbusDriver getCanDriver() {
		return canDriver;
	}

	@ConfigurationProperty(Optional = false)
	public final void setCanDriver(CanFieldbusDriver canDriver) {
		immutableWhenInitialized();
		this.canDriver = canDriver;
	}

	@Override
	public String getDeviceType() {
		return "canopenmaster";
	}

	@Override
	public final boolean build() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("candevice", getCanDriver().getDeviceName());

		return loadDeviceDriver(parameters);
	}

	@Override
	public final void delete() {
		deleteDeviceDriver();
	}

	@Override
	protected boolean checkDeviceInterfaces(List<String> interfaces) {
		return true; // TODO: No interfaces available until now
	}
	
	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		checkNotNullAndInitialized("canDriver", canDriver);
	}

}
