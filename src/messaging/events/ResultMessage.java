package messaging.events;

import java.sql.ResultSet;

import messaging.Message;

public class ResultMessage implements Message {
	
	private ResultSet resultSet;
	private boolean needsCalculation;
	
	public ResultMessage(ResultSet resultSet, boolean needsCalculation) {
		
		if (resultSet == null)
			throw new IllegalArgumentException("Invalid resultSet provided");
		
		this.resultSet = resultSet;
		this.needsCalculation = needsCalculation;
	}
	
	public ResultSet getResultSet() {
		return this.resultSet;
	}
	
	public boolean needsCalculation() {
		return this.needsCalculation;
	}
}
