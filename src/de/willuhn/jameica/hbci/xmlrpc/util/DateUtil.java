/**********************************************************************
 * $Source: /cvsroot/hibiscus/hibiscus.xmlrpc/src/de/willuhn/jameica/hbci/xmlrpc/util/DateUtil.java,v $
 * $Revision: 1.1 $
 * $Date: 2010/03/31 12:24:51 $
 * $Author: willuhn $
 *
 * Copyright (c) by willuhn - software & services
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Hilfsklasse fuer Datums-Konvertierungen.
 */
public class DateUtil
{
  /**
   * Wandelt ein Datum vom Format YYYY-MM-DD in einen
   * java.util.Date-Objekt um
   * @param date im Format YYYY-MM-DD
   * @return das Datum.
   * @throws ParseException
   */
  public static Date toDate(String date) throws ParseException
  {
    // Dateformat ist nicht multithreading-tauglich, daher kein statisches Member
    return new SimpleDateFormat("yyyy-MM-dd").parse(date);
  }
  
  /**
   * Wandelt ein Datum in das Format 'YYYY-MM-DD' um
   * @param date das Datum.
   * @return der formatierte String.
   * @throws ParseException
   */
  public static String toString(Date date) throws ParseException
  {
    // Dateformat ist nicht multithreading-tauglich, daher kein statisches Member
    return new SimpleDateFormat("yyyy-MM-dd").format(date);
  }


}



/**********************************************************************
 * $Log: DateUtil.java,v $
 * Revision 1.1  2010/03/31 12:24:51  willuhn
 * @N neue XML-RPC-Funktion "find" zum erweiterten Suchen in Auftraegen
 * @C Code-Cleanup
 *
 **********************************************************************/