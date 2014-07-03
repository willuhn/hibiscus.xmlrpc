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
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.rmi.Konto;
import de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;
import de.willuhn.jameica.messaging.QueryMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;

/**
 * Implementierung des Konto-Service.
 */
public class KontoServiceImpl extends AbstractServiceImpl implements
    KontoService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public KontoServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#list()
   */
  public String[] list() throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(Konto.class);

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

        Konto k = (Konto) i.next();
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtil.quote(StringUtil.notNull(k.getID())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getKontonummer())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getBLZ())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getBezeichnung())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getKundennummer())));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(k.getName())));
        
        double saldo = k.getSaldo();
        Date date    = k.getSaldoDatum();
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(date != null ? (""+saldo) : "")));
        sb.append(":");
        sb.append(StringUtil.quote(StringUtil.notNull(date != null ? HBCI.DATEFORMAT.format(date) : "")));
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#find()
   */
  public List<Map<String, String>> find() throws RemoteException
  {
    try
    {
      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      DBIterator i = service.createList(Konto.class);

      List<Map<String,String>> result = new ArrayList<Map<String,String>>();
      
      int count = 0;
      int limit = de.willuhn.jameica.hbci.xmlrpc.Settings.getResultLimit();

      while (i.hasNext())
      {
        if (count++ > limit)
        {
          Logger.warn("result size limited to " + limit + " items");
          break;
        }

        Konto k = (Konto) i.next();
        Date datum = k.getSaldoDatum();
        double sa  = k.getSaldoAvailable();
        
        Map<String,String> m = new HashMap<String,String>();
        m.put("id",                  k.getID());
        m.put(PARAM_BEZEICHNUNG,     StringUtil.notNull(k.getBezeichnung()));
        m.put(PARAM_BIC,             StringUtil.notNull(k.getBic()));
        m.put(PARAM_BLZ,             StringUtil.notNull(k.getBLZ()));
        m.put(PARAM_IBAN,            StringUtil.notNull(k.getIban()));
        m.put(PARAM_KOMMENTAR,       StringUtil.notNull(k.getKommentar()));
        m.put(PARAM_KONTONUMMER,     StringUtil.notNull(k.getKontonummer()));
        m.put(PARAM_KUNDENNUMMER,    StringUtil.notNull(k.getKundennummer()));
        m.put(PARAM_NAME,            StringUtil.notNull(k.getName()));
        m.put(PARAM_SALDO,           HBCI.DECIMALFORMAT.format(k.getSaldo()));
        m.put(PARAM_SALDO_AVAILABLE, Double.isNaN(sa) ? "" : HBCI.DECIMALFORMAT.format(sa));
        m.put(PARAM_SALDO_DATUM,     datum != null ? HBCI.DATEFORMAT.format(datum) : "");
        m.put(PARAM_UNTERKONTO,      StringUtil.notNull(k.getUnterkonto()));
        m.put(PARAM_WAEHRUNG,        StringUtil.notNull(k.getWaehrung()));
        result.add(m);
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
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] konto";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#checkAccountCRC(java.lang.String, java.lang.String)
   */
  public boolean checkAccountCRC(String blz, String kontonummer) throws RemoteException
  {
    if (blz == null || kontonummer == null || blz.length() == 0 || kontonummer.length() == 0)
      return false;
    QueryMessage msg = new QueryMessage(blz + ":" + kontonummer);
    Application.getMessagingFactory().getMessagingQueue("hibiscus.query.accountcrc").sendSyncMessage(msg);
    Object value = msg.getData();
    if (value == null || !(value instanceof Boolean))
      return false;
    return ((Boolean)value).booleanValue();
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#getBankname(java.lang.String)
   */
  public String getBankname(String blz) throws RemoteException
  {
    if (blz == null || blz.length() == 0)
      return "";
    QueryMessage msg = new QueryMessage(blz);
    Application.getMessagingFactory().getMessagingQueue("hibiscus.query.bankname").sendSyncMessage(msg);
    Object value = msg.getData();
    return value == null ? "" : value.toString();
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.KontoService#calculateIBAN(java.lang.String, java.lang.String)
   */
  public String[] calculateIBAN(String blz, String kontonummer) throws RemoteException
  {
	  try
	  {
      if (blz == null || kontonummer == null || blz.length() == 0 || kontonummer.length() == 0)
        return null;
      QueryMessage msg = new QueryMessage(blz + ":" + kontonummer);
      Application.getMessagingFactory().getMessagingQueue("hibiscus.query.ibancalc").sendSyncMessage(msg);
      Object value = msg.getData();
      if(value instanceof Exception)
      {
      	throw (Exception) value;
      }
      return (String[])value ;
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
}
