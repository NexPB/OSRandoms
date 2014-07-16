package pb.osrandoms.randoms;

import com.logicail.wrappers.NpcDefinition;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Menu;
import org.powerbot.script.rt4.Npc;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @author Robert G
 */
@OSRandom.RandomManifest(name = "Frog Cave")
public class FrogCave extends OSRandom {

	private final int[] TARGET_MODELS = {6948, 6944};
	private final String FROG = "Frog";
	private final Filter<Npc> FROG_FILTER = new Filter<Npc>() {

		@Override
		public boolean accept(Npc npc) {
			if (!npc.name().equalsIgnoreCase(FROG)) return false;
			final NpcDefinition def = ctx.definitions.get(npc);
			return def != null && Arrays.equals(def.modelIds, TARGET_MODELS);
		}

	};

	public FrogCave(RandomContext ctx) {
		super(ctx);
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
			status("Handling widgets.");
			Condition.wait(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return !ctx.randomMethods.queryContinue();
				}

			});
		} else {
			final Component comp = ctx.randomMethods.getComponentByText("i'm very sorry");
			if (comp.valid() && comp.click()) {
				status("Handling widgets.");
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return !comp.valid();
					}

				});
			} else {
				status("Talking to Frog.");
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
