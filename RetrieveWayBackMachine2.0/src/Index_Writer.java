import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Index_Writer {
	private static BufferedWriter bw;
	
	public static void setTarget(String pathname) {
		try {
			bw = new BufferedWriter(new FileWriter(new File(pathname), true));
		} catch (IOException e) {
			logRecord.err("Index writer error open file.");
			e.printStackTrace();
		}
	}
	
	public static synchronized void write(String s) {
		try {
			bw.write(s + "\n");
			bw.flush();
		} catch (IOException e) {
			logRecord.err("Index writer error write file.");
			e.printStackTrace();
		}
	}
	
	public static void close() {
		try {
			bw.close();
		} catch (IOException e) {
			logRecord.err("Index writer error close file.");
			e.printStackTrace();
		}
	}
}
