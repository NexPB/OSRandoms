package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game.Tab;
import org.powerbot.script.rt4.Menu;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

/**
 * @author Robert G
 */
public class ExpRewardClaimer extends OSRandom {
	private static final int REWARD_WIDGET_ID = 134;
	private static final int SELECTED_REWARD_SETTING_ID = 261;
	private static final int EXP_LAMP_ID = 2528;
	private static final int EXP_BOOK_ID = 11640;
	private static final Reward DEFAULT_REWARD = Reward.FISHING;

	private static Reward selected_reward = null;

	public ExpRewardClaimer(RandomContext script) {
		super(script);
	}

	private Reward getReward() {
		return selected_reward == null ? DEFAULT_REWARD : selected_reward;
	}

	@Override
	public void run() {
		final Component comp = ctx.widgets.widget(REWARD_WIDGET_ID).component(getReward().componentId());
		if (comp.valid()) {
			if (selectedReward() != getReward().settingValue()) {
				if (comp.interact("advance")) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return selectedReward() == getReward().settingValue();
						}
					}, 150, 10);
					return;
				}
			} else {
				final Component confirm = ctx.randomMethods.getComponentByText("confirm");
				if (confirm.valid() && confirm.interact(Menu.filter("confirm"))) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return !comp.valid();
						}
					}, 150, 10);
					return;
				}
			}
		} else {
			if (ctx.game.tab(Tab.INVENTORY)) {
				if (ctx.inventory.poll().click()) {
					Condition.wait(new Callable<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return comp.valid();
						}
					}, 150, 10);
					return;
				}
			}
		}
	}

	private int selectedReward() {
		return ctx.varpbits.varpbit(SELECTED_REWARD_SETTING_ID);
	}

	@Override
	public boolean valid() {
		return !ctx.inventory.select().id(EXP_LAMP_ID, EXP_BOOK_ID).isEmpty();
	}

	public enum Reward {
		ATTACK(3, 1),
		STRENGTH(4, 2),
		RANGED(5, 3),
		MAGIC(6, 4),
		DEFENCE(7, 5),
		HITPOINTS(8, 6),
		PRAYER(9, 7),
		AGILITY(10, 8),
		HERBLORE(11, 9),
		THIEVING(12, 10),
		CRAFTING(13, 11),
		RUNECRAFTING(14, 12),
		MINING(15, 13),
		SMITHING(16, 14),
		FISHING(17, 15),
		COOKING(18, 16),
		FIREMAKING(19, 17),
		WOODCUTTING(20, 18),
		FLETCHING(21, 19),
		SLAYER(22, 20),
		FARMING(23, 21),
		CONSTRUCTION(24, 22),
		HUNTER(25, 23);

		private final int component_id, setting_value;

		private Reward(int component, int settingVal) {
			this.component_id = component;
			this.setting_value = settingVal;
		}

		public int componentId() {
			return component_id;
		}

		public int settingValue() {
			return setting_value;
		}
	}
}
