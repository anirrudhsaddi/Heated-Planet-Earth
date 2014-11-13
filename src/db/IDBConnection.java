package db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IDBConnection {
	
	public void close();

	public PreparedStatement createPreparedStatement(String queryName, String query) throws SQLException;
	
	public PreparedStatement getPreparedStatement(String queryName);
	
	public ResultSet query(PreparedStatement stmt) throws SQLException;
	
	public ResultSet query(String query) throws SQLException;
}
