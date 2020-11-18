package pcu.dao;

import java.sql.SQLException;

import pcu.vo.Device;
import pcu.vo.Result;

/**
 * Device IMPL
 * @author hyun
 *
 */
public interface DeviceDAO {
	boolean isExist(String did) throws SQLException;
	String createDevice(Device 	device)  throws SQLException;
	Device readDevice(String 	did)  throws SQLException;
	void updateDevice(Device device)  throws SQLException;
	void deleteDevice(String 	did)  throws SQLException;
	Result<Device> listDevices(String startCursor)  throws SQLException;	
}
