package com.github.sourjson.test.struct;

import com.github.sourjson.annotation.FieldName;

public class SimpleBean {
	public String name;
	public @FieldName("count") int value;
	public SimpleBean(String name, int value) {
		this.name = name;
		this.value = value;
	}

	protected SimpleBean() { /* has nothing */ }
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SimpleBean))
			return false;
		SimpleBean sbo = (SimpleBean)obj;
		return ((this.name != null && this.name.equals(sbo.name)) || sbo.name == null) && this.value == sbo.value;
	}
	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + (name != null ? name.hashCode() : -1);
		hash = hash * 31 + value;
		return hash;
	}
}
