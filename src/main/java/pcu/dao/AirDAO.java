package pcu.dao;

import java.sql.SQLException;

import pcu.vo.Air;
import pcu.vo.Result;

/**
 * AirDao create 
 * 20190514  
 * @author arim-hyun
 *
 */
public interface AirDAO{
	String 	createAir(Air air) 	throws SQLException;
	Air 	readAir(String aId)	throws SQLException;
	void 	updateAir(Air air)	throws SQLException;
	void 	deleteAir(String aId)	throws SQLException;;
	void 	deleteAll(String did)	throws SQLException;
	Air 	getLastAir(String did)	throws SQLException;
	Result<Air>	listAirs(String startCursor, String did, int size) throws SQLException;
	

}
