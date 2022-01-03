package com.feng.util.net.http.proxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 代理服务
 * 
 * @author WangJing
 * 
 */
public class SimpleHttpServer {
	private ExecutorService mthreadPool;
	private ServerSocket mserverSocket;
	private Object mclosingWaiter = new Object();
	private boolean misEnable;
	private List<AbsUriHandler> mresourceUriHandlers;
	private String mipAddr;
	private int mport;
	public volatile static SimpleHttpServer Instance = null;

	private SimpleHttpServer() throws Exception {
		mthreadPool = Executors.newCachedThreadPool();
		mresourceUriHandlers = new ArrayList<AbsUriHandler>();

		String ipAddr = "127.0.0.1";
		String[] ipStr = ipAddr.split("\\.");
		byte[] ipBuf = new byte[4];
		for (int i = 0; i < 4; i++) {
			ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
		}

		mserverSocket = new ServerSocket(0, 1, InetAddress.getByAddress(ipBuf));
		mipAddr = mserverSocket.getInetAddress().getHostAddress().toString();
		mport = mserverSocket.getLocalPort();

	}

	public static SimpleHttpServer getInstance() throws Exception {
		if (Instance == null) {
			synchronized (SimpleHttpServer.class) {
				if (null == Instance)
					Instance = new SimpleHttpServer();
			}
		}
		return Instance;
	}

	public void registerResourceUriHandler(AbsUriHandler handler) {
		mresourceUriHandlers.add(handler);
	}

	public String getAddr() {
		return mipAddr;
	}

	public int getPort() {
		return mport;
	}

	/**
	 * 开启代理服务
	 * 
	 */
	public void startServer() {
		misEnable = true;
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (mclosingWaiter) {
					try {
						while (misEnable) {
							final Socket remotePeer = mserverSocket.accept();
							mthreadPool.submit(new Runnable() {
								@Override
								public void run() {
									onAcceptRemotePeer(remotePeer);
								}
							});
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						return;
					} finally {
						mclosingWaiter.notifyAll();
					}
				}
			}
		});

		thread.setName("thread.proxyServer");
		thread.start();
	}

	/**
	 * 关闭代理服务
	 * 
	 * @throws Exception
	 */
	public void stopServer() throws Exception {
		if (!misEnable) {
			return;
		}
		misEnable = false;
		if (mserverSocket == null) {
			return;
		}
		mserverSocket.close();
		mserverSocket = null;
		synchronized (mclosingWaiter) {
			mclosingWaiter.wait();
		}
	}

	/**
	 * 处理连接请求
	 * 
	 * @param peer
	 */
	private void onAcceptRemotePeer(Socket peer) {
		HttpContext httpContext = new HttpContext();
		try {
			InputStream in = peer.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String buf = null;
			buf = reader.readLine();
			if (buf == null) {
				throw new Exception("read net io error!");
			}
			String resourceUri = buf.split(" ")[1];
			HttpHeadersCollection headers = new HttpHeadersCollection();
			while ((buf = reader.readLine()) != null) {
				if (buf.length() < 2) {
					break;
				}
				String[] pairs = buf.split(":");
				if (pairs.length == 2) {
					String headerKey = pairs[0];
					String headerValue = buf.substring(headerKey.length() + 1).trim();
					headers.addHeader(headerKey, headerValue);
				}
			}

			httpContext.setUnderlySocket(peer);
			httpContext.setOutputStream(peer.getOutputStream());
			httpContext.setRequestHeaders(headers);
			for (AbsUriHandler handler : mresourceUriHandlers) {
				if (handler.isAccept(resourceUri)) {
					handler.handle(resourceUri, httpContext);
					break;
				}
			}
		} catch (Exception ex) {
			try {
				peer.close();
			} catch (IOException ioEx) {
			}
		} finally {
			try {
				if (!httpContext.isBindToAsynchronizeProcessor()) {
					peer.close();
				}
			} catch (IOException ioEx) {
			}
		}
	}
}