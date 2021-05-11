 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.crawl.zongheng.db;

import com.evenliu.lucene.crawl.zongheng.model.CrawlListInfo;
import com.evenliu.lucene.crawl.zongheng.model.NovelChapterModel;
import com.evenliu.lucene.crawl.zongheng.model.NovelInfoModel;
import com.evenliu.lucene.crawl.zongheng.model.NovelReadModel;
import com.evenliu.lucene.db.manager.DBServer;
import com.evenliu.lucene.util.JsonUtil;
import com.evenliu.lucene.util.ParseMD5;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


  
public class ZonghengDB {
	//数据库连接池的别名
	private static final String POOLNAME = "proxool.jikexueyuan";
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取更新列表页地址信息
	 */
	public List<CrawlListInfo> getCrawlListInfos() {
		List<CrawlListInfo> infos = new ArrayList<CrawlListInfo>();
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "select * from crawllist where `state` = '1'";
			ResultSet rs = dbServer.select(sql);
			while(rs.next()) {
				CrawlListInfo info = new CrawlListInfo();
				infos.add(info);
				info.setUrl(rs.getString("url"));
				info.setInfo(rs.getString("info"));
				info.setFrequency(rs.getInt("frequency"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		return infos;
	}
	
	/**
	 * @param state
	 * @return
	 * @Author:lulei  
	 * @Description: 随机获取简介页url
	 */
	public String getRandIntroPageUrl(int state) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "select * from novelinfo where state='" + state + "' order by rand() limit 1";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				return rs.getString("url");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		return null;
	}
	
	/**
	 * @param state
	 * @return
	 * @Author:lulei  
	 * @Description:随机获取章节信息
	 */
	public NovelChapterModel getRandChapter(int state) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "select * from novelchapter where state='" + state + "' order by rand() limit 1";
			ResultSet rs = dbServer.select(sql);
			while (rs.next()) {
				NovelChapterModel bean = new NovelChapterModel();
				bean.setId(rs.getString("id"));
				bean.setUrl(rs.getString("url"));
				bean.setChapterId(rs.getInt("chapterid"));
				bean.setChapterTime(rs.getLong("chaptertime"));
				return bean;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		return null;
	}
	
	/**
	 * @param urls
	 * @Author:lulei  
	 * @Description: 将采集到的简介页url信息保存到数据库中
	 */
	public void saveInfoUrls(List<String> urls) {
		if(urls == null || urls.size() < 1) {
			return;
		}
		for (String url : urls) {
			String id = ParseMD5.parseStrToMD5(url);
			if (haveNovelInfo(id)) {
				updateInfoState(id, 1);
			} else {
				insertInfoUrl(id, url);
			}
		}
	}
	
	/**
	 * @param beans
	 * @param novelId
	 * @Author:lulei  
	 * @Description: 将采集到的章节信息保存到数据库中
	 */
	public void saveNovelChpter(List<NovelChapterModel> beans, String novelId){
		if (beans == null) {
			return;
		}
		for (NovelChapterModel bean : beans) {
			if (!haveNovelChapter(bean.getId())) {
				inserNovelChapter(bean, novelId);
			}
		}
	}
	
	/**
	 * @param bean
	 * @Author:lulei  
	 * @Description: 将采集到的阅读页信息保存到数据库中
	 */
	public void saveNovelRead(NovelReadModel bean){
		if (bean == null) {
			return;
		}
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			if (haveNovelRead(bean.getId())) {
				return;
			}
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			params.put(i++, bean.getId());
			params.put(i++, bean.getUrl());
			params.put(i++, bean.getTitle());
			params.put(i++, bean.getWordCount());
			params.put(i++, bean.getChapterId());
			params.put(i++, bean.getContent());
			params.put(i++, bean.getChapterTime());
			long now = System.currentTimeMillis();
			params.put(i++, now);
			params.put(i++, now);
			String columns = "id,url,title,wordcount,chapterid,content,chaptertime,createtime,updatetime";
			dbServer.insert("novelchapterdetail", columns, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
	}
	
	/**
	 * @param id
	 * @return
	 * @Author:lulei  
	 * @Description: 判断阅读页信息是否存在
	 */
	private boolean haveNovelRead(String id) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "select sum(1) as count from novelchapterdetail where id='" + id + "'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		return true;
	}
	
	/**
	 * @param id
	 * @return
	 * @Author:lulei  
	 * @Description: 判断章节信息时候存在
	 */
	private boolean haveNovelChapter(String id) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "select sum(1) as count from novelchapter where id='" + id + "'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		return true;
	}
	
	/**
	 * @param bean
	 * @param novelId
	 * @Author:lulei  
	 * @Description: 将章节信息保存到数据库中
	 */
	private void inserNovelChapter(NovelChapterModel bean, String novelId) {
		if(bean == null) {
			return;
		}
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			params.put(i++, bean.getId());
			params.put(i++, novelId);
			params.put(i++, bean.getUrl());
			params.put(i++, bean.getTitle());
			params.put(i++, bean.getWordCount());
			params.put(i++, bean.getChapterId());
			params.put(i++, bean.getChapterTime());
			long now = System.currentTimeMillis();
			params.put(i++, now);
			params.put(i++, "1");
			String columns = "id,novelid,url,title,wordcount,chapterid,chaptertime,createtime,state";
			dbServer.insert("novelchapter", columns, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
	}
	
	/**
	 * @param bean
	 * @Author:lulei  
	 * @Description: 更新小说的简介信息
	 */
	public void updateNovelInfo(NovelInfoModel bean) {
		if (bean == null) {
			return;
		}
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			params.put(i++, bean.getName());
			params.put(i++, bean.getAuthor());
			params.put(i++, bean.getDesc());
			params.put(i++, bean.getType());
			params.put(i++, bean.getLastChapter());
			params.put(i++, bean.getChapterListUrl());
			params.put(i++, bean.getWordCount());
			params.put(i++, bean.getKeyWords());
			long now = System.currentTimeMillis();
			params.put(i++, now);
			params.put(i++, "0");
			
			String columns = "name,author,description,type,lastchapter,chapterlisturl,wordcount,keywords,updatetime,state";
			String condition = "where id = '" + bean.getId() + "'";
			dbServer.update("novelinfo", columns, condition, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
	}
	
	/**
	 * @param id
	 * @param state
	 * @Author:lulei  
	 * @Description:更新章节信息的state值
	 */
	public void updateChapterState(String id, int state) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "update novelchapter set `state`='" + state + "' where id = '" + id + "'";
			dbServer.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
	}
	
	/**
	 * @param id
	 * @param state
	 * @Author:lulei  
	 * @Description: 更新简介信息的state值
	 */
	public void updateInfoState(String id, int state) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "update novelinfo set `state`='" + state + "' where id = '" + id + "'";
			dbServer.update(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
	}
	
	/**
	 * @param id
	 * @return
	 * @Author:lulei  
	 * @Description: 判断小说简介信息是否存在
	 */
	private boolean haveNovelInfo(String id) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			String sql = "select sum(1) as count from novelinfo where id='" + id + "'";
			ResultSet rs = dbServer.select(sql);
			if (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
		return true;
	}
	
	/**
	 * @param id
	 * @param url
	 * @Author:lulei  
	 * @Description:将采集到的简介页url保存到数据库中
	 */
	private void insertInfoUrl(String id, String url) {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			HashMap<Integer, Object> params = new HashMap<Integer, Object>();
			int i = 1;
			params.put(i++, id);
			params.put(i++, url);
			long now = System.currentTimeMillis();
			params.put(i++, now);
			params.put(i++, now);
			params.put(i++, 1);
			
			dbServer.insert("novelinfo", "id,url,createtime,updatetime,state", params);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
	}
	
	/**
	 * @Author:lulei  
	 * @Description: 如何使用DBServer类实现数据库的操作
	 */
	@SuppressWarnings("unused")
	private void method() {
		DBServer dbServer = new DBServer(POOLNAME);
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbServer.close();
		}
	}

	/**  
	 * @param args
	 * @Author:lulei  
	 * @Description:  
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub  
		ZonghengDB db = new ZonghengDB();
		System.out.println(JsonUtil.parseJson(db.getCrawlListInfos()));
		System.out.println(db.getRandIntroPageUrl(1));
		System.out.println(JsonUtil.parseJson(db.getRandChapter(1)));
	}

}
