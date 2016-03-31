package com.ai.dbselect.balance;

import java.util.List;
import java.util.Random;

public class BalanceAlg {
	private static Integer pos = 0;
	/**
	 * 轮询算法
	 * 推荐测试用，效果比较容易观察，
	 * 但相较random算法，要枷锁，效率较低，跨系统无效
	 * @param addList
	 * @return
	 */
	public static String roundRobin(List<String> addList) {
		// 同步位置变量
		String server = null;
		synchronized (pos) {
			if (pos >= addList.size()) {
				pos = 0;
			}
			server = addList.get(pos);
			pos++;
		}
		return server;
	}

	/**
	 * 随机算法
	 * 没有锁，性能较RoundRobin好
	 * @param addList
	 * @return
	 */
	public static String random(List<String> addList) {
		// 随机
		Random random = new Random();
		int rvalue = random.nextInt(addList.size());
		String server = addList.get(rvalue);
		return server;
	}

}
