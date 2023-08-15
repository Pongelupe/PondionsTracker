package pondionstracker.data.components;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class QueryExecutor {

	private final Connection conn;
	
	public <T> T queryFirst(String query, RowMapper<T> mapper, Object... parameters) {
		var allResults = queryAll(query, mapper, parameters);
		return allResults.isEmpty() ? null : allResults.get(0);
	}
	
	@SneakyThrows
	public <T> List<T> queryAll(String query, RowMapper<T> mapper, Object... parameters) {
		var result = new ArrayList<T>();
		var stmt = conn.createStatement();
		
		var rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			result.add(mapper.mapRow(rs));
		}
		
		stmt.close();
		return result;
	}
	
	@SneakyThrows
	public <T> Set<T> queryAllSet(String query, RowMapper<T> mapper) {
		var result = new HashSet<T>();
		var stmt = conn.createStatement();
		
		var rs = stmt.executeQuery(query);
		
		while(rs.next()) {
			result.add(mapper.mapRow(rs));
		}
		
		stmt.close();
		return result;
	}
	
}
