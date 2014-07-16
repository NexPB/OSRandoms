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
public class ScriptDefinitionLoader extends ArchiveLoader<VarpDefinition> {
	public ScriptDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 14);
	}

	@Override
	public VarpDefinition load(int id) {
		final FileData data = getValidFile(id);
		final VarpDefinition definition = new VarpDefinition(this, id);
		definition.decode(data.getDataAsStream());
		return definition;
	}
}
