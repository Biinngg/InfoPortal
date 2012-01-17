package com.iBeiKe.InfoPortal.common;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class ComBlockingQueue {
	BlockingQueue<Map<String,Map<String,String>>> dbStructure;
	public ComBlockingQueue(BlockingQueue<Map<String,Map<String,String>>> queue) {
		dbStructure = queue;
	}
	public void add(Map<String,Map<String,String>> struct) throws InterruptedException {
		dbStructure.put(struct);
	}
	public Map<String,Map<String,String>> fetch() throws InterruptedException {
		if(dbStructure.isEmpty()) {
			return null;
		} else {
			return dbStructure.take();
		}
	}
}