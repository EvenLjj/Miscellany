 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.crawl.zongheng;

import com.evenliu.lucene.crawl.CrawlBase;
import com.evenliu.lucene.crawl.zongheng.db.ZonghengDB;
import com.evenliu.lucene.crawl.zongheng.model.NovelChapterModel;
import com.evenliu.lucene.util.JsonUtil;
import com.evenliu.lucene.util.ParseMD5;
import com.evenliu.lucene.util.RegexUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

  
public class ChapterPage extends CrawlBase {
	private String url;
	//请求头信息
	private static HashMap<String, String> params;
	//章节信息正则表达式
	private static final String CHAPTER = "<td class=\"chapterBean\" chapterId=\"\\d*\" chapterName=\"(.*?)\" chapterLevel=\"\\d*\" wordNum=\"(.*?)\" updateTime=\"(\\d*?)\"><a href=\"(.*?)\" title=\".*?\">";
	//提取的内容在正则表达式中的位置
	private static final int[] array = {1, 2, 3, 4};
	
	static {
		params = new HashMap<String, String>();
		params.put("Referer", "http://book.zongheng.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
		params.put("Host", "book.zongheng.com");
	}
	
	public ChapterPage(String url) {
		readPageByGet(url, params, "utf-8");
		this.url = url;
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 返回小说章节列表页信息
	 */
	public List<NovelChapterModel> analyzer() {
		List<NovelChapterModel> list = new ArrayList<NovelChapterModel>();
		List<String[]> arrays = getChapters();
		int i = 0;
		for (String[] array : arrays) {
			i++;
			list.add(analyzer(array, i));
		}
		return list;
	}
	
	/**
	 * @param array
	 * @param i
	 * @return
	 * @Author:lulei  
	 * @Description: 返回小说章节信息
	 */
	private NovelChapterModel analyzer(String[] array, int i) {
		NovelChapterModel bean = new NovelChapterModel();
		bean.setUrl(array[3]);
		bean.setId(ParseMD5.parseStrToMD5(bean.getUrl()));
		bean.setTitle(array[0]);
		bean.setWordCount(Integer.parseInt(array[1]));
		bean.setChapterTime(Long.parseLong(array[2]));
		bean.setChapterId(i);
		return bean;
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取章节信息：章节名、字数、更新时间、阅读页地址
	 */
	public List<String[]> getChapters() {
		return RegexUtil.getList(getPageSourceCode(), CHAPTER, array);
	}

	/**  
	 * @param args
	 * @Author:lulei  
	 * @Description:  
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub  
		ChapterPage chapterPage = new ChapterPage("http://book.zongheng.com/showchapter/496438.html");
		for (String[] ss : chapterPage.getChapters()) {
			for (String s : ss) {
				System.out.println(s);
			}
			System.out.println("-------------------------");
		}
		System.out.println(JsonUtil.parseJson(chapterPage.analyzer()));
		//01a7cc1c1210d856b50c823a4e375d0f
		ZonghengDB db = new ZonghengDB();
		db.saveNovelChpter(chapterPage.analyzer(), "01a7cc1c1210d856b50c823a4e375d0f");
	}

}
