package com.logicail;

import com.logicail.wrappers.VarpDefinition;
import com.logicail.wrappers.loaders.ArchiveLoader;
import com.logicail.wrappers.loaders.ScriptDefinitionLoader;
import com.sk.cache.DataSource;
import com.sk.cache.fs.CacheSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

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

	private static void print(ScriptDefinitionLoader loader) throws FileNotFoundException, NoSuchMethodException {

		TreeMap<Integer, List<VarpDefinition>> map = new TreeMap<Integer, List<VarpDefinition>>(); // Just so don't have to sort keys

		for (int i = 0; i < loader.size; i++) {
			if (!loader.canLoad(i)) {
				continue;
			}

			try {
				final VarpDefinition script = loader.load(i);
				List<VarpDefinition> list;
				if (!map.containsKey(script.configId)) {
					list = new LinkedList<VarpDefinition>();
					map.put(script.configId, list);
				}
				list = map.get(script.configId);
				list.add(script);
			} catch (IllegalArgumentException e) {
				if (!e.getMessage().equals("Bad id") && !e.getMessage().equals("Empty")) {
					throw e;
				}
			}
		}
		PrintWriter writer = null;
		try {
			final File output = new File("output" + File.separator + loader.getClass().getDeclaredMethod("get", int.class).getReturnType().getSimpleName().toLowerCase() + "-" + loader.version + ".txt");
			output.mkdirs();
			writer = new PrintWriter(output);
			writer.println("Version: " + loader.version);
			writer.println("Generated: " + new Date().toString());
			writer.println();

			for (Integer key : map.keySet()) {
				writer.println("[id=" + key + "]");

				final List<VarpDefinition> scripts = map.get(key);
				for (VarpDefinition script : scripts) {
					writer.println("  [" + script.id + "] ctx.varpbits.varpbit(" + script.configId + ", " + script.lowerBitIndex + ", 0x" + Integer.toHexString(script.mask) + ")");
				}
				writer.println("");
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private static void print(ArchiveLoader<?> loader) throws FileNotFoundException, NoSuchMethodException {
		PrintWriter writer = null;
		try {
			final File output = new File("output" + File.separator + loader.getClass().getDeclaredMethod("get", int.class).getReturnType().getSimpleName().toLowerCase() + "-" + loader.version + ".txt");
			output.mkdirs();
			writer = new PrintWriter(output);
			writer.println("Version: " + loader.version);
			writer.println("Generated: " + new Date().toString());
			writer.println();

			for (int i = 0; i < loader.size; i++) {
				if (!loader.canLoad(i)) {
					continue;
				}
				try {
					writer.println(loader.load(i));
				} catch (IllegalArgumentException e) {
					if (!e.getMessage().equals("Bad id") && !e.getMessage().equals("Empty")) {
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
