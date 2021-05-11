 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.crawl.zongheng;

import com.evenliu.lucene.crawl.CrawlListPageBase;
import com.evenliu.lucene.crawl.zongheng.db.ZonghengDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


  
public class UpdateList extends CrawlListPageBase {
	//请求头信息
	private static HashMap<String, String> params;
	
	static {
		params = new HashMap<String, String>();
		params.put("Referer", "http://book.zongheng.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
		params.put("Host", "book.zongheng.com");
	}

	public UpdateList(String pageUrl) {
		super(pageUrl, "utf-8", params);  
	}


	@Override
	public String getUrlRegexStr() {
		return "<a class=\"fs14\" href=\"(.*?)\"";
	}

	@Override
	public int getUrlRegexStrNum() {
		return 1;
	}
	
	/**
	 * @param exceptOther 是否需要排除纵横以外的网址
	 * @return
	 * @Author:lulei  
	 * @Description:
	 */
	public List<String> getPageUrl(boolean exceptOther) {
		List<String> urls = getPageUrl();
		if (exceptOther) {
			List<String> excepUrls = new ArrayList<String>();
			for (String url : urls) {
				//url中是否包含"zongheng"
				if (url.indexOf("zongheng") > 0) {
					excepUrls.add(url);
				}
			}
			return excepUrls;
		}
		return urls;
	}
	/**  
	 * @param args
	 * @Author:lulei  
	 * @Description:  
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub  
		UpdateList updateList = new UpdateList("http://book.zongheng.com/store/c0/c0/b9/u0/p1/v0/s9/t0/ALL.html");
		for (String s : updateList.getPageUrl(true)) {
			System.out.println(s);
		}
		ZonghengDB db = new ZonghengDB();
		db.saveInfoUrls(updateList.getPageUrl(true));
	}

}
