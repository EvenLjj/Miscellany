 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.crawl.zongheng;

import com.evenliu.lucene.crawl.zongheng.db.ZonghengDB;
import com.evenliu.lucene.crawl.zongheng.model.NovelChapterModel;
import com.evenliu.lucene.crawl.zongheng.model.NovelInfoModel;

import java.util.List;
import java.util.concurrent.TimeUnit;


  
public class IntroPageThread extends Thread{
	private boolean flag = false;
	
	public IntroPageThread(String name) {
		super(name);
	}

	@Override
	public void run() {
		flag = true;
		try {
			ZonghengDB db = new ZonghengDB();
			while (flag) {
				//获取可以采集的简介页URL
				String url = db.getRandIntroPageUrl(1);
				if (url != null) {
					IntroPage intro = new IntroPage(url);
					//获取简介页信息
					NovelInfoModel bean = intro.analyzer();
					if (bean != null) {
						ChapterPage chapterPage = new ChapterPage(bean.getChapterListUrl());
						//获取章节列表页信息
						List<NovelChapterModel> chapters = chapterPage.analyzer();
						//写入小说章节个数
						bean.setChapterCount(chapters == null ? 0 : chapters.size());
						//保存简介页信息
						db.updateNovelInfo(bean);
						//保存章节列表页信息
						db.saveNovelChpter(chapters, bean.getId());
					}
					TimeUnit.MILLISECONDS.sleep(500);
				} else {
					TimeUnit.MILLISECONDS.sleep(1000);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block  
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub  
		IntroPageThread thread = new IntroPageThread("novelinfo");
		thread.start();
	}

}
