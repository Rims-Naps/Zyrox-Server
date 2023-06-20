package com.zenyte.database;

import com.google.common.base.Objects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public abstract class SQLRunnable implements Runnable {

	public SQLRunnable() { }
	
	public abstract void execute(final DatabaseCredential auth);
	
	public void prepare() {
		DatabasePool.submit(this);
	}
	
	@Override
	public void run() {
		prepare();
	}
}

