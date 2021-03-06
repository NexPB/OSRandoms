package pb.osrandoms.randoms;

import com.logicail.wrappers.NpcDefinition;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @author Robert G
 *         <p/>
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

	private Npc molly;

	private int[] target_models = null;

	public Molly(RandomContext ctx) {
		super(ctx);
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

	private Npc suspect() {
		return ctx.npcs.select().select(new Filter<Npc>() {

			@Override
			public boolean accept(Npc npc) {
				final NpcDefinition suspect = ctx.definitions.get(npc);
				return suspect != null && suspect.name.equalsIgnoreCase("Suspect") && Arrays.equals(target_models, suspect.modelIds);
			}

		}).poll();
	}

	private void navigateClaw() {
		if (!controllerOpen() || target_models == null) {
			return;
		}
		GameObject claw;
		Npc suspect = null;
		while ((claw = objectByName("evil claw")).valid() && (suspect = suspect()).valid()) {
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
			status("Handling widgets.");
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
				target_models = null;
				status("Talking to Molly.");
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
				if (target_models == null) {
					final NpcDefinition def = ctx.definitions.get(molly);
					target_models = def != null ? def.modelIds : null;
					status("Molly ID: " + Integer.toString(molly.id()));
				}
				final Component yes = ctx.randomMethods.getComponentByText("yes, I");
				if (yes.valid()) {
					status("Handling widgets.");
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
					status("Entering control room.");
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
					status("Opening control panel.");
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
					status("Navigating claw.");
					navigateClaw();
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return ctx.randomMethods.clickContinue();
						}

					}, 100, 120);
				}
			} else {
				status("Exiting control room.");
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
