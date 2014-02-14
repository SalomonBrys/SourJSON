package com.github.sourjson.test.struct;

import java.util.Date;

import com.github.sourjson.annotation.SCheckForNull;

public class DatedSimpleBean extends SimpleBean {
	public @SCheckForNull Date date;
	public DatedSimpleBean(String name, int value, Date date) {
		super(name, value);
		this.date = date;
	}
	@SuppressWarnings("unused")
	private DatedSimpleBean() {}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DatedSimpleBean))
			return false;
		DatedSimpleBean sbo = (DatedSimpleBean)obj;
		return super.equals(obj) && date.equals(sbo.date);
	}
	@Override
	public int hashCode() {
		int hash = super.hashCode();
		hash = hash * 7 + date.hashCode();
		return hash;
	}
}