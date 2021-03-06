package net.ion.craken.node.exception;

import org.infinispan.CacheException;

public class NodeNotExistsException extends CacheException {

	private static final long serialVersionUID = 779376138690777440L;

	public NodeNotExistsException() {
		super();
	}

	public NodeNotExistsException(String msg) {
		super(msg);
	}

	public NodeNotExistsException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
