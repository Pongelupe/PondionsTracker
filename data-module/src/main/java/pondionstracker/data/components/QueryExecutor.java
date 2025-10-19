package pondionstracker.data.components;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import pondionstracker.data.constants.Query;

@RequiredArgsConstructor
public class QueryExecutor {

	private final Connection conn;
	
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
		var stmt = new PreparedStatementBuilder(conn, query, parameters).build();
		var rs = stmt.executeQuery();
		
		var result = new ArrayList<T>();
		
		while(rs.next()) {
			result.add(mapper.mapRow(rs));
		}
		
		stmt.close();
		return result;
	}
	
	
}
