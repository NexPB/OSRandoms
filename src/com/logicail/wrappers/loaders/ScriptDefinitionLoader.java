package com.logicail.wrappers.loaders;

import com.logicail.wrappers.VarpDefinition;
import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class ScriptDefinitionLoader extends Loader<VarpDefinition> {
	public ScriptDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 14);
	}

	public VarpDefinition get(int id) {
		final FileData data = getValidFile(id);

		return new VarpDefinition(id, data.getDataAsStream());
	}
}
