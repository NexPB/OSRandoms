package pb.osrandoms.randoms;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;

import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

public class Molly extends OSRandom {

	private static final int CONTROL_INTERFACEGROUP = 240;
	private static final int CONTROLS_GRAB = 28;
	private static final int CONTROLS_UP = 29;
	private static final int CONTROLS_DOWN = 30;
	private static final int CONTROLS_LEFT = 31;
	private static final int CONTROLS_RIGHT = 32;

	private Npc molly;
	private GameObject controlPanel;
	private int suspectID = -1;

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
		return ctx.npcs.select().id(suspectID).poll();
	}

	private void navigateClaw() {
		if (!controllerOpen() || suspectID < 1) {
			return;
		}
		GameObject claw;
		Npc suspect;
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
			target.set(door);
			if (door.inViewport() && door.interact("Open")) {
				return true;
			}
			ctx.camera.turnTo(door);
			if (!door.inViewport()) {
				if (ctx.randomMethods.walkTileOnScreen(door)) {
					Condition.sleep(Random.nextInt(800, 1200));
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public void run() {
		if (ctx.randomMethods.clickContinue()) {
			status("[Molly] Handling widgets.");
			Condition.wait(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return !ctx.randomMethods.getContinue().valid();
				}

			});
			return;
		}
		if (ctx.players.local().inMotion() || ctx.players.local().animation() != -1) {
			Condition.sleep(600);
			return;
		}
		final int suspectsLoaded = ctx.npcs.select().name("suspect").size();
		if (!inControlRoom()) {
			if (suspectsLoaded == 2) {
				suspectID = -1;
				status("[Molly] Talking to Molly.");
				target.set(molly);
				if (molly.inViewport() && molly.interact("talk")) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return ctx.randomMethods.getContinue().valid();
						}

					});
				} else {
					ctx.camera.turnTo(molly);
				}
			} else {
				if (suspectID == -1) {
					final int id = molly.id();
					switch (id) {
					case 4277:
						suspectID = (id - 405);
					default:
						suspectID = (id - 40);
					}
					status("[Molly] Molly ID: " + Integer.toString(id));
					status("[Molly] Evil Twin ID:" + Integer.toString(suspectID));
				}
				if (ctx.randomMethods.getComponentByText("yes, I").click()) {
					status("[Molly] Handling widgets.");
					Condition.sleep(Random.nextInt(800, 1300));
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
		molly = molly();
		controlPanel = objectByName("control panel");
		return molly.valid() && molly.interacting().equals(ctx.players.local()) || controlPanel.valid();
	}

}
