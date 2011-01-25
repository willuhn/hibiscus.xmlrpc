/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/rmi/UmsatzService.java,v $
 * $Revision: 1.7 $
 * $Date: 2011/01/25 13:43:54 $
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
  public static final String KEY_GEGENKONTO_NAME   = "gegenkonto_name";
  public static final String KEY_GEGENKONTO_NUMMER = "gegenkonto_nr";
  public static final String KEY_GEGENKONTO_BLZ    = "gegenkonto_blz";
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
   * gegenkonto_name
   * gegenkonto_nr
   * gegenkonto_blz
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
   * umsatz_typ (ID der Umsatz-Kategorie)
   * zweck
   *
   * Die Funktion liefer eine Liste mit den Umsaetzen zurueck
   * jeder Umsatz liegt als Map vor und enthält die folgenden
   * Elemente:
   *
   * id
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
   * @return Liste der Umsaetze.
   * @throws RemoteException
   */
  public List<Map<String,Object>> list(HashMap<String,Object> options) throws RemoteException;
}


/*********************************************************************
 * $Log: UmsatzService.java,v $
 * Revision 1.7  2011/01/25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.6  2010/03/31 12:30:40  willuhn
 * *** empty log message ***
 *
 * Revision 1.5  2009/02/13 14:06:51  willuhn
 * @D "id" war in javadoc nicht mit angegeben, wurde jedoch uebertragen
 *
 * Revision 1.4  2008/12/18 21:17:33  willuhn
 * @N Drittes Patch von Julian (Parameter "art")
 *
 * Revision 1.3  2008/12/17 14:40:56  willuhn
 * @N Aktualisiertes Patch von Julian
 *
 * Revision 1.2  2008/12/12 01:26:42  willuhn
 * @N Patch von Julian
 *
 * Revision 1.1  2007/09/30 14:11:20  willuhn
 * @N hibiscus.xmlrpc.umsatz.list
 *
 **********************************************************************/