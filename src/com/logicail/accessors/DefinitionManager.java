package com.logicail.accessors;

import com.logicail.DefinitionCache;
import com.logicail.wrappers.ItemDefinition;
import com.logicail.wrappers.NpcDefinition;
import com.logicail.wrappers.ObjectDefinition;
import com.logicail.wrappers.VarpDefinition;
import com.sk.cache.fs.CacheSystem;
import org.powerbot.script.rt4.*;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 20:57
 */
public class DefinitionManager extends ClientAccessor {
	private final CacheSystem system;
	private final DefinitionCache<NpcDefinition> npc;
	private final DefinitionCache<VarpDefinition> varp;
	private final DefinitionCache<ObjectDefinition> object;
	private final DefinitionCache<ItemDefinition> item;

	public NpcDefinition get(Npc npc) {
		return this.npc.get(npc.id());
	}

	public ObjectDefinition get(GameObject npc) {
		return this.object.get(npc.id());
	}

	public ItemDefinition get(Item npc) {
		return this.item.get(npc.id());
	}

	public VarpDefinition varp(int scriptId) {
		return this.varp.get(scriptId);
	}

	public DefinitionManager(ClientContext ctx) throws FileNotFoundException {
		super(ctx);

		String directory = System.getProperty("user.home") + File.separator + "jagexcache" + File.separator + "oldschool" + File.separator + "LIVE" + File.separator;
		//ctx.controller.script().log.info("Loading cache from: " + directory);

		system = new CacheSystem(new File(directory));

		npc = new DefinitionCache<NpcDefinition>(this.ctx, this, system.npcLoader);
		varp = new DefinitionCache<VarpDefinition>(this.ctx, this, system.varpLoader);
		object = new DefinitionCache<ObjectDefinition>(this.ctx, this, system.objectLoader);
		item = new DefinitionCache<ItemDefinition>(this.ctx, this, system.itemLoader);
	}

	public NpcDefinition npc(int id) {
		return npc.get(id);
	}

	public ObjectDefinition object(int id) {
		return object.get(id);
	}
}
