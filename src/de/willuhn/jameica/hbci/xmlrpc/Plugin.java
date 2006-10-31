/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/Plugin.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/10/31 01:44:10 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc;

import java.io.File;

import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.util.ApplicationException;

/**
 * hibiscus.xmlrpc Plugin.
 */
public class Plugin extends AbstractPlugin
{

  /**
   * ct.
   * @param file
   */
  public Plugin(File file)
  {
    super(file);
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
   */
  public void init() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#install()
   */
  public void install() throws ApplicationException
  {
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#shutDown()
   */
  public void shutDown()
  {
  }

  /**
   * @see de.willuhn.jameica.plugin.AbstractPlugin#update(double)
   */
  public void update(double oldVersion) throws ApplicationException
  {
  }

}


/*********************************************************************
 * $Log: Plugin.java,v $
 * Revision 1.1  2006/10/31 01:44:10  willuhn
 * @Ninitial checkin
 *
 **********************************************************************/