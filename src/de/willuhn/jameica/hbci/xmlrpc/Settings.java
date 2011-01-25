/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/Settings.java,v $
 * $Revision: 1.2 $
 * $Date: 2011/01/25 13:49:26 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc;

import de.willuhn.jameica.system.Application;

/**
 * Einstellungen.
 */
public class Settings
{
  private final static de.willuhn.jameica.system.Settings SETTINGS = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings();
  
  /**
   * Liefert true, wenn die XML-RPC-Implementierung NULL (<ex:nil/>) unterstuetzen soll.
   * Ist das der Fall, liefern u.a. die create-Funktionen zum Anlegen neuer Auftraege
   * im Erfolgsfall NULL zurueck und im Fehlerfall den Fehlertext als String. Falls die
   * Funktion false liefert, wird im Erfolgsfall die ID des erstellten Auftrages zurueckgegeben
   * und im Fehlerfall eine Exception geworfen.
   * @return true, wenn XML-RPC NULL unterstuetzt (default: true).
   */
  public final static boolean isNullSupported()
  {
    return SETTINGS.getBoolean("xmlrpc.supports.null",true);
  }
  
  /**
   * Liefert die maximale Anzahl zurueckzuliefernder Datensaetze.
   * Verhindert OutOfMemoryException bei zuvielen Ergebnissen.
   * @return maximale Anzahl zurueckzuliefernder Datensaetze. Per Default 10.000.
   */
  public final static int getResultLimit()
  {
    return SETTINGS.getInt("xmlrpc.result.limit",10000);
  }

}



/**********************************************************************
 * $Log: Settings.java,v $
 * Revision 1.2  2011/01/25 13:49:26  willuhn
 * @N Limit konfigurierbar und auch in Auftragslisten beruecksichtigen
 *
 * Revision 1.1  2011-01-25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 **********************************************************************/