package pb.osrandoms.randoms;

import com.logicail.wrappers.ObjectDefinition;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Player;
import org.powerbot.script.rt4.Widget;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.Arrays;
import java.util.concurrent.Callable;

/**
 * @author Timer
 *         <p/>
 *         Updated by Robert G
 */
@OSRandom.RandomManifest(name = "Surprise Exam")
public class SurpriseExam extends OSRandom {

	private class ObjectRelations {
		private final String text;
		private final int[] items;

		private ObjectRelations(final String text, final int[] items) {
			this.text = text;
			this.items = items;
		}
	}

	private static final int WIDGET_NEXT = 103;
	private static final int WIDGET_RELATED = 559;
	private static final int WIDGET_CHAT = 242;
	private static final int WIDGET_CHAT_TEXT = 2;

	private static final int[] WIDGET_ITEM_RANGE = {11539, 11540, 11541, 11614, 11615, 11633};
	private static final int[] WIDGET_ITEM_CULINARY = {11526, 11529, 11545, 11549, 11550, 11555, 11560, 11563, 11564, 11607, 11608, 11616, 11620, 11621, 11622, 11623, 11628, 11629, 11634, 11639, 11641, 11649, 11624};
	private static final int[] WIDGET_ITEM_FISH = {11527, 11574, 11578, 11580, 11599, 11600, 11601, 11602, 11603, 11604, 11605, 11606, 11625};
	private static final int[] WIDGET_ITEM_COMBAT = {11528, 11531, 11536, 11537, 11579, 11591, 11592, 11593, 11597, 11627, 11631, 11635, 11636, 11638, 11642, 11648, 11617};
	private static final int[] WIDGET_ITEM_FARM = {11530, 11532, 11547, 11548, 11554, 11556, 11571, 11581, 11586, 11610, 11645};
	private static final int[] WIDGET_ITEM_MAGIC = {11533, 11534, 11538, 11562, 11567, 11582};
	private static final int[] WIDGET_ITEM_FIREMAKING = {11535, 11551, 11552, 11559, 11646};
	private static final int[] WIDGET_ITEM_HATS = {11540, 11557, 11558, 11560, 11570, 11619, 11626, 11630, 11632, 11637, 11654};
	private static final int[] WIDGET_ITEM_PIRATE = {11570, 11626, 11558};
	private static final int[] WIDGET_ITEM_JEWELLERY = {11572, 11576, 11652};
	private static final int[] WIDGET_ITEM_JEWELLERY_2 = {11572, 11576, 11652};
	private static final int[] WIDGET_ITEM_DRINKS = {11542, 11543, 11544, 11644, 11647};
	private static final int[] WIDGET_ITEM_LUMBER = {11573, 11595};
	private static final int[] WIDGET_ITEM_BOOTS = {11561, 11618, 11650, 11651};
	private static final int[] WIDGET_ITEM_CRAFT = {11546, 11553, 11565, 11566, 11568, 11569, 11572, 11575, 11576, 11577, 11581, 11583, 11584, 11585, 11643, 11652, 11653};
	private static final int[] WIDGET_ITEM_MINING = {11587, 11588, 11594, 11596, 11598, 11609, 11610};
	private static final int[] WIDGET_ITEM_SMITHING = {11611, 11612, 11613, 11553};

	public static final int[][] WIDGET_ITEMS = {
			WIDGET_ITEM_RANGE, WIDGET_ITEM_CULINARY,
			WIDGET_ITEM_FISH, WIDGET_ITEM_COMBAT,
			WIDGET_ITEM_FARM, WIDGET_ITEM_MAGIC,
			WIDGET_ITEM_FIREMAKING, WIDGET_ITEM_HATS,
			WIDGET_ITEM_DRINKS, WIDGET_ITEM_LUMBER,
			WIDGET_ITEM_BOOTS, WIDGET_ITEM_CRAFT,
			WIDGET_ITEM_MINING, WIDGET_ITEM_SMITHING
	};

	private static final String[] COLORS = {"red", "blue", "purple", "green"};
	private static final int[][] OBJECT_MODELS_DOORS = {{27078}, {27099}, {27083}, {27076}};

	private GameObject door = null;

	public final ObjectRelations[] WIDGET_ITEM_RELATIONS = {
			new ObjectRelations("I never leave the house without some sort of jewellery.", WIDGET_ITEM_JEWELLERY),
			new ObjectRelations("There is no better feeling than", WIDGET_ITEM_JEWELLERY_2),
			new ObjectRelations("I'm feeling dehydrated", WIDGET_ITEM_DRINKS),
			new ObjectRelations("All this work is making me thirsty", WIDGET_ITEM_DRINKS),
			new ObjectRelations("quenched my thirst", WIDGET_ITEM_DRINKS),
			new ObjectRelations("light my fire", WIDGET_ITEM_FIREMAKING),
			new ObjectRelations("fishy", WIDGET_ITEM_FISH),
			new ObjectRelations("fishing for answers", WIDGET_ITEM_FISH),
			new ObjectRelations("fish out of water", WIDGET_ITEM_DRINKS),
			new ObjectRelations("strange headgear", WIDGET_ITEM_HATS),
			new ObjectRelations("tip my hat", WIDGET_ITEM_HATS),
			new ObjectRelations("thinking cap", WIDGET_ITEM_HATS),
			new ObjectRelations("wizardry here", WIDGET_ITEM_MAGIC),
			new ObjectRelations("rather mystical", WIDGET_ITEM_MAGIC),
			new ObjectRelations("abracada", WIDGET_ITEM_MAGIC),
			new ObjectRelations("hide one's face", WIDGET_ITEM_HATS),
			new ObjectRelations("shall unmask", WIDGET_ITEM_HATS),
			new ObjectRelations("hand-to-hand", WIDGET_ITEM_COMBAT),
			new ObjectRelations("melee weapon", WIDGET_ITEM_COMBAT),
			new ObjectRelations("prefers melee", WIDGET_ITEM_COMBAT),
			new ObjectRelations("me hearties", WIDGET_ITEM_PIRATE),
			new ObjectRelations("puzzle for landlubbers", WIDGET_ITEM_PIRATE),
			new ObjectRelations("mighty pirate", WIDGET_ITEM_PIRATE),
			new ObjectRelations("mighty archer", WIDGET_ITEM_RANGE),
			new ObjectRelations("as an arrow", WIDGET_ITEM_RANGE),
			new ObjectRelations("Ranged attack", WIDGET_ITEM_RANGE),
			new ObjectRelations("shiny things", WIDGET_ITEM_CRAFT),
			new ObjectRelations("igniting", WIDGET_ITEM_FIREMAKING),
			new ObjectRelations("sparks from my synapses.", WIDGET_ITEM_FIREMAKING),
			new ObjectRelations("fire.", WIDGET_ITEM_FIREMAKING),
			new ObjectRelations("disguised", WIDGET_ITEM_HATS),
			new ObjectRelations("range", WIDGET_ITEM_RANGE),
			new ObjectRelations("arrow", WIDGET_ITEM_RANGE),
			new ObjectRelations("drink", WIDGET_ITEM_DRINKS),
			new ObjectRelations("logs", WIDGET_ITEM_FIREMAKING),
			new ObjectRelations("light", WIDGET_ITEM_FIREMAKING),
			new ObjectRelations("headgear", WIDGET_ITEM_HATS),
			new ObjectRelations("hat", WIDGET_ITEM_HATS),
			new ObjectRelations("cap", WIDGET_ITEM_HATS),
			new ObjectRelations("mine", WIDGET_ITEM_MINING),
			new ObjectRelations("mining", WIDGET_ITEM_MINING),
			new ObjectRelations("ore", WIDGET_ITEM_MINING),
			new ObjectRelations("fish", WIDGET_ITEM_FISH),
			new ObjectRelations("fishing", WIDGET_ITEM_FISH),
			new ObjectRelations("thinking cap", WIDGET_ITEM_HATS),
			new ObjectRelations("cooking", WIDGET_ITEM_CULINARY),
			new ObjectRelations("cook", WIDGET_ITEM_CULINARY),
			new ObjectRelations("bake", WIDGET_ITEM_CULINARY),
			new ObjectRelations("farm", WIDGET_ITEM_FARM),
			new ObjectRelations("farming", WIDGET_ITEM_FARM),
			new ObjectRelations("cast", WIDGET_ITEM_MAGIC),
			new ObjectRelations("magic", WIDGET_ITEM_MAGIC),
			new ObjectRelations("craft", WIDGET_ITEM_CRAFT),
			new ObjectRelations("boot", WIDGET_ITEM_BOOTS),
			new ObjectRelations("chop", WIDGET_ITEM_LUMBER),
			new ObjectRelations("cut", WIDGET_ITEM_LUMBER),
			new ObjectRelations("tree", WIDGET_ITEM_LUMBER)
	};

	public SurpriseExam(RandomContext ctx) {
		super(ctx);
	}

	private int[] getItemArray(final int item) {
		for (final int[] items : SurpriseExam.WIDGET_ITEMS) {
			Arrays.sort(items);
			if (Arrays.binarySearch(items, item) >= 0) {
				return items;
			}
		}
		return null;
	}

	@Override
	public void run() {
		final Player local = ctx.players.local();
		if (local.inMotion() || local.animation() != -1) {
			Condition.sleep(Random.getDelay());
			return;
		}
		if (ctx.widgets.widget(WIDGET_CHAT).component(WIDGET_CHAT_TEXT).valid()) {
			final String text = ctx.widgets.widget(WIDGET_CHAT).component(WIDGET_CHAT_TEXT).text().toLowerCase();
			for (int i = 0; i < COLORS.length; i++) {
				if (text.contains(COLORS[i])) {
					final int[] models = OBJECT_MODELS_DOORS[i];
					door = ctx.objects.select().name("door").select(new Filter<GameObject>() {

						@Override
						public boolean accept(GameObject object) {
							final ObjectDefinition def = ctx.definitions.get(object);
							return def != null && Arrays.equals(def.modelIds, models);
						}

					}).poll();
					break;
				}
			}
			if (door != null && door.valid()) {
				if (!door.inViewport()) {
					ctx.movement.step(door);
					for (int i = 0; i < 200; i++)
						if (ctx.players.local().inMotion()) i = 0;
					Condition.sleep(150);
				}
				ctx.camera.turnTo(door);
			}
			if (door.inViewport() && door.interact("Open")) {
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return !valid();
					}

				});
			}
			return;
		}
		if (ctx.randomMethods.clickContinue()) {
			status("Following conversation");
			Condition.sleep(Random.nextInt(1200, 1800));
			return;
		}
		final Widget next = ctx.widgets.widget(WIDGET_NEXT);
		if (next.component(8).valid()) {
			status("WIDGET_VALIDATED: Next item");
			final int item_1 = next.component(8).modelId();
			final int item_2 = next.component(9).modelId();
			final int item_3 = next.component(10).modelId();
			status("Items: " + item_1 + ", " + item_2 + ", " + item_3);
			final int[] item_arr_1 = getItemArray(item_1);
			final int[] item_arr_2 = getItemArray(item_2);
			final int[] item_arr_3 = getItemArray(item_3);
			final int[] item_arr;
			final int[] item_arr_o;
			if (Arrays.equals(item_arr_2, item_arr_3)) {
				item_arr = item_arr_2;
				item_arr_o = item_arr_1;
			} else {
				item_arr = item_arr_1;
				if (Arrays.equals(item_arr_1, item_arr_2)) {
					item_arr_o = item_arr_3;
				} else {
					item_arr_o = item_arr_2;
				}
			}
			status("Matched 1: " + Arrays.toString(item_arr_1));
			status("Matched 2: " + Arrays.toString(item_arr_2));
			status("Matched 3: " + Arrays.toString(item_arr_3));
			if (item_arr_1 != null && item_arr_2 != null && item_arr_3 != null) {
				final int[] choices = {
						next.component(12).modelId(),
						next.component(13).modelId(),
						next.component(14).modelId(),
						next.component(15).modelId()
				};
				status("Possible choices: " + Arrays.toString(choices));
				int index = 12;
				for (final int choice : choices) {
					Arrays.sort(item_arr);
					if (Arrays.binarySearch(item_arr, choice) >= 0) {
						status("Found choice at index " + index + ".");
						next.component(index).click();
						Condition.sleep(Random.nextInt(1500, 2000));
						return;
					}
					++index;
				}
				index = 12;
				status("Unknown, making an educated guess.");
				for (final int choice : choices) {
					Arrays.sort(item_arr_o);
					if (Arrays.binarySearch(item_arr_o, choice) >= 0) {
						status("Found choice at index " + index + ".");
						next.component(index).click();
						Condition.sleep(Random.nextInt(1500, 2000));
						return;
					}
					++index;
				}
				status("Just going to guess...");
				final int randomIndex = Random.nextInt(12, 16);
				next.component(randomIndex).click();
				Condition.sleep(Random.nextInt(1500, 2000));
			}
			return;
		}
		final Widget related = ctx.widgets.widget(WIDGET_RELATED);
		if (related.component(72).valid()) {
			status("WIDGET_VALIDATED: Related items");
			final String text = related.component(72).text();
			status("HINT: " + text);
			for (final ObjectRelations question : WIDGET_ITEM_RELATIONS) {
				if (text.toLowerCase().contains(question.text.toLowerCase())) {
					status("Relation validated: " + question.text);
					status("Searching children");
					for (int childIndex = 24; childIndex <= 38; childIndex++) {
						status("[" + childIndex + "] Searching for " + related.component(childIndex).modelId() + " in " + Arrays.toString(question.items) + ".");
						Arrays.sort(question.items);
						if (Arrays.binarySearch(question.items, related.component(childIndex).modelId()) >= 0) {
							status("Found relation for this index (" + childIndex + "), selecting.");
							related.component(childIndex).click();
							Condition.sleep(Random.nextInt(1200, 2000));
						}
					}
					break;
				}
			}
			Condition.sleep(Random.nextInt(1200, 2000));
			status("Confirming attempt");
			if (related.component(70).click()) {
				Condition.sleep(Random.nextInt(1200, 2000));
			}
			return;
		}
		status("Unknown position - talking");
		final Npc dude = ctx.npcs.select().name("Mr. Mordaut").poll();
		if (!dude.inViewport()) {
			ctx.camera.turnTo(dude);
			if (!dude.inViewport()) {
				ctx.movement.step(dude);
				Condition.sleep(1500);
				ctx.camera.turnTo(dude);
			}
		}
		if (dude.inViewport() && dude.interact("Talk-to")) {
			Condition.wait(new Callable<Boolean>() {

				@Override
				public Boolean call() throws Exception {
					return ctx.randomMethods.queryContinue();
				}

			});
		}
	}

	@Override
	public boolean valid() {
		if (!ctx.npcs.select().name("Mr. Mordaut").within(15).isEmpty()) {
			return true;
		} else {
			door = null;
			return false;
		}
	}

}
