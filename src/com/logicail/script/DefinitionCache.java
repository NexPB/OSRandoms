package com.logicail.script;

import com.logicail.wrappers.Definition;
import com.logicail.wrappers.loaders.Loader;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 19:29
 */
public class DefinitionCache<T extends Definition> extends ClientAccessor {
	private static final int MAX_ENTRIES = 100;

	private final DefinitionManager manager;
	private final Loader<T> loader;

	private LinkedHashMap<Integer, T> cache = new LinkedHashMap<Integer, T>(MAX_ENTRIES + 1, 0.75f, true) {
		public boolean removeEldestEntry(Map.Entry eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	public DefinitionCache(ClientContext ctx, DefinitionManager manager, Loader<T> loader) {
		super(ctx);
		this.manager = manager;
		this.loader = loader;
	}

	public T get(int id) {
		if (id < 0) {
			return null;
		}

		if (cache.containsKey(id)) {
			return cache.get(id);
		} else {
			try {
				T definition = loader.get(id);
				cache.put(id, definition);
				return definition;
			} catch (Exception ignored) {
			}
		}

		// TODO: Consider placing child() here

		return null;
	}
}
