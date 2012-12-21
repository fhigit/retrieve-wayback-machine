import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import net.htmlparser.jericho.*;

public class Crawler implements Runnable {
	private String[] hosts = new String[] {
			"http://wayback.archive.org/web/19960101000000*/",
			"http://wayback.archive.org/web/19970101000000*/",
			"http://wayback.archive.org/web/19980101000000*/",
			"http://wayback.archive.org/web/19990101000000*/",
			"http://wayback.archive.org/web/20000101000000*/",
			"http://wayback.archive.org/web/20010101000000*/",
			"http://wayback.archive.org/web/20020101000000*/",
			"http://wayback.archive.org/web/20030101000000*/",
			"http://wayback.archive.org/web/20040101000000*/",
			"http://wayback.archive.org/web/20050101000000*/",
			"http://wayback.archive.org/web/20060101000000*/",
			"http://wayback.archive.org/web/20070101000000*/",
			"http://wayback.archive.org/web/20080101000000*/",
			"http://wayback.archive.org/web/20090101000000*/",
			"http://wayback.archive.org/web/20100101000000*/",
			"http://wayback.archive.org/web/20110101000000*/",
			"http://wayback.archive.org/web/20120101000000*/",
			};
	private List<String> links = new LinkedList<String>(); 
	private String url;
	private int initYear = 1996;
	private int downloadedTime = -1;
	private String record;
	
	private String fetch(String url) {
		sleep((Integer.parseInt(record) % 2) * 1000);
		
		int s = 0;
		String content = null;
		InputStream is = null;
		BufferedReader br = null;
		while(s < Configuration.errorCorrection) {
			try {
				int timeout = 3000; // milliseconds
				URL u = new URL(url);
				HttpURLConnection uc = (HttpURLConnection) u.openConnection();
				uc.setRequestProperty("User-agent", "Pramote T., MIKE lab, Kasetsart University, boatblaster@gmail.com");
				uc.setConnectTimeout(timeout);
				uc.setReadTimeout(timeout);
				if(uc.getResponseCode() != 200) // Response Code: HTTP/1.1 200 OK
					return null;
				if(!uc.getContentType().startsWith("text/html")) // content-type: text/html; charset={something} 
					return null;
				uc.connect();
				is = uc.getInputStream();
				logRecord.info("crawl - %s", url);
				br = new BufferedReader(new InputStreamReader(is));
				String tmp;
				content = "";
				while ((tmp = br.readLine()) != null) {
					content += tmp + "\n";
				}
			}
			catch (Exception e) {
				logRecord.err("recrawl(%d/%d) - %s - %s", s+1, Configuration.errorCorrection, url, e.getMessage());
			}
			finally {
				try {
					if(br != null)
						br.close();
					if(is != null)
						is.close();
				} catch(Exception e) {
					
				}
				if(content != null)
					break;
				else
					s++;
			}
		}
		return content;
	}
	
	private void ExtractLink(String html) {
		Source s = new Source(html);
		s.fullSequentialParse();
		Element cal = s.getElementById("calOver");
		if(cal != null) {
			for(Element e : cal.getAllElements(HTMLElementName.A)) {
				String link = e.getAttributeValue("href");
				if(!links.contains(link))
					links.add(link);
			}
		}
		else {
			Element cal2 = s.getElementById("calUnder");
			if(cal2 != null) {
				for(Element e : cal2.getAllElements(HTMLElementName.A)) {
					String link = e.getAttributeValue("href");
					if(!links.contains(link))
						links.add(link);
				}
			}
		}
	}
	
	public List<String> getLinks() {
		return links;
	}

	private void initialValue() {
		String initurl = "http://wayback.archive.org/web/*/" + this.url;
		String html = fetch(initurl);
		if(html != null && !html.equals("")) {
			Source s = new Source(html);

			try {
				String rawStr = s.getFirstElementByClass("wbThis").getAllElements(HTMLElementName.A).get(1).getTextExtractor().toString();
				this.initYear = Integer.parseInt(rawStr.substring(rawStr.length()-4).trim());
				Element ee = s.getFirstElement(HTMLElementName.STRONG);
				String[] temp = ee.getTextExtractor().toString().split(" ");
				this.downloadedTime = Integer.parseInt(temp[0].trim().replaceAll(",", ""));
			} catch(Exception e) {
				return;
			}
		}
	}

	public void sleep(double n) {
		try {
			Thread.sleep(Math.round(n));
		} catch (InterruptedException e) {		}		
	}
	
	@Override
	public void run() {
		logRecord.info("get no.%s %s", this.record, this.url);
		initialValue();
		for(int i=this.initYear-1996; i < hosts.length && this.links.size() < this.downloadedTime; i++) {
			String url_target = hosts[i] + this.url;
			String html = fetch(url_target);
			if(html != null) {
				ExtractLink(html);
			}
		}
		Downloader dl = new Downloader(this.getLinks());
		if(dl.getFileName() != null) {
			//System.out.println(dl.getFileName() + "\t" + this.url + "\t" + dl.getNumberOfRecord());
			Index_Writer.write(dl.getFileName() + "\t" + this.url + "\t" + dl.getNumberOfRecord());
			logRecord.info("Finish %s. %s %s close", this.record, this.url, dl.getFileName());
		}
		else
			logRecord.err("Finish %s. %s doesn't not exist in history.", this.record, this.url);
	}
	
	public Crawler(Crawler_Main.data d) throws InterruptedException {
		this.url = d.url;
		this.record = String.valueOf(d.record);
	}
}