package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;
import pb.osrandoms.core.OSRandom;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

public class Mime extends OSRandom {

    private Component component = null;

    public Mime(RandomContext ctx) {
        super(ctx);
    }

    private Npc getMime() {
        return ctx.npcs.select().name("Mime").within(10).poll();
    }

    private Component getComponent(final int anim) {
        switch (anim) {
            case 860:
                return ctx.randomMethods.getComponentByText("Cry");
            case 857:
                return ctx.randomMethods.getComponentByText("Think");
            case 861:
                return ctx.randomMethods.getComponentByText("Laugh");
            case 866:
                return ctx.randomMethods.getComponentByText("Dance");
            case 1130:
                return ctx.randomMethods.getComponentByText("Climb Rope");
            case 1129:
                return ctx.randomMethods.getComponentByText("Lean on air");
            case 1128:
                return ctx.randomMethods.getComponentByText("Glass Wall");
            case 1131:
                return ctx.randomMethods.getComponentByText("Glass Box");
        }
        return null;
    }

    @Override
    public boolean valid() {
        return getMime().valid();
    }

    @Override
    public void run() {
        if (component != null && component.valid()) {
            target.set(component);
            status("Clicking on Mime answer.");
            if (component.click()) {
                status("Waiting for animtion to be started.");
                if (Condition.wait(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return ctx.players.local().animation() != -1;
                    }
                }, 200, 8)) {
                    status("Animation started, answer set to null.");
                    component = null;
                }
            }
        } else {
            final Npc mime = getMime();
            target.set(mime);
            final int anim = mime.animation();
            status("Checking animation - animation = " + anim);
            if (anim != -1 && anim != 858) {
                status("Found animation.");
                component = getComponent(anim);
            }
        }
    }

}
