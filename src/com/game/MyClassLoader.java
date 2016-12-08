package com.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author nullzZ
 *
 */
public class MyClassLoader extends ClassLoader {

	// 构�?器私有化,禁止使用者直接生成实�?
	private MyClassLoader() {
	}

	// 获取MyClassLoads的唯�?���?
	public static MyClassLoader GetInstance() {
		return new MyClassLoader();
	}

	public Object findNewClass(String classPath) {
		try {
			byte[] b = getBytes(classPath);
			return defineClass(null, b, 0, b.length).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param filename
	 * @return Byte[]
	 * @throws IOException
	 *             返回class文件的Byte
	 */
	private byte[] getBytes(String filename) throws IOException {
		File file = new File(filename);
		byte raw[] = new byte[(int) file.length()];
		FileInputStream fin = new FileInputStream(file);
		fin.read(raw);
		fin.close();
		return raw;
	}

	/**
	 * @param o
	 * @return T 包装返回类对�?
	 */
	@SuppressWarnings("unchecked")
	public static <T> T reLoadClass(Object o) {
		return (T) o;
	}
}
