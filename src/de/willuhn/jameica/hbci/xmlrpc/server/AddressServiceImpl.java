/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/server/AddressServiceImpl.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/02/07 12:22:13 $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.rmi.Address;
import de.willuhn.jameica.hbci.rmi.AddressbookService;
import de.willuhn.jameica.hbci.rmi.HibiscusAddress;
import de.willuhn.jameica.hbci.xmlrpc.rmi.AddressService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des Adress-Service.
 */
public class AddressServiceImpl extends AbstractServiceImpl implements AddressService
{

  /**
   * ct.
   * @throws RemoteException
   */
  public AddressServiceImpl() throws RemoteException
  {
    super();
  }

  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] address";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.AddressService#create(java.util.Map)
   */
  public String create(Map<String,String> address) throws RemoteException
  {
    boolean supportNull = de.willuhn.jameica.hbci.xmlrpc.Settings.isNullSupported();

    try
    {
      if (address == null || address.size() == 0)
        throw new ApplicationException(i18n.tr("Keine Adresseigenschaften angegeben"));

      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      HibiscusAddress a = (HibiscusAddress) service.createObject(HibiscusAddress.class,null);
      a.setBic(address.get(PARAM_BIC));
      a.setBlz(address.get(PARAM_BLZ));
      a.setIban(address.get(PARAM_IBAN));
      a.setKategorie(address.get(PARAM_KATEGORIE));
      a.setKommentar(address.get(PARAM_KOMMENTAR));
      a.setKontonummer(address.get(PARAM_KONTONUMMER));
      a.setName(address.get(PARAM_NAME));
      a.store();
      return supportNull ? null : a.getID();
    }
    catch (ApplicationException ae)
    {
      if (supportNull)
        return ae.getMessage();
      throw new RemoteException(ae.getMessage(),ae);
    }
    catch (Exception e)
    {
      Logger.error("unable to create address",e);
      throw new RemoteException(i18n.tr("Fehler beim Erstellen der Adresse: {0}",e.getMessage()),e);
    }
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.AddressService#createParams()
   */
  public Map<String,String> createParams() throws RemoteException
  {
    Map<String,String> m = new HashMap<String,String>();
    m.put(PARAM_BIC,         (String) null);
    m.put(PARAM_BLZ,         (String) null);
    m.put(PARAM_IBAN,        (String) null);
    m.put(PARAM_KATEGORIE,   (String) null);
    m.put(PARAM_KOMMENTAR,   (String) null);
    m.put(PARAM_KONTONUMMER, (String) null);
    m.put(PARAM_NAME,        (String) null);
    return m;
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.AddressService#delete(java.lang.String)
   */
  public String delete(String id) throws RemoteException
  {
    boolean supportNull = de.willuhn.jameica.hbci.xmlrpc.Settings.isNullSupported();

    try
    {
      if (id == null || id.length() == 0)
        throw new ApplicationException(i18n.tr("Keine ID des zu löschenden Datensatzes angegeben"));

      DBService service = (DBService) Application.getServiceFactory().lookup(HBCI.class,"database");
      HibiscusAddress a = (HibiscusAddress) service.createObject(HibiscusAddress.class,id);
      a.delete();
      Logger.info("deleted address [ID: " + id + "]");
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
   * @see de.willuhn.jameica.hbci.xmlrpc.rmi.AddressService#find(java.lang.String)
   */
  public List<Map<String, String>> find(String query) throws RemoteException
  {
    try
    {
      AddressbookService service = (AddressbookService) Application.getServiceFactory().lookup(HBCI.class,"addressbook");
      List<Address> list = service.findAddresses(query);
      List<Map<String,String>> result = new ArrayList<Map<String,String>>();
      
      for (Address a:list)
      {
        Map<String,String> m = new HashMap<String,String>();
        if (a instanceof HibiscusAddress)
          m.put("id",            ((HibiscusAddress)a).getID());
        
        m.put(PARAM_BIC,         a.getBic());
        m.put(PARAM_BLZ,         a.getBlz());
        m.put(PARAM_IBAN,        a.getIban());
        m.put(PARAM_KATEGORIE,   a.getKategorie());
        m.put(PARAM_KOMMENTAR,   a.getKommentar());
        m.put(PARAM_KONTONUMMER, a.getKontonummer());
        m.put(PARAM_NAME,        a.getName());
        
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
  
  
}


/*********************************************************************
 * $Log: AddressServiceImpl.java,v $
 * Revision 1.1  2011/02/07 12:22:13  willuhn
 * @N XML-RPC Address-Service
 *
 **********************************************************************/