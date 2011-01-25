/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/SepaUeberweisungServiceImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2011/01/25 13:43:54 $
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

/**
 * Implementierung des SEPA-Ueberweisung-Service.
 */
public class SepaUeberweisungServiceImpl extends AbstractBaseUeberweisungServiceImpl<AuslandsUeberweisung> implements SepaUeberweisungService
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
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#getType()
   */
  Class getType()
  {
    return AuslandsUeberweisung.class;
  }
}


/*********************************************************************
 * $Log: SepaUeberweisungServiceImpl.java,v $
 * Revision 1.3  2011/01/25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.2  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 * Revision 1.1  2010/01/19 12:28:25  willuhn
 * @N Support fuer SEPA-Ueberweisungen
 *
 **********************************************************************/