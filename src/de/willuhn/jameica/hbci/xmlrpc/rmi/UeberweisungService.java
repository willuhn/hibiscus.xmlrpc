/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/UeberweisungService.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/11/07 00:18:11 $
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

import de.willuhn.datasource.Service;

/**
 * XML-RPC-Service zum Zugriff auf Ueberweisungen.
 */
public interface UeberweisungService extends Service
{
  /**
   * Liefert eine Liste der Ueberweisungen.
   * Jede Zeile entspricht einer Ueberweisung. Die einzelnen Werte sind durch Doppelpunkt getrennt.
   * @return Liste der Ueberweisungen.
   * @throws RemoteException
   */
  public String[] list() throws RemoteException;
  
  /**
   * Legt eine neue Ueberweisung an.
   * @param kontoID ID des Kontos, ueber das die Ueberweisung ausgefuehrt werden soll.
   * @param kto Kontonummer des Empfaengers.
   * @param blz BLZ des Empfaengers.
   * @param name Name des Empfaengers.
   * @param zweck Verwendungszweck.
   * @param betrag Betrag.
   * @return null wenn das Anlegen erfolgreich war, sonst den Fehlertext.
   * @throws RemoteException
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, double betrag) throws RemoteException;
}


/*********************************************************************
 * $Log: UeberweisungService.java,v $
 * Revision 1.1  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/31 01:44:09  willuhn
 * @Ninitial checkin
 *
 **********************************************************************/