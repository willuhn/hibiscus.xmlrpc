/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/AbstractServiceImpl.java,v $
 * $Revision: 1.4 $
 * $Date: 2009/03/08 22:25:47 $
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
  private String quote = null;
  private boolean started = false;
  protected I18N i18n = null;
  private final static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * Wandelt ein Datum vom Format YYYY-MM-DD in einen
   * java.util.Date-Objekt um
   * @param date im Format YYYY-MM-DD
   * @return das Datum.
   * @throws ParseException
   */
  static Date toDate(String date) throws ParseException
  {
    return dateFormatter.parse(date);
  }
  
  /**
   * Wandelt ein Datum in das Format 'YYYY-MM-DD' um
   * @param date das Datum.
   * @return der formatierte String.
   * @throws ParseException
   */
  static String toString(Date date) throws ParseException
  {
    return dateFormatter.format(date);
  }

  /**
   * Quotet den Text.
   * @param s zu quotender Text.
   * @return der gequotete Text.
   */
  String quote(String s)
  {
    if (s == null || this.quote == null || this.quote.length() == 0)
      return s;
    
    // Erstmal enthaltene Quoting-Zeichen escapen
    s = s.replaceAll(this.quote,"\\" + this.quote);
    return this.quote + s + this.quote;
  }
  
  /**
   * Wandelt ein Objekt in einen String um.
   * @param o das Objekt.
   * @return Die String-Repraesentation oder "" - niemals aber null.
   */
  static String notNull(Object o)
  {
    if (o == null)
      return "";
    String s = o.toString();
    return s == null ? "" : s;
  }
  

  /**
   * ct.
   * @throws RemoteException
   */
  public AbstractServiceImpl() throws RemoteException
  {
    super();
    i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();
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
    this.quote = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings().getString("quoting.char",null);
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
    this.quote = null;
  }
  
}


/*********************************************************************
 * $Log: AbstractServiceImpl.java,v $
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