/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/KontoService.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/10/31 01:44:09 $
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
  public String[] getList() throws RemoteException;
  
  /**
   * Legt ein neues Konto an.
   * @param kontonummer
   * @param blz
   * @param name
   * @param inhaber
   * @return null wenn das Anlegen erfolgreich war, sonst den Fehlertext.
   * @throws RemoteException
   */
  public String create(String kontonummer, String blz, String name, String inhaber) throws RemoteException;
}


/*********************************************************************
 * $Log: KontoService.java,v $
 * Revision 1.1  2006/10/31 01:44:09  willuhn
 * @Ninitial checkin
 *
 **********************************************************************/