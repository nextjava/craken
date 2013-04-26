package net.ion.craken.node.ref;

import java.util.List;

import net.ion.craken.node.IteratorList;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.search.util.TransactionJobs;

public class TestRefNode extends TestBaseRef {

	public void testFirst() throws Exception {
		session.tranSync(TransactionJobs.dummy("/emp", 10)) ;
		session.tranSync(TransactionJobs.dummy("/dept", 5)) ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emp/1").refToLast("dept", "/dept/1").refToLast("dept", "/dept/2") ;
				return null;
			}
		}).get() ;
		
		ReadNode refNode = session.pathBy("/emp/1").ref("dept") ;
		assertEquals(1, refNode.property("dummy")) ;
		
		assertEquals(true, session.pathBy("/emp/2").ref("dept") == null);
	}
	
	public void testRefs() throws Exception {
		session.tranSync(TransactionJobs.dummy("/emp", 10)) ;
		session.tranSync(TransactionJobs.dummy("/dept", 5)) ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emp/1").refToLast("dept", "/dept/1").refToLast("dept", "/dept/2") ;
				return null;
			}
		}).get() ;

		List<ReadNode> refs = session.pathBy("/emp/1").refs("dept").toList() ;
		
		assertEquals(2, refs.size()) ;
		assertEquals(1, refs.get(0).property("dummy")) ;
		assertEquals(2, refs.get(1).property("dummy")) ;
	}
	
	
}