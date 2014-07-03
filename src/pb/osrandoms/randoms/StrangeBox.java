package pb.osrandoms.randoms;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game.Tab;
import org.powerbot.script.rt4.Item;

import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

/**
 * 
 * @author Robert G
 *
 */
public class StrangeBox extends OSRandom {
	private static final int QUESTION_WIDGET_ID = 190;
	private static final int ANSWER_SETTING_ID = 312;
	private static final int STRANGE_BOX_ID = 3062;

	public StrangeBox(RandomContext ctx) {
		super(ctx);
	}
	
	private int answerComponentId() {
		switch (ctx.varpbits.varpbit(ANSWER_SETTING_ID) >> 24) {
		case 0:
			return 10;
		case 1:
			return 11;
		case 2:
			return 12;
		}
		return -1;
	}
	
	@Override
	public void run() {
		if (answerComponentId() > -1) {
			final Component comp = ctx.widgets.widget(QUESTION_WIDGET_ID).component(answerComponentId());
			status("Selecting " + comp.text() + ".");
			target.set(comp);
			if (comp.click()) {
				Condition.wait(new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return answerComponentId() == -1;
					}
					
				});
			}
		} else {
			if (ctx.game.tab(Tab.INVENTORY)) {
				final Item box = ctx.inventory.poll();
				target.set(box);
				if (box.interact("open")) {
					Condition.wait(new Callable<Boolean>() {

						@Override
						public Boolean call() throws Exception {
							return answerComponentId() > -1;
						}
						
					});
				}
			}
		}
	}

	@Override
	public boolean valid() {
		return !ctx.inventory.select().id(STRANGE_BOX_ID).isEmpty();
	}

}
