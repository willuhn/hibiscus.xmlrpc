/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/Attic/AbstractTransferServiceImpl.java,v $
 * $Revision: 1.10 $
 * $Date: 2009/10/29 00:31:38 $
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.BaseUeberweisung;
import de.willuhn.jameica.hbci.rmi.HibiscusTransfer;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.rmi.Terminable;
import de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des Transfer-Service.
 */
public abstract class AbstractTransferServiceImpl extends AbstractServiceImpl implements TransferService
{
  final static String PARAM_KONTO            = "konto";
  final static String PARAM_TERMIN           = "termin";
  
  final static String PARAM_BLZ              = "blz";
  final static String PARAM_KONTONUMMER      = "kontonummer";
  final static String PARAM_NAME             = "name";
  final static String PARAM_BETRAG           = "betrag";
  final static String PARAM_VERWENDUNGSZWECK = "verwendungszweck";

  final static String PARAM_TEXTSCHLUESSEL   = "textschluessel";

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
        sb.append(quote(notNull(k.getID())));
        sb.append(":");
        sb.append(quote(notNull(t.getGegenkontoNummer())));
        sb.append(":");
        sb.append(quote(notNull(t.getGegenkontoBLZ())));
        sb.append(":");
        sb.append(quote(notNull(t.getGegenkontoName())));
        sb.append(":");
        sb.append(quote(notNull(t.getZweck())));
        sb.append(":");
        sb.append(quote(notNull(t.getZweck2())));
        sb.append(":");
        sb.append(quote(notNull(HBCI.DECIMALFORMAT.format(t.getBetrag()))));
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
   * @param termin der Termin im Format TT.MM.JJJJ.
   * @return der erzeugte Transfer.
   * @throws RemoteException
   * @throws ApplicationException
   */
  protected HibiscusTransfer createObject(String kontoID, String kto, String blz, String name, String zweck, String zweck2, double betrag, String termin)
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

    Date date = null;
    if (termin != null && termin.length() > 0)
    {
      try
      {
        date = HBCI.DATEFORMAT.parse(termin);
      }
      catch (Exception e)
      {
        throw new ApplicationException(i18n.tr("Angegebenes Datum ungültig: {0}",termin));
      }
    }
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
    
    if (zweck2 != null && zweck2.length() > 0)
      t.setZweck2(zweck2);

    t.setBetrag(betrag);
    
    if (date != null)
    {
      if (!(t instanceof Terminable))
        throw new ApplicationException(i18n.tr("Auftrag unterstützt keinen Ziel-Termin"));
      ((Terminable)t).setTermin(date);
    }
    t.store();
    return t;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService#create(java.util.Map)
   */
  public String create(Map auftrag) throws RemoteException, ApplicationException
  {
    if (auftrag == null || auftrag.size() == 0)
      throw new RemoteException("no params given");
    
    ////////////////////////////////////////////////////////////////////////////
    // Termin
    Date date = null;
    String termin = (String) auftrag.get(PARAM_TERMIN);
    if (termin != null && termin.length() > 0)
    {
      try
      {
        date = HBCI.DATEFORMAT.parse(termin);
      }
      catch (Exception e)
      {
        throw new ApplicationException(i18n.tr("Angegebenes Datum ungültig: {0}",termin));
      }
    }
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // DB-Service
    DBService service = null;
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
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////
    // Konto
    Konto k = null;
    Object kontoID = auftrag.get(PARAM_KONTO);
    if (kontoID == null)
      throw new ApplicationException(i18n.tr("Kein Konto angegeben"));
    
    String id = kontoID.toString();
    try
    {
      k = (Konto) service.createObject(Konto.class,id);
    }
    catch (ObjectNotFoundException oe)
    {
      throw new ApplicationException(i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",id));
    }
    ////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // Betrag
    double betrag = 0.0d;
    Object b = auftrag.get(PARAM_BETRAG);
    if (b == null)
      throw new ApplicationException(i18n.tr("Kein Betrag angegeben"));
    try
    {
      if (b instanceof Double)
        betrag = ((Double)b).doubleValue();
      else
        betrag = HBCI.DECIMALFORMAT.parse(b.toString()).doubleValue();
    }
    catch (Exception e)
    {
      throw new ApplicationException(i18n.tr("Ungültiger Betrag: {0}",b.toString()));
    }
    if (betrag > Settings.getUeberweisungLimit())
      throw new ApplicationException(i18n.tr("Auftragslimit überschritten: {0} ", HBCI.DECIMALFORMAT.format(Settings.getUeberweisungLimit()) + " " + HBCIProperties.CURRENCY_DEFAULT_DE));
    ////////////////////////////////////////////////////////////////////////
      

    ////////////////////////////////////////////////////////////////////////////
    // Auftrag anlegen
    HibiscusTransfer t = null;
    try
    {
      t = (HibiscusTransfer) service.createObject(getTransferType(),null);
      t.setKonto(k);

      if (date != null)
      {
        if (!(t instanceof Terminable))
          throw new ApplicationException(i18n.tr("Auftrag unterstützt keinen Ziel-Termin"));
        ((Terminable)t).setTermin(date);
      }
      
      t.setBetrag(betrag);
      t.setGegenkontoBLZ((String) auftrag.get(PARAM_BLZ));
      t.setGegenkontoNummer((String)auftrag.get(PARAM_KONTONUMMER));
      t.setGegenkontoName((String)auftrag.get(PARAM_NAME));
      
      String ts = (String)auftrag.get(PARAM_TEXTSCHLUESSEL);
      if (ts != null && ts.length() > 0)
      {
        if (!(t instanceof BaseUeberweisung))
          throw new ApplicationException(i18n.tr("Auftrag unterstützt keinen Textschluessel"));
        ((BaseUeberweisung)t).setTextSchluessel(ts);
      }
        
      ////////////////////////////////////////////////////////////////////////
      // Verwendungszweck
      Object zweck = auftrag.get(PARAM_VERWENDUNGSZWECK);
      if (zweck == null)
        throw new ApplicationException("Kein Verwendungszweck angegeben");
      if (zweck instanceof Object[])
      {
        Object[] list = (Object[]) zweck;
        if (list.length == 0)
          throw new ApplicationException("Kein Verwendungszweck angegeben");
        t.setZweck(list[0].toString());
        if (list.length > 1)
          t.setZweck2(list[1].toString());
        if (list.length > 2)
        {
          ArrayList<String> lines = new ArrayList<String>();
          for (int n=2;n<list.length;++n)
            lines.add(list[n].toString());
          t.setWeitereVerwendungszwecke((String[])lines.toArray(new String[lines.size()]));
        }
      }
      else
      {
        // Nur eine Zeile Verwendungszweck
        t.setZweck(zweck.toString());
      }
      ////////////////////////////////////////////////////////////////////////
      t.store();
      return null;
    }
    catch (Exception e)
    {
      if (e instanceof ApplicationException)
        return e.getMessage();

      Logger.error("unable to create transfer",e);
      return i18n.tr("Fehler beim Erstellen des Auftrages: {0}",e.getMessage());
    }
    ////////////////////////////////////////////////////////////////////////////
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.TransferService#createParams()
   */
  public Map createParams() throws RemoteException
  {
    Map<String,Object> m = new HashMap<String,Object>();
    m.put(PARAM_KONTO,           (Integer) null);
    m.put(PARAM_TERMIN,          (String) null);
    m.put(PARAM_BETRAG,          (Double) null);
    m.put(PARAM_BLZ,             (String) null);
    m.put(PARAM_KONTONUMMER,     (String) null);
    m.put(PARAM_NAME,            (String) null);
    m.put(PARAM_TEXTSCHLUESSEL,  (String) null);
    m.put(PARAM_VERWENDUNGSZWECK, new ArrayList<String>());
    return m;
  }

}


/*********************************************************************
 * $Log: AbstractTransferServiceImpl.java,v $
 * Revision 1.10  2009/10/29 00:31:38  willuhn
 * @N Neue Funktionen createParams() und create(Map) in Einzelauftraegen (nahezu identisch zu Sammel-Auftraegen)
 *
 * Revision 1.9  2009/03/08 22:27:14  willuhn
 * @N optionales Quoting
 *
 * Revision 1.8  2007/09/11 15:34:06  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2007/09/10 16:09:32  willuhn
 * @N Termin in XML-RPC Connector fuer Auftraege
 *
 * Revision 1.6  2007/07/24 14:49:57  willuhn
 * @N Neuer Paramater "zweck2"
 *
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