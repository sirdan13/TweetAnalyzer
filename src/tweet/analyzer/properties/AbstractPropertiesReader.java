package tweet.analyzer.properties;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;


public abstract class AbstractPropertiesReader {
	
	protected Properties properties;
	protected String path;
	
	public AbstractPropertiesReader(String path) {
		this.path = path;
		properties = new Properties();
	}
	
	public void loadProperties() throws Exception{
		try {			
			properties.load(new FileReader(new File(path)));
		} catch (FileNotFoundException e) {
			throw new Exception("File "+path+" not found");
		} catch (IOException e) {
			throw new Exception("Impossible to load file "+path);
		}
	}
	
	public Properties getProperties(){
		return properties;
	}
	

}

