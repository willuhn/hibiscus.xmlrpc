/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/Attic/TransferService.java,v $
 * $Revision: 1.3 $
 * $Date: 2007/09/10 16:09:32 $
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
}

/*********************************************************************
 * $Log: TransferService.java,v $
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