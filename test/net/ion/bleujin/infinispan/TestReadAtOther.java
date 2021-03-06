package net.ion.bleujin.infinispan;


import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfiguration;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import net.ion.craken.loaders.FastFileCacheStore;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import junit.framework.TestCase;


public class TestReadAtOther extends TestCase {

	private DefaultCacheManager dm;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		GlobalConfiguration gconfig = GlobalConfigurationBuilder
		.defaultClusteredBuilder()
		.transport().clusterName("craken").addProperty("configurationFile", "./resource/config/jgroups-udp.xml")
		.build();
		this.dm = new DefaultCacheManager(gconfig);
		dm.defineConfiguration("test", FastFileCacheStore.testCacheStore("./resource/ff", 10)) ;
		dm.start() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		dm.stop() ;
		super.tearDown();
	}
	
	public void testRun() throws Exception {
		Cache<Object, Object> cache = dm.getCache("test");
		for (int i = 0; i < 50; i++) {
			cache.put("/bleujin/" + i, i) ;
		}
	}
	
	public void testRunning() throws Exception {
		while (true) {
			Cache<Object, Object> cache = dm.getCache("test");

			Set<Object> keyset = cache.keySet();
			if (keyset.size() > 0) {
				Debug.line(keyset.size(), keyset) ;
			}
			Thread.sleep(1000) ;
		}
	}
	
	
	public void testView() throws Exception {
		Cache<Object, Object> cache = dm.getCache("test");
		for (int i = 0; i < 50; i++) {
			Debug.line(cache.get("/bleujin/" + i)) ;
		}
	}
	
}
