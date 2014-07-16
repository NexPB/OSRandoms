package com.logicail.wrappers.loaders;

import com.logicail.wrappers.Wrapper;
import com.sk.cache.fs.Archive;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.CacheType;
import com.sk.cache.fs.FileData;
import com.sk.cache.meta.ArchiveMeta;
import com.sk.cache.meta.ReferenceTable;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 15/07/2014
 * Time: 20:18
 */
public abstract class ArchiveLoader<T extends Wrapper> extends WrapperLoader<T> {
	protected final int archiveId;
	public final int size;
	public final int version;

	public ArchiveLoader(CacheSystem cacheSystem, CacheType cache, int archiveId) {
		super(cacheSystem, cache);
		this.archiveId = archiveId;
		final ReferenceTable table = cache.getTable();
		final ArchiveMeta entry = table.getEntry(archiveId);
		version = entry.getVersion();
		size = entry.getChildCount();
	}


	protected FileData getFile(int id) {
		final Archive archive = cache.getArchive(archiveId);
		FileData data = archive.getFile(id);
		if (data == null)
			return null;
		return data;
	}

	@Override
	public boolean canLoad(int id) {
		return getFile(id) != null;
	}
}
