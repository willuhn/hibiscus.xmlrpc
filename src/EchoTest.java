/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/EchoTest.java,v $
 * $Revision: 1.3 $
 * $Date: 2011/01/25 13:43:54 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;


/**
 * Testklasse.
 */
public class EchoTest
{

  /**
   * @param args
   * @throws Exception
   */
  public static void main(String[] args) throws Exception
  {
    // Die URL, unter der Jameica auf XML-RPC Anfragen reagiert.
    // Der Port 8080 kann ueber Datei->Einstellungen->HTTP festgelegt werden
    // Wenn die Jameica-Installation auf einem anderen Rechner laeuft, kann
    // auch eine andere IP verwendet werden.
    // Ist in Jameica die Verwendung von SSL fuer XML-RPC-Anfragen aktiv,
    // muss die URL mit "https" beginnen

    // Der Slash am Ende ist wichtig. Der XML-RPC-Server macht bei Bedarf
    // zwar automatisch ein HTTP-Redirect, der XML-RPC-Client versteht
    // dieses redirect jedoch nicht.
    String url = "https://localhost:8080/xmlrpc/";

    // Client-Config erzeugen
    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    
    // Jameica-Masterpasswort
    // Falls in Jameica die Abfrage des Passwortes bei XML-RPC-Anfragen aktiviert ist
    config.setBasicPassword("test");
    
    // Ein Username muss leider angegeben werden, wenn in Jameica die
    // Passwort-Kontrolle aktiv ist. Sonst wirft Apache XML-RPC einen Fehler.
    // Geprueft wird der Username aber nicht, man kann also einen beliebigen
    // Wert angeben
    config.setBasicUserName("egal");
    
    // optional: Freischaltung von Apache-spezifischen Erweiterungen.
    // Damit lassen sich z.Bsp. auch Objekte des Typs java.io.Serializable uebertragen
    config.setEnabledForExtensions(true);
    
    // Angabe der Server-URL.
    // "https" falls in Jameica Verschluesselung fuer XML-RPC aktiviert wurde
    config.setServerURL(new URL(url));

    if (url.startsWith("https"))
    {
      initSSL();
    }
    
    // Client erzeugen und Config uebernehmen
    final XmlRpcClient client = new XmlRpcClient();
    client.setConfig(config);

    
    
    ////////////////////////////////////////////////////////////////////////////
    // Test 1: Liste der Umsaetze abrufen
    {
      System.out.println("Test 1: Umsaetze abrufen");
      Map params = new HashMap();
      params.put("datum:min","01.01.2010");
      params.put("datum:max","31.12.2011");
      params.put("zweck","test");
      
      Object[] l = (Object[]) client.execute("hibiscus.xmlrpc.umsatz.list",new Object[]{params});
      for (Object o:l)
      {
        System.out.println(o);
      }
    }
    ////////////////////////////////////////////////////////////////////////////


    ////////////////////////////////////////////////////////////////////////////
    // Test 2: Liste der Konten abrufen
    {
      System.out.println("Test 2: Konten abrufen");
      Object[] l = (Object[]) client.execute("hibiscus.xmlrpc.konto.list",(Object[]) null);
      for (Object o:l)
      {
        System.out.println(o);
      }
    }
    ////////////////////////////////////////////////////////////////////////////

    
    ////////////////////////////////////////////////////////////////////////////
    // Test 3: Liste der Ueberweisungen abrufen
    // Die Aufrufe fuer "hibiscus.xmlrpc.lastschrift.find" und "hibiscus.xmlrpc.sepaueberweisung.find" sind identisch
    {
      System.out.println("Test 3: Überweisungen abrufen");
      Object[] l = (Object[]) client.execute("hibiscus.xmlrpc.ueberweisung.find",new String[]{"test","01.01.2009","31.12.2011"});
      for (Object o:l)
      {
        System.out.println(o);
      }
    }
    ////////////////////////////////////////////////////////////////////////////
  
    ////////////////////////////////////////////////////////////////////////////
    // Test 4: Ueberweisung anlegen
    {
      // Variante 1
      Map params = new HashMap();
      params.put("betrag",1.50d);
      params.put("termin","15.01.2011");
      params.put("konto",195);
      params.put("name","Max Mustermann");
      params.put("blz","12345678");
      params.put("kontonummer","1111111111");
      params.put("verwendungszweck","Test");
      Object result = client.execute("hibiscus.xmlrpc.ueberweisung.create",new Object[]{params});
      System.out.println(result);
      
      // Auftrag wieder loeschen
      System.out.println(client.execute("hibiscus.xmlrpc.ueberweisung.delete",new Object[]{result}));

      // Variante 2
      result = client.execute("hibiscus.xmlrpc.ueberweisung.create",new Object[]{"195",             // Konto-ID
                                                                                 "1111111111",      // Empfaenger-Konto
                                                                                 "12345678",        // Empfaenger-BLZ
                                                                                 "Max Mustermann",  // Empfaenger-Name
                                                                                 "Test",            // Verwendungszweck 1
                                                                                 "Test 2",          // Verwendungszweck 2
                                                                                 2.5d,              // Betrag
                                                                                 "15.01.2011"});    // Termin
      System.out.println(result);
      
      // Auftrag wieder loeschen
      System.out.println(client.execute("hibiscus.xmlrpc.ueberweisung.delete",new Object[]{result}));
    }
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Test 4: Sammel-Lastschrift anlegen
    {
      Map params = new HashMap();
      params.put("name","Test");
      params.put("termin","15.01.2011");
      params.put("konto",195);
      
      Map buchung = new HashMap();
      buchung.put("betrag",1.50d);
      buchung.put("name","Max Mustermann");
      buchung.put("blz","12345678");
      buchung.put("kontonummer","1111111111");
      buchung.put("verwendungszweck","Test");
      List buchungen = new ArrayList();
      buchungen.add(buchung);
      
      params.put("buchungen",buchungen);
      
      Object result = client.execute("hibiscus.xmlrpc.sammellastschrift.create",new Object[]{params});
      System.out.println(result);

      // Auftrag wieder loeschen
      System.out.println(client.execute("hibiscus.xmlrpc.sammellastschrift.delete",new Object[]{result}));
    }
    ////////////////////////////////////////////////////////////////////////////
  }


  
  /**
   * Falls SSL akrtiv ist, initialisiert diese Funktion Java fuer die Verwendung von HTTPs.
   * @throws Exception
   */
  private static void initSSL() throws Exception
  {
    // Hinweis: Die folgenden Zeilen Code sind nur fuer Testzwecke gedacht.
    // Im produktiven Einsatz sollte man selbstverstaendlich pruefen, ob
    // die Zertifikate in Ordnung sind und ob der Hostname des Rechners
    // mit dem des Zertifikates uebereinstimmt. Um den Code hier jedoch
    // zu verkuerzen, akzeptieren wir alle Zertifikate.
    
    // Wenn SSL aktiv ist, muessen wir dem SSL-Zertifikat von Jameica vertrauen.
    // Da Java das nicht von allein macht, muessen wir das tun: Wir implementieren
    // hierzu einen TrustManager, der alle Zertifikate akzeptiert und verwenden
    // diesen, um den SSLContext zu initialisieren.
    TrustManager trustAll = new X509TrustManager()
    {
       /**
       * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
       */
      public java.security.cert.X509Certificate[] getAcceptedIssuers()
      {
        return null;
      }
      
      /**
       * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
       */
      public void checkClientTrusted (X509Certificate[] certs, String authType) {}
      
      /**
       * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
       */
      public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    };
    
    // Initialisieren des SSLContext mit unserem eigenen TrustManager
    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, new TrustManager[]{trustAll}, new SecureRandom());
    
    // Jetzt muessen wir Java noch mitteilen, dass fuer HTTPS-Verbindungen
    // bitte unser SSLContext verwendet werden soll.
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    
    // Und als ob das alles noch nicht geung ist, muss der Hostname, der
    // in dem Zertifikat steht auch noch mit dem tatsaechlichen Hostnamen
    // des Rechners uebereinstimmen. Fuer diesen Fall kann man auch
    // noch den HostnameVerifier in Java ersetzen, damit dieser auch
    // dann Zertifikate akzeptiert, wenn der Hostname nicht mit dem
    // CN-Namen des Zertifikats uebereinstimmt.
    
    HostnameVerifier verifier = new HostnameVerifier() {
    
      public boolean verify(String arg0, SSLSession arg1)
      {
        // Wir sagen immer ja.
        return true;
      }
    };
    HttpsURLConnection.setDefaultHostnameVerifier(verifier); 

  }
}


/*********************************************************************
 * $Log: EchoTest.java,v $
 * Revision 1.3  2011/01/25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.2  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 * Revision 1.1  2009/03/09 10:37:34  willuhn
 * @N Payment-Server Build 2009-03-09
 *
 * Revision 1.9  2008/04/04 00:17:14  willuhn
 * @N Apache XML-RPC von 3.0 auf 3.1 aktualisiert
 * @N jameica.xmlrpc ist jetzt von jameica.webadmin abhaengig
 * @N jameica.xmlrpc nutzt jetzt keinen eigenen embedded Webserver mehr sondern den Jetty von jameica.webadmin mittels Servlet. Damit kann nun XML-RPC ueber den gleichen TCP-Port (8080) gemacht werden, wo auch die restlichen Webfrontends laufen -> spart einen TCP-Port und skaliert besser wegen Multi-Threading-Support in Jetty
 *
 * Revision 1.8  2006/11/08 00:01:45  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2006/11/08 00:01:31  willuhn
 * *** empty log message ***
 *
 * Revision 1.6  2006/11/07 23:57:16  willuhn
 * @N Testcode erweitert
 *
 * Revision 1.5  2006/11/01 00:49:30  willuhn
 * @N Beispiel-Code dokumentiert
 *
 * Revision 1.4  2006/10/31 17:06:26  willuhn
 * @N GUI to configure xml-rpc
 *
 * Revision 1.3  2006/10/31 01:43:08  willuhn
 * *** empty log message ***
 *
 * Revision 1.2  2006/10/28 01:05:37  willuhn
 * @N add bindings on demand
 *
 * Revision 1.1  2006/10/26 23:54:15  willuhn
 * @N added needed jars
 * @N first working version
 *
 **********************************************************************/