/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.rmi;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;


/**
 * XML-RPC-Service zum Zugriff auf SEPA-Sammel-Auftraege.
 */
public interface SepaSammelTransferService extends SammelTransferService
{
  /**
   * Liefert eine Liste von Auftraegen.
   * 
   * @param options Map mit den folgenden Optionen
   * <ul>
   *  <li> text : optionaler Suchbegriff. Gesucht wird in
   *  - allen Verwendungszweck-Zeilen
   *  - Name des Gegenkonto-Inhabers
   *  - Nummer des Gegenkontos</li>
   * <li> von : Angabe des Start-Datums.</li>
   * <li> bis : Angabe des End-Datums.</li>
   * <li> konto : Angabe des End-Datums.</li>
   * @return Liste der gefundenen Auftraege.
   * @throws RemoteException
   */
  public List<Map> find(Map<String,Object> options) throws RemoteException;
}
