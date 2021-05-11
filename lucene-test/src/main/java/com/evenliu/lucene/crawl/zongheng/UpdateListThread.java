 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.crawl.zongheng;

import com.evenliu.lucene.crawl.zongheng.db.ZonghengDB;

import java.util.List;
import java.util.concurrent.TimeUnit;

  
  
public class UpdateListThread extends Thread{
	private boolean flag = false;
	private String url = "";
	private int frequency;
	
	/**
	* @param name 线程名
	* @param url 更新列表页url
	* @param frequency 采集频率
	 */
	public UpdateListThread(String name, String url, int frequency) {
		super(name);
		this.url = url;
		this.frequency = frequency;
	}
	

	@Override
	public void run() {
		flag = true;
		ZonghengDB db = new ZonghengDB();
		while (flag) {
			try {
				UpdateList updateList = new UpdateList(this.url);
				//获取更新列表页URL信息
				List<String> urls = updateList.getPageUrl(true);
				db.saveInfoUrls(urls);
				TimeUnit.SECONDS.sleep(this.frequency);
			} catch (Exception e) {
				// TODO Auto-generated catch block  
				e.printStackTrace();
			}
		}
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub  
		UpdateListThread thread = new UpdateListThread("lll", "http://book.zongheng.com/store/c0/c0/b9/u0/p1/v0/s9/t0/ALL.html", 30);
		thread.start();
	}

}
