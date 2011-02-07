/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/AddressService.java,v $
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

package de.willuhn.jameica.hbci.xmlrpc.rmi;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.Service;

/**
 * XML-RPC-Service zum Zugriff auf das Adressbuch.
 */
public interface AddressService extends Service
{
  public final static String PARAM_NAME        = "name";
  public final static String PARAM_KONTONUMMER = "kontonummer";
  public final static String PARAM_BLZ         = "blz";
  public final static String PARAM_KOMMENTAR   = "kommentar";
  public final static String PARAM_BIC         = "bic";
  public final static String PARAM_IBAN        = "iban";
  public final static String PARAM_KATEGORIE   = "kategorie";
  
  /**
   * Liefert eine Liste von Adressen.
   * @param query Suchbegriff.
   * @return Liste der gefundenen Adressen.
   * @throws RemoteException
   */
  public List<Map<String,String>> find(String query) throws RemoteException;

  /**
   * Erzeugt eine Map mit den Attributen einer Adresse.
   * @return Vorkonfigurierte Map mit den Attributen einer Adresse.
   * @throws RemoteException
   */
  public Map<String,String> createParams() throws RemoteException;

  /**
   * Erzeugt eine neue Adresse.
   * @param address die zu erstellende Adresse.
   * @return NULL wenn der Vorgang erfolgreich war, sonst den Fehlertext.
   * 
   * Falls der Parameter "xmlrpc.supports.null" in de.willuhn.jameica.hbci.xmlrpc.Plugin.properties
   * jedoch auf "false" gestellt ist, dann: ID des Datensatzes bei
   * Erfolg. Andernfalls wird eine Exception geworfen.
   * 
   * Also:
   * xmlrpc.supports.null=true  -> OK=return NULL, FEHLER=return Fehlertext
   * xmlrpc.supports.null=false -> OK=return ID,   FEHLER=throws Exception
   * @throws RemoteException
   */
  public String create(Map<String,String> address) throws RemoteException;
  
  /**
   * Loescht die Adresse mit der angegebenen ID.
   * @param id die ID des Auftrages.
   * @return siehe {@link AddressService#create(Map)}
   * @throws RemoteException
   */
  public String delete(String id) throws RemoteException;

}


/*********************************************************************
 * $Log: AddressService.java,v $
 * Revision 1.1  2011/02/07 12:22:13  willuhn
 * @N XML-RPC Address-Service
 *
 **********************************************************************/