/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/UmsatzServiceImpl.java,v $
 * $Revision: 1.15 $
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

import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.hbci.rmi.UmsatzTyp;
import de.willuhn.jameica.hbci.server.UmsatzUtil;
import de.willuhn.jameica.hbci.server.VerwendungszweckUtil.Tag;
import de.willuhn.jameica.hbci.xmlrpc.rmi.UmsatzService;
import de.willuhn.jameica.hbci.xmlrpc.util.DateUtil;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;
import de.willuhn.logging.Logger;

/**
 * Implementierung des Umsatz-Service.
 */
public class UmsatzServiceImpl extends AbstractServiceImpl implements UmsatzService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public UmsatzServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.UmsatzService#list(java.lang.String,java.lang.String, java.lang.String)
   */
  public String[] list(String text, String von, String bis) throws RemoteException {
    try
    {
      DBIterator list = UmsatzUtil.getUmsaetzeBackwards();

      Date start = de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(von);
      if (start != null)
        list.addFilter("valuta >= ?",new Object[]{new java.sql.Date(de.willuhn.jameica.util.DateUtil.startOfDay(start).getTime())});

      Date end = de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(bis);
      if (end != null)
        list.addFilter("valuta <= ?",new Object[]{new java.sql.Date(de.willuhn.jameica.util.DateUtil.endOfDay(end).getTime())});
      
      // Wir suchen nicht selbst nach dem Suchbegriff sondern lassen das einfach
      // eine virtuelle Umsatz-Kategorie machen
      UmsatzTyp typ = null;
      if (text != null && text.length() > 0)
      {
        typ = (UmsatzTyp) Settings.getDBService().createObject(UmsatzTyp.class,null);
        typ.setPattern(text);
      }

      int count = 0;
      int limit = de.willuhn.jameica.hbci.xmlrpc.Settings.getResultLimit();
      List<String> result = new ArrayList<String>();
      while (list.hasNext())
      {
        if (count++ > limit)
        {
          // Sonst koennte man eine OutOfMemoryException provozieren
          Logger.warn("result size limited to " + limit + " items");
          break;
        }

        Umsatz u = (Umsatz) list.next();
        if (typ != null && !typ.matches(u,true))
          continue;

        StringBuffer sb = new StringBuffer();
        sb.append(StringUtil.quote(u.getKonto().getID()));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getGegenkontoNummer())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getGegenkontoBLZ())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getGegenkontoName())));
        sb.append(":");
        sb.append(StringUtil.quote(HBCI.DECIMALFORMAT.format(u.getBetrag())));
        sb.append(":");
        sb.append(StringUtil.quote(u.getValuta() == null ? "" : HBCI.DATEFORMAT.format(u.getValuta())));
        sb.append(":");
        sb.append(StringUtil.quote(u.getDatum() == null ? "" : HBCI.DATEFORMAT.format(u.getDatum())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getZweck())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getZweck2())));
        sb.append(":");
        sb.append(StringUtil.quote(HBCI.DECIMALFORMAT.format(u.getSaldo())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getPrimanota())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getCustomerRef())));
        UmsatzTyp kat = u.getUmsatzTyp();
        String skat = "";
        if (kat != null)
          skat = kat.getName();
        sb.append(":");
        sb.append(StringUtil.quote(skat));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(u.getKommentar())));
        result.add(sb.toString());
      }
      return (String[]) result.toArray(new String[result.size()]);
    }
    catch (RemoteException re)
    {
      throw re;
    }
    catch (Exception e)
    {
      Logger.error("unable to load list", e);
    }
    return null;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.UmsatzService#list(java.util.HashMap)
   */
  public List<Map<String, Object>> list(HashMap<String, Object> options) throws RemoteException
  {
    DBIterator list = UmsatzUtil.getUmsaetzeBackwards();
    
    UmsatzTyp typ = null;

    // Optionen durchlaufen
    for (Map.Entry<String, Object> entrySet : options.entrySet())
    {
      String key = entrySet.getKey();
      Object value = entrySet.getValue();
      
      if (key == null || value == null)
        continue; // nichts zum Filtern da
      
      key = key.trim();
      
      // SQL-Injection-Prevention. Alles ausfiltern, was nicht a-z,A-Z,_,: ist
      String stripped = key.replaceAll("[^a-zA-Z_:]","");
      if (!key.equals(stripped))
      {
        Logger.error("key \"" + key + "\" contains invalid chars, possible SQL injection, skipping");
        continue;
      }

      boolean min = key.endsWith(":min");
      boolean max = key.endsWith(":max");

      try
      {
        // groesser/kleiner Vergleich ausfuehren?
        if (min || max)
        {
          // linker Teil vom Doppelpunkt
          String newKey = key.substring(0,key.lastIndexOf(':'));
          String operator = max ? "<=" : ">=";
            
          // Vergleiche mit Zahl
          if (newKey.equals(KEY_ID) || newKey.equals(KEY_BETRAG) || newKey.equals(KEY_SALDO))
          {
            list.addFilter(newKey + " " + operator + " ?", value);
            continue;
          }
          
          // Vergleiche mit Datum
          if (newKey.equals(KEY_VALUTA) || newKey.equals(KEY_DATUM))
          {
            list.addFilter(newKey + " " + operator + " ?", new java.sql.Date(DateUtil.parse(value).getTime()));
            continue;
          }
          
          Logger.warn("parameter " + key + " does not support :min or :max, skipping");
          continue;
        }
        
        
        // Exakte Uebereinstimmung
        if (key.equals(KEY_KONTO_ID) || key.equals(KEY_ART) || key.equals(KEY_BETRAG) || key.equals(KEY_SALDO) || key.equals(KEY_PRIMANOTA) || key.equals(KEY_CUSTOMER_REF))
        {
          list.addFilter(key + " = ?", value);
          continue;
        }
        
        // Vergleiche mit Datum
        if (key.equals(KEY_VALUTA) || key.equals(KEY_DATUM))
        {
          list.addFilter(key + " = ?", new java.sql.Date(DateUtil.parse(value).getTime()));
          continue;
        }
        
        
        // Verwendungszweck, Kommentar und Gegenkonto sucht unscharf
        if (key.equals(KEY_GEGENKONTO_BLZ) || key.equals(KEY_GEGENKONTO_NAME) || key.equals(KEY_GEGENKONTO_NUMMER) || key.equals(KEY_KOMMENTAR))
        {
          String s = "%" + value.toString().toLowerCase() + "%";
          list.addFilter("lower(" + key + ")" + " like ?", s);
          continue;
        }
        
        // BUGZILLA 1478 - auch in zweck2 und zweck3 suchen
        if (key.equals(KEY_ZWECK))
        {
          String s = "%" + value.toString().toLowerCase() + "%";
          list.addFilter("(lower(zweck) like ? or lower(zweck2) like ? or lower(zweck3) like ?)", s, s, s);
          continue;
        }

        // Umsatz-Typ
        if (key.equals(KEY_UMSATZ_TYP))
        {
          typ = findKategorie(value.toString());
          continue;
        }
        Logger.warn("unknown parameter " + key + ", skipping");
      }
      catch (Exception e)
      {
        Logger.error("error while parsing parameter: " + key + ", value: " + value + ", skipping",e);
      }
    }

    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    int count = 0;
    int limit = de.willuhn.jameica.hbci.xmlrpc.Settings.getResultLimit();
    while (list.hasNext())
    {
      if (count++ > limit)
      {
        Logger.warn("result size limited to " + limit + " items");
        break;
      }
      
      Umsatz u = (Umsatz) list.next();
      if (typ != null && !typ.matches(u)) // wenn eine Kategorie angegeben ist, muss sie passen
        continue;

      Map<String, Object> map = new HashMap<String, Object>();
      map.put(KEY_ID,                u.getID());
      map.put(KEY_KONTO_ID,          u.getKonto().getID());
      map.put(KEY_GEGENKONTO_NAME,   StringUtil.notNull(u.getGegenkontoName()));
      map.put(KEY_GEGENKONTO_NUMMER, StringUtil.notNull(u.getGegenkontoNummer()));
      map.put(KEY_GEGENKONTO_BLZ,    StringUtil.notNull(u.getGegenkontoBLZ()));
      map.put(KEY_ART,               StringUtil.notNull(u.getArt()));
      map.put(KEY_BETRAG,            HBCI.DECIMALFORMAT.format(u.getBetrag()));
      map.put(KEY_VALUTA,            DateUtil.format(u.getValuta()));
      map.put(KEY_DATUM,             DateUtil.format(u.getDatum()));
      map.put(KEY_ZWECK,             BeanUtil.get(u,Tag.SVWZ.name()));
      map.put(KEY_SALDO,             StringUtil.notNull(u.getSaldo()));
      map.put(KEY_PRIMANOTA,         StringUtil.notNull(u.getPrimanota()));
      map.put(KEY_CUSTOMER_REF,      StringUtil.notNull(u.getCustomerRef()));
      map.put(KEY_KOMMENTAR,         StringUtil.notNull(u.getKommentar()));
      map.put(KEY_GVCODE,            StringUtil.notNull(u.getGvCode()));
      
      List<String> usages = new ArrayList<String>();
      usages.add(u.getZweck());
      String z2   = u.getZweck2();
      String[] z3 = u.getWeitereVerwendungszwecke();
      if (z2 != null && z2.length() > 0) usages.add(z2);
      if (z3 != null && z3.length > 0)   usages.addAll(Arrays.asList(z3));
      map.put(KEY_ZWECK_RAW,usages);


      UmsatzTyp kat = u.getUmsatzTyp();
      
      if (kat != null)
        map.put(KEY_UMSATZ_TYP, kat.getName());

      result.add(map);
    }
    return result;
  }
  
  /**
   * Sucht die Kategorie mit dem angegebenen Namen oder der ID.
   * @param s Name oder ID der Kategorie.
   * @return die gefundene Kategorie oder NULL.
   * @throws RemoteException
   */
  private UmsatzTyp findKategorie(String s) throws RemoteException
  {
    if (s == null)
      return null;
    
    s = s.trim().toLowerCase();
    if (s.length() == 0)
      return null;
    
    // Checken, ob wir sie anhand der ID finden
    try
    {
      return (UmsatzTyp) Settings.getDBService().createObject(UmsatzTyp.class,s);
    }
    catch (ObjectNotFoundException oe)
    {
      // Ne, gibts nicht
    }
    
    // Dann suchen wir anhand des Namens
    DBIterator list = Settings.getDBService().createList(UmsatzTyp.class);
    list.addFilter("lower(name) like ?",new Object[]{s});
    if (list.hasNext())
      return (UmsatzTyp) list.next();
    
    return null;
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] umsatz";
  }
}

/*********************************************************************
 * $Log: UmsatzServiceImpl.java,v $
 * Revision 1.15  2012/03/28 22:18:41  willuhn
 * @C Umstellung auf DateUtil, javadoc Fixes
 *
 * Revision 1.14  2011-06-07 10:07:53  willuhn
 * @C Verwendungszweck-Handling vereinheitlicht/vereinfacht - geht jetzt fast ueberall ueber VerwendungszweckUtil
 *
 * Revision 1.13  2011-02-10 11:55:19  willuhn
 * @B minor debugging
 *
 * Revision 1.12  2011-01-25 14:05:12  willuhn
 * @B Kompatibilitaet zu Hibiscus 1.12
 *
 * Revision 1.11  2011-01-25 13:53:25  willuhn
 * @C Jameica 1.10 Kompatibilitaet
 *
 * Revision 1.10  2011-01-25 13:49:26  willuhn
 * @N Limit konfigurierbar und auch in Auftragslisten beruecksichtigen
 *
 * Revision 1.9  2011-01-25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.8  2010/03/31 12:31:35  willuhn
 * *** empty log message ***
 *
 * Revision 1.7  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 **********************************************************************/