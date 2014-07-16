package com.logicail.wrappers;

import com.logicail.wrappers.loaders.WrapperLoader;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Wrapper {

	protected final WrapperLoader<?> loader;
	protected final int id;

	public Wrapper(WrapperLoader<?> loader, int id) {
		this.loader = loader;
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public WrapperLoader<?> getLoader() {
		return loader;
	}

	public Map<String, Object> getDeclaredFields() {
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		for (Field f : getClass().getDeclaredFields()) {
			Object o;
			try {
				o = f.get(this);
			} catch (IllegalArgumentException e) {
				o = null;
			} catch (IllegalAccessException e) {
				o = null;
			}
			ret.put(f.getName(), o);
		}
		return ret;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append(getClass().getSimpleName());
		output.append(" ");
		output.append(getId());
		output.append(" {");
		for (Field f : getClass().getDeclaredFields()) {
			if ((f.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) != 0) {
				continue;
			}
			Object o;
			try {
				o = f.get(this);
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
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		output.delete(output.length() - 2, output.length());
		output.append("}");
		return output.toString();
	}
}
