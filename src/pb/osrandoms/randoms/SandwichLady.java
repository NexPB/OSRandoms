package pb.osrandoms.randoms;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;
import org.powerbot.script.rt4.Widget;
import pb.osrandoms.core.GraphScript;
import pb.osrandoms.core.RandomContext;

import java.util.concurrent.Callable;

/**
 * TODO:
 * - Make sure modelids are right!
 */
public class SandwichLady extends GraphScript.Action<RandomContext> {

    public SandwichLady(RandomContext ctx) {
        super(ctx);
    }

    public enum Answer {

        SQUARE(10731, "square"),
        ROLL(10727, "roll"),
        CHOCOLATE(10728, "chocolate"),
        BAQUETTE(10726, "baquette"),
        TRIANGLE(10732, "triangle"),
        KEBAB(10729, "kebab"),
        PIE(10730, "pie");

        private int modelId;
        private String name;

        Answer(final int modelId, final String name) {
            this.modelId = modelId;
            this.name = name;
        }

        public int getModelId() {
            return modelId;
        }

        public String getName() {
            return name;
        }

        public Component getAnswer(final RandomContext ctx, final Widget widget) {
            for (int i = 1; i < 8; i++) {
                Component component = widget.component(i);
                if (component.valid() && component.modelId() == getModelId()) {
                    return component;
                }
            }
            return ctx.widgets.widget(0).component(0);
        }
    }

    private Component getComponent() {
        for (Answer answer : Answer.values()) {
            return ctx.randomMethods.getComponentByText(answer.getName());
        }
        return ctx.widgets.widget(0).component(0);
    }

    @Override
    public boolean valid() {
        return ctx.randomMethods.getNpc("Sandwich Lady").valid() || getComponent().valid();
    }

    @Override
    public void run() {
        final Component component = getComponent();
        if (component.valid()) {
            System.out.println("interace open sandwich lady.");
            for (final Answer answer : Answer.values()) {
                final Component food;
                if ((food = answer.getAnswer(ctx, component.widget())).valid()) {
                    System.out.println("found answer food = " + food);
                    food.click();
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !getComponent().valid();
                        }
                    }, 250, 10);
                }
            }
        } else {
            final Component cont = ctx.randomMethods.getContinue();
            if (cont.valid()) {
                System.out.println("talk to sandwich lady.");
                cont.click();
                Condition.sleep(1600);
            } else {
                System.out.println("interact with sandwich lady.");
                final Npc lady = ctx.randomMethods.getNpc("Sandwich Lady");
                if (lady.inViewport() && lady.interact("Talk-to", lady.name())) {
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return getComponent().valid();
                        }
                    }, 250, 10);
                } else {
                    System.out.println("walk to sandwich lady.");
                    ctx.movement.step(lady);
                }
            }
        }
    }
}
