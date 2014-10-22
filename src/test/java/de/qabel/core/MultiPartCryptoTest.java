package de.qabel.core;

import de.qabel.core.config.*;
import de.qabel.core.crypto.QblKeyFactory;
import de.qabel.core.crypto.QblPrimaryKeyPair;
import de.qabel.core.drop.*;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class MultiPartCryptoTest {

    class TestObject extends ModelObject {
        public TestObject() { }
        private String str;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    private DropController dropController;
    private DropQueueCallback<TestObject> mQueue;

    @Before
    public void setUp() throws MalformedURLException {
        dropController = new DropController();

        loadContacts();
        loadDropServers();

        mQueue = new DropQueueCallback<TestObject>();
        dropController.register(TestObject.class, mQueue);
    }

    @Test
    @Ignore
    public void multiPartCryptoOnlyOneMessageTest() throws InterruptedException {

        this.sendMessage();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dropController.retrieve();
        assertTrue(mQueue.size() >= 1);

        DropMessage<TestObject> msg = mQueue.take();
        assertEquals("Test", msg.getData().toString());
    }

    @Test
    @Ignore
    public void multiPartCryptoMultiMessageTest() throws InterruptedException {

        this.sendMessage();
        this.sendMessage();
        this.sendMessage();
        this.sendMessage();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        dropController.retrieve();
        assertTrue(mQueue.size() >= 4);

        DropMessage<TestObject> msg = mQueue.take();
        assertEquals("Test", msg.getData().toString());
        msg = mQueue.take();
        assertEquals("Test", msg.getData().toString());
        msg = mQueue.take();
        assertEquals("Test", msg.getData().toString());
        msg = mQueue.take();
        assertEquals("Test", msg.getData().toString());
    }

    private void loadContacts() throws MalformedURLException {
        Identity alice = new Identity(
                "Alice",
                new URL(
                        "http://localhost:6000/123456789012345678901234567890123456789012a"));
        QblPrimaryKeyPair alicesKey = QblKeyFactory.getInstance()
                .generateQblPrimaryKeyPair();
        alice.setPrimaryKeyPair(alicesKey);

        Identity bob = new Identity(
                "Bob",
                new URL(
                        "http://localhost:6000/123456789012345678901234567890123456789012b"));
        QblPrimaryKeyPair bobsKey = QblKeyFactory.getInstance()
                .generateQblPrimaryKeyPair();
        bob.setPrimaryKeyPair(bobsKey);

        Contact alicesContact = new Contact(alice);
        alicesContact.setPrimaryPublicKey(bobsKey.getQblPrimaryPublicKey());
        alicesContact.setEncryptionPublicKey(bobsKey.getQblEncPublicKey());
        alicesContact.setSignaturePublicKey(bobsKey.getQblSignPublicKey());
        alicesContact.getDropUrls().add(new URL("http://localhost:6000/123456789012345678901234567890123456789012b"));

        Contact bobsContact = new Contact(bob);
        bobsContact.setPrimaryPublicKey(alicesKey.getQblPrimaryPublicKey());
        bobsContact.setEncryptionPublicKey(alicesKey.getQblEncPublicKey());
        bobsContact.setSignaturePublicKey(alicesKey.getQblSignPublicKey());
        alicesContact.getDropUrls().add(new URL("http://localhost:6000/123456789012345678901234567890123456789012a"));

        Contacts contacts = new Contacts();
        contacts.getContacts().add(alicesContact);
        contacts.getContacts().add(bobsContact);

        dropController.setContacts(contacts);
    }

    private void loadDropServers() throws MalformedURLException {
        DropServers servers = new DropServers();

        DropServer alicesServer = new DropServer();
        alicesServer
                .setUrl(new URL(
                        "http://localhost:6000/123456789012345678901234567890123456789012a"));

        DropServer bobsServer = new DropServer();
        bobsServer
                .setUrl(new URL(
                        "http://localhost:6000/123456789012345678901234567890123456789012b"));

        servers.getDropServer().add(alicesServer);
        servers.getDropServer().add(bobsServer);

        dropController.setDropServers(servers);
    }

    private void sendMessage() {
        DropMessage<TestObject> dm = new DropMessage<TestObject>();
        TestObject data = new TestObject();
        data.setStr("Test");
        dm.setData(data);

        Date date = new Date();
        dm.setTime(date);
        dm.setVersion(1);
        dm.setModelObject(TestObject.class);

        Drop<TestObject> drop = new Drop<TestObject>();

        // Send hello world to all contacts.
        drop.sendAndForget(dm, dropController.getContacts().getContacts());
    }
}