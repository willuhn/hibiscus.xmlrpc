/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/util/DecimalUtil.java,v $
 * $Revision: 1.1 $
 * $Date: 2011/01/25 13:43:54 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.util;

import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.xmlrpc.Plugin;
import de.willuhn.jameica.system.Application;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * Hilfsklasse zum Parsen und Formatieren von Betraegen.
 */
public class DecimalUtil
{
  private final static I18N i18n = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getI18N();

  /**
   * Versucht, das Objekt als Dezimal-Zahl zu parsen.
   * @param o das Objekt. Darf NICHT NULL sein.
   * @return die Zahl.
   * @throws ApplicationException
   */
  public static double parse(Object o) throws ApplicationException
  {
    if (o == null)
      throw new ApplicationException(i18n.tr("Kein Betrag angegeben"));
    
    try
    {
      if (o instanceof Number)
        return ((Number)o).doubleValue();
      
      return HBCI.DECIMALFORMAT.parse(o.toString()).doubleValue();
    }
    catch (Exception e)
    {
      throw new ApplicationException(i18n.tr("Ungültiger Betrag: {0}",o.toString()));
    }
  }
}



/**********************************************************************
 * $Log: DecimalUtil.java,v $
 * Revision 1.1  2011/01/25 13:43:54  willuhn
 * @N Loeschen von Auftraegen
 * @N Verhalten der Rueckgabewerte von create/delete konfigurierbar (kann jetzt bei Bedarf die ID des erstellten Datensatzes liefern und Exceptions werfen)
 * @N Filter fuer Zweck, Kommentar, Gegenkonto in Umsatzsuche fehlten
 * @B Parameter-Name in Umsatzsuche wurde nicht auf ungueltige Zeichen geprueft
 * @C Code-Cleanup
 * @N Limitierung der zurueckgemeldeten Umsaetze auf 10.000
 *
 **********************************************************************/