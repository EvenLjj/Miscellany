 /**  
 *@Description:爬虫程序入口地址信息：更新列表页信息    
 */ 
package com.evenliu.lucene.crawl.zongheng.model;
  
public class CrawlListInfo {
	private String url;
	private String info;
	private int frequency;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

}
