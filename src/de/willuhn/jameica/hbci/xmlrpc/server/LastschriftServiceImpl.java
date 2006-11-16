/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/LastschriftServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/11/16 22:11:26 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.server;

import java.rmi.RemoteException;

import de.willuhn.jameica.hbci.rmi.Lastschrift;
import de.willuhn.jameica.hbci.xmlrpc.rmi.LastschriftService;

/**
 * Implementierung des Lastschrift-Service.
 */
public class LastschriftServiceImpl extends AbstractTransferServiceImpl implements
    LastschriftService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public LastschriftServiceImpl() throws RemoteException
  {
    super();
  }


  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] lastschrift";
  }


  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractTransferServiceImpl#getTransferType()
   */
  Class getTransferType()
  {
    return Lastschrift.class;
  }

}


/*********************************************************************
 * $Log: LastschriftServiceImpl.java,v $
 * Revision 1.1  2006/11/16 22:11:26  willuhn
 * @N Added lastschrift support
 *
 **********************************************************************/