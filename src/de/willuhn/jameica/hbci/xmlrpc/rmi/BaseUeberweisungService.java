/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/BaseUeberweisungService.java,v $
 * $Revision: 1.3 $
 * $Date: 2011/01/25 13:43:53 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.rmi;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.Service;

/**
 * XML-RPC-Service zum Zugriff auf Transfers.
 */
public interface BaseUeberweisungService extends Service
{
  /**
   * Liefert eine Liste der Auftraege.
   * Jede Zeile entspricht einem Auftrag. Die einzelnen Werte sind durch Doppelpunkt getrennt.
   * @return Liste der Transfers.
   * @throws RemoteException
   */
  public String[] list() throws RemoteException;
  
  /**
   * Liefert eine Liste von Auftraegen.
   * @param text optionaler Suchbegriff. Gesucht wird in
   *  - allen Verwendungszweck-Zeilen
   *  - Name des Gegenkonto-Inhabers
   *  - Nummer des Gegenkontos
   * @param von optionale Angabe des Start-Datums.
   * @param bis optionale Angabe des End-Datums.
   * @return Liste der gefundenen Auftraege.
   * @throws RemoteException
   */
  public List<Map> find(String text, String von, String bis) throws RemoteException;
  
  /**
   * Legt einen neuen Auftrag an.
   * Die Funktion unterscheidet sich von der folgenden nur durch den fehlenden Parameter "type".
   * Sie existiert lediglich aus Gruenden der Abwaertskompatibilitaet.
   * @param kontoID ID des Kontos, ueber das der Auftrag ausgefuehrt werden soll.
   * @param kto Kontonummer des Gegenkontos.
   * @param blz BLZ des Gegenkontos.
   * @param name Name des Gegenkontos.
   * @param zweck Verwendungszweck.
   * @param zweck2 weiterer Verwendungszweck.
   * @param betrag Betrag.
   * @param termin Termin im Format TT.MM.JJJJ. oder JJJJ-MM-TT
   * @return siehe {@link BaseUeberweisungService#create(Map)}
   * @throws RemoteException
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, String zweck2, double betrag, String termin) throws RemoteException;

  /**
   * Legt einen neuen Auftrag an.
   * @param kontoID ID des Kontos, ueber das der Auftrag ausgefuehrt werden soll.
   * @param kto Kontonummer des Gegenkontos.
   * @param blz BLZ des Gegenkontos.
   * @param name Name des Gegenkontos.
   * @param zweck Verwendungszweck.
   * @param zweck2 weiterer Verwendungszweck.
   * @param betrag Betrag.
   * @param termin Termin im Format TT.MM.JJJJ. oder JJJJ-MM-TT
   * @param type der Text-Schluessel.
   * @return siehe {@link BaseUeberweisungService#create(Map)}
   * @throws RemoteException
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, String zweck2, double betrag, String termin, String type) throws RemoteException;

  /**
   * Erzeugt eine Map mit den Job-Parametern fuer einen Auftrag.
   * @return Vorkonfigurierte Map mit den noetigen Parametern.
   * @throws RemoteException
   */
  public Map createParams() throws RemoteException;
  
  /**
   * Erzeugt einen neuen Auftrag mit den ausgefuellten Parametern.
   * @param auftrag der zu erstellende Auftrag.
   * @return NULL wenn der Vorgang erfolgreich war, sonst den Fehlertext.
   * 
   * Falls der Parameter "xmlrpc.supports.null" in de.willuhn.jameica.hbci.xmlrpc.Plugin.properties
   * jedoch auf "false" gestellt ist, dann: ID des Datensatzes bei
   * Erfolg. Andernfalls wird eine Exception geworfen.
   * 
   * Also:
   * xmlrpc.supports.null=true  -> OK=return NULL, FEHLER=return Fehlertext
   * xmlrpc.supports.null=false -> OK=return ID,   FEHLER=throws Exception
   * @throws RemoteException
   */
  public String create(Map auftrag) throws RemoteException;
  
  /**
   * Loescht den Auftrag mit der angegebenen ID.
   * @param id die ID des Auftrages.
   * @return siehe {@link BaseUeberweisungService#create(Map)}
   * @throws RemoteException
   */
  public String delete(String id) throws RemoteException;
}

/*********************************************************************
 * $Log: BaseUeberweisungService.java,v $
 * Revision 1.3  2011/01/25 13:43:53  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.2  2010/03/31 12:27:45  willuhn
 * @N Auch in Kontonummer suchen
 *
 * Revision 1.1  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 * Revision 1.4  2009/10/29 00:31:38  willuhn
 * @N Neue Funktionen createParams() und create(Map) in Einzelauftraegen (nahezu identisch zu Sammel-Auftraegen)
 *
 * Revision 1.3  2007/09/10 16:09:32  willuhn
 * @N Termin in XML-RPC Connector fuer Auftraege
 *
 * Revision 1.2  2007/07/24 14:49:57  willuhn
 * @N Neuer Paramater "zweck2"
 *
 * Revision 1.1  2006/11/16 22:11:26  willuhn
 * @N Added lastschrift support
 *
 **********************************************************************/