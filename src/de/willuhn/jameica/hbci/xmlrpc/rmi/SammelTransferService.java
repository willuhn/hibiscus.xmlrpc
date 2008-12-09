/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/SammelTransferService.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/12/09 14:00:18 $
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
   * @return NULL, wenn das Anlegen erfolgreich war, sonst ein Fehlertext.
   * @throws RemoteException
   * @throws ApplicationException
   */
  public String create(Map auftrag) throws RemoteException, ApplicationException;
}


/*********************************************************************
 * $Log: SammelTransferService.java,v $
 * Revision 1.1  2008/12/09 14:00:18  willuhn
 * @N Update auf Java 1.5
 * @N Unterstuetzung fuer Sammel-Lastschriften und Sammel-Ueberweisungen
 *
 **********************************************************************/