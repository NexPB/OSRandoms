package pb.osrandoms.randoms;

import org.powerbot.script.Filter;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;

public class Methods extends ClientAccessor {

    public Methods(ClientContext arg0) {
        super(arg0);
    }

    public Npc getNpc(final String... name) {
        return getNpc(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return true;
            }
        }, name);
    }

    public Npc getNpc(final Filter<Npc> filter, final String... name) {
        return ctx.npcs.select().name(name).select(filter).select(new Filter<Npc>() {
            @Override
            public boolean accept(Npc npc) {
                return npc.interacting().equals(ctx.players.local());
            }
        }).poll();
    }

}
