package useful;

import java.net.URL;

public class IO {

	public static URL getResourceURL(String path)
	{
		return Thread.currentThread().getContextClassLoader().getResource(path);
	}
	
}
