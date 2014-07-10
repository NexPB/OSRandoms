package pb.osrandoms.randoms;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Menu;
import org.powerbot.script.rt4.Npc;

import com.logicail.DefinitionCache;
import com.logicail.wrappers.NpcDefinition;

import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

/**
 * 
 * @author Robert G
 *		
 *		TODO Check lever interaction works ok, if not adjust bounds.
 */
@OSRandom.RandomManifest(name = "Prison Pete")
public class PrisonPete extends OSRandom {
	
	private final int key_id = 6966;
	private final int balloon_type_widget_id = 273;
	private final int model_component_id = 3;
	private final int balloon_type_close_component_id = 4;
	private final int setting_balloons_popped_id = 638;
	private final int[] lever_bounds = {56, 80, -140, -48, -24, 32};
	private final Tile exit = new Tile(2104, 4466, 0);
	private final String lever = "Lever";
	private final String balloon_animal = "Balloon Animal";
	private final Filter<Npc> balloon_filter = new Filter<Npc>() {
		@Override
		public boolean accept(Npc arg0) {
			final NpcDefinition def = npcLoader.get(arg0.id());
			return def != null && Arrays.equals(def.modelIds, target_models);
		}
	};
	private final DefinitionCache<NpcDefinition> npcLoader;
	
	private int[] target_models = null;

	public PrisonPete(RandomContext ctx) {
		super(ctx);
		this.npcLoader = ctx.definitions.getLoader(NpcDefinition.class);
	}
	
	private enum Balloon {
		
		SKINNY_STRAIGHT_TAIL(10750, 10736),
		FAT_NO_HORNS(10751, 10737),
		SKINNY_BOBBED_TAIL(11028, 16034),
		FAT_WITH_HORNS(11034, 27098);
		
		private int model_id;
		private int[] model_ids;
		
		private Balloon(int model_id, int... model_ids) {
			this.model_id = model_id;
			this.model_ids = model_ids;
		}
		
		@Override
		public String toString() {
			return name().substring(0, 1) + name().substring(1).toLowerCase();
		}
		
	}
	
	@Override
	public boolean valid() {
		return !ctx.npcs.select().name(balloon_animal).isEmpty() || ctx.randomMethods.getNpc("Prison Pete").valid();
	}

	@Override
	public void run() {
		if (target_models != null && !ctx.inventory.select().id(key_id).isEmpty()) {
			target_models = null;
			return;
		}
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
		if (ctx.players.local().inMotion() || ctx.players.local().animation() != -1) {
			Condition.sleep(Random.getDelay());
			return;
		}
		if (ctx.varpbits.varpbit(setting_balloons_popped_id) == 96) {
			status("[PrisonPete] Exiting prison.");
			ctx.movement.step(exit);
			return;
		}
		if (target_models == null) {
			final Component model_component = ctx.widgets.widget(balloon_type_widget_id).component(model_component_id);
			if (model_component.valid()) {
				for (Balloon balloon : Balloon.values()) {
					if (balloon.model_id == model_component.modelId()) {
						status("Target set to: " + balloon.toString());
						target_models = balloon.model_ids;
						break;
					}
				}
				final Component close = ctx.widgets.widget(balloon_type_widget_id).component(balloon_type_close_component_id);
				target.set(close);
				if (close.click()) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return !model_component.valid();
						}

					});
				}
				return;
			}
			if (!ctx.objects.select().name(lever).isEmpty()) {
				status("Pulling Lever.");
				final GameObject lever = ctx.objects.poll();
				target.set(lever);
				lever.bounds(lever_bounds);
				if (lever.inViewport()) {
					if (lever.interact(Menu.filter("Pull", "Lever"))) {
						Condition.wait(new Callable<Boolean>() {
	
							@Override
							public Boolean call() throws Exception {
								return model_component.valid();
							}
	
						});
					}
				}
			}
			return;
		}
		final Npc balloon = ctx.npcs.select().name(balloon_animal).select(balloon_filter).nearest().poll();
		if (balloon.valid()) {
			status("Popping Balloon Animal.");
			if (balloon.inViewport()) {
				target.set(balloon);
				if (balloon.interact(Menu.filter("Pop", "Balloon Animal"))) {
					Condition.wait(new Callable<Boolean>() {
	
						@Override
						public Boolean call() throws Exception {
							return !ctx.inventory.select().id(key_id).isEmpty();
						}
	
					});
				}
				return;
			}
			ctx.movement.step(balloon);
		}
	}

}
