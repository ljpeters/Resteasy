package org.jboss.resteasy.examples.springmvc;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientURI;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.messagebody.WriterUtility;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class ContactsTest
{

   @Path(ContactsResource.CONTACTS_URL)
   public interface ContactProxy
   {
      @Path("data")
      @POST
      @Consumes(MediaType.APPLICATION_XML)
      Response createContact(Contact contact);

      @GET
      @Produces(MediaType.APPLICATION_XML)
      Contact getContact(@ClientURI String uri);

      @GET
      String getString(@ClientURI String uri);
   }

   private static TJWSEmbeddedSpringMVCServer server;
   private static ContactProxy proxy;
   public static final String host = "http://localhost:8080/";

   @BeforeClass
   public static void setup()
   {
      // server = new
      // TJWSEmbeddedSpringMVCServer("classpath:springmvc-servlet.xml", 8080);
      // server.start();

      RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
      proxy = ProxyFactory.create(ContactProxy.class, host);
   }

   @AfterClass
   public static void end()
   {
      server.stop();
   }

   @Ignore
   @Test
   public void performTest()
   {
      Response response = proxy.createContact(new Contact("Solomon", "Duskis"));
      String duskisUri = (String) response.getMetadata().getFirst(
            HttpHeaderNames.LOCATION);
      System.out.println(duskisUri);
      Assert.assertTrue(duskisUri.endsWith(ContactsResource.CONTACTS_URL
            + "/data/Duskis"));
      Assert
            .assertEquals("Solomon", proxy.getContact(duskisUri).getFirstName());
      proxy.createContact(new Contact("Bill", "Burkie"));
      System.out.println(proxy.getString(ContactsResource.CONTACTS_URL
            + "/data"));
   }

   @Ignore
   @Test
   public void readHTML()
   {
      System.out.println(proxy.getString(ContactsResource.CONTACTS_URL));
   }

   @Test
   public void writeJson() throws IOException
   {
      Contact contact = new Contact("Solomon", "Duskis");
      System.out.println(WriterUtility.asString(contact,
            MediaType.APPLICATION_JSON));
   }
}
