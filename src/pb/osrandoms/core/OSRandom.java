package pb.osrandoms.core;

import java.util.logging.Logger;

public abstract class OSRandom extends GraphScript.Action<RandomContext> {

    public final Logger log;

    private String status = "";

    public OSRandom(RandomContext ctx) {
        super(ctx);
        log = ctx.controller.script().log;
    }

    public String status() {
        return status;
    }

    public void status(String status) {
        this.status = status == null ? "" : status;
    }
}
