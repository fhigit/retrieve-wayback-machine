import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Downloader {
	List<String> links;
	private String filename = null;
	private int numberOfRecord = 0;
	
	private String fetch(String url) {
		//sleep(1000);
		
		int s = 0;
		String content = null;
		InputStream is = null;
		BufferedReader br = null;
		HttpURLConnection uc = null;
		while(s < Configuration.errorCorrection) {
			try {
				int timeout = 3000; // milliseconds
				URL u = new URL(url);
				uc = (HttpURLConnection) u.openConnection();
				uc.setRequestProperty("User-agent", "Pramote T., MIKE lab, Kasetsart University, boatblaster@gmail.com");
				uc.setConnectTimeout(timeout);
				uc.setReadTimeout(timeout);
				if(uc.getResponseCode() != 200) // Response Code: HTTP/1.1 200 OK
					return null;
				if(!uc.getContentType().startsWith("text/html")) // content-type: text/html; charset={something} 
					return null;
				uc.connect();
				is = uc.getInputStream();
				logRecord.info("download - %s", url);
				br = new BufferedReader(new InputStreamReader(is));
				String tmp;
				content = "";
				while ((tmp = br.readLine()) != null) {
					content += tmp + "\n";
				}
				break;
			}
			catch (Exception e) {
				logRecord.err("redownload(%d/%d) - %s - %s", s+1, Configuration.errorCorrection, url, e.getMessage());
				s++;
				continue;
			}
		}
		try {
			if(br != null)
				br.close();
			if(is != null)
				is.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public void sleep(double n) {
		try {
			Thread.sleep(Math.round(n));
		} catch (InterruptedException e) {		}		
	}
	
	private void run() {
		List<String[]> pages = new LinkedList<String[]>();
		for(String link : links) {
			link = "http://wayback.archive.org" + link;
			String html = fetch(link);
			if(html != null && !html.equals("")) {
				pages.add(new String[] {link, html});
				numberOfRecord++;
			}
		}
		if(pages.size() > 0) {
			this.filename = ParallelArcWriter.write(pages);
		}
		else {
			this.filename = null;
		}
	}
	
	public String getFileName() {
		return this.filename;
	}

	public String getNumberOfRecord() {
		return String.valueOf(this.numberOfRecord);
	}
	
	public Downloader(List<String> links) {
		this.links = links;
		run();
	}
}
