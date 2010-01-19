/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/SepaUeberweisungServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/01/19 12:28:25 $
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

import de.willuhn.jameica.hbci.rmi.AuslandsUeberweisung;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SepaUeberweisungService;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des SEPA-Ueberweisung-Service.
 */
public class SepaUeberweisungServiceImpl extends AbstractTransferServiceImpl implements SepaUeberweisungService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public SepaUeberweisungServiceImpl() throws RemoteException
  {
    super();
  }


  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] sepa-ueberweisung";
  }


  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractTransferServiceImpl#getTransferType()
   */
  Class getTransferType()
  {
    return AuslandsUeberweisung.class;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, double, java.lang.String)
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, String zweck2, double betrag, String termin) throws RemoteException
  {
    try
    {
      createObject(kontoID,kto,blz,name,zweck,zweck2, betrag,termin);
      return null;
    }
    catch (ApplicationException ae)
    {
      return ae.getMessage();
    }
    catch (RemoteException re)
    {
      Logger.error("unable to create transfer",re);
      return i18n.tr("Fehler beim Erstellen der SEPA-Überweisung: {0}",re.getMessage());
    }
  }
}


/*********************************************************************
 * $Log: SepaUeberweisungServiceImpl.java,v $
 * Revision 1.1  2010/01/19 12:28:25  willuhn
 * @N Support fuer SEPA-Ueberweisungen
 *
 **********************************************************************/