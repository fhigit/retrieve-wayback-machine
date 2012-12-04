import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Crawler_Main {
	static class data {
		public data(int r,String u) {
			this.url = u;
			this.record = r;
		}
		public String url;
		public int record;
	}
	
	private static List<String> urls = new LinkedList<String>();
	
	public static void readInputFile(String path) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
		} catch (FileNotFoundException e) {
			logRecord.err("Cannot Open File...");
			return;
		}
		String temp;
		try {
			while((temp = br.readLine()) != null) {
				urls.add(temp);
			}
		} catch (IOException e) {
			logRecord.err("I/O File!");
			return;
		}
	}
	
	public static void main(String[] args) throws SecurityException, IOException, InterruptedException, ExecutionException {
		// ======================= Default Value =============================
		Configuration.nThread = Runtime.getRuntime().availableProcessors();
		Configuration.inputPath = "";
		Configuration.outputDir = "";
		Configuration.indexFilePath = "";
		Configuration.startRecord = 1;
		Configuration.errorCorrection = 3;
		// ===================================================================
		
		// ======================= Parsing Arguments ===========================
		String _temp;
		Configuration.inputPath = (_temp = System.getProperty("INPUT_FILE_PATH")) != null ? _temp : null;
		Configuration.outputDir = (_temp = System.getProperty("OUTPUT_DIRECTORY")) != null ? _temp : null;
		Configuration.indexFilePath = (_temp = System.getProperty("INDEX_FILE_PATH")) != null ? _temp : null;
		Configuration.startRecord = (_temp = System.getProperty("START_RECORD_INDEX")) != null ? Integer.parseInt(_temp) : -1;
		Configuration.nThread = (_temp = System.getProperty("NUMBER_OF_THREAD")) != null ? Integer.parseInt(_temp) : -1;
		Configuration.errorCorrection = (_temp = System.getProperty("ERROR_CORRECTION")) != null ? Integer.parseInt(_temp) : -1;
		// =====================================================================

		System.out.println(Configuration.inputPath);
		System.out.println(Configuration.outputDir);
		System.out.println(Configuration.indexFilePath);
		System.out.println(Configuration.startRecord);
		System.out.println(Configuration.nThread);
		System.out.println(Configuration.errorCorrection);
		
		
		logRecord.setLogger(Crawler_Main.class);
		Index_Writer.setTarget(Configuration.indexFilePath);
		
		logRecord.info("START.");
		logRecord.info("------------------------------------------------------");
		logRecord.info("Reading Input File at %s", Configuration.inputPath);
		logRecord.info("Output Directory at %s", Configuration.outputDir);
		logRecord.info("Start Record at %d", Configuration.startRecord);
		logRecord.info("Number of used thread: %d", Configuration.nThread);
		logRecord.info("------------------------------------------------------");
		readInputFile(Configuration.inputPath);
		logRecord.info("Total Record: %d",urls.size());

		ExecutorService pool = Executors.newFixedThreadPool(Configuration.nThread);
		for(int idx=Configuration.startRecord-1;idx<urls.size();idx++) {
			Runnable crawler = new Crawler(new data(idx+1, urls.get(idx)));
			pool.execute(crawler);
		}
		pool.shutdown();
		while(!pool.isTerminated());
		
		logRecord.info("------------------------------------------------------");
		logRecord.info("END.");
		
		Index_Writer.close();
	}
}