/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/AbstractBaseUeberweisungServiceImpl.java,v $
 * $Revision: 1.9 $
 * $Date: 2012/03/28 22:18:41 $
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
import java.util.Arrays;
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
import de.willuhn.jameica.hbci.rmi.AuslandsUeberweisung;
import de.willuhn.jameica.hbci.rmi.BaseUeberweisung;
import de.willuhn.jameica.hbci.rmi.HBCIDBService;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.rmi.SepaLastschrift;
import de.willuhn.jameica.hbci.server.VerwendungszweckUtil;
import de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService;
import de.willuhn.jameica.hbci.xmlrpc.util.DecimalUtil;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.util.DateUtil;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Abstrakte Basis-Implementierung des Service fuer Ueberweisungen/Lastschriften.
 * @param <T> der konkrete Auftragstyp.
 */
public abstract class AbstractBaseUeberweisungServiceImpl<T extends BaseUeberweisung> extends AbstractServiceImpl implements BaseUeberweisungService
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
  public AbstractBaseUeberweisungServiceImpl() throws RemoteException
  {
    super();
  }
  
  /**
   * Liefert den Objekttyp des Transfers.
   * @return Objekt-Typ.
   */
  abstract Class<T> getType();
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService#list()
   */
  public String[] list() throws RemoteException
  {
    try
    {
      HBCIDBService service = (HBCIDBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(getType());
      i.setOrder("ORDER BY " + service.getSQLTimestamp("termin") + " DESC, id DESC");
      
      List<String> list = new ArrayList<String>();

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
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtil.quote(StringUtil.notNull(k.getID())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(t.getGegenkontoNummer())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(t.getGegenkontoBLZ())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(t.getGegenkontoName())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(t.getZweck())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(t.getZweck2())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(HBCI.DECIMALFORMAT.format(t.getBetrag()))));
        list.add(sb.toString());
      }
      return list.toArray(new String[list.size()]);
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService#find(java.lang.String, java.lang.String, java.lang.String)
   */
  public List<Map> find(String text, String von, String bis) throws RemoteException
  {
    try
    {
      HBCIDBService service = (HBCIDBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(getType());

      Date start = de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(von);
      if (start != null)
        i.addFilter("termin >= ?",new Object[]{new java.sql.Date(DateUtil.startOfDay(start).getTime())});

      Date end = de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(bis);
      if (end != null)
        i.addFilter("termin <= ?",new Object[]{new java.sql.Date(DateUtil.endOfDay(end).getTime())});

      if (text != null && text.length() > 0)
      {
        List<String> params = new ArrayList<String>();
        String s = "%" + text.toLowerCase() + "%";
        params.add(s);
        params.add(s);
        params.add(s);
        // Das ist ja mal haesslich und hat in der Basis-Klasse einfach nichts
        // zu suchen. Aber Auslandsueberweisungen haben nur eine Zeile Verwendungszweck
        // und ich hab jetzt keine Lust, hier noch eine abstrakte Methode einzufuehren
        String filter = "(lower(empfaenger_name) like ? or lower(empfaenger_konto) like ? or lower(zweck) like ? ";
        if (!getType().equals(AuslandsUeberweisung.class) && !getType().equals(SepaLastschrift.class)) // BUGZILLA 1469
        {
          params.add(s);
          params.add(s);
          filter += " or lower(zweck2) like ? or lower(zweck3) like ? ";
        }
        filter += ")";
        Object[] op = params.toArray(); // BUGZILLA 1468
        i.addFilter(filter,op);
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
        values.put("id",                t.getID());
        values.put("ausgefuehrt",       Boolean.toString(t.ausgefuehrt()));
        values.put(PARAM_BETRAG,        HBCI.DECIMALFORMAT.format(t.getBetrag()));
        values.put(PARAM_BLZ,           StringUtil.notNull(t.getGegenkontoBLZ()));
        values.put(PARAM_KONTO,         k.getID());
        values.put(PARAM_KONTONUMMER,   StringUtil.notNull(t.getGegenkontoNummer()));
        values.put(PARAM_NAME,          StringUtil.notNull(t.getGegenkontoName()));
        values.put(PARAM_TERMIN,        HBCI.DATEFORMAT.format(t.getTermin()));
        values.put(PARAM_TEXTSCHLUESSEL,StringUtil.notNull(t.getTextSchluessel()));
        
        List<String> usages = new ArrayList<String>();
        usages.add(t.getZweck());
        String z2   = t.getZweck2();
        String[] z3 = t.getWeitereVerwendungszwecke();
        if (z2 != null && z2.length() > 0) usages.add(z2);
        if (z3 != null && z3.length > 0)   usages.addAll(Arrays.asList(z3));
        values.put(PARAM_VERWENDUNGSZWECK,usages);
        
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, double, java.lang.String)
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, String zweck2, double betrag, String termin) throws RemoteException
  {
    return create(kontoID, kto, blz, name, zweck, zweck2, betrag, termin, null);
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService#create(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, double, java.lang.String, java.lang.String)
   */
  public String create(String kontoID, String kto, String blz, String name, String zweck, String zweck2, double betrag, String termin, String typ) throws RemoteException
  {
    Map<String,Object> params = new HashMap<String,Object>();
    params.put(PARAM_KONTO,kontoID);
    params.put(PARAM_KONTONUMMER,kto);
    params.put(PARAM_BLZ,blz);
    params.put(PARAM_NAME,name);
    
    List<String> usage = new ArrayList<String>();
    if (zweck != null && zweck.length() > 0) usage.add(zweck);
    if (zweck2 != null && zweck2.length() > 0) usage.add(zweck2);
    params.put(PARAM_VERWENDUNGSZWECK,usage);
    
    params.put(PARAM_BETRAG,betrag);
    params.put(PARAM_TERMIN,termin);
    params.put(PARAM_TEXTSCHLUESSEL,typ);
    
    return create(params);
  }
  
  /**
   * Erstellt den Auftrag.
   * @param auftrag der Auftrag.
   * @return der erstellte Auftrag.
   * @throws ApplicationException
   */
  private T createObject(Map auftrag) throws ApplicationException
  {
    if (auftrag == null || auftrag.size() == 0)
      throw new ApplicationException(i18n.tr("Keine Auftragseigenschaften angegeben"));

    Object konto = auftrag.get(PARAM_KONTO);
    if (konto == null)
      throw new ApplicationException(i18n.tr("Kein Konto angegeben"));

    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      
      ////////////////////////////////////////////////////////////////////////
      // Betrag
      double betrag = DecimalUtil.parse(auftrag.get(PARAM_BETRAG));

      // Wird sonst nur in der GUI geprueft. Da ich es nicht direkt in
      // den Hibiscus-Fachobjekten einbauen will (dort koennte es Fehler
      // beim Import von DTAUS/CSV-Dateien verursachen), machen wir den
      // Check hier nochmal.
      if (betrag > Settings.getUeberweisungLimit())
        throw new ApplicationException(i18n.tr("Auftragslimit überschritten: {0} ", HBCI.DECIMALFORMAT.format(Settings.getUeberweisungLimit()) + " " + HBCIProperties.CURRENCY_DEFAULT_DE));
      ////////////////////////////////////////////////////////////////////////////


      ////////////////////////////////////////////////////////////////////////////
      // Auftrag anlegen
      T t = (T) service.createObject(getType(),null);
      t.setKonto((Konto) service.createObject(Konto.class,konto.toString()));
      t.setBetrag(betrag);
      t.setGegenkontoBLZ((String) auftrag.get(PARAM_BLZ));
      t.setGegenkontoNummer((String)auftrag.get(PARAM_KONTONUMMER));
      t.setGegenkontoName((String)auftrag.get(PARAM_NAME));
      t.setTextSchluessel((String)auftrag.get(PARAM_TEXTSCHLUESSEL));
      t.setTermin(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(auftrag.get(PARAM_TERMIN)));
      VerwendungszweckUtil.apply(t,StringUtil.parseUsage(auftrag.get(PARAM_VERWENDUNGSZWECK)));
      
      this.beforeStore(auftrag,t);

      t.store();
      Logger.info("created transfer [ID: " + t.getID() + " (" + t.getClass().getName() + ")]");

      return t;
    }
    catch (ObjectNotFoundException oe)
    {
      throw new ApplicationException(i18n.tr("Das Konto mit der ID {0} wurde nicht gefunden",konto.toString()));
    }
    catch (ApplicationException ae)
    {
      throw ae;
    }
    catch (Exception e)
    {
      Logger.error("unable to create transfer",e);
      throw new ApplicationException(i18n.tr("Fehler beim Erstellen des Auftrages: {0}",e.getMessage()),e);
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
   * Kann von abgeleiteten Klassen ueberschrieben werden, um nach dem Laden (aber vor dem Ausliefern) noch weitere Properties zu setzen.
   * @param params die Map mit den Properties.
   * @param auftrag der Auftrag.
   * @throws Exception
   */
  protected void afterLoad(Map params, T auftrag) throws Exception
  {
  }


  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService#create(java.util.Map)
   */
  public String create(Map auftrag) throws RemoteException
  {
    boolean supportNull = de.willuhn.jameica.hbci.xmlrpc.Settings.isNullSupported();

    try
    {
      T t = createObject(auftrag);
      return supportNull ? null : t.getID();
    }
    catch (ApplicationException e)
    {
      if (supportNull)
        return e.getMessage();
      throw new RemoteException(e.getMessage(),e);
    }
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.BaseUeberweisungService#createParams()
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
      T t = (T) service.createObject(getType(),id);
      t.delete();
      Logger.info("deleted transfer [ID: " + id + " (" + t.getClass().getName() + ")]");
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
