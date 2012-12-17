import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.archive.io.arc.ARCWriter;

public class ParallelArcWriter {
	public synchronized static String write(List<String[]> pages) {
		String filename;
		ByteArrayInputStream bis = null;
		long start_time = new Date().getTime();
		List<File> dirs = new LinkedList<File>();
		dirs.add(new File(Configuration.outputDir));
		ARCWriter arc_writer = new ARCWriter(new AtomicInteger(), dirs, "test", false, -1);
		try {
			for(String[] page : pages) {
				String url = page[0];
				String html = page[1];
				byte[] content = html.getBytes();
				bis = new ByteArrayInputStream(content);
				arc_writer.write(url, "text/html", "127.0.0.1", System.currentTimeMillis(), content.length, bis);
			}
		} catch(Exception e) {	
			
		}
		finally {
			try {
				arc_writer.close();
				if(bis != null)
					bis.close();
			} catch(Exception e) {
				logRecord.err("Can't close arc file.");
				e.printStackTrace();
			}
		}
		while(arc_writer.getFile().getName().endsWith(".open"));
		filename = arc_writer.getFile().getName();
		long elapsed = start_time - new Date().getTime();
		if(elapsed < 1000) {
			try {
				Thread.sleep(1000-elapsed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return filename;
	}
}
