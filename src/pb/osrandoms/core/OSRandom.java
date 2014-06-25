package pb.osrandoms.core;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.Interactive;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class OSRandom extends GraphScript.Action<RandomContext> implements PaintListener {
	protected final Logger log;
	protected AtomicReference<Interactive> target = new AtomicReference<Interactive>();

	private String status = "";

	public OSRandom(RandomContext ctx) {
		super(ctx);
		log = ctx.controller.script().log;
	}

	public String status() {
		return status;
	}

	public void status(String status) {
		if (status != null) {
			log.info(status);
		}
		this.status = status == null ? "" : status;
	}

	@Override
	public void repaint(Graphics graphics) {
		final Interactive interactive = target.get();
		if (interactive != null) {
			interactive.draw(graphics);
		}
	}
}
