/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
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
  
  
  /**
   * Berechnet die IBAN und BIC zur angegebenen BLZ/Kontonummer.
   * @param blz BLZ.
   * @param kontonummer Kontonummer.
   * @return IBAN (auf Index 0) und BIC (auf Index 1).
   * @throws RemoteException
   */
  public String[] calculateIBAN(String blz, String kontonummer) throws RemoteException;
  
  
}
