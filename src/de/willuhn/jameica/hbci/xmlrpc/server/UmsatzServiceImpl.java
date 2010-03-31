/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/UmsatzServiceImpl.java,v $
 * $Revision: 1.7 $
 * $Date: 2010/03/31 12:24:51 $
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.hbci.rmi.UmsatzTyp;
import de.willuhn.jameica.hbci.server.UmsatzUtil;
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

      ArrayList result = new ArrayList();
      while (list.hasNext())
      {
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.UmsatzService#list(java.lang.Map)
   */
  public List<Map<String, Object>> list(HashMap<String, Object> options) throws RemoteException
  {

    DBIterator list = UmsatzUtil.getUmsaetzeBackwards();
    
    ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

    UmsatzTyp typ = null;

    // Optionen durchlaufen
    for (Map.Entry<String, Object> entrySet : options.entrySet())
    {

      String key = entrySet.getKey();
      Object value = entrySet.getValue();

      try
      {
        
        boolean min = key.endsWith(":min");
        boolean max = key.endsWith(":max");
        
        // groesser/kleiner Vergleich ausfuehren?
        if(min || max)
        {
          // linker Teil vom Doppelpunkt
          String newKey = key.substring(0,key.lastIndexOf(':'));
          String operator = max ? "<=" : ">=";
          
          // Vergleiche mit Zahl
          if(newKey.equals(KEY_ID) ||
             newKey.equals(KEY_BETRAG) ||
             newKey.equals(KEY_SALDO))
            list.addFilter(newKey + " " + operator + " ?", new Object[] {value});

          // Vergleiche mit Datum
          else if(newKey.equals(KEY_VALUTA) || newKey.equals(KEY_DATUM))
            list.addFilter(newKey+" "+operator+" ?", new Object[] {DateUtil.toDate((String)value)});
          else
            throw new IllegalArgumentException("unsupported option key='"+newKey+"', operator='"+operator+"'");
        }
        // Exakte Uebereinstimmung
        else if (key.equals(KEY_KONTO_ID) ||
                 key.equals(KEY_ART) || 
                 key.equals(KEY_BETRAG) || 
                 key.equals(KEY_SALDO) ||
                 key.equals(KEY_VALUTA) || 
                 key.equals(KEY_DATUM) || 
                 key.equals(KEY_PRIMANOTA) ||
                 key.equals(KEY_CUSTOMER_REF))
        {
          list.addFilter(key + " = ?", new Object[] {value});
        }
        // Sonderbehandlung fuer Umsatz-Typ
        else if (key.equals(KEY_UMSATZ_TYP))
        {
          String text = (String) value;
          if (text.length() > 0)
          {
            typ = (UmsatzTyp) Settings.getDBService().createObject(UmsatzTyp.class, null);
            typ.setPattern(text);
          }
        }
        else
        {
          throw new IllegalArgumentException("unsupported option");
        }

      }
      catch (Exception e)
      {
        throw new RemoteException("option '" + key + "' invalid: " + value + " (" + e.getMessage() + ")");
      }
    }

    try
    {
      while (list.hasNext())
      {
        Umsatz u = (Umsatz) list.next();
        if (typ != null && !typ.matches(u))
          continue;

        Map<String, Object> map = new HashMap<String, Object>();
        
        // Zweck zusammenbauen
        StringBuffer zweck = new StringBuffer();
        zweck.append(StringUtil.notNull(u.getZweck()));
        
        String zweck2 = u.getZweck2();
        if(zweck2 != null){
          zweck.append('\n');
          zweck.append(zweck2);
        }
        
        String[] weitere = u.getWeitereVerwendungszwecke();
        for (int i=0; i < weitere.length; i++)
        {
          zweck.append('\n');
          zweck.append(weitere[i]);
        }
        
        map.put(KEY_ID,                u.getID());
        map.put(KEY_KONTO_ID,          u.getKonto().getID());
        map.put(KEY_GEGENKONTO_NAME,   StringUtil.notNull(u.getGegenkontoName()));
        map.put(KEY_GEGENKONTO_NUMMER, StringUtil.notNull(u.getGegenkontoNummer()));
        map.put(KEY_GEGENKONTO_BLZ,    StringUtil.notNull(u.getGegenkontoBLZ()));
        map.put(KEY_ART,               StringUtil.notNull(u.getArt()));
        map.put(KEY_BETRAG,            StringUtil.notNull(u.getBetrag()));
        map.put(KEY_VALUTA,            DateUtil.toString(u.getValuta()));
        map.put(KEY_DATUM,             DateUtil.toString(u.getDatum()));
        map.put(KEY_ZWECK,             zweck.toString());
        map.put(KEY_SALDO,             StringUtil.notNull(u.getSaldo()));
        map.put(KEY_PRIMANOTA,         StringUtil.notNull(u.getPrimanota()));
        map.put(KEY_CUSTOMER_REF,      StringUtil.notNull(u.getCustomerRef()));

        UmsatzTyp kat = u.getUmsatzTyp();
        
        if (kat != null)
          map.put(KEY_UMSATZ_TYP, kat.getName());

        map.put(KEY_KOMMENTAR, StringUtil.notNull(u.getKommentar()));
        
        result.add(map);
      }

    }
    catch (ParseException e)
    {
      throw new RemoteException(e.getMessage());
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
 * Revision 1.7  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 * Revision 1.6  2009/03/09 10:44:52  willuhn
 * not null
 *
 * Revision 1.5  2009/03/08 22:25:47  willuhn
 * @N optionales Quoting
 *
 * Revision 1.4  2008/12/18 21:17:33  willuhn
 * @N Drittes Patch von Julian (Parameter "art")
 *
 * Revision 1.3  2008/12/17 14:40:56  willuhn
 * @N Aktualisiertes Patch von Julian
 *
 * Revision 1.2  2008/12/12 01:26:42  willuhn
 * @N Patch von Julian
 * Revision 1.1 2007/09/30 14:11:20 willuhn
 * 
 * @N hibiscus.xmlrpc.umsatz.list
 * 
 **********************************************************************/
