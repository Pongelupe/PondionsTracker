package pondionstracker.data.components;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import lombok.SneakyThrows;
import pondionstracker.data.constants.Query;

public class QueryExecutor {

	private final DataSource dataSource;
	private final Connection conn;
	
	public QueryExecutor(DataSource dataSource) {
		this.dataSource = dataSource;
		this.conn = null;
	}
	
	public QueryExecutor(Connection conn) {
		this.dataSource = null;
		this.conn = conn;
	}
	
	public <T> T queryFirst(Query query, RowMapper<T> mapper, Map<String, Object> parameters) {
		return queryFirst(query.name(), mapper, parameters);
	}
	
	public <T> List<T> queryAll(Query query, RowMapper<T> mapper, Map<String, Object> parameters) {
		return queryAll(query.name(), mapper, parameters);
	}
	
	public <T> T queryFirst(String query, RowMapper<T> mapper, Map<String, Object> parameters) {
		var allResults = queryAll(query, mapper, parameters);
		return allResults.isEmpty() ? null : allResults.get(0);
	}

	@SneakyThrows
	public <T> List<T> queryAll(String query, RowMapper<T> mapper, Map<String, Object> parameters) {
		if (dataSource != null) {
			try (var connection = dataSource.getConnection()) {
				return executeQuery(connection, query, mapper, parameters);
			}
		}
		return executeQuery(conn, query, mapper, parameters);
	}
	
	@SneakyThrows
	private <T> List<T> executeQuery(Connection connection, String query, RowMapper<T> mapper, Map<String, Object> parameters) {
		var stmt = new PreparedStatementBuilder(connection, query, parameters).build();
		var rs = stmt.executeQuery();
		
		var result = new ArrayList<T>();
		
		while(rs.next()) {
			result.add(mapper.mapRow(rs));
		}
		
		stmt.close();
		return result;
	}
	
	
}
