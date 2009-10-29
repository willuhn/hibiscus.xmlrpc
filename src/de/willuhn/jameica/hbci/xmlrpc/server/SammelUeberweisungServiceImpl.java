/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/SammelUeberweisungServiceImpl.java,v $
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

import de.willuhn.jameica.hbci.rmi.SammelUeberweisung;
import de.willuhn.jameica.hbci.rmi.SammelUeberweisungBuchung;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SammelUeberweisungService;

/**
 * Implementierung des Service zur Erstellung von Sammel-Ueberweisungen
 */
public class SammelUeberweisungServiceImpl extends AbstractSammelTransferServiceImpl implements SammelUeberweisungService
{
  /**
   * ct.
   * @throws RemoteException
   */
  public SammelUeberweisungServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] sammelueberweisung";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSammelTransferServiceImpl#getBuchungType()
   */
  Class getBuchungType() throws RemoteException
  {
    return SammelUeberweisungBuchung.class;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractSammelTransferServiceImpl#getTransferType()
   */
  Class getTransferType() throws RemoteException
  {
    return SammelUeberweisung.class;
  }

  
}

/**********************************************************************
 * $Log: SammelUeberweisungServiceImpl.java,v $
 * Revision 1.2  2009/10/29 00:31:38  willuhn
 * @N Neue Funktionen createParams() und create(Map) in Einzelauftraegen (nahezu identisch zu Sammel-Auftraegen)
 *
 * Revision 1.1  2008/12/09 14:00:18  willuhn
 * @N Update auf Java 1.5
 * @N Unterstuetzung fuer Sammel-Lastschriften und Sammel-Ueberweisungen
 *
 **********************************************************************/
