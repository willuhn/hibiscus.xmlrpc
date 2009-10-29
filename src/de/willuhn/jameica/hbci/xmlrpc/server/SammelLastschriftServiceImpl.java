/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/SammelLastschriftServiceImpl.java,v $
 * $Revision: 1.2 $
 * $Date: 2009/10/29 00:31:38 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.server;

import java.rmi.RemoteException;

import de.willuhn.jameica.hbci.rmi.SammelLastBuchung;
import de.willuhn.jameica.hbci.rmi.SammelLastschrift;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SammelLastschriftService;

/**
 * Implementierung des Service zur Erstellung von Sammel-Lastschriften
 */
public class SammelLastschriftServiceImpl extends AbstractSammelTransferServiceImpl implements SammelLastschriftService
{
  /**
   * ct.
   * @throws RemoteException
   */
  public SammelLastschriftServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] sammellastschrift";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSammelTransferServiceImpl#getBuchungType()
   */
  Class getBuchungType() throws RemoteException
  {
    return SammelLastBuchung.class;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSammelTransferServiceImpl#getTransferType()
   */
  Class getTransferType() throws RemoteException
  {
    return SammelLastschrift.class;
  }

  
}

/**********************************************************************
 * $Log: SammelLastschriftServiceImpl.java,v $
 * Revision 1.2  2009/10/29 00:31:38  willuhn
 * @N Neue Funktionen createParams() und create(Map) in Einzelauftraegen (nahezu identisch zu Sammel-Auftraegen)
 *
 * Revision 1.1  2008/12/09 14:00:18  willuhn
 * @N Update auf Java 1.5
 * @N Unterstuetzung fuer Sammel-Lastschriften und Sammel-Ueberweisungen
 *
 **********************************************************************/
