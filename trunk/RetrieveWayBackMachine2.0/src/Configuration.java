import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Configuration {
	/* GLOBAL VARIABLE */
	public static int nThread;
	public static String inputPath;
	public static String outputDir;
	public static String indexFilePath;
	public static int startRecord;
	public static int errorCorrection;
	
	private static enum options {
		INPUT_FILE_PATH,
		OUTPUT_DIRECTORY,
		INDEX_FILE_PATH,
		START_RECORD_INDEX,
		NUMBER_OF_THREAD,
		ERROR_CORRECTION
	}
	
	public static void ReadConfig(String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		} catch (FileNotFoundException e) {
			logRecord.err("Cannot Open Configuration File...");
			return;
		}
		String temp;
		try {
			while((temp = br.readLine()) != null) {
				String[] temp2 = temp.split("=");
				if(temp2.length != 2)
					continue;
				switch (options.valueOf(temp2[0])) {
				case INPUT_FILE_PATH:
					inputPath = (temp2[1].length() > 0) ? temp2[1] : inputPath;
					break;
				case OUTPUT_DIRECTORY:
					outputDir = (temp2[1].length() > 0) ? temp2[1] : outputDir;
					break;
				case INDEX_FILE_PATH:
					indexFilePath = (temp2[1].length() > 0) ? temp2[1] : indexFilePath;
					break;
				case START_RECORD_INDEX:
					startRecord = (temp2[1].length() > 0) ? Integer.parseInt(temp2[1]) : startRecord;
					break;
				case NUMBER_OF_THREAD:
					nThread = (temp2[1].length() > 0) ? Integer.parseInt(temp2[1]) : nThread;
					break;
				case ERROR_CORRECTION:
					errorCorrection = (temp2[1].length() > 0) ? Integer.parseInt(temp2[1]) : errorCorrection;
				}
			}
		} catch (IOException e) {
			logRecord.err("I/O Problem!");
			return;
		}
	}
}
