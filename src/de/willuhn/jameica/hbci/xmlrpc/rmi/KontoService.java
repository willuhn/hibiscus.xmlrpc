/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/KontoService.java,v $
 * $Revision: 1.5 $
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
 * XML-RPC-Service zum Zugriff auf Konten.
 */
public interface KontoService extends Service
{
  public final static String PARAM_KONTONUMMER     = "kontonummer";
  public final static String PARAM_UNTERKONTO      = "unterkonto";
  public final static String PARAM_BLZ             = "blz";
  public final static String PARAM_NAME            = "name";
  public final static String PARAM_BEZEICHNUNG     = "bezeichnung";
  public final static String PARAM_KUNDENNUMMER    = "kundennummer";
  public final static String PARAM_KOMMENTAR       = "kommentar";
  public final static String PARAM_BIC             = "bic";
  public final static String PARAM_IBAN            = "iban";
  public final static String PARAM_WAEHRUNG        = "waehrung";
  public final static String PARAM_SALDO           = "saldo";
  public final static String PARAM_SALDO_AVAILABLE = "saldo_available";
  public final static String PARAM_SALDO_DATUM     = "saldo_datum";
  
  /**
   * Liefert eine Liste der Konten.
   * Jede Zeile entspricht einem Konto. Die einzelnen Werte sind durch Doppelpunkt getrennt.
   * @return Liste der Konten.
   * @throws RemoteException
   */
  public String[] list() throws RemoteException;
  
  /**
   * Liefert eine Liste der Konten.
   * @return Liste der Konten als Map.
   * @throws RemoteException
   */
  public List<Map<String,String>> find() throws RemoteException;
  
  /**
   * Prueft eine BLZ/Kontonummer auf Plausibilitaet anhand der Pruefsumme.
   * @param blz BLZ.
   * @param kontonummer Kontonummer.
   * @return true oder false.
   * @throws RemoteException
   */
  public boolean checkAccountCRC(String blz, String kontonummer) throws RemoteException;
  
  /**
   * Liefert den Namen des Kredit
   * @param blz BLZ
   * @return Name des Kreditinstitut.s
   * @throws RemoteException
   */
  public String getBankname(String blz) throws RemoteException;
}


/*********************************************************************
 * $Log: KontoService.java,v $
 * Revision 1.5  2011/02/07 12:22:13  willuhn
 * @N XML-RPC Address-Service
 *
 * Revision 1.4  2009/11/19 22:58:05  willuhn
 * @R Konto#create entfernt - ist Unsinn
 *
 * Revision 1.3  2007/11/27 15:17:12  willuhn
 * @N CRC-Check und Bankname-Lookup
 *
 * Revision 1.2  2006/11/07 00:18:11  willuhn
 * *** empty log message ***
 *
 * Revision 1.1  2006/10/31 01:44:09  willuhn
 * @Ninitial checkin
 *
 **********************************************************************/