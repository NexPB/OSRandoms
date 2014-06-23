package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;
import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

public class TalkToRandoms extends GraphScript.Action<RandomContext> {

    private static final String[] NPC_NAMES = {
            "Drunken Dwarf",
            "Genie",
            "Security Guard",
            "Dr Jekyll",
            "Rick Turpentine",
            "Cap'n Hand",
            "Mysterious Old Man"
    };


    public TalkToRandoms(RandomContext ctx) {
        super(ctx);
    }

    private Npc getNpc() {
        return ctx.randomMethods.getNpc(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.tile().distanceTo(ctx.players.local()) < 3 &&
                        npc.overheadMessage().contains(ctx.players.local().name());
            }
        }, NPC_NAMES);
    }

    @Override
    public boolean valid() {
        return getNpc().valid();
    }

    @Override
    public void run() {
        final Npc random = ctx.randomMethods.getNpc(NPC_NAMES);
        final Component cont = ctx.randomMethods.getContinue();
        if (cont.valid() && cont.visible() && cont.click()) {
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return !ctx.randomMethods.getContinue().visible();
                }
            }, 120, 10);
        } else {
            if (random.inViewport() && random.interact("Talk-to", random.name())) {
                Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.randomMethods.getContinue().visible();
                    }
                }, 250, 10);
            } else {
                ctx.movement.step(random);
            }
        }
    }

}
