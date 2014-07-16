package com.logicail.wrappers.loaders;

import com.logicail.wrappers.Wrapper;
import com.sk.cache.fs.Archive;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.CacheType;
import com.sk.cache.fs.FileData;

public abstract class WrapperLoader<T extends Wrapper> {

	protected final CacheSystem cacheSystem;
	protected final CacheType cache;

	public WrapperLoader(CacheSystem cacheSystem, CacheType cache) {
		this.cacheSystem = cacheSystem;
		this.cache = cache;
	}

	public CacheSystem getCacheSystem() {
		return cacheSystem;
	}

	public abstract T load(int id);

	public abstract boolean canLoad(int id);

	protected FileData getValidFile(int id) {
		FileData ret = getFile(id);
		if (ret == null)
			throw new IllegalArgumentException("Bad id");
		return ret;
	}

	protected FileData getFile(int id) {
		Archive archive = cache.getArchive(id >>> 8);
		if (archive == null)
			return null;
		return archive.getFile(id & 0xff);
	}
}