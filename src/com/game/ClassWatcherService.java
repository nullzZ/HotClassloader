package com.game;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

public class ClassWatcherService implements Runnable {
	private static final Logger logger = Logger.getLogger(ClassWatcherService.class);
	private WatchService watcher;
	private Map<WatchKey, String> keys;

	// 监控目录
	private String path;

	private static ClassWatcherService classWatcherService = null;
	private boolean IsRuner = false;

	private ClassWatcherService(String path) throws IOException {
		this.path = path;
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<WatchKey, String>();

	}

	public static ClassWatcherService GetInstance(String path) throws IOException {
		if (classWatcherService == null)
			classWatcherService = new ClassWatcherService(path);

		return classWatcherService;
	}

	public boolean IsRun() {
		return IsRuner;
	}

	public ClassWatcherService StartServers() {
		try {
			if (!IsRuner) {
				WatchKey wk = Paths.get(path).register(watcher, ENTRY_MODIFY);
				this.keys.put(wk, path);
				File file = new File(path);
				LinkedList<File> fList = new LinkedList<File>();
				fList.addLast(file);
				while (fList.size() > 0) {
					File f = fList.removeFirst();
					if (f.listFiles() == null)
						continue;
					for (File file2 : f.listFiles()) {
						if (file2.isDirectory()) {// 下一级目录
							fList.addLast(file2);
							// 依次注册子目录
							String s = file2.getAbsolutePath();
							wk = Paths.get(s).register(watcher, ENTRY_MODIFY);
							this.keys.put(wk, s);
						}
					}
				}
				new Thread(this).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 监控文件
	 */
	private void handleEvents() {
		while (true) {
			try {
				WatchKey key = watcher.take();
				System.err.println("#####" + key + "@" + Thread.currentThread().getName());
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent.Kind<?> kind = event.kind();
					if (kind == OVERFLOW) {
						continue;
					}
					@SuppressWarnings("unchecked")
					WatchEvent<Path> e = (WatchEvent<Path>) event;
					Path fileName = e.context();
					logger.error(event.kind().name() + "发现目录下有Class发生变化.进行热加载" + path + "\\" + fileName);

					// ----------------------编译-------------------------------
					// boolean ret =
					// JavaCompilerService.getInstance().build(path,
					// fileName.toString());
					// if (!ret) {
					// System.err.println("编译失败" + fileName.toString());
					// continue;
					// }
					// String javaName = fileName.toString().substring(0,
					// fileName.toString().lastIndexOf("."));
					// MyClassLoader.GetInstance().findNewClass(path + "\\" +
					// javaName + ".class");
					// ----------------------编译-------------------------------
					String keypath = this.keys.get(key);
					Object obj = MyClassLoader.GetInstance().findNewClass(keypath + "\\" + fileName);
					ScriptManager.getInstance().putScript((IScript) obj);
				}
				if (!key.reset()) {
					logger.error("key.reset()");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * @see 禁止外部方法直接调用该方法,只能通过 StartServers启动
	 ***/
	@Deprecated
	@Override
	public void run() {
		if (!IsRuner) {
			IsRuner = !IsRuner;
			handleEvents();
		}
		logger.error("文件监听已运行");
	}
}