package com.wwm.db;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.internal.SyncedAttrDefinitionMgr;
import com.wwm.context.JVMAppListener;
import com.wwm.db.Client;
import com.wwm.db.EmbeddedClientFactory;
import com.wwm.db.Factory;
import com.wwm.db.Store;
import com.wwm.db.core.Settings;
import com.wwm.db.exceptions.UnknownStoreException;
import com.wwm.db.internal.StoreImpl;
import com.wwm.db.internal.server.Database;
import com.wwm.db.services.IndexImplementationsService;
import com.wwm.io.packet.layer1.SocketListeningServer;


public abstract class BaseDatabaseTest {

	protected static boolean useEmbeddedDatabase = true;
	
	private static final String defaultAddress = "127.0.0.1";
//	public static final InetAddress defaultAddress = InetAddress.getLocalHost();
	private static final int serverPort = 5002;

	protected final String storeName = "TestStore";

	private Database database;
	
	// TODO: make client, store and attrMgr private and use getClient, getStore etc so that they're read only
	protected Client client; 
	protected Store store;
	
	
	@BeforeClass
	static public void tweakSettings()	{
		// These affect transaction scavenging.
		Settings.getInstance().setTransactionInactivityTimeoutSecs(30);
		Settings.getInstance().setTransactionTimeToLiveSecs(30);
		// And this is us waiting for server to respond .. which should usually be < 1 sec even with paging.
		Settings.getInstance().setCommandTimeoutSecs(25);
	}
	
	
	/**
	 * setUp - Establish database with empty store, ready to use.
	 */
	@Before
	public void setUpDatabase() throws Exception {
		JVMAppListener.getInstance().preRequest();

		if (useEmbeddedDatabase) {
			database = null;
			client = EmbeddedClientFactory.getInstance().createEmbeddedClient();
		}
		else {
			database = startNewDatabase();
			// Make client
			client = Factory.createClient();
			client.connect(new InetSocketAddress(defaultAddress, serverPort));
			
		}
		

		try {
			client.deleteStore(storeName);
		} catch (UnknownStoreException e) {
			// ignore
		}
		
		store = client.createStore(storeName);
	}
  
	/**
	 * call this in tests where you're going to use overlapped Txs
	 */
	protected void allowOverlappedTx() {
		((StoreImpl)store).setAllowTxOverlapInThread(true);
	}
	
	
	@After
	public void closeDatabase() throws Exception {
		if (useEmbeddedDatabase) {
			EmbeddedClientFactory.getInstance().shutdownDatabase();
		}
		else {
			if (database != null) {
				database.close();
			}
		}
	}
	
	private Database startNewDatabase() throws IOException {
		// NOTE: We use the single parameter version of InetSocketAddr
		InetSocketAddress anyLocalAddress = new InetSocketAddress(serverPort);
		Database db = new Database(new SocketListeningServer(anyLocalAddress));
		IndexImplementationsService service = new IndexImplementationsService();
//			service.add( new WhirlwindIndexImpl());
		db.setIndexImplsService(service);
		db.startServer();
		return db;

	}
	
	protected void restartDatabase() throws IOException, UnknownHostException {
		if (useEmbeddedDatabase) {
			EmbeddedClientFactory.getInstance().shutdownDatabase();
			client = EmbeddedClientFactory.getInstance().createEmbeddedClient();
		}
		else {
			database.close();
			// Make server
			database = startNewDatabase();
			
			// Make client
			client = Factory.createClient();
			client.connect(new InetSocketAddress(defaultAddress, serverPort));
		}

		store = client.openStore(storeName);
	}

	protected boolean isDatabaseClosed() {
		if (useEmbeddedDatabase) {
			return EmbeddedClientFactory.getInstance().isDatabaseClosed();
		}
		else {
			return database.isClosed();
		}
	}

	/**
	 * Get latest ADM.  Calling function should not store references on object instances
	 * as this gets refreshed after modifications, so they need to see the mods.
	 * getInstance() actually does a Transaction.refresh(adm)
	 */
	protected AttrDefinitionMgr getAttrMgr() {
		return SyncedAttrDefinitionMgr.getInstance(store).getObject();
	}
}