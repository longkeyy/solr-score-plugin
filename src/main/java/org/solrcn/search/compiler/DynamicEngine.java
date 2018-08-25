package org.solrcn.search.compiler;

import javax.tools.*;

import org.apache.solr.core.SolrCore;

import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;

public class DynamicEngine {
	
	private static DynamicEngine ourInstance = new DynamicEngine();

	public static DynamicEngine getInstance() {
		return ourInstance;
	}

	private URLClassLoader parentClassLoader;
	private String classpath;

	private DynamicEngine() {
		this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
		this.buildClassPath();
	}

	private void buildClassPath() {
		this.classpath = null;
		StringBuilder sb = new StringBuilder();
		for (URL url : ((URLClassLoader) this.getClass().getClassLoader()).getURLs()) {
			String p = url.getFile();
			sb.append(p).append(File.pathSeparator);
		}
		try {
			for (URL url : ((URLClassLoader) Class.forName("org.apache.solr.core.SolrResourceLoader").getClassLoader()).getURLs()) {
				String p = url.getFile();
				sb.append(p).append(File.pathSeparator);
			}
		} catch (ClassNotFoundException e) {
			SolrCore.log.warn("load solr core class error",e);
		}
		try {
			for (URL url : ((URLClassLoader) Class.forName("org.slf4j.Logger").getClassLoader()).getURLs()) {
				String p = url.getFile();
				sb.append(p).append(File.pathSeparator);
			}
		} catch (ClassNotFoundException e) {
			SolrCore.log.warn("load slf4j class error",e);
		}
		this.classpath = sb.toString();
		SolrCore.log.info("DynamicEngine classpath:\n{}", classpath);
	}

	public Class javaCodeToObject(String fullClassName, String javaCode)  {
		long start = System.currentTimeMillis();
		// Object instance = null;
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, null));

		List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
		jfiles.add(new CharSequenceJavaFileObject(fullClassName, javaCode));

		List<String> options = new ArrayList<String>();
		options.add("-encoding");
		options.add("UTF-8");
		options.add("-classpath");
		options.add(this.classpath);

		JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
		boolean success = task.call();

		if (success) {
			DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(this.parentClassLoader);
			JavaClassObject jco = fileManager.getMainJavaClassObject();
			List<JavaClassObject> innerClassJcos = fileManager.getInnerClassJavaClassObject();
			if (innerClassJcos != null && innerClassJcos.size() > 0) {
				for (JavaClassObject inner : innerClassJcos) {
					String name = inner.getName();
					name = name.substring(1, name.length() - 6);
					SolrCore.log.info("dynamicClassLoader innerClassJcos " + name);
					dynamicClassLoader.loadClass(name, inner);
				}
			}
			Class clazz = dynamicClassLoader.loadClass(fullClassName, jco);
			long end = System.currentTimeMillis();
			SolrCore.log.info("javaCodeToObject use: {} ms", (end - start));
			return clazz;
			// instance = clazz.newInstance();
		} else {
			StringBuilder error = new StringBuilder();
			for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
				error.append(compilePrint(diagnostic));
			}
			SolrCore.log.warn("javaCodeToObject error: ",error.toString());
		}
		return null;
		// return instance;
	}

	private String compilePrint(Diagnostic diagnostic) {
//		System.out.println("Code:" + diagnostic.getCode());
//		System.out.println("Kind:" + diagnostic.getKind());
//		System.out.println("Position:" + diagnostic.getPosition());
//		System.out.println("Start Position:" + diagnostic.getStartPosition());
//		System.out.println("End Position:" + diagnostic.getEndPosition());
//		System.out.println("Source:" + diagnostic.getSource());
//		System.out.println("Message:" + diagnostic.getMessage(null));
//		System.out.println("LineNumber:" + diagnostic.getLineNumber());
//		System.out.println("ColumnNumber:" + diagnostic.getColumnNumber());
		StringBuilder res = new StringBuilder();
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