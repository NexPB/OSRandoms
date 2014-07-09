package pb.osrandoms.randoms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;

import com.logicail.DefinitionCache;
import com.logicail.wrappers.NpcDefinition;

import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

/**
 * 
 * @author Robert G
 *
 *         TODO: Check if new system to find suspect works properly.
 */
@OSRandom.RandomManifest(name = "Molly")
public class Molly extends OSRandom {

	private static final int CONTROL_INTERFACEGROUP = 240;
	private static final int CONTROLS_GRAB = 28;
	private static final int CONTROLS_UP = 29;
	private static final int CONTROLS_DOWN = 30;
	private static final int CONTROLS_LEFT = 31;
	private static final int CONTROLS_RIGHT = 32;

	private final DefinitionCache<NpcDefinition> npcLoader;

	private Npc molly, suspect;

	public Molly(RandomContext ctx) {
		super(ctx);
		this.npcLoader = ctx.definitions.getLoader(NpcDefinition.class);
	}

	private boolean clawMoved(final Tile prevClawLoc) {
		final GameObject claw = objectByName("evil claw");
		if (!claw.valid()) {
			return false;
		}
		final Tile currentClawLoc = claw.tile();
		return !prevClawLoc.equals(currentClawLoc);
	}

	private boolean controllerOpen() {
		return ctx.widgets.widget(CONTROL_INTERFACEGROUP).component(CONTROLS_GRAB).valid();
	}

	private boolean inControlRoom() {
		final GameObject o = objectByName("door");
		return o.valid() && o.tile().x() < ctx.players.local().tile().x();
	}

	private Npc molly() {
		return ctx.npcs.select().name("Molly").poll();
	}

	private Npc suspect(final Npc molly) {
		final NpcDefinition mollyDef = npcLoader.get(molly.id());
		if (mollyDef != null) {
			return ctx.npcs.select().select(new Filter<Npc>() {

				@Override
				public boolean accept(Npc arg0) {
					if (!arg0.name().equalsIgnoreCase("suspect")) return false;
					final NpcDefinition suspect = npcLoader.get(arg0.id());
					return suspect != null && Arrays.equals(mollyDef.modelIds, suspect.modelIds);
				}

			}).poll();
		}
		return ctx.npcs.nil();
	}

	private void navigateClaw() {
		if (!controllerOpen() || suspect == null || !suspect.valid()) {
			return;
		}
		GameObject claw;
		while ((claw = objectByName("evil claw")).valid() && suspect.valid()) {
			final Tile clawLoc = claw.tile();
			final Tile susLoc = suspect.tile();
			final ArrayList<Integer> options = new ArrayList<Integer>();
			if (susLoc.x() > clawLoc.x()) {
				options.add(CONTROLS_LEFT);
			}
			if (susLoc.x() < clawLoc.x()) {
				options.add(CONTROLS_RIGHT);
			}
			if (susLoc.y() > clawLoc.y()) {
				options.add(CONTROLS_DOWN);
			}
			if (susLoc.y() < clawLoc.y()) {
				options.add(CONTROLS_UP);
			}
			if (options.isEmpty()) {
				options.add(CONTROLS_GRAB);
			}
			final Component i = ctx.widgets.widget(CONTROL_INTERFACEGROUP).component(options.get(Random.nextInt(0, options.size())));
			target.set(i);
			if (i.click()) {
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return clawMoved(clawLoc);
					}

				});
			}
		}
	}

	private GameObject objectByName(String name) {
		return ctx.objects.select().name(name).nearest().poll();
	}

	private boolean openDoor() {
		final GameObject door = objectByName("door");
		if (door.valid()) {
			door.bounds(new int[]{60, 80, -232, 0, -44, 68});
			if (door.inViewport()) {
				target.set(door);
				return door.interact("Open");
			}
			ctx.camera.turnTo(door);
			if (!door.inViewport()) {
				if (ctx.randomMethods.walkTileOnScreen(door)) {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		final Component cont = ctx.randomMethods.getContinue();
		if (cont.valid()) {
			status("[Molly] Handling widgets.");
			if (cont.click()) {
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return !cont.valid();
					}

				});
			}
			return;
		}
		if (ctx.players.local().inMotion() || ctx.players.local().animation() != -1) {
			Condition.sleep(Random.getDelay());
			return;
		}
		final int suspectsLoaded = ctx.npcs.select().name("suspect").size();
		if (!inControlRoom()) {
			if (suspectsLoaded > 1 && suspectsLoaded < 5) {
				suspect = null;
				status("[Molly] Talking to Molly.");
				if (molly.inViewport()) {
					target.set(molly);
					if (molly.interact("talk")) {
						Condition.wait(new Callable<Boolean>() {

							@Override
							public Boolean call() throws Exception {
								return ctx.randomMethods.getContinue().valid();
							}

						});
					}
				} else {
					ctx.camera.turnTo(molly);
				}
			} else {
				if (suspect == null) {
					suspect = suspect(molly);
					status("[Molly] Molly ID: " + Integer.toString(molly.id()));
					status("[Molly] Evil Twin ID: " + Integer.toString(suspect.id()));
				}
				final Component yes = ctx.randomMethods.getComponentByText("yes, I");
				if (yes.valid()) {
					status("[Molly] Handling widgets.");
					target.set(yes);
					if (yes.click()) {
						Condition.wait(new Callable<Boolean>() {

							@Override
							public Boolean call() throws Exception {
								return !yes.valid();
							}

						});
					}
				} else {
					status("[Molly] Entering control room.");
					if (openDoor()) {
						Condition.wait(new Callable<Boolean>() {

							@Override
							public Boolean call() throws Exception {
								return ctx.randomMethods.clickContinue();
							}

						});
					}
				}
			}
		} else {
			if (suspectsLoaded > 2) {
				if (!controllerOpen()) {
					status("[Molly] Opening control panel.");
					final GameObject panel = objectByName("control panel");
					if (panel.valid()) {
						if (panel.inViewport()) {
							target.set(panel);
							if (panel.interact("use")) {
								Condition.wait(new Callable<Boolean>() {

									@Override
									public Boolean call() throws Exception {
										return controllerOpen();
									}

								});
							}
						} else {
							ctx.camera.turnTo(panel);
							if (!panel.inViewport() && ctx.randomMethods.walkTileOnScreen(panel)) {
								Condition.sleep(Random.nextInt(800, 1300));
							}
						}
					}
				} else {
					status("[Molly] Navigating claw.");
					navigateClaw();
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return ctx.randomMethods.clickContinue();
						}

					}, 100, 120);
				}
			} else {
				status("[Molly] Exiting control room.");
				if (openDoor()) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return !inControlRoom();
						}

					});
				}
			}
		}
	}

	@Override
	public boolean valid() {
		return (molly = molly()).valid() && molly.interacting().equals(ctx.players.local())
				|| objectByName("control panel").valid();
	}

}
