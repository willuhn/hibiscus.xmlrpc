/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/LastschriftService.java,v $
 * $Revision: 1.2 $
 * $Date: 2007/06/04 12:49:05 $
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


/**
 * XML-RPC-Service zum Zugriff auf Lastschriften.
 */
public interface LastschriftService extends TransferService
{
  /**
   * Erzeugt eine Lastschrift.
   * Die Funktion wurde ueberschrieben, um zusaetzlich angeben
   * zu koennen, ob das Abbuchungsverfahren oder Einzugsermaechtigung
   * verwendet werden soll. 
   * @param kontoID ID des Kontos, ueber das der Transfer ausgefuehrt werden soll.
   * @param kto Kontonummer des Gegenkontos.
   * @param blz BLZ des Gegenkontos.
   * @param name Name des Gegenkontos.
   * @param zweck Verwendungszweck.
   * @param betrag Betrag.
   * @param type Art der Lastschrift. Moegliche Werte.
   * 04: Abbuchungsverfahren
   * 05: Einzugsermaechtigung
   * @return null wenn das Anlegen erfolgreich war, sonst den Fehlertext.
   * @throws RemoteException
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, double betrag, String type) throws RemoteException;

}


/*********************************************************************
 * $Log: LastschriftService.java,v $
 * Revision 1.2  2007/06/04 12:49:05  willuhn
 * @N Angabe des Typs bei Lastschriften
 *
 * Revision 1.1  2006/11/16 22:11:26  willuhn
 * @N Added lastschrift support
 *
 * Revision 1.1  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/31 01:44:09  willuhn
 * @Ninitial checkin
 *
 **********************************************************************/