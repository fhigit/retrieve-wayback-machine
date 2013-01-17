import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.archive.io.arc.ARCWriter;

public class ParallelArcWriter {
	private static AtomicInteger aInt = new AtomicInteger();
	
	public static String write(List<String[]> pages) {
		String filename;
		List<File> dirs = new LinkedList<File>();
		dirs.add(new File(Configuration.outputDir));
		ByteArrayInputStream bis = null;
		ARCWriter arc_writer = new ARCWriter(aInt, dirs, "test", false, -1);
		try {
			for(String[] page : pages) {
				String url = page[0];
				String html = page[1];
				byte[] content = html.getBytes();
				bis = new ByteArrayInputStream(content);
				arc_writer.write(url, "text/html", "127.0.0.1", System.currentTimeMillis(), content.length, bis);
				
			}
		} catch(Exception e) {	
			e.printStackTrace();
		}
		try {
			bis.close();
			arc_writer.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		while(arc_writer.getFile().getName().endsWith(".open"))
			;
		filename = arc_writer.getFile().getName();
		return filename;
	}
}