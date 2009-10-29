/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/Attic/TransferService.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/10/29 00:31:38 $
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
import de.willuhn.util.ApplicationException;

/**
 * XML-RPC-Service zum Zugriff auf Transfers.
 */
public interface TransferService extends Service
{
  /**
   * Liefert eine Liste der Transfers.
   * Jede Zeile entspricht einem Transfer. Die einzelnen Werte sind durch Doppelpunkt getrennt.
   * @return Liste der Transfers.
   * @throws RemoteException
   */
  public String[] list() throws RemoteException;
  
  /**
   * Legt einen neuen Transfer an.
   * @param kontoID ID des Kontos, ueber das der Transfer ausgefuehrt werden soll.
   * @param kto Kontonummer des Gegenkontos.
   * @param blz BLZ des Gegenkontos.
   * @param name Name des Gegenkontos.
   * @param zweck Verwendungszweck.
   * @param zweck2 weiterer Verwendungszweck.
   * @param betrag Betrag.
   * @param termin Termin im Format TT.MM.JJJJ.
   * @return null wenn das Anlegen erfolgreich war, sonst den Fehlertext.
   * @throws RemoteException
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, String zweck2, double betrag, String termin) throws RemoteException;

  /**
   * Erzeugt eine Map mit den Job-Parametern fuer einen Auftrag.
   * @return Vorkonfigurierte Map mit den noetigen Parametern.
   * @throws RemoteException
   */
  public Map createParams() throws RemoteException;
  
  /**
   * Erzeugt einen neuen Auftrag mit den ausgefuellten Parametern.
   * @param auftrag der zu erstellende Auftrag.
   * @return NULL, wenn das Anlegen erfolgreich war, sonst ein Fehlertext.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public String create(Map auftrag) throws RemoteException, ApplicationException;
}

/*********************************************************************
 * $Log: TransferService.java,v $
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