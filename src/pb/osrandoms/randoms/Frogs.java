package pb.osrandoms.randoms;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Menu;
import org.powerbot.script.rt4.Npc;

import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import com.logicail.DefinitionCache;
import com.logicail.wrappers.NpcDefinition;

/**
 * 
 * @author Robert G
 * 
 * TODO: Find other options as there are more than 3.
 *
 */
@OSRandom.RandomManifest(name = "Frogs")
public class Frogs extends OSRandom {
	
	private final int[] TARGET_MODELS = {6948, 6944};
	private final String[] OPTIONS = {"allright", "okay", "sure, i will"};
	private final String FROG = "Frog";
	private final Filter<Npc> FROG_FILTER = new Filter<Npc>() {

		@Override
		public boolean accept(Npc arg0) {
			if (!arg0.name().equalsIgnoreCase(FROG) || !arg0.interacting().equals(ctx.players.local()))return false;
			final NpcDefinition def = npcLoader.get(arg0.id());
			return def != null && Arrays.equals(def.modelIds, TARGET_MODELS);
		}
		
	};
	
	private final DefinitionCache<NpcDefinition> npcLoader;

	public Frogs(RandomContext ctx) {
		super(ctx);
		this.npcLoader = ctx.definitions.getLoader(NpcDefinition.class);
	}
	
	@Override
	public boolean valid() {
		return !ctx.npcs.select().select(FROG_FILTER).isEmpty();
	}
	
	private Component getComp() {
		Component comp = ctx.widgets.widget(0).component(0);
		for (String s : OPTIONS) {
			if ((comp = ctx.randomMethods.getComponentByText(s)).valid() && comp.visible()) {
				return comp;
			}
		}
		return comp;
	}

	@Override
	public void run() {
		if (ctx.randomMethods.clickContinue()) {
			status("Clicking continue.");
			Condition.wait(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return !ctx.randomMethods.queryContinue();
				}
				
			});
			return;
		}
		final Component click = getComp();
		if (click.valid()) {
			status("Clicking " + click.text());
			if (click.click()) {
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return !click.valid();
					}
					
				});
			}
			return;
		}
		final Npc frog = ctx.npcs.poll();
		if (frog.valid()) {
			if (frog.animation() != -1) {
				status("Waiting for Frog to transform.");
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return frog.animation() == -1;
					}
					
				});
				return;
			}
			status("Talking to frog.");
			if (frog.inViewport() && frog.interact(Menu.filter("talk", "frog"))) {
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return ctx.randomMethods.queryContinue();
					}
					
				});
				return;
			}
			ctx.randomMethods.walkTileOnScreen(frog);
		}
	}

}
