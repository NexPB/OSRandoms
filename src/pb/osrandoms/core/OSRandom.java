package pb.osrandoms.core;

import org.powerbot.script.PaintListener;
import org.powerbot.script.rt4.Interactive;

import java.awt.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public abstract class OSRandom extends GraphScript.Action<RandomContext> implements PaintListener {
	protected final Logger log;
	private final RandomManifest manifest;
	protected AtomicReference<Interactive> target = new AtomicReference<Interactive>();

	private String status = "";

	public OSRandom(RandomContext ctx) {
		super(ctx);
		log = ctx.controller.script().log;
		manifest = getClass().getAnnotation(RandomManifest.class);
		if (manifest == null) {
			log.severe("RandomManifest missing: " + getClass().getSimpleName());
		}
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

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface RandomManifest {
		java.lang.String name();
	}

	public String name() {
		return manifest != null ? manifest.name() : "";
	}
}
