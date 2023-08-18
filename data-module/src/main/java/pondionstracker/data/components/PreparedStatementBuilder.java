package pondionstracker.data.components;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import lombok.SneakyThrows;
import pondionstracker.data.constants.Query;
import pondionstracker.data.constants.Query.Parameter;
import pondionstracker.data.exceptions.QueryNotFoundException;
import pondionstracker.data.exceptions.SQLParameterNotFound;

public class PreparedStatementBuilder {
	
	private static final Pattern REGEX_PARAMETERS = Pattern.compile("\\:(?!.*geometry)\\w+");

	private static final String SQL_EXT = ".sql";
	private static final String CLASSPATH_SQL_FOLDER = "/sql/"; //NOSONAR
	
	private final Connection conn;
	private final Query query; 
	private final Map<Parameter, Object> originalParameters;
	
	private List<Object> parameters;
	
	public PreparedStatementBuilder(Connection conn, Query query, Map<Parameter, Object> parameters) {
		this.conn = conn;
		this.query = query;
		this.originalParameters = parameters;
		
		this.parameters = new ArrayList<>();
	}
	
	@SneakyThrows
	public PreparedStatement build() {
		var stmt = conn.prepareStatement(prepareEffectiveQuery());
		
		var index = new AtomicInteger(1);
		parameters.forEach(p -> setObject(stmt, index, p));
		
		return stmt;
	}
	
	private String prepareEffectiveQuery() {
		StringBuilder sb = new StringBuilder();
		var matcher = REGEX_PARAMETERS.matcher(loadQuery(query.name()));
		
		while (matcher.find()) {
			var parameter = Parameter.valueOf(matcher.group().substring(1));
			
			if (originalParameters.containsKey(parameter)) {
				processParameterValue(originalParameters.get(parameter), sb, matcher);
			} else {
				throw new SQLParameterNotFound(parameter);
			}
			
		}
		
		matcher.appendTail(sb);
		
		return sb.toString();
	}
	
	private void processParameterValue(Object v, StringBuilder sb, Matcher matcher) {
		var replacement = "?";
		if (v != null && (v instanceof Collection<?> || v.getClass().isArray())) {
			var collection = v.getClass().isArray() ? createCollectionFromArr(v) : (Collection<?>) v;
			var parameterList = new StringBuilder();
			
			collection.forEach(i -> {
				parameters.add(i);
				parameterList.append(", ?");
			});
			
			replacement = collection.isEmpty() ? "" : parameterList.toString()
					.substring(1)
					.trim();
		} else {
			parameters.add(v);
		}
		
		matcher.appendReplacement(sb, replacement);
		
	}

	private Collection<?> createCollectionFromArr(Object arr) {
		var collection = new ArrayList<>();
		
		int length = Array.getLength(arr);
		for (int i = 0; i < length; i++) {
			collection.add(Array.get(arr, i));
		}
		
		return collection;
	}

	private String loadQuery(String filename) {
		var filePath = CLASSPATH_SQL_FOLDER
				.concat(filename)
				.concat(SQL_EXT);
		
		try (var is = this.getClass().getResourceAsStream(filePath)) {
			return IOUtils.toString(is, Charset.defaultCharset());
		} catch (Exception e) {
			throw new QueryNotFoundException(filename, e);
		}
	}
	
	@SneakyThrows
	private void setObject(PreparedStatement stmt, AtomicInteger index, Object v) {
		stmt.setObject(index.getAndIncrement(), v);
	}

}
