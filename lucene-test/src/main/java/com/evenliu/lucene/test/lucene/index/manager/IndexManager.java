 /**  
 *@Description:     
 */ 
package com.evenliu.lucene.test.lucene.index.manager;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.evenliu.lucene.test.lucene.index.model.ConfigBean;
import com.evenliu.lucene.test.lucene.index.model.IndexConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NRTManager;
import org.apache.lucene.search.NRTManager.TrackingIndexWriter;
import org.apache.lucene.search.NRTManagerReopenThread;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

public class IndexManager {
	private IndexWriter indexWriter;
	private TrackingIndexWriter trackingIndexWriter;
	private NRTManager nrtManager;
	private NRTManagerReopenThread nrtManagerReopenThread;
	private IndexCommitThread indexCommitThread;
	private ConfigBean configBean;
	
	private static class LazyIndexManager {
		//保存系统中的IndexManager对象
		private static HashMap<String, IndexManager> indexManagerMap = new HashMap<String, IndexManager>();
		
		static {
			for (ConfigBean bean : IndexConfig.getConfig()) {
				indexManagerMap.put(bean.getIndexName(), new IndexManager(bean));
			}
		}
	}
	
	/**
	 * @param indexName
	 * @return
	 * @Author:lulei  
	 * @Description: 获取索引的IndexManager对象
	 */
	public static IndexManager getIndexManager(String indexName) {
		return LazyIndexManager.indexManagerMap.get(indexName);
	}
	
	/**
	* @param configBean
	 */
	private IndexManager(ConfigBean configBean) {
		//索引的存储路径
		String indexFile = configBean.getIndexPath()  + "/" + configBean.getIndexName();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_43, configBean.getAnalyzer());
		indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
		this.configBean = configBean;
		Directory directory = null;
		try {
			directory = NIOFSDirectory.open(new File(indexFile));
			if (IndexWriter.isLocked(directory)) {
				IndexWriter.unlock(directory);
			}
			this.indexWriter = new IndexWriter(directory, indexWriterConfig);
			//将indexWriter委托给trackingIndexWriter
			this.trackingIndexWriter = new TrackingIndexWriter(indexWriter);
			//创建NRTManager
			this.nrtManager = new NRTManager(trackingIndexWriter, new SearcherFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//开启系统的守护线程
		setThread();
	}
	
	/**
	 * @return
	 * @Author:lulei  
	 * @Description: 获取最新可用的indexSearcher
	 */
	public IndexSearcher getIndexSearcher() {
		try {
			return this.nrtManager.acquire();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @param indexSearcher
	 * @Author:lulei  
	 * @Description: 释放indexSearcher
	 */
	public void relase(IndexSearcher indexSearcher) {
		try {
			this.nrtManager.release(indexSearcher);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @Author:lulei  
	 * @Description: 设置indexSearcher的守护线程
	 */
	private void setThread () {
		//内存索引重读线程
		this.nrtManagerReopenThread = new NRTManagerReopenThread(nrtManager, configBean.getIndexReopenMaxStaleSec(), configBean.getIndexReopenMinStaleSec());
		this.nrtManagerReopenThread.setName("NRTManager reopen thread");
		this.nrtManagerReopenThread.setDaemon(true);
		this.nrtManagerReopenThread.start();
		
		//内存索引提交线程
		this.indexCommitThread = new IndexCommitThread(configBean.getIndexName() + " index commmit thread");
		this.indexCommitThread.setDaemon(true);
		this.indexCommitThread.start();
	}
	
	private class IndexCommitThread extends Thread {
		private boolean flag = false;
		public IndexCommitThread (String name) {
			super(name);
		}
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			flag = true;
			while (flag){
				try {
					//内存索引提交至硬盘
					indexWriter.commit();
					System.out.println(new Date().toLocaleString() + "\t" + configBean.getIndexName() + "\tcommit");
					TimeUnit.SECONDS.sleep(configBean.getIndexCommitSeconds());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			super.run();
		}
	}
	
	public void commit() {
		try {
			indexWriter.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IndexWriter getIndexWriter() {
		return indexWriter;
	}

	public TrackingIndexWriter getTrackingIndexWriter() {
		return trackingIndexWriter;
	}
}
