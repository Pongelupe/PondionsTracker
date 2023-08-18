package pondionstracker.data.exceptions;

import lombok.Getter;

public class QueryNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -8935719771439606075L;
	
	@Getter
	private final String queryFileName;
	
	public QueryNotFoundException(String queryFileName, Exception e) {
		super("SQL file %s.sql not found!".formatted(queryFileName), e);
		this.queryFileName = queryFileName;
	}

}
