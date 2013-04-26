package net.ion.craken.node.crud;

import java.util.concurrent.Future;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteNode;
import net.ion.craken.node.WriteSession;

public class TestWriteSession extends TestBaseCrud {

	
	public void testTran() throws Exception {
		assertEquals(false, session.exists("/test")) ;
		
		Future<Void> future = session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession tsession) {
				WriteNode node = tsession.pathBy("/test") ;
				node.property("name", "bleujin") ;
				
				return null;
			}
		});
		future.get() ;
		
		assertEquals(true, session.exists("/test")) ;
		ReadNode found = session.pathBy("/test");
		assertEquals("bleujin", found.property("name")) ;
	}
}