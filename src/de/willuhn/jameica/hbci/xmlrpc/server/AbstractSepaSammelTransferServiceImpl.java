/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.HBCIDBService;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.rmi.SammelTransfer;
import de.willuhn.jameica.hbci.rmi.SepaSammelTransfer;
import de.willuhn.jameica.hbci.rmi.SepaSammelTransferBuchung;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SepaSammelTransferService;
import de.willuhn.jameica.hbci.xmlrpc.util.DecimalUtil;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.DateUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Abstrakte Basis-Implementierung des Service zur Erstellung von Sammel-Auftraegen.
 * @param <T> Der konkrete Typ des Sammelauftrages.
 * @param <K> Der konkrete Typ der einzelnen Buchungen.
 */
public abstract class AbstractSepaSammelTransferServiceImpl<T extends SepaSammelTransfer, K extends SepaSammelTransferBuchung>  extends AbstractServiceImpl implements SepaSammelTransferService
{
  /**
   * ct.
   * @throws RemoteException
   */
  public AbstractSepaSammelTransferServiceImpl() throws RemoteException
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
    
    T l = null;

    try
    {
      if (auftrag == null || auftrag.size() == 0)
        throw new ApplicationException(i18n.tr("Keine Auftragseigenschaften angegeben"));
      
      Object konto = auftrag.get(XmlRpcParameter.PARAM_KONTO);
      if (konto == null)
        throw new ApplicationException(i18n.tr("Kein Konto angegeben"));
  
  
      ////////////////////////////////////////////////////////////////////////////
      // Buchungen checken
      Object buchungen = auftrag.get(XmlRpcParameter.PARAM_BUCHUNGEN);
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

      l = (T) service.createObject(getTransferType(),null);
    
      l.transactionBegin();
    
      l.setBezeichnung((String)auftrag.get(XmlRpcParameter.PARAM_NAME));
      l.setKonto((Konto) service.createObject(Konto.class,konto.toString()));
      l.setTermin(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(auftrag.get(XmlRpcParameter.PARAM_TERMIN)));
      
      this.beforeStore(auftrag, l);
      
      l.store();
    

      for (Map m:items)
      {
        ////////////////////////////////////////////////////////////////////////
        // Betrag
        double betrag = DecimalUtil.parse(m.get(XmlRpcParameter.PARAM_BUCHUNGEN_BETRAG));

        if (betrag > Settings.getUeberweisungLimit())
          throw new ApplicationException(i18n.tr("Auftragslimit überschritten: {0} ", HBCI.DECIMALFORMAT.format(Settings.getUeberweisungLimit()) + " " + HBCIProperties.CURRENCY_DEFAULT_DE));
        ////////////////////////////////////////////////////////////////////////
      
        K buchung = (K) service.createObject(getBuchungType(),null);
        buchung.setSammelTransfer(l);
        buchung.setBetrag(betrag);
        buchung.setGegenkontoBLZ((String)m.get(XmlRpcParameter.PARAM_BUCHUNGEN_BLZ));
        buchung.setGegenkontoNummer((String)m.get(XmlRpcParameter.PARAM_BUCHUNGEN_KONTONUMMER));
        buchung.setGegenkontoName((String)m.get(XmlRpcParameter.PARAM_BUCHUNGEN_NAME));
        
        String[] lines = StringUtil.parseUsage(m.get(XmlRpcParameter.PARAM_BUCHUNGEN_VERWENDUNGSZWECK));
        if (lines != null && lines.length > 0)
        {
          buchung.setZweck(lines[0]); // Bei SEPA-Auftraegen wird nur die erste Zeile beruecksichtigt
        }
        
        this.beforeStoreBuchung(m, buchung);

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
          return i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",auftrag.get(XmlRpcParameter.PARAM_KONTO).toString());
        return i18n.tr("Fehler beim Erstellen des Auftrages: {0}",e.getMessage());
      }
      
      // OK, wir duerfen Exceptions werfen
      if (e instanceof ApplicationException)
        throw new RemoteException(e.getMessage(),e);
      if (e instanceof ObjectNotFoundException)
        throw new RemoteException(i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",auftrag.get(XmlRpcParameter.PARAM_KONTO).toString()));
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
    m.put(XmlRpcParameter.PARAM_NAME,         (String) null);
    m.put(XmlRpcParameter.PARAM_KONTO,        (Integer) null);
    m.put(XmlRpcParameter.PARAM_TERMIN,       (String) null);
    m.put(XmlRpcParameter.PARAM_SEQUENCETYPE, (String) null);
    m.put(XmlRpcParameter.PARAM_TARGETDATE,   (String) null);
    m.put(XmlRpcParameter.PARAM_SEPATYPE,     (String) null);
    
    Map<String,Object> buchung = new HashMap<String,Object>();
    buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_KONTONUMMER,      (String) null);
    buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_NAME,             (String) null);
    buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_BLZ,              (String) null);
    buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_BETRAG,           (Double) null);
    buchung.put(XmlRpcParameter.PARAM_BUCHUNGEN_VERWENDUNGSZWECK, (String) null);
    buchung.put(XmlRpcParameter.PARAM_ENDTOEND_ID,                (String) null);
    buchung.put(XmlRpcParameter.PARAM_CREDITOR_ID,                (String) null);
    buchung.put(XmlRpcParameter.PARAM_MANDATE_ID,                 (String) null);
    buchung.put(XmlRpcParameter.PARAM_SIGNATUREDATE,              (String) null);
    
    
    m.put(XmlRpcParameter.PARAM_BUCHUNGEN,new Map[]{buchung});
    return m;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.SammelTransferService#delete(java.lang.String)
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
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.SepaSammelTransferService#find(java.util.Map)
   */
  public List<Map> find(Map<String,Object> options) throws RemoteException
  {
	  
	    try
	    {
	      HBCIDBService service = (HBCIDBService) Application.getServiceFactory().lookup(HBCI.class,"database");
	      DBIterator i = service.createList(getTransferType());

	      if(options.containsValue(XmlRpcParameter.PARAM_FIND_DATUM_MIN))
	      {
	      Date start = de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(options.get(XmlRpcParameter.PARAM_FIND_DATUM_MIN));
	      if (start != null)
	        i.addFilter("termin >= ?",new Object[]{new java.sql.Date(DateUtil.startOfDay(start).getTime())});
	      }
	      if(options.containsValue(XmlRpcParameter.PARAM_FIND_DATUM_MAX))
	      {
	      Date end = de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(options.get(XmlRpcParameter.PARAM_FIND_DATUM_MAX));
	      if (end != null)
	        i.addFilter("termin <= ?",new Object[]{new java.sql.Date(DateUtil.endOfDay(end).getTime())});
	      }
	      i.setOrder("ORDER BY " + service.getSQLTimestamp("termin") + " DESC, id DESC");

	      List<Map> result = new ArrayList<Map>();

	      int count = 0;
	      int limit = de.willuhn.jameica.hbci.xmlrpc.Settings.getResultLimit();
	      while (i.hasNext())
	      {
	        if (count++ > limit)
	        {
	          Logger.warn("result size limited to " + limit + " items");
	          break;
	        }

	        T t = (T) i.next();
	        Konto k = t.getKonto();
	        Map<String,Object> values = new HashMap<String,Object>();
	        values.put(XmlRpcParameter.PARAM_ID,          t.getID());
	        values.put(XmlRpcParameter.PARAM_AUSGEFUEHRT, Boolean.toString(t.ausgefuehrt()));
	        values.put(XmlRpcParameter.PARAM_KONTO,       k.getID());
	        values.put(XmlRpcParameter.PARAM_TERMIN,      HBCI.DATEFORMAT.format(t.getTermin()));
	        values.put(XmlRpcParameter.PARAM_NAME,        t.getBezeichnung());
	         
	        this.afterLoad(values,t);
	        
	        result.add(values);
	      }
	      return result;
	    }
	    catch (RemoteException re)
	    {
	      throw re;
	    }
	    catch (Exception e)
	    {
	      throw new RemoteException(e.getMessage(),e);
	    }
	  
  }
  
  /**
   * Kann von abgeleiteten Klassen ueberschrieben werden, um vor dem Speichern noch weitere Properties zu setzen.
   * @param params die Map mit den Properties.
   * @param auftrag der Auftrag.
   * @throws Exception
   */
  protected void beforeStore(Map params, T auftrag) throws Exception
  {
  }
  
  
  /**
   * Kann von abgeleiteten Klassen ueberschrieben werden, um vor dem Speichern noch weitere Properties zu setzen.
   * @param params die Map mit den Properties.
   * @param auftrag der Auftrag.
   * @throws Exception
   */
  protected void beforeStoreBuchung(Map params, K buchung) throws Exception
  {
  }
  /**
   * Kann von abgeleiteten Klassen ueberschrieben werden, um nach dem Laden (aber vor dem Ausliefern) noch weitere Properties zu setzen.
   * @param params die Map mit den Properties.
   * @param auftrag der Auftrag.
   * @throws Exception
   */
  protected void afterLoad(Map params, T auftrag) throws Exception
  {
  }
  
}
