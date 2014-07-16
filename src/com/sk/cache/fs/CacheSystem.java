package com.sk.cache.fs;

import com.logicail.wrappers.Definition;
import com.logicail.wrappers.loaders.*;
import com.sk.cache.DataSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CacheSystem {
	public final ItemDefinitionLoader itemLoader;
	public final ObjectDefinitionLoader objectLoader;
	public final ScriptDefinitionLoader varpLoader;
	public final NpcDefinitionLoader npcLoader;
	private final CacheSource cache;
	private final Map<Type, WrapperLoader<?>> loaderMap = new HashMap<Type, WrapperLoader<?>>();

	public CacheSystem(CacheSource cache) {
		this.cache = cache;
		addLoader(itemLoader = new ItemDefinitionLoader(this));
		addLoader(objectLoader = new ObjectDefinitionLoader(this));
		addLoader(varpLoader = new ScriptDefinitionLoader(this));
		addLoader(npcLoader = new NpcDefinitionLoader(this));
	}

	public <T extends Definition> void addLoader(WrapperLoader<T> loader) {
		ParameterizedType type = (ParameterizedType) loader.getClass().getGenericSuperclass();
		loaderMap.put(type.getActualTypeArguments()[0], loader);
	}

	public CacheSystem(DataSource source) {
		this(new CacheSource(source));
	}

	public CacheSystem(File cacheFolder) throws FileNotFoundException {
		this(new DataSource(cacheFolder));
	}

	public CacheSource getCacheSource() {
		return cache;
	}

	@SuppressWarnings("unchecked")
	public <T extends Definition> WrapperLoader<T> getLoader(Class<T> wrapperClass) {
		return (WrapperLoader<T>) loaderMap.get(wrapperClass);
	}
}
