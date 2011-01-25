/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/UmsatzServiceImpl.java,v $
 * $Revision: 1.11 $
 * $Date: 2011/01/25 13:53:25 $
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
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.hbci.rmi.UmsatzTyp;
import de.willuhn.jameica.hbci.server.UmsatzUtil;
import de.willuhn.jameica.hbci.server.VerwendungszweckUtil;
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

      if (von != null && von.length() > 0)
      {
        try
        {
          Date start = HBCI.DATEFORMAT.parse(von);
          list.addFilter("valuta >= ?", new Object[] { new java.sql.Date(HBCIProperties.startOfDay(start).getTime()) });
        }
        catch (Exception e)
        {
          throw new RemoteException("invalid start date: " + von);
        }
      }

      if (bis != null && bis.length() > 0)
      {
        try
        {
          Date end = HBCI.DATEFORMAT.parse(bis);
          list.addFilter("valuta <= ?", new Object[] { new java.sql.Date(HBCIProperties.endOfDay(end).getTime()) });
        }
        catch (Exception e)
        {
          throw new RemoteException("invalid end date: " + bis);
        }
      }

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
          Logger.warn("result size limited to " + limit + " tems");
          break;
        }

        Umsatz u = (Umsatz) list.next();
        if (typ != null && !typ.matches(u))
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
            list.addFilter(newKey + " " + operator + " ?", new Object[] {value});
            continue;
          }
          
          // Vergleiche mit Datum
          if (newKey.equals(KEY_VALUTA) || newKey.equals(KEY_DATUM))
          {
            list.addFilter(newKey + " " + operator + " ?", new Object[] {new java.sql.Date(DateUtil.parse(value).getTime())});
            continue;
          }
          
          Logger.warn("parameter " + key + " does not support :min or :max, skipping");
          continue;
        }
        
        
        // Exakte Uebereinstimmung
        if (key.equals(KEY_KONTO_ID) || key.equals(KEY_ART) || key.equals(KEY_BETRAG) || key.equals(KEY_SALDO) || key.equals(KEY_VALUTA) || key.equals(KEY_DATUM) || key.equals(KEY_PRIMANOTA) || key.equals(KEY_CUSTOMER_REF))
        {
          list.addFilter(key + " = ?", new Object[] {value});
          continue;
        }
        
        // Verwendungszweck, Kommentar und Gegenkonto sucht unscharf
        if (key.equals(KEY_GEGENKONTO_BLZ) || key.equals(KEY_GEGENKONTO_NAME) || key.equals(KEY_GEGENKONTO_NUMMER) || key.equals(KEY_ZWECK) || key.equals(KEY_KOMMENTAR))
        {
          String s = "%" + value.toString().toLowerCase() + "%";
          list.addFilter("lower(" + key + ")" + " like ?", new Object[] {s});
          continue;
        }

        // Umsatz-Typ
        if (key.equals(KEY_UMSATZ_TYP))
        {
          try
          {
            typ = (UmsatzTyp) Settings.getDBService().createObject(UmsatzTyp.class, value.toString());
          }
          catch (ObjectNotFoundException e)
          {
            Logger.warn("category [ID: " + value + "] not found, skipping");
          }
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
      if (typ != null && !typ.matches(u))
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
      map.put(KEY_ZWECK,             VerwendungszweckUtil.toString(u));
      map.put(KEY_SALDO,             StringUtil.notNull(u.getSaldo()));
      map.put(KEY_PRIMANOTA,         StringUtil.notNull(u.getPrimanota()));
      map.put(KEY_CUSTOMER_REF,      StringUtil.notNull(u.getCustomerRef()));
      map.put(KEY_KOMMENTAR,         StringUtil.notNull(u.getKommentar()));

      UmsatzTyp kat = u.getUmsatzTyp();
      
      if (kat != null)
        map.put(KEY_UMSATZ_TYP, kat.getName());

      result.add(map);
    }
    return result;
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
 * Revision 1.11  2011/01/25 13:53:25  willuhn
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