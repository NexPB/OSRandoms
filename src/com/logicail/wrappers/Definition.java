package com.logicail.wrappers;

import com.sk.datastream.Stream;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created with IntelliJ IDEA.
 * User: Logicail
 * Date: 07/07/2014
 * Time: 13:41
 */
public abstract class Definition {
	public final int id;

	protected Definition(int id) {
		this.id = id;
	}
	
	protected void decode(Stream stream) {
		while (true) {
			int opcode = stream.getUByte();
			if (opcode != 0) {
				this.decode(stream, opcode);
			} else {
				return;
			}
		}
	}

	protected abstract void decode(Stream stream, int opcode);

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append(getClass().getSimpleName());
		output.append(" ");
		output.append(id);
		output.append(" {");
		for (Field f : getClass().getDeclaredFields()) {
			if ((f.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) != 0) {
				continue;
			}
			Object o;
			try {
				o = f.get(this);
			} catch (IllegalArgumentException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
			if (o == null)
				continue;
			output.append(f.getName());
			output.append(": ");
			if (f.getType().isArray()) {
				output.append("[");
				for (int i = 0, len = Array.getLength(o); i < len; ++i) {
					if (i != 0) {
						output.append(", ");
					}
					final Object o1 = Array.get(o, i);
					if (o1 != null && o1.getClass().isArray()) {
						for (int j = 0, len2 = Array.getLength(o1); j < len2; ++j) {
							if (j != 0) {
								output.append(", ");
							}
							output.append(Array.get(o1, j));
						}
					} else {
						output.append(o1);
					}
				}
				output.append("]");
			} else {
				output.append(o);
			}
			output.append(", ");
		}
		output.delete(output.length() - 2, output.length());
		output.append("}");
		return output.toString();
	}
}
