/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/util/StringUtil.java,v $
 * $Revision: 1.4 $
 * $Date: 2011/06/07 10:07:52 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.util;

import java.util.ArrayList;
import java.util.List;

import de.willuhn.jameica.hbci.xmlrpc.Plugin;
import de.willuhn.jameica.system.Application;

/**
 * Hilfsklasse fuer String-Operationen.
 */
public class StringUtil
{
  /**
   * Quotet den Text.
   * @param s zu quotender Text.
   * @return der gequotete Text.
   */
  public static String quote(String s)
  {
    if (s == null)
      return s;

    String quote = Application.getPluginLoader().getPlugin(Plugin.class).getResources().getSettings().getString("quoting.char",null);

    if (quote == null || quote.length() == 0)
      return s;
    
    // Erstmal enthaltene Quoting-Zeichen escapen
    s = s.replaceAll(quote,"\\" + quote);
    return quote + s + quote;
  }
  
  /**
   * Wandelt ein Objekt in einen String um.
   * @param o das Objekt.
   * @return Die String-Repraesentation oder "" - niemals aber null.
   */
  public static String notNull(Object o)
  {
    if (o == null)
      return "";
    String s = o.toString();
    return s == null ? "" : s;
  }
  
  /**
   * Wandelt ein Objekt in einen Strim um.
   * @param o das Objekt.
   * @return die String-Repraesentation oder null - niemals "".
   */
  public static String trimToNull(Object o)
  {
    if (o == null)
      return null;
    
    String s = o.toString();
    return s != null ? s.trim() : null;
  }
  
  /**
   * Versucht das Objekt als Verwendungszweck zu parsen.
   * Die Funktion erkennt selbst, ob "object" ein Array oder vom Typ "List" ist.
   * @param object der potentielle Verwendungszweck. Darf NULL sein.
   * @return String-Array mit den Verwendungszweck-Zeilen.
   */
  public static String[] parseUsage(Object object)
  {
    if (object == null)
      return null;

    if (object instanceof Object[])
    {
      Object[] list = (Object[]) object;
      ArrayList<String> lines = new ArrayList<String>();
      for (Object o:list)
      {
        if (o != null)
          lines.add(o.toString());
      }
      return lines.toArray(new String[lines.size()]);
    }
    
    if (object instanceof List)
    {
      List list = (List) object;
      ArrayList<String> lines = new ArrayList<String>();
      for (Object o:list)
      {
        if (o != null)
          lines.add(o.toString());
      }
      return lines.toArray(new String[lines.size()]);
    }
    return new String[]{object.toString()};
  }
}



/**********************************************************************
 * $Log: StringUtil.java,v $
 * Revision 1.4  2011/06/07 10:07:52  willuhn
 * @C Verwendungszweck-Handling vereinheitlicht/vereinfacht - geht jetzt fast ueberall ueber VerwendungszweckUtil
 *
 * Revision 1.3  2011-01-25 14:05:12  willuhn
 * @B Kompatibilitaet zu Hibiscus 1.12
 *
 * Revision 1.2  2011-01-25 13:43:54  willuhn
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