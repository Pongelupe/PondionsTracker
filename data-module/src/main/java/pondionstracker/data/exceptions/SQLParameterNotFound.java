package pondionstracker.data.exceptions;

import lombok.Getter;
import pondionstracker.data.constants.Query.Parameter;

public class SQLParameterNotFound extends RuntimeException {

	private static final long serialVersionUID = 7396726585875850149L;
	
	@Getter
	private final Parameter parameter;

	public SQLParameterNotFound(Parameter parameter) {
		super("Parameter %s not found!".formatted(parameter));
		this.parameter = parameter;
	}

}
