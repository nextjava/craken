package net.ion.craken.node.search;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;

public class TestFirst extends TestBaseSearch {

	public void testListener() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().addChild("/bleujin").property("name", "bleujin");
				return null;
			}
		}).get() ;
	}
	
	public void testWhenModify() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/1/2/3/4") ;
				
				wsession.root().addChild("/bleujin").property("name", "bleujin").property("age", 15) ;
				wsession.root().addChild("/hero").property("name", "hero").property("age", 20) ;
				wsession.root().addChild("/jin").property("name", "jin").property("age", 20) ;
				
				return null;
			}
		}).get() ;
		
		ReadNode readNode = session.queryRequest("name:bleujin").find().first();
		
		assertEquals("bleujin", readNode.property("name").value()) ;
		assertEquals(15, readNode.property("age").value()) ;
		
		assertEquals(7, session.queryRequest("").find().toList().size()) ;
		
//		new InfinityThread().startNJoin() ;
	}
	
	public void testWhenRemoveSelf() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().addChild("/bleujin").property("name", "bleujin").property("age", 15) ;
				return null;
			}
		}).get() ;
		
		ReadNode readNode = session.queryRequest("name:bleujin").find().first();
		assertEquals(15, readNode.property("age").value()) ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/bleujin").property("name", "bleujin").property("age", 15).removeSelf() ;
				return null;
			}
		}).get() ;

	 	assertEquals(0, session.queryRequest("").find().toList().size()) ;
	}
	

	public void testWhenRemoveChild() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().addChild("/bleujin").property("name", "bleujin").property("age", 15) ;
				return null;
			}
		}).get() ;
		
		ReadNode readNode = session.queryRequest("name:bleujin").find().first();
		assertEquals(15, readNode.property("age").value()) ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().removeChild("bleujin") ;
				return null;
			}
		}).get() ;

	 	assertEquals(0, session.awaitListener().queryRequest("").find().toList().size()) ;
	}
	
	

	public void testWhenRemoveChildren() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().addChild("/emps/bleujin").property("name", "bleujin").property("age", 15) ;
				wsession.root().addChild("/emps/hero").property("name", "hero").property("age", 20) ;
				return null;
			}
		}).get() ;
		
		assertEquals(3, session.queryRequest("").find().toList().size()) ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps").removeChildren() ;
				return null;
			}
		}).get() ;

		assertEquals(1, session.queryRequest("").find().toList().size()) ;
		
//	 	assertEquals(0, session.awaitIndex().createRequest("").find().toList().size()) ;
	}
	

	public void testWhenRemoveChildren2() throws Exception {
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.root().addChild("/emps/bleujin").property("name", "bleujin").property("age", 15).child("address").property("city", "seoul") ;
				wsession.root().addChild("/emps/hero").property("name", "hero").property("age", 20) ;
				return null;
			}
		}).get() ;
		
		assertEquals(4, session.queryRequest("").find().toList().size()) ;
		
		session.tran(new TransactionJob<Void>() {
			@Override
			public Void handle(WriteSession wsession) {
				wsession.pathBy("/emps").removeChildren() ;
				return null;
			}
		}).get() ;

		assertEquals(1, session.queryRequest("").find().toList().size()) ;
		
//	 	assertEquals(0, session.awaitIndex().createRequest("").find().toList().size()) ;
	}
	

	
	
	
	
	
	
	
}
