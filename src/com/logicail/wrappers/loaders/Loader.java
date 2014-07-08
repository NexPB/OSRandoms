package com.logicail.wrappers.loaders;

import com.sk.cache.fs.Archive;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.CacheType;
import com.sk.cache.fs.FileData;
import com.sk.cache.meta.ArchiveMeta;
import com.sk.cache.meta.ReferenceTable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:07
 */
public abstract class Loader<T> {

	protected final CacheType cache;
	private final int archiveId;
	protected final CacheSystem cacheSystem;
	public final int size;
	public final int version;

	public Loader(CacheSystem cacheSystem, CacheType cache, int archiveId) {
		this.cacheSystem = cacheSystem;
		this.cache = cache;
		this.archiveId = archiveId;
		final ReferenceTable table = this.cache.getTable();
		final ArchiveMeta entry = table.getEntry(archiveId);
		version = entry.getVersion();
		size = entry.getChildCount();
	}

	public CacheSystem getCacheSystem() {
		return cacheSystem;
	}

	protected FileData getValidFile(int id) {
		FileData ret = getFile(id);
		if (ret == null)
			throw new IllegalArgumentException("Bad id");
		return ret;
	}

	protected FileData getFile(int id) {
		final Archive archive = cache.getArchive(archiveId);
		FileData data = archive.getFile(id);
		if (data == null)
			return null;
		return data;
	}

	public abstract T get(int id);
}
