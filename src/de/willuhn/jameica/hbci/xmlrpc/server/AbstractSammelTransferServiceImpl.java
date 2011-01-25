/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/AbstractSammelTransferServiceImpl.java,v $
 * $Revision: 1.3 $
 * $Date: 2011/01/25 13:43:54 $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.rmi.SammelTransfer;
import de.willuhn.jameica.hbci.rmi.SammelTransferBuchung;
import de.willuhn.jameica.hbci.server.VerwendungszweckUtil;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SammelTransferService;
import de.willuhn.jameica.hbci.xmlrpc.util.DecimalUtil;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;
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
  abstract Class getTransferType() throws RemoteException;
  
  /**
   * Liefert den Typ der Buchung zurueck.
   * @return Typ der Buchung.
   * @throws RemoteException
   */
  abstract Class getBuchungType() throws RemoteException;

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.SammelTransferService#create(java.util.Map)
   */
  public String create(Map auftrag) throws RemoteException
  {
    boolean supportNull = de.willuhn.jameica.hbci.xmlrpc.Settings.isNullSupported();
    
    SammelTransfer l = null;

    try
    {
      if (auftrag == null || auftrag.size() == 0)
        throw new ApplicationException(i18n.tr("Keine Auftragseigenschaften angegeben"));
      
      Object konto = auftrag.get(PARAM_KONTO);
      if (konto == null)
        throw new ApplicationException(i18n.tr("Kein Konto angegeben"));
  
  
      ////////////////////////////////////////////////////////////////////////////
      // Buchungen checken
      Object buchungen = auftrag.get(PARAM_BUCHUNGEN);
      if (buchungen == null)
        throw new ApplicationException(i18n.tr("Keine Buchungen angegeben"));
  
      List<Map> items = new ArrayList<Map>();
      if (buchungen instanceof Map)
      {
        items.add((Map)buchungen); // nur eine Buchung
      }
      else if (buchungen instanceof Object[])
      {
        Object[] ol = (Object[]) buchungen;
        for (Object o:ol)
        {
          if (!(o instanceof Map))
            continue;
          items.add((Map) o);
        }
      }
      if (items.size() == 0)
        throw new ApplicationException(i18n.tr("Keine Buchungen angegeben"));
      //
      ////////////////////////////////////////////////////////////////////////////

      
      // Auftrag anlegen
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");

      l = (SammelTransfer) service.createObject(getTransferType(),null);
    
      l.transactionBegin();
    
      l.setBezeichnung((String)auftrag.get(PARAM_NAME));
      l.setKonto((Konto) service.createObject(Konto.class,konto.toString()));
      l.setTermin(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(auftrag.get(PARAM_TERMIN)));
      l.store();
    

      for (Map m:items)
      {
        ////////////////////////////////////////////////////////////////////////
        // Betrag
        double betrag = DecimalUtil.parse(m.get(PARAM_BUCHUNGEN_BETRAG));

        if (betrag > Settings.getUeberweisungLimit())
          throw new ApplicationException(i18n.tr("Auftragslimit überschritten: {0} ", HBCI.DECIMALFORMAT.format(Settings.getUeberweisungLimit()) + " " + HBCIProperties.CURRENCY_DEFAULT_DE));
        ////////////////////////////////////////////////////////////////////////
      
        SammelTransferBuchung buchung = (SammelTransferBuchung) service.createObject(getBuchungType(),null);
        buchung.setSammelTransfer(l);
        buchung.setBetrag(betrag);
        buchung.setGegenkontoBLZ((String)m.get(PARAM_BUCHUNGEN_BLZ));
        buchung.setGegenkontoNummer((String)m.get(PARAM_BUCHUNGEN_KONTONUMMER));
        buchung.setGegenkontoName((String)m.get(PARAM_BUCHUNGEN_NAME));
        buchung.setTextSchluessel((String)m.get(PARAM_BUCHUNGEN_TEXTSCHLUESSEL));
        VerwendungszweckUtil.apply(buchung,StringUtil.parseUsage(m.get(PARAM_BUCHUNGEN_VERWENDUNGSZWECK)));

        buchung.store();
      }
      
      l.transactionCommit();
      Logger.info("created bundle transfer [ID: " + l.getID() + " (" + l.getClass().getName() + ")]");
      
      return supportNull ? null : l.getID();
    }
    catch (Exception e)
    {
      // Wir loggen nur echte Fehler
      if (!(e instanceof ApplicationException) && !(e instanceof ObjectNotFoundException))
        Logger.error("unable to create bundle transfer",e);

      // Auf jeden Fall erstmal die Transaktion zurueckrollen.
      if (l != null)
      {
        try {
          l.transactionRollback();
        }
        catch (Exception e2) {
          Logger.error("rollback failed",e2);
        }
      }

      // Fehlerbehandlung
      if (supportNull)
      {
        if (e instanceof ApplicationException)
          return e.getMessage();
        if (e instanceof ObjectNotFoundException)
          return i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",auftrag.get(PARAM_KONTO).toString());
        return i18n.tr("Fehler beim Erstellen des Auftrages: {0}",e.getMessage());
      }
      
      // OK, wir duerfen Exceptions werfen
      if (e instanceof ApplicationException)
        throw new RemoteException(e.getMessage(),e);
      if (e instanceof ObjectNotFoundException)
        throw new RemoteException(i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",auftrag.get(PARAM_KONTO).toString()));
      throw new RemoteException(i18n.tr("Fehler beim Erstellen des Auftrages: {0}",e.getMessage()),e);
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

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService#delete(java.lang.String)
   */
  public String delete(String id) throws RemoteException
  {
    boolean supportNull = de.willuhn.jameica.hbci.xmlrpc.Settings.isNullSupported();

    try
    {
      if (id == null || id.length() == 0)
        throw new ApplicationException(i18n.tr("Keine ID des zu löschenden Datensatzes angegeben"));

      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      SammelTransfer t = (SammelTransfer) service.createObject(getTransferType(),id);
      t.delete();
      Logger.info("deleted bundle transfer [ID: " + id + " (" + t.getClass().getName() + ")]");
      return supportNull ? null : id;
    }
    catch (Exception e)
    {
      if (supportNull)
        return e.getMessage();

      if (e instanceof RemoteException)
        throw (RemoteException) e;
      throw new RemoteException(e.getMessage(),e);
    }
  }
}

/**********************************************************************
 * $Log: AbstractSammelTransferServiceImpl.java,v $
 * Revision 1.3  2011/01/25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.2  2009/10/29 00:31:38  willuhn
 * @N Neue Funktionen createParams() und create(Map) in Einzelauftraegen (nahezu identisch zu Sammel-Auftraegen)
 *
 * Revision 1.1  2008/12/09 14:00:18  willuhn
 * @N Update auf Java 1.5
 * @N Unterstuetzung fuer Sammel-Lastschriften und Sammel-Ueberweisungen
 *
 **********************************************************************/
