package com.logicail.accessors;

import com.logicail.DefinitionCache;
import com.logicail.wrappers.*;
import com.sk.cache.fs.CacheSystem;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 20:57
 */
public class DefinitionManager extends ClientAccessor {
	private final CacheSystem system;
	private final Map<Type, DefinitionCache<?>> loaderMap = new HashMap<Type, DefinitionCache<?>>();

	public DefinitionManager(ClientContext arg0) throws FileNotFoundException {
		super(arg0);

		String directory = System.getProperty("user.home") + File.separator + "jagexcache" + File.separator + "oldschool" + File.separator + "LIVE" + File.separator;
		//ctx.controller.script().log.info("Loading cache from: " + directory);

		system = new CacheSystem(new File(directory));

		loaderMap.put(NpcDefinition.class, new DefinitionCache<NpcDefinition>(ctx, this, system.npcLoader));
		loaderMap.put(VarpDefinition.class, new DefinitionCache<VarpDefinition>(ctx, this, system.varpLoader));
		loaderMap.put(ObjectDefinition.class, new DefinitionCache<ObjectDefinition>(ctx, this, system.objectLoader));
		loaderMap.put(ItemDefinition.class, new DefinitionCache<ItemDefinition>(ctx, this, system.itemLoader));
	}

	@SuppressWarnings("unchecked")
	public <T extends Definition> DefinitionCache<T> getLoader(Class<T> wrapperClass) {
		return (DefinitionCache<T>) loaderMap.get(wrapperClass);
	}
}
