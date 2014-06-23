package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;
import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

/**
 * TODO:
 * - NPC names (randoms) that only need talking.
 * - getContinue widget
 */
public class TalkToRandoms extends GraphScript.Action<RandomContext> {

    private final String[] RANDOM_NPC_NAMES = {"Security gaurd", "Genie"};
    private final int WIDGET_ID = -1;
    private final int COMPONENT_ID = -1;

    public TalkToRandoms(RandomContext ctx) {
        super(ctx);
    }

    public Component getContinue() {
        return ctx.widgets.widget(WIDGET_ID).component(COMPONENT_ID);
    }

    @Override
    public boolean valid() {
        return ctx.randomMethods.getNpc(RANDOM_NPC_NAMES).id() != -1;
    }

    @Override
    public void run() {
        final Npc random = ctx.randomMethods.getNpc(RANDOM_NPC_NAMES);
        final Component cont = getContinue();
        if (cont.valid() && cont.visible() && cont.click()) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !getContinue().visible();
                }
            }, 100, 7);
        } else {
            if (random.inViewport() && random.interact("Pick", random.name())) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return getContinue().visible();
                    }
                }, 250, 10);
            } else {
                ctx.movement.step(random);
            }
        }
    }

}
