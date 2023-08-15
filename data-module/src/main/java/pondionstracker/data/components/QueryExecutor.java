package pondionstracker.data.components;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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
		var index = new AtomicInteger(1);
		var stmt = conn.prepareStatement(query);
		
		List.of(parameters).forEach(p -> setObject(stmt, index, p));
		
		var rs = stmt.executeQuery();
		
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
	
	@SneakyThrows
	private void setObject(PreparedStatement stmt, AtomicInteger index, Object v) {
		stmt.setObject(index.getAndIncrement(), v);
	}
	
}
