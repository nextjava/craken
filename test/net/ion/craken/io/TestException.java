package net.ion.craken.io;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.TestBaseCrud;

public class TestException extends TestBaseCrud {

	public void testThrowExInSyncMode() throws Exception {
		try {
			session.tranSync(new TransactionJob<Void>() {
				@Override
				public Void handle(WriteSession wsession) throws Exception {
					throw new IOException("test");
				}
			});
			fail() ;
		} catch (ExecutionException expect) {
			assertEquals(IOException.class, expect.getCause().getClass()) ;
		}

	}
}
