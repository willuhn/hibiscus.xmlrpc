/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/Attic/AbstractTransferServiceImpl.java,v $
 * $Revision: 1.5 $
 * $Date: 2007/06/04 16:39:19 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.HibiscusTransfer;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des Transfer-Service.
 */
public abstract class AbstractTransferServiceImpl extends AbstractServiceImpl implements TransferService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public AbstractTransferServiceImpl() throws RemoteException
  {
    super();
  }
  
  /**
   * Liefert den Objekttyp des Transfers.
   * @return Objekt-Typ.
   */
  abstract Class getTransferType();
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService#list()
   */
  public String[] list() throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(getTransferType());
      String[] list = new String[i.size()];
      int count = 0;
      while (i.hasNext())
      {
        HibiscusTransfer t = (HibiscusTransfer) i.next();
        Konto k = t.getKonto();
        StringBuffer sb = new StringBuffer();
        sb.append(k.getID());
        sb.append(":");
        sb.append(t.getGegenkontoNummer());
        sb.append(":");
        sb.append(t.getGegenkontoBLZ());
        sb.append(":");
        sb.append(t.getGegenkontoName());
        sb.append(":");
        sb.append(t.getZweck());
        sb.append(":");
        sb.append(HBCI.DECIMALFORMAT.format(t.getBetrag()));
        list[count++] = sb.toString();
      }
      return list;
    }
    catch (RemoteException re)
    {
      throw re;
    }
    catch (Exception e)
    {
      Logger.error("unable to load list",e);
    }
    return null;
  }

  /**
   * Erzeugt das Objekt.
   * @param kontoID ID des Kontos.
   * @param kto Kontonummer Gegenkonto.
   * @param blz BLZ Gegenkonto.
   * @param name Name Gegenkontoinhaber.
   * @param zweck Verwendungszweck.
   * @param betrag Betrag.
   * @return der erzeugte Transfer.
   * @throws RemoteException
   * @throws ApplicationException
   */
  protected HibiscusTransfer createObject(String kontoID, String kto, String blz, String name, String zweck, double betrag)
    throws RemoteException, ApplicationException
  {
    DBService service = null;

    // Wird sonst nur in der GUI geprueft. Da ich es nicht direkt in
    // den Hibiscus-Fachobjekten einbauen will (dort koennte es Fehler
    // beim Import von DTAUS/CSV-Dateien verursachen), machen wir den
    // Check hier nochmal.
    if (betrag > Settings.getUeberweisungLimit())
      throw new ApplicationException(i18n.tr("Auftragslimit überschritten: {0} ", 
          HBCI.DECIMALFORMAT.format(Settings.getUeberweisungLimit()) + " " + HBCIProperties.CURRENCY_DEFAULT_DE));
    
    try
    {
      service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
    }
    catch (RemoteException re)
    {
      throw re;
    }
    catch (Exception e)
    {
      throw new RemoteException("unable to load service",e);
    }

    Konto k = null;
    try
    {
      k = (Konto) service.createObject(Konto.class,kontoID);
    }
    catch (ObjectNotFoundException oe)
    {
      throw new ApplicationException(i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",kontoID));
    }
    
    HibiscusTransfer t = (HibiscusTransfer) service.createObject(getTransferType(),null);
    t.setKonto(k);
    t.setGegenkontoNummer(kto);
    t.setGegenkontoBLZ(blz);
    t.setGegenkontoName(name);
    t.setZweck(zweck);
    t.setBetrag(betrag);
    t.store();
    return t;
  }
}


/*********************************************************************
 * $Log: AbstractTransferServiceImpl.java,v $
 * Revision 1.5  2007/06/04 16:39:19  willuhn
 * @N Pruefung des Auftragslimits
 *
 * Revision 1.4  2007/06/04 12:49:05  willuhn
 * @N Angabe des Typs bei Lastschriften
 *
 * Revision 1.3  2007/05/02 09:36:01  willuhn
 * @C API changes
 *
 * Revision 1.2  2006/11/20 22:41:10  willuhn
 * @B wrong transfer type
 *
 * Revision 1.1  2006/11/16 22:11:26  willuhn
 * @N Added lastschrift support
 *
 * Revision 1.1  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 **********************************************************************/