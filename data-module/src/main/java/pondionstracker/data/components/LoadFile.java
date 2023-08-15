package pondionstracker.data.components;

import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import pondionstracker.data.exceptions.QueryNotFoundException;

public class LoadFile {

	private static final String CLASSPATH_SQL_FOLDER = "sql/";
	
	public String loadQuery(String filename) {
		var filePath = CLASSPATH_SQL_FOLDER.concat(filename);
		
		try (InputStream is = this.getClass().getResourceAsStream(filePath)) {
			return IOUtils.toString(is, Charset.defaultCharset());
		} catch (Exception e) {
			throw new QueryNotFoundException(filename, e);
		}
	}
	
}
