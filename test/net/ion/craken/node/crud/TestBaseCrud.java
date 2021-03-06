package net.ion.craken.node.crud;

import junit.framework.TestCase;
import net.ion.craken.loaders.lucene.CentralCacheStoreConfig;
import net.ion.craken.node.ReadSession;

public class TestBaseCrud extends TestCase {

	protected RepositoryImpl r ;
	protected ReadSession session;
	protected CentralCacheStoreConfig config;

	@Override
	protected void setUp() throws Exception {
		this.r = RepositoryImpl.create() ;
		this.config = CentralCacheStoreConfig.createDefault();
		r.defineWorkspaceForTest("test", config) ;
		
		r.start() ;
		this.session = r.login("test") ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		this.r.shutdown() ;
		super.tearDown();
	}
	

}
