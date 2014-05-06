/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/UmsatzService.java,v $
 * $Revision: 1.9 $
 * $Date: 2011/02/10 11:55:19 $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.willuhn.datasource.Service;

/**
 * XML-RPC-Service zum Zugriff auf Umsaetze.
 */
public interface UmsatzService extends Service
{

  public static final String KEY_ID                = "id";
  public static final String KEY_KONTO_ID          = "konto_id";
  public static final String KEY_GEGENKONTO_NAME   = "empfaenger_name";
  public static final String KEY_GEGENKONTO_NUMMER = "empfaenger_konto";
  public static final String KEY_GEGENKONTO_BLZ    = "empfaenger_blz";
  public static final String KEY_ART               = "art";
  public static final String KEY_BETRAG            = "betrag";
  public static final String KEY_VALUTA            = "valuta";
  public static final String KEY_DATUM             = "datum";
  public static final String KEY_ZWECK             = "zweck";
  public static final String KEY_SALDO             = "saldo";
  public static final String KEY_PRIMANOTA         = "primanota";
  public static final String KEY_CUSTOMER_REF      = "customer_ref";
  public static final String KEY_UMSATZ_TYP        = "umsatz_typ";
  public static final String KEY_KOMMENTAR         = "kommentar";
  public static final String KEY_GVCODE            = "gvcode";


  /**
   * Liefert eine Liste der Umsaetze.
   * Jede Zeile entspricht einem Umsatz. Die einzelnen Werte sind durch Doppelpunkt getrennt.
   * @param text Suchbegriff.
   * @param von Datum im Format dd.mm.yyyy.
   * @param bis Datum im Format dd.mm.yyyy.
   * @return Liste der Konten.
   * @throws RemoteException
   */
  public String[] list(String text, String von, String bis) throws RemoteException;

  /**
   * Liefert eine Liste der Umsaetze.
   * ueber dem Hash koennen die folgenden Filter gesetzt werden:
   *
   * konto_id
   * art
   * empfaenger_name
   * empfaenger_konto
   * empfaenger_blz
   * id
   * id:min
   * id:max
   * saldo
   * saldo:min
   * saldo:max
   * valuta
   * valuta:min
   * valuta:max
   * datum
   * datum:min
   * datum:max
   * betrag
   * betrag:min
   * betrag:max
   * primanota
   * customer_ref
   * umsatz_typ (Name oder ID der Umsatz-Kategorie)
   * zweck
   *
   * Die Funktion liefer eine Liste mit den Umsaetzen zurueck
   * jeder Umsatz liegt als Map vor und enthält die folgenden
   * Elemente:
   *
   * id
   * konto_id
   * empfaenger_name
   * empfaenger_konto
   * empfaenger_blz
   * saldo
   * valuta
   * datum
   * betrag
   * primanota
   * customer_ref
   * umsatz_typ
   * zweck
   * kommentar
   * 
   * @param options Map mit den Filter-Parametern.
   * @return Liste der Umsaetze.
   * @throws RemoteException
   */
  public List<Map<String,Object>> list(HashMap<String,Object> options) throws RemoteException;
}
