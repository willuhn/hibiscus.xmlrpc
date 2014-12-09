package de.willuhn.jameica.hbci.xmlrpc.server;

/**
 * Enthaelt eine Liste der moeglichen Parameter bei XML-RPC-Aufrufen.
 */
public class XmlRpcParameter
{

	
	// Auftragsparameter
  
	/**
	 * EndToEnd-ID
	 */
	public final static String PARAM_ENDTOEND_ID = "endtoendid";

	 /**
   * Purpose-Code
   */
  public final static String PARAM_PURPOSE_CODE = "purposecode";

  /**
   * Payment-Information-ID
   */
  public final static String PARAM_PMTINF_ID = "pmtinfid";

	/**
	 * Mandatsreferenz.
	 */
	public final static String PARAM_MANDATE_ID = "mandateid";
	
	/**
	 * Glaeubiger-ID.
	 */
	public final static String PARAM_CREDITOR_ID = "creditorid";
	
	/**
	 * Unterschriftsdatum des Mandats.
	 */
	public final static String PARAM_SIGNATUREDATE = "sigdate";
	
	/**
	 * Sequenztyp (FRST, RCUR, etc).
	 */
	public final static String PARAM_SEQUENCETYPE = "sequencetype";
	
	/**
	 * Art der SEPA-Lastschrift (CORE,COR1,B2B).
	 */
	public final static String PARAM_SEPATYPE = "sepatype";
	
	/**
	 * Faelligkeitsdatum der Lastschrift.
	 */
	public final static String PARAM_TARGETDATE = "targetdate";
	
  /**
   * Batchbooking-Parameter.
   */
  public final static String PARAM_BATCHBOOK = "batchbook";

	/**
	 * Bezeichnung des Sammelauftrages.
	 */
	public final static String PARAM_NAME = "name";
	
	/**
	 * ID des zu verwendenden Kontos.
	 */
	public final static String PARAM_KONTO = "konto";
	
	/**
	 * Hibiscus-interner Termin.
	 */
	public final static String PARAM_TERMIN = "termin";
	
	/**
	 * ID des Auftrages.
	 */
	public final static String PARAM_ID = "id";
	
	/**
	 * Ausfuehrungsdatum.
	 */
	public final static String PARAM_AUSGEFUEHRT = "ausgefuehrt";

	/**
	 * Die enthaltenen Buchungen.
	 */
	public final static String PARAM_BUCHUNGEN = "buchungen";

	/**
	 * Betrag.
	 */
	public final static String PARAM_BUCHUNGEN_BETRAG = "betrag";
	
	/**
	 * BLZ/BIC.
	 */
	public final static String PARAM_BUCHUNGEN_BLZ = "blz";
	
	/**
	 * Kontonummer/IBAN.
	 */
	public final static String PARAM_BUCHUNGEN_KONTONUMMER = "kontonummer";
	
	/**
	 * Name des Gegenkontoinhabers.
	 */
	public final static String PARAM_BUCHUNGEN_NAME = "name";
	
	/**
	 * Verwendungszweck.
	 */
	public final static String PARAM_BUCHUNGEN_VERWENDUNGSZWECK = "verwendungszweck";
	
	// Suchparameter
	
	/**
	 * ID des Kontos, dem der Umsatz zugeordnet ist
	 */
	public final static String PARAM_FIND_KONTO = "konto_id";
	
	/**
	 *  Buchungsart
	 */
	public final static String PARAM_FIND_BUCHUNGSART = "art";
	
	/**
	 *  Inhaber-Name des Gegenkontos
	 */
	public final static String PARAM_FIND_EMPFAENGER_NAME = "empfaenger_name";
	
	/**
	 *  Kontonummer des Gegenkontos
	 */
	public final static String PARAM_FIND_EMPFAENGER_KONTO = "empfaenger_konto";
	
	/**
	 *  Bankleitzahl des Gegenkontos
	 */
	public final static String PARAM_FIND_EMPFAENGER_BLZ_BIC = "empfaenger_blz";
	
	/**
	 * ID des Umsatzes oder Auftrages
	 */
	public final static String PARAM_FIND_ID = "id";
	
	/**
	 * niedrigste zul?ssige ID des Umsatzes oder Auftrages
	 */
	public final static String PARAM_FIND_ID_MIN = "id:min";
	
	/**
	 *  h?chste zul?ssige ID des Umsatzes oder Auftrages
	 */
	public final static String PARAM_FIND_ID_MAX = "id:max";
	
	/**
	 *  Saldo des Kontos bei diesem Umsatz
	 */
	public final static String PARAM_FIND_SALDO = "saldo";
	
	/**
	 *  niedrigster zul?ssiger Saldo des Kontos bei diesem Umsatz
	 */
	public final static String PARAM_FIND_SALDO_MIN = "saldo:min";
	
	/**
	 *  h?chster zul?ssiger Saldo des Kontos bei diesem Umsatz
	 */
	public final static String PARAM_FIND_SALDO_MAX = "saldo:max";
	
	/**
	 *  Valuta-Datum im Format "dd.mm.yyyy" oder "yyyy-mm-dd"
	 */
	public final static String PARAM_FIND_VALUTA = "valuta";
	
	/**
	 *  niedrigstes Valuta-Datum im Format "dd.mm.yyyy" oder "yyyy-mm-dd"
	 */
	public final static String PARAM_FIND_VALUTA_MIN = "valuta:min";
	
	/**
	 *  hoechstes Valuta-Datum im Format "dd.mm.yyyy" oder "yyyy-mm-dd"
	 */
	public final static String PARAM_FIND_VALUTA_MAX = "valuta:max";
	
	/**
	 *  Buchungsdatum im Format "dd.mm.yyyy" oder "yyyy-mm-dd"
	 */
	public final static String PARAM_FIND_DATUM = "datum";
	
	/**
	 * niedrigstes Buchungsdatum im Format "dd.mm.yyyy" oder "yyyy-mm-dd"
	 */
	public final static String PARAM_FIND_DATUM_MIN = "datum:min";
	
	/**
	 * hoechstes Buchungsdatum im Format "dd.mm.yyyy" oder "yyyy-mm-dd"
	 */
	public final static String PARAM_FIND_DATUM_MAX = "datum:max";
	
	/**
	 *  Betrag des Umsatzes
	 */
	public final static String PARAM_FIND_BETRAG = "betrag";
	
	/**
	 *  niedrigster zul?ssiger Betrag des Umsatz
	 */
	public final static String PARAM_FIND_BETRAG_MIN= "betrag:min";
	
	/**
	 *  hoechster zulaessiger Betrag des Umsatzes
	 */
	public final static String PARAM_FIND_BETRAG_MAX = "betrag:max";
	
	/**
	 *  Primanota-Kennzeichen
	 */
	public final static String PARAM_FIND_PRIMANOTA = "primanota";
	
	/**
	 *  Kunden-Referenz
	 */
	public final static String PARAM_FIND_KUNDEN_REF = "customer_ref";
	
	/**
	 *  Name oder ID der Umsatz-Kategorie
	 */
	public final static String PARAM_FIND_UMSATZ_TYP = "umsatz_typ";
	
	/**
	 *  Verwendungszweck
	 */
	public final static String PARAM_FIND_VERWENDUNGSZWECK = "zweck";
	
	/**
	 *  Enthaltener Text in verschiedenen Feldern
	 */
	public final static String PARAM_FIND_TEXT = "text";
	
}
