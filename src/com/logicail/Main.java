package com.logicail;

import com.logicail.wrappers.loaders.Loader;
import com.sk.cache.DataSource;
import com.sk.cache.fs.CacheSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 06/07/2014
 * Time: 16:37
 */
public class Main {
	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		final CacheSystem system = new CacheSystem(new DataSource(new File(System.getProperty("user.home") + File.separator + "jagexcache" + File.separator + "oldschool" + File.separator + "LIVE" + File.separator)));
		print(system.itemLoader);
		print(system.objectLoader);
		print(system.npcLoader);
		print(system.varpLoader);
	}

	private static void print(Loader loader) throws FileNotFoundException, NoSuchMethodException {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(loader.getClass().getDeclaredMethod("get", int.class).getReturnType().getSimpleName().toLowerCase() + ".txt");
			writer.println("Version: " + loader.version);
			writer.println("Generated: " + new Date().toString());
			writer.println();

			for (int i = 0; i < loader.size; i++) {
				try {
					writer.println(loader.get(i));
				} catch (IllegalArgumentException e) {
					if (!e.getMessage().equals("Bad id")) {
						throw e;
					}
				}
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
