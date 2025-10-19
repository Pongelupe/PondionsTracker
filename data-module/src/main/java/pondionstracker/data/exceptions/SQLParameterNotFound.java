package pondionstracker.data.exceptions;

import lombok.Getter;

public class SQLParameterNotFound extends RuntimeException {

	private static final long serialVersionUID = 7396726585875850149L;
	
	@Getter
	private final String parameter;

	public SQLParameterNotFound(String parameter) {
		super("Parameter %s not found!".formatted(parameter));
		this.parameter = parameter;
	}

}
