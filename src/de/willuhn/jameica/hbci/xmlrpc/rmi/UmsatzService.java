/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/UmsatzService.java,v $
 * $Revision: 1.2 $
 * $Date: 2008/12/12 01:26:42 $
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
 * XML-RPC-Service zum Zugriff auf Umsaetze.
 */
public interface UmsatzService extends Service
{
  
  public static final String KEY_ID                = "id";
  public static final String KEY_KONTO_ID          = "konto_id";
  public static final String KEY_GEGENKONTO_NAME   = "gegenkonto_name";
  public static final String KEY_GEGENKONTO_NUMMER = "gegenkonto_nr";
  public static final String KEY_GEGENKONTO_BLZ    = "gegenkonto_blz";
  public static final String KEY_BETRAG            = "betrag";
  public static final String KEY_VALUTA            = "valuta";
  public static final String KEY_DATUM             = "datum";
  public static final String KEY_ZWECK             = "zweck";
  public static final String KEY_SALDO             = "saldo";
  public static final String KEY_PRIMANOTA         = "primanota";
  public static final String KEY_CUSTOMER_REF      = "customer_ref";
  public static final String KEY_UMSATZ_TYP        = "umsatz_typ";
  public static final String KEY_KOMMENTAR         = "kommentar";
  
  
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
   * gegenkonto_name
   * gegenkonto_nr
   * gegenkonto_blz
   * saldo
   * saldo:from
   * saldo:to
   * valuta
   * valuta:from
   * valuta:to
   * datum
   * datum:from
   * datum:to
   * betrag
   * betrag:from
   * betrag:to
   * primanota
   * customer_ref
   * umsatz_typ
   * 
   * Die Funktion liefer eine Liste mit den Umsaetzen zurueck
   * jeder Umsatz liegt als Map vor und enthält die folgenden
   * Elemente:
   * 
   * konto_id
   * gegenkonto_name
   * gegenkonto_nr
   * gegenkonto_blz
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
   * @param text optionen fuer den Filter
   * @return Liste der Umsaetze.
   * @throws RemoteException
   */
  public List<Map<String,Object>> list(Map<String,Object> options) throws RemoteException;
}


/*********************************************************************
 * $Log: UmsatzService.java,v $
 * Revision 1.2  2008/12/12 01:26:42  willuhn
 * @N Patch von Julian
 *
 * Revision 1.1  2007/09/30 14:11:20  willuhn
 * @N hibiscus.xmlrpc.umsatz.list
 *
 **********************************************************************/