package com.game;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.log4j.Logger;

/**
 * @author nullzZ
 *
 */
public class JavaCompilerService {

	private static final Logger logger = Logger.getLogger(JavaCompilerService.class);
	private URLClassLoader parentClassLoader;
	private String classpath;

	public static final JavaCompilerService instance = new JavaCompilerService();

	private JavaCompilerService() {
		load();
	}

	public static JavaCompilerService getInstance() {
		return instance;
	}

	public void load() {
		this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
		this.buildClassPath();
	}

	private void buildClassPath() {
		this.classpath = null;
		StringBuilder sb = new StringBuilder();
		for (URL url : this.parentClassLoader.getURLs()) {
			String p = url.getFile();
			sb.append(p).append(File.pathSeparator);
		}
		this.classpath = sb.toString();
		System.out.println("脚本加载需要类路径：" + this.classpath);
	}

	public boolean build(String path, String fileName) {
		try {
			// File file = new File(path + "\\script");
			// if (!file.exists()) {
			// file.mkdir();
			// }
			String name = path + "\\" + fileName;
			InputStream in = new FileInputStream(name);

			ByteBuffer buf = ByteBuffer.allocate(2048);

			byte[] bytes = new byte[1024];
			int len = 0;
			while ((len = in.read(bytes)) != -1) {
				buf.put(bytes, 0, len);
			}
			buf.flip();

			byte[] allbytes = new byte[buf.remaining()];
			buf.get(allbytes);
			in.close();

			JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
			StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

			List<String> options = new ArrayList<String>();
			options.add("-encoding");
			options.add("UTF-8");
			options.add("-classpath");
			options.add(classpath);
			// options.add("-d");
			// options.add("lib");

			Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjects(name);
			JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null,
					fileObjects);

			boolean success = task.call();

			fileManager.close();

			if (success) {
				return true;
			} else {
				String error = "";
				for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
					error = error + compilePrint(diagnostic);
				}
				logger.error(error);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String compilePrint(Diagnostic<?> diagnostic) {
		StringBuffer res = new StringBuffer();
		res.append("Code:[" + diagnostic.getCode() + "]\n");
		res.append("Kind:[" + diagnostic.getKind() + "]\n");
		res.append("Position:[" + diagnostic.getPosition() + "]\n");
		res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
		res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
		res.append("Source:[" + diagnostic.getSource() + "]\n");
		res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
		res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
		res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
		return res.toString();
	}

}
