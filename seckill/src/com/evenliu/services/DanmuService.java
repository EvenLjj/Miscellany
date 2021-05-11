package com.evenliu.services;

import java.util.List;

import com.evenliu.db.entity.Danmu;

public interface DanmuService {
	
	List<Danmu> getAllDanmu();
	
	int saveDanmu(Danmu data);
}
