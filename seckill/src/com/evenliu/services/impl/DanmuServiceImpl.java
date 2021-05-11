package com.evenliu.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.evenliu.db.dao.DanmuMapper;
import com.evenliu.db.entity.Danmu;
import com.evenliu.services.DanmuService;

@Service
public class DanmuServiceImpl implements DanmuService{

	@Autowired
	private DanmuMapper danmuMapper;
	
	@Override
	public List<Danmu> getAllDanmu() {
		return danmuMapper.queryAll(0, 1);
	}

	@Override
	public int saveDanmu(Danmu data) {
		return danmuMapper.insert(data);
	}

}
