/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/util/DateUtil.java,v $
 * $Revision: 1.2 $
 * $Date: 2011/01/25 13:43:54 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.willuhn.jameica.hbci.xmlrpc.Plugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Hilfsklasse fuer Datums-Konvertierungen.
 */
public class DateUtil
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * Versucht, das Objekt als Datum zu parsen.
   * Folgende Objekte werden unterstuetzt:
   * - Date-Objekt.
   * - String im Format YYYY-MM-DD
   * - String im Format DD.MM.YYYY
   * Wandelt ein Datum vom Format YYYY-MM-DD oder DD.MM.YYYY in einen
   * java.util.Date-Objekt um
   * @param object das Datum. Darf NULL sein.
   * @return das geparste Datum.
   * @throws ApplicationException
   */
  public static Date parse(Object object) throws ApplicationException
  {
    if (object == null)
      return null;
    
    if (object instanceof Date)
      return (Date) object;

    String s = object.toString();

    try
    {
      // Dateformat ist nicht multithreading-tauglich, daher kein statisches Member
      if (s.indexOf("-") != -1)
        return new SimpleDateFormat("yyyy-MM-dd").parse(s);
      return new SimpleDateFormat("dd.MM.yyyy").parse(s);
    }
    catch (Exception e)
    {
      throw new ApplicationException(i18n.tr("Angegebenes Datum ungültig: {0}",s));
    }
  }
  
  /**
   * Wandelt ein Datum in das Format 'YYYY-MM-DD' um
   * @param date das Datum.
   * @return der formatierte String.
   */
  public static String format(Date date)
  {
    if (date == null)
      return "";
    
    // Dateformat ist nicht multithreading-tauglich, daher kein statisches Member
    return new SimpleDateFormat("yyyy-MM-dd").format(date);
  }


}



/**********************************************************************
 * $Log: DateUtil.java,v $
 * Revision 1.2  2011/01/25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 * Revision 1.1  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 **********************************************************************/