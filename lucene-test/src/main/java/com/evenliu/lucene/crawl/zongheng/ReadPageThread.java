 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.crawl.zongheng;

import com.evenliu.lucene.crawl.zongheng.db.ZonghengDB;
import com.evenliu.lucene.crawl.zongheng.model.NovelChapterModel;
import com.evenliu.lucene.crawl.zongheng.model.NovelReadModel;

import java.util.concurrent.TimeUnit;


public class ReadPageThread extends Thread{
	private boolean flag = false;
	
	public ReadPageThread (String name) {
		super(name);
	}
	

	@Override
	public void run() {
		flag = true;
		ZonghengDB db = new ZonghengDB();
		while (flag) {
			try {
				//获取可以采集的章节信息
				NovelChapterModel chapter = db.getRandChapter(1);
				if (chapter != null) {
					ReadPage readPage = new ReadPage(chapter.getUrl());
					//获取小说阅读页信息
					NovelReadModel novel = readPage.analyzer();
					if (novel == null) {
						continue;
					}
					//写入小说章节序号
					novel.setChapterId(chapter.getChapterId());
					//写入章节发布时间
					novel.setChapterTime(chapter.getChapterTime());
					//保存小说阅读页信息
					db.saveNovelRead(novel);
					//将小说的章节设置成已采集
					db.updateChapterState(chapter.getId(), 0);
					TimeUnit.MILLISECONDS.sleep(500);
				} else {
					TimeUnit.MILLISECONDS.sleep(1000);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**  
	 * @param args
	 * @Author:lulei  
	 * @Description:  
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub  
		ReadPageThread thread = new ReadPageThread("readPage");
		thread.start();
	}

}
