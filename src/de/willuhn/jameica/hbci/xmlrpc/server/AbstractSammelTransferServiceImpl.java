/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/AbstractSammelTransferServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2008/12/09 14:00:18 $
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.rmi.SammelTransfer;
import de.willuhn.jameica.hbci.rmi.SammelTransferBuchung;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SammelTransferService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Abstrakte Basis-Implementierung des Service zur Erstellung von Sammel-Auftraegen.
 */
public abstract class AbstractSammelTransferServiceImpl extends AbstractServiceImpl implements SammelTransferService
{
  final static String PARAM_NAME                       = "name";
  final static String PARAM_KONTO                      = "konto";
  final static String PARAM_TERMIN                     = "termin";
  
  final static String PARAM_BUCHUNGEN                  = "buchungen";
  
  final static String PARAM_BUCHUNGEN_BETRAG           = "betrag";
  final static String PARAM_BUCHUNGEN_BLZ              = "blz";
  final static String PARAM_BUCHUNGEN_KONTONUMMER      = "kontonummer";
  final static String PARAM_BUCHUNGEN_NAME             = "name";
  final static String PARAM_BUCHUNGEN_TEXTSCHLUESSEL   = "textschluessel";
  final static String PARAM_BUCHUNGEN_VERWENDUNGSZWECK = "verwendungszweck";

  /**
   * ct.
   * @throws RemoteException
   */
  public AbstractSammelTransferServiceImpl() throws RemoteException
  {
    super();
  }
  
  /**
   * Liefert den Typ des Transfers zurueck.
   * @return Klasse des Transfer-Typs.
   * @throws RemoteException
   */
  abstract Class getTranferType() throws RemoteException;
  
  /**
   * Liefert den Typ der Buchung zurueck.
   * @return Typ der Buchung.
   * @throws RemoteException
   */
  abstract Class getBuchungType() throws RemoteException;

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.SammelTransferService#create(java.util.Map)
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

    ////////////////////////////////////////////////////////////////////////////
    // Auftrag anlegen
    SammelTransfer l = null;
    try
    {
      l = (SammelTransfer) service.createObject(getTranferType(),null);
      
      l.transactionBegin();
      
      l.setBezeichnung((String)auftrag.get(PARAM_NAME));
      l.setKonto(k);
      l.setTermin(date);
      l.store();
      
      Object o = auftrag.get(PARAM_BUCHUNGEN);
      if (o == null)
        throw new ApplicationException(i18n.tr("Keine Buchungen angegeben"));

      Map[] buchungen = null;
      if (o instanceof Map)
      {
        buchungen = new Map[]{(Map)o}; // nur eine Buchung
      }
      else if (o instanceof Object[])
      {
        Object[] ol = (Object[]) o;
        ArrayList<Map> al = new ArrayList<Map>();
        for (int n=0;n<ol.length;++n)
        {
          if (!(ol[n] instanceof Map))
            continue;
          al.add((Map)ol[n]);
        }
        buchungen = (Map[]) al.toArray(new Map[al.size()]);
      }

      if (buchungen == null || buchungen.length == 0)
        throw new ApplicationException(i18n.tr("Keine Buchungen angegeben"));

      for (int i=0;i<buchungen.length;++i)
      {
        ////////////////////////////////////////////////////////////////////////
        // Betrag
        double betrag = 0.0d;
        Object b = buchungen[i].get(PARAM_BUCHUNGEN_BETRAG);
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
          throw new ApplicationException(i18n.tr("Auftragslimit überschritten: {0} ", 
              HBCI.DECIMALFORMAT.format(Settings.getUeberweisungLimit()) + " " + HBCIProperties.CURRENCY_DEFAULT_DE));
        ////////////////////////////////////////////////////////////////////////
        
        SammelTransferBuchung buchung = (SammelTransferBuchung) service.createObject(getBuchungType(),null);
        buchung.setSammelTransfer(l);
        buchung.setBetrag(betrag);
        buchung.setGegenkontoBLZ((String)buchungen[i].get(PARAM_BUCHUNGEN_BLZ));
        buchung.setGegenkontoNummer((String)buchungen[i].get(PARAM_BUCHUNGEN_KONTONUMMER));
        buchung.setGegenkontoName((String)buchungen[i].get(PARAM_BUCHUNGEN_NAME));
        buchung.setTextSchluessel((String)buchungen[i].get(PARAM_BUCHUNGEN_TEXTSCHLUESSEL));
        
        ////////////////////////////////////////////////////////////////////////
        // Verwendungszweck
        Object zweck = buchungen[i].get(PARAM_BUCHUNGEN_VERWENDUNGSZWECK);
        if (zweck == null)
          throw new ApplicationException("Kein Verwendungszweck angegeben");
        if (zweck instanceof Object[])
        {
          Object[] list = (Object[]) zweck;
          if (list.length == 0)
            throw new ApplicationException("Kein Verwendungszweck angegeben");
          buchung.setZweck(list[0].toString());
          if (list.length > 1)
            buchung.setZweck2(list[1].toString());
          if (list.length > 2)
          {
            ArrayList<String> lines = new ArrayList<String>();
            for (int n=2;n<list.length;++n)
              lines.add(list[n].toString());
            buchung.setWeitereVerwendungszwecke((String[])lines.toArray(new String[lines.size()]));
          }
        }
        else
        {
          // Nur eine Zeile Verwendungszweck
          buchung.setZweck(zweck.toString());
        }
        ////////////////////////////////////////////////////////////////////////
        buchung.store();
      }
      l.transactionCommit();
      Logger.info("created bundle transfer \"" + l.getBezeichnung() + "\" [ID: " + l.getID() + "]");
      
      return null;
    }
    catch (Exception e)
    {
      if (l != null)
      {
        try {
          l.transactionRollback();
        }
        catch (Exception e2) {
          Logger.error("rollback failed",e2);
        }
      }
      if (e instanceof ApplicationException)
        return e.getMessage();

      Logger.error("unable to create transfer",e);
      return i18n.tr("Fehler beim Erstellen des Auftrages: {0}",e.getMessage());
    }
    ////////////////////////////////////////////////////////////////////////////
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.SammelTransferService#createParams()
   */
  public Map createParams() throws RemoteException
  {
    Map<String,Object> m = new HashMap<String,Object>();
    m.put(PARAM_NAME,  (String) null);
    m.put(PARAM_KONTO, (Integer) null);
    m.put(PARAM_TERMIN,(String) null);
    
    Map<String,Object> buchung = new HashMap<String,Object>();
    buchung.put(PARAM_BUCHUNGEN_BETRAG,          (Double) null);
    buchung.put(PARAM_BUCHUNGEN_BLZ,             (String) null);
    buchung.put(PARAM_BUCHUNGEN_KONTONUMMER,     (String) null);
    buchung.put(PARAM_BUCHUNGEN_NAME,            (String) null);
    buchung.put(PARAM_BUCHUNGEN_TEXTSCHLUESSEL,  (String) null);
    buchung.put(PARAM_BUCHUNGEN_VERWENDUNGSZWECK, new ArrayList<String>());
    m.put(PARAM_BUCHUNGEN,new Map[]{buchung});
    return m;
  }
}

/**********************************************************************
 * $Log: AbstractSammelTransferServiceImpl.java,v $
 * Revision 1.1  2008/12/09 14:00:18  willuhn
 * @N Update auf Java 1.5
 * @N Unterstuetzung fuer Sammel-Lastschriften und Sammel-Ueberweisungen
 *
 **********************************************************************/
