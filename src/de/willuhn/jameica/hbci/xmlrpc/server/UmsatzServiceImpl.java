/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/UmsatzServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2007/09/30 14:11:20 $
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

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.HBCIProperties;
import de.willuhn.jameica.hbci.Settings;
import de.willuhn.jameica.hbci.rmi.Umsatz;
import de.willuhn.jameica.hbci.rmi.UmsatzTyp;
import de.willuhn.jameica.hbci.server.UmsatzUtil;
import de.willuhn.jameica.hbci.xmlrpc.rmi.UmsatzService;
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.UmsatzService#list(java.lang.String, java.lang.String, java.lang.String)
   */
  public String[] list(String text, String von, String bis) throws RemoteException
  {
    try
    {
      DBIterator list = UmsatzUtil.getUmsaetzeBackwards();
      
      if (von != null && von.length() > 0)
      {
        try
        {
          Date start = HBCI.DATEFORMAT.parse(von);
          list.addFilter("valuta >= ?", new Object[] { new java.sql.Date(HBCIProperties.startOfDay(start).getTime())});
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
          list.addFilter("valuta <= ?", new Object[] {new java.sql.Date(HBCIProperties.endOfDay(end).getTime())});
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

        // $!{umsatz.CustomerRef};$!{kat};$!{umsatz.Kommentar}

        StringBuffer sb = new StringBuffer();
        sb.append(u.getKonto().getID());
        sb.append(":");
        sb.append(notNull(u.getGegenkontoNummer()));
        sb.append(":");
        sb.append(notNull(u.getGegenkontoBLZ()));
        sb.append(":");
        sb.append(notNull(u.getGegenkontoName()));
        sb.append(":");
        sb.append(HBCI.DECIMALFORMAT.format(u.getBetrag()));
        sb.append(":");
        sb.append(u.getValuta() == null ? "" : HBCI.DATEFORMAT.format(u.getValuta()));
        sb.append(":");
        sb.append(u.getDatum() == null ? "" : HBCI.DATEFORMAT.format(u.getDatum()));
        sb.append(":");
        sb.append(notNull(u.getZweck()));
        sb.append(":");
        sb.append(notNull(u.getZweck2()));
        sb.append(":");
        sb.append(HBCI.DECIMALFORMAT.format(u.getSaldo()));
        sb.append(":");
        sb.append(notNull(u.getPrimanota()));
        sb.append(":");
        sb.append(notNull(u.getCustomerRef()));
        UmsatzTyp kat = u.getUmsatzTyp();
        String skat = "";
        if (kat != null)
          skat = kat.getName();
        sb.append(":");
        sb.append(skat);
        sb.append(":");
        sb.append(u.getKommentar());
        result.add(sb.toString());
      }
      return (String[])result.toArray(new String[result.size()]);
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
   * Wandelt ein Objekt in einen String um.
   * @param o das Objekt.
   * @return Die String-Repraesentation oder "" - niemals aber null.
   */
  private String notNull(Object o)
  {
    if (o == null)
      return "";
    String s = o.toString();
    return s == null ? "" : s;
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
 * Revision 1.1  2007/09/30 14:11:20  willuhn
 * @N hibiscus.xmlrpc.umsatz.list
 *
 **********************************************************************/