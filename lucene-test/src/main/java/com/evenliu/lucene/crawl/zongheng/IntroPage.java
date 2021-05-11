 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.crawl.zongheng;

import com.evenliu.lucene.crawl.CrawlBase;
import com.evenliu.lucene.crawl.zongheng.db.ZonghengDB;
import com.evenliu.lucene.crawl.zongheng.model.NovelInfoModel;
import com.evenliu.lucene.util.JsonUtil;
import com.evenliu.lucene.util.ParseMD5;
import com.evenliu.lucene.util.RegexUtil;

import java.util.HashMap;


  
public class IntroPage extends CrawlBase {
	private String url;
	//请求头信息
	private static HashMap<String, String> params;
	//作者信息正则
	private static final String AUTHOR = "<meta name=\"og:novel:author\" content=\"(.*?)\"/>";
	//书名信息正则
	private static final String NAME = "<meta name=\"og:novel:book_name\" content=\"(.*?)\"/>";
	//简介信息正则
	private static final String DESC = "<meta property=\"og:description\" content=\"(.*?)\"/>";
	//分类信息正则
	private static final String TYPE = "<meta name=\"og:novel:category\" content=\"(.*?)\"/>";
	//最新章节信息正则
	private static final String LASTCHAPTER = "<a class=\"chap\" href=\".*?\">(.*?)<p>";
	//字数信息正则
	private static final String WORDCOUNT = "<span title=\"(\\d*?)字\">";
	//关键字html信息正则
	private static final String KEYWORDS = "<div class=\"keyword\">(.*?)</div>";
	//关键字信息正则
	private static final String KEYWORD = "<a.*?>(.*?)</a>";
	//章节列表URL信息正则
	private static final String CHAPTERLISTURL = "<meta name=\"og:novel:read_url\" content=\"(.*?)\"/>";
	static {
		params = new HashMap<String, String>();
		params.put("Referer", "http://book.zongheng.com");
		params.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.135 Safari/537.36");
		params.put("Host", "book.zongheng.com");
	}
	
	public IntroPage(String url) {
		readPageByGet(url, params, "utf-8");
		this.url = url;
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description:返回小说简介信息
	 */
	public NovelInfoModel analyzer() {
		NovelInfoModel bean = new NovelInfoModel();
		bean.setUrl(url);
		bean.setId(ParseMD5.parseStrToMD5(bean.getUrl()));
		bean.setName(getName());
		bean.setAuthor(getAuthor());
		bean.setDesc(getDesc());
		bean.setType(getType());
		bean.setLastChapter(getLastCharpter());
		bean.setWordCount(getWordCount());
		bean.setKeyWords(getKeyWord());
		bean.setChapterListUrl(getChapterListUrl());
		return bean;
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说书名
	 */
	private String getName() {
		return RegexUtil.getFirstString(getPageSourceCode(), NAME, 1);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说作者
	 */
	private String getAuthor() {
		return RegexUtil.getFirstString(getPageSourceCode(), AUTHOR, 1);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说简介
	 */
	private String getDesc() {
		return RegexUtil.getFirstString(getPageSourceCode(), DESC, 1);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说分类
	 */
	private String getType() {
		return RegexUtil.getFirstString(getPageSourceCode(), TYPE, 1);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说最新章节
	 */
	private String getLastCharpter() {
		return RegexUtil.getFirstString(getPageSourceCode(), LASTCHAPTER, 1);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说字数
	 */
	private int getWordCount() {
		String wordCount = RegexUtil.getFirstString(getPageSourceCode(), WORDCOUNT, 1);
		return Integer.parseInt(wordCount);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取关键字html
	 */
	private String getKeyWordStr() {
		return RegexUtil.getFirstString(getPageSourceCode(), KEYWORDS, 1);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说关键字
	 */
	private String getKeyWord() {
		return RegexUtil.getString(getKeyWordStr(), KEYWORD, " ", 1);
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取小说章节列表页URL
	 */
	private String getChapterListUrl() {
		return RegexUtil.getFirstString(getPageSourceCode(), CHAPTERLISTURL, 1);
	}

	/**  
	 * @param args
	 * @Author:lulei  
	 * @Description:  
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub  
		IntroPage introPage = new IntroPage("http://book.zongheng.com/book/496438.html");
		System.out.println(introPage.getName());
		System.out.println(introPage.getAuthor());
		System.out.println(introPage.getDesc());
		System.out.println(introPage.getType());
		System.out.println(introPage.getLastCharpter());
		System.out.println(introPage.getWordCount());
		System.out.println(introPage.getKeyWord());
		System.out.println(introPage.getChapterListUrl());
		
		System.out.println(JsonUtil.parseJson(introPage.analyzer()));
		ZonghengDB db = new ZonghengDB();
		db.updateNovelInfo(introPage.analyzer());
	}

}
