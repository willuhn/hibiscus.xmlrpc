/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/AbstractServiceImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2010/03/31 12:24:51 $
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
import java.rmi.server.UnicastRemoteObject;

import de.willuhn.datasource.Service;
import de.willuhn.jameica.hbci.xmlrpc.Plugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.I18N;

/**
 * Abstrakte Basis-Implementierung der Services.
 */
public abstract class AbstractServiceImpl extends UnicastRemoteObject implements Service
{
  protected final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  private boolean started = false;

  /**
   * ct.
   * @throws RemoteException
   */
  public AbstractServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.Service#isStartable()
   */
  public boolean isStartable() throws RemoteException
  {
    return !started;
  }

  /**
   * @see de.willuhn.datasource.Service#isStarted()
   */
  public boolean isStarted() throws RemoteException
  {
    return started;
  }

  /**
   * @see de.willuhn.datasource.Service#start()
   */
  public void start() throws RemoteException
  {
    if (!isStartable())
    {
      Logger.warn("service allready started or not startable, skipping request");
      return;
    }
    this.started = true;
  }

  /**
   * @see de.willuhn.datasource.Service#stop(boolean)
   */
  public void stop(boolean arg0) throws RemoteException
  {
    if (!isStarted())
    {
      Logger.warn("service not started, skipping request");
      return;
    }
    this.started = false;
  }
  
}


/*********************************************************************
 * $Log: AbstractServiceImpl.java,v $
 * Revision 1.5  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 * Revision 1.4  2009/03/08 22:25:47  willuhn
 * @N optionales Quoting
 *
 * Revision 1.3  2008/12/17 14:40:56  willuhn
 * @N Aktualisiertes Patch von Julian
 *
 * Revision 1.2  2008/12/12 01:26:41  willuhn
 * @N Patch von Julian
 *
 * Revision 1.1  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 **********************************************************************/