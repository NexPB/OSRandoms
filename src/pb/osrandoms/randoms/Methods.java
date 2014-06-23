package pb.osrandoms.randoms;

import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Npc;

/**
 * TODO:
 * - getContinue widget
 */
public class Methods extends ClientAccessor {

    private final int WIDGET_ID = -1;
    private final int COMPONENT_ID = -1;

    public Methods(ClientContext arg0) {
        super(arg0);
    }

    public Npc getNpc(final String... name) {
        return getNpc(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.interacting().equals(ctx.players.local());
            }
        }, name);
    }

    public Npc getNpc(final Filter<Npc> filter, final String... name) {
        return ctx.npcs.select().name(name).select(filter).poll();
    }

    public Component getContinue() {
        return ctx.widgets.widget(WIDGET_ID).component(COMPONENT_ID);
    }

}
