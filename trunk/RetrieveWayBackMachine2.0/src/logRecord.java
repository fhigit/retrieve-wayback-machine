import org.apache.log4j.Logger;

public class logRecord {
	private static Logger log; 
	
	public static void setLogger(Class<?> clazz) {
		log = Logger.getLogger(clazz);
	}
	
	public static synchronized void info(String msg, Object... arg) {
			log.info(String.format(msg, arg));
	}

	public static synchronized void err(String msg, Object... arg) {
		log.error(String.format(msg, arg));
	}
}
