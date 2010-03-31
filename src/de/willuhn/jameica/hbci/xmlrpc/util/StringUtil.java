/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/util/StringUtil.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/03/31 12:24:51 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.util;

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
  


}



/**********************************************************************
 * $Log: StringUtil.java,v $
 * Revision 1.1  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 **********************************************************************/