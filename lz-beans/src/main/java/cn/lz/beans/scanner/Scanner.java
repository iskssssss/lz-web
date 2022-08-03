package cn.lz.beans.scanner;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Scanner {

	public static Set<Class<?>> getClasss(Set<Class<?>> classes, String packageName) throws Exception {
		String packageDirName = packageName.replace('.', '/').replace('\\', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				switch (protocol) {
					case "file":
						String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
						addClass(classes, filePath, packageName);
						break;
					case "jar":
						addJar(classes, url, packageDirName);
						break;
					default:
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	public static <A extends Annotation> Set<Class<?>> getAnnotationClasses(String packageName, Class<A> annotationClass) throws Exception {
		Set<Class<?>> classes = new HashSet<>();
		getClasss(classes, packageName);
		if (classes.isEmpty()) {
			return Collections.emptySet();
		}
		Set<Class<?>> controllers = new HashSet<>();
		for (Class<?> cls : classes) {
			Class<? extends Annotation> source = (Class<? extends Annotation>) cls;
			if (!isAnnotation(source, annotationClass)) {
				continue;
			}
			controllers.add(cls);
		}
		return controllers;
	}

	public static boolean isAnnotation(Class<? extends Annotation> source, Class<? extends Annotation> target) {
		if (source.isAnnotationPresent(target)) {
			return true;
		}
		Annotation[] annotations = source.getAnnotations();
		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			if (annotationType.isAssignableFrom(Inherited.class) ||
					annotationType.isAssignableFrom(Documented.class) ||
					annotationType.isAssignableFrom(Retention.class) ||
					annotationType.isAssignableFrom(Target.class)) {
				continue;
			}
			return isAnnotation(annotationType, target);
		}
		return false;
	}

	private static void addJar(Set<Class<?>> classes, URL url, String packageDirName) {
		JarFile jar;
		try {
			jar = ((JarURLConnection) url.openConnection()).getJarFile();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String name = entry.getName();
				// 如果是以/开头的
				if (name.charAt(0) == '/') {
					// 获取后面的字符串
					name = name.substring(1);
				}
				if (!name.startsWith(packageDirName)) {
					continue;
				}
				int idx = name.lastIndexOf('/');
				if (idx == -1) {
					continue;
				}
				String packageName = name.substring(0, idx).replace('/', '.');
				if (!name.endsWith(".class") || entry.isDirectory()) {
					continue;
				}
				String className = name.substring(packageName.length() + 1, name.length() - 6);
				try {
					classes.add(Class.forName(packageName + '.' + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addClass(Set<Class<?>> classes, String filePath, String packageName) throws Exception {
		File[] files = new File(filePath).listFiles(file -> (file.isFile() && file.getName().endsWith(".class")) || file.isDirectory());
		assert files != null;
		for (File file : files) {
			String fileName = file.getName();
			if (file.isFile()) {
				String className = fileName.substring(0, fileName.lastIndexOf("."));
				if (!packageName.isEmpty()) {
					className = packageName.replace('/', '.').replace('\\', '.') + "." + className;
				}
				Class<?> tClass = classLoader.loadClass(className);
				if (tClass.isAnnotation() || tClass.isEnum() || tClass.isMemberClass()) {
					continue;
				}
				classes.add(tClass);
				continue;
			}
			if (file.isDirectory()) {
				String path = file.getPath();
				String str = "target\\classes";
				int length = str.length();
				int x = path.indexOf(str);
				Scanner.getClasss(classes, path.substring(x + length + 1));
			}
		}
	}

	private final static ClassLoader classLoader = new ClassLoader() {

		@Override
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			return super.loadClass(name);
		}
	};

	public static String getAppPath(Class<?> cls) {
		//检查用户传入的参数是否为空
		if (cls == null)
			throw new java.lang.IllegalArgumentException("参数不能为空！");
		ClassLoader loader = cls.getClassLoader();
		//获得类的全名，包括包名
		String clsName = cls.getName() + ".class";
		//获得传入参数所在的包
		Package pack = cls.getPackage();
		StringBuilder path = new StringBuilder();
		//如果不是匿名包，将包名转化为路径
		if (pack != null) {
			String packName = pack.getName();
			//此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
			if (packName.startsWith("java.") || packName.startsWith("javax."))
				throw new java.lang.IllegalArgumentException("不要传送系统类！");
			//在类的名称中，去掉包名的部分，获得类的文件名
			clsName = clsName.substring(packName.length() + 1);
			//判定包名是否是简单包名，如果是，则直接将包名转换为路径，
			if (!packName.contains(".")) path = new StringBuilder(packName + "/");
			else {//否则按照包名的组成部分，将包名转换为路径
				int start = 0, end;
				end = packName.indexOf(".");
				while (end != -1) {
					path.append(packName, start, end).append("/");
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path.append(packName.substring(start)).append("/");
			}
		}
		//调用ClassLoader的getResource方法，传入包含路径信息的类文件名
		java.net.URL url = loader.getResource(path + clsName);
		//从URL对象中获取路径信息
		String realPath = url.getPath();
		//去掉路径信息中的协议名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1) realPath = realPath.substring(pos + 5);
		//去掉路径信息最后包含类文件信息的部分，得到类所在的路径
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		//如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
		if (realPath.endsWith("!"))
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
   /*------------------------------------------------------------
    ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
     中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
     的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
     中文及空格路径
   -------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}
}
