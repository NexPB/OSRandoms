package com.logicail.wrappers.loaders;

import com.logicail.wrappers.NpcDefinition;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class NpcDefinitionLoader extends ArchiveLoader<NpcDefinition> {
	public NpcDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 9);
	}

	@Override
	public NpcDefinition load(int id) {
		FileData data = getValidFile(id);
		NpcDefinition ret = new NpcDefinition(this, id);
		ret.decode(data.getDataAsStream());
		return ret;
	}
}
