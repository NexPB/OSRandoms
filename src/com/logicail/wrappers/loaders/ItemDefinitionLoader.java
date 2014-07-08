package com.logicail.wrappers.loaders;

import com.sk.cache.fs.CacheSystem;
import com.sk.cache.fs.FileData;
import com.logicail.wrappers.ItemDefinition;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 18:06
 */
public class ItemDefinitionLoader extends Loader<ItemDefinition> {
	public ItemDefinitionLoader(CacheSystem cacheSystem) {
		super(cacheSystem, cacheSystem.getCacheSource().getCacheType(2), 10);

		ItemDefinition.loader = this;
	}

	public ItemDefinition get(int id) {
		final FileData data = getValidFile(id);

		return new ItemDefinition(id, data.getDataAsStream());
	}
}
