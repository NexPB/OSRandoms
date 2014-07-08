package com.logicail.script;

import com.logicail.wrappers.NpcDefinition;
import com.logicail.wrappers.ObjectDefinition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Interactive;
import org.powerbot.script.rt4.Npc;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Arrays;

@Script.Manifest(name = "Cache loading test", description = "loads definitions from cache")
public class DefinitionLoadingScript extends PollingScript<ClientContext> implements PaintListener {
	private final DefinitionManager definitionManager;
	private final DefinitionCache<ObjectDefinition> objects;
	private final DefinitionCache<NpcDefinition> npcs;

	public DefinitionLoadingScript() throws FileNotFoundException {
		definitionManager = new DefinitionManager(ctx);
		objects = definitionManager.getLoader(ObjectDefinition.class);
		npcs = definitionManager.getLoader(NpcDefinition.class);
	}

	@Override
	public void poll() {

	}

	@Override
	public void repaint(Graphics graphics) {
		for (Npc npc : ctx.npcs.select().within(20).select(Interactive.areInViewport())) {
			NpcDefinition definition = npcs.get(npc.id());
			if (definition != null) {
				final Point p = npc.centerPoint();
				graphics.drawString(definition.name + "", p.x, p.y);
				graphics.drawString(Arrays.toString(definition.modelIds), p.x, p.y + 15);
				// Note don't need to load child, the id the client shows is the child
			}
		}
	}
}
