/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/SammelTransferService.java,v $
 * $Revision: 1.2 $
 * $Date: 2011/01/25 13:43:54 $
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
import java.util.Map;

import de.willuhn.datasource.Service;


/**
 * XML-RPC-Service zum Zugriff auf Sammel-Auftraege.
 */
public interface SammelTransferService extends Service
{
  /**
   * Erzeugt eine Map mit den Job-Parametern fuer einen Sammelauftrag.
   * @return Vorkonfigurierte Map mit den noetigen Parametern.
   * @throws RemoteException
   */
  public Map createParams() throws RemoteException;
  
  /**
   * Erzeugt einen neuen Sammel-Auftrag mit den ausgefuellten Parametern.
   * @param auftrag der zu erstellende Sammel-Auftrag.
   * @return NULL wenn das Anlegen erfolgreich war, sonst den Fehlertext.
   * Falls der Parameter "xmlrpc.supports.null" in de.willuhn.jameica.hbci.xmlrpc.Plugin.properties
   * jedoch auf "false" gestellt ist, dann: ID des erzeugten Datensatzes bei
   * erfolgreicher Anlage. Andernfalls wird eine Exception geworfen.
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
   * @return siehe {@link SammelTransferService#create(Map)}
   * @throws RemoteException
   */
  public String delete(String id) throws RemoteException;
}


/*********************************************************************
 * $Log: SammelTransferService.java,v $
 * Revision 1.2  2011/01/25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.1  2008/12/09 14:00:18  willuhn
 * @N Update auf Java 1.5
 * @N Unterstuetzung fuer Sammel-Lastschriften und Sammel-Ueberweisungen
 *
 **********************************************************************/