package pb.osrandoms.randoms;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;
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
 */
public class FrogCave extends OSRandom {
	
	private final int[] TARGET_MODELS = {6948, 6944};
	private final String FROG = "Frog";
	private final Filter<Npc> FROG_FILTER = new Filter<Npc>() {

		@Override
		public boolean accept(Npc arg0) {
			if (!arg0.name().equalsIgnoreCase(FROG))return false;
			final NpcDefinition def = npcLoader.get(arg0.id());
			return def != null && Arrays.equals(def.modelIds, TARGET_MODELS);
		}
		
	};
	
	private final DefinitionCache<NpcDefinition> npcLoader;

	public FrogCave(RandomContext ctx) {
		super(ctx);
		this.npcLoader = ctx.definitions.getLoader(NpcDefinition.class);
	}
	
	@Override
	public boolean valid() {
		return !ctx.objects.select().name("gas hole").isEmpty() && !ctx.npcs.select().select(FROG_FILTER).isEmpty();
	}

	@Override
	public void run() {
		if (ctx.players.local().animation() != -1 || ctx.players.local().inMotion()) {
			Condition.sleep(Random.getDelay());
			return;
		}
		if (ctx.randomMethods.clickContinue()) {
			status("[FrogCave] Handling widgets.");
			Condition.wait(new Callable<Boolean>() {
				
				@Override
				public Boolean call() throws Exception {
					return !ctx.randomMethods.queryContinue();
				}
				
			});
		} else {
			final Component comp = ctx.randomMethods.getComponentByText("i'm very sorry");
			if (comp.valid() && comp.click()) {
				status("[FrogCave] Handling widgets.");
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return !comp.valid();
					}
					
				});
			} else {
				status("[FrogCave] Talking to FROG.");
				final Npc frog = ctx.npcs.poll();
				if (frog.inViewport() && frog.interact(Menu.filter(""))) {
					Condition.wait(new Callable<Boolean>() {
	
						@Override
						public Boolean call() throws Exception {
							return ctx.randomMethods.queryContinue();
						}
						
					});
					return;
				}
				ctx.movement.step(frog);
			}
		}
	}

}
