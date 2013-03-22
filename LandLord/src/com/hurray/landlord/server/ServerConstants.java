package com.hurray.landlord.server;

/**
 * @Author: Yizhou He 4/26/12 14:32
 */
public class ServerConstants {

	// 外网服务器
	public static final String HTTP_HOST = "http://211.99.199.106:8888/lord4";
// 鸿斌
//	public static final String HTTP_HOST = "http://10.36.97.15:8000/lord4";
// 何一舟的服务器
	// public static final String HTTP_HOST =
	// "http://10.36.97.33:8080/Lord4_war_exploded";
	// public static final String HTTP_HOST =
	// "http://10.36.97.33:8080/Lord4_war_exploded";

	// 庄志勇的第二个服务器
	// public static final String HTTP_HOST = "http://10.36.97.12:8000/lord4";

	// 李恒旭测试服务器
	// public static final String HTTP_HOST = "http://10.36.97.31:8000/lord4";

	// 林欣测试服务器
	// public static final String HTTP_HOST = "http://10.36.97.50:8000/lord4";

	// 曹丽测试服务器
	// public static final String HTTP_HOST = "http://10.36.97.42:8889/lord4";

	public static final String MSG_URI = "/msg";

	public static String getServerUrl() {
		return HTTP_HOST + MSG_URI;
	}

	// 连接超时
	public static final int CONNECT_TIMEOUT = 20000;

	// 读取超时
	public static final int SO_TIMEOUT = 20000;

	// 默认消息队列的刷新时间
	public static final long DEFAULT_MSG_FREQUENCY = 2000;

	public static String LOG_TAG = "Lord.Network";
}
