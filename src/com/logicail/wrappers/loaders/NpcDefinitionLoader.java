package com.logicail.wrappers.loaders;

import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;
import com.logicail.wrappers.NpcDefinition;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class NpcDefinitionLoader extends Loader<NpcDefinition> {
	public NpcDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 9);
	}

	public NpcDefinition get(int id) {
		final FileData data = getValidFile(id);

		return new NpcDefinition(id, data.getDataAsStream());
	}
}
