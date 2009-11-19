/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/KontoService.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/11/19 22:58:05 $
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
 * XML-RPC-Service zum Zugriff auf Konten.
 */
public interface KontoService extends Service
{
  /**
   * Liefert eine Liste der Konten.
   * Jede Zeile entspricht einem Konto. Die einzelnen Werte sind durch Doppelpunkt getrennt.
   * @return Liste der Konten.
   * @throws RemoteException
   */
  public String[] list() throws RemoteException;
  
  /**
   * Prueft eine BLZ/Kontonummer auf Plausibilitaet anhand der Pruefsumme.
   * @param blz BLZ.
   * @param kontonummer Kontonummer.
   * @return true oder false.
   * @throws RemoteException
   */
  public boolean checkAccountCRC(String blz, String kontonummer) throws RemoteException;
  
  /**
   * Liefert den Namen des Kredit
   * @param blz BLZ
   * @return Name des Kreditinstitut.s
   * @throws RemoteException
   */
  public String getBankname(String blz) throws RemoteException;
}


/*********************************************************************
 * $Log: KontoService.java,v $
 * Revision 1.4  2009/11/19 22:58:05  willuhn
 * @R Konto#create entfernt - ist Unsinn
 *
 * Revision 1.3  2007/11/27 15:17:12  willuhn
 * @N CRC-Check und Bankname-Lookup
 *
 * Revision 1.2  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/31 01:44:09  willuhn
 * @Ninitial checkin
 *
 **********************************************************************/