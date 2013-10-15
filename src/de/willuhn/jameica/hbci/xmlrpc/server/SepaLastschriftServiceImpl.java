/**********************************************************************
 *
 * Copyright (c) by Olaf Willuhn
 * All rights reserved
 *
 **********************************************************************/

package de.willuhn.jameica.hbci.xmlrpc.server;

import java.rmi.RemoteException;
import java.util.Map;

import de.willuhn.jameica.hbci.HBCI;
import de.willuhn.jameica.hbci.rmi.SepaLastSequenceType;
import de.willuhn.jameica.hbci.rmi.SepaLastType;
import de.willuhn.jameica.hbci.rmi.SepaLastschrift;
import de.willuhn.jameica.hbci.xmlrpc.rmi.SepaLastschriftService;
import de.willuhn.jameica.hbci.xmlrpc.util.StringUtil;
import de.willuhn.util.ApplicationException;

/**
 * Implementierung des SEPA-Lastschrift-Service.
 */
public class SepaLastschriftServiceImpl extends AbstractBaseUeberweisungServiceImpl<SepaLastschrift> implements SepaLastschriftService
{
  final static String PARAM_ENDTOEND_ID   = "endtoendid";
  final static String PARAM_MANDATE_ID    = "mandateid";
  final static String PARAM_CREDITOR_ID   = "creditorid";
  final static String PARAM_SIGNATUREDATE = "sigdate";
  final static String PARAM_SEQUENCETYPE  = "sequencetype";
  final static String PARAM_SEPATYPE      = "sepatype";
  final static String PARAM_TARGETDATE    = "targetdate";

  /**
   * ct.
   * @throws RemoteException
   */
  public SepaLastschriftServiceImpl() throws RemoteException
  {
    super();
  }


  /**
   * @see de.willuhn.datasource.Service#getName()
   */
  public String getName() throws RemoteException
  {
    return "[xml-rpc] sepa-lastschrift";
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#createParams()
   */
  @Override
  public Map createParams() throws RemoteException
  {
    Map<String,Object> m = super.createParams();
    m.put(PARAM_ENDTOEND_ID,   (String) null);
    m.put(PARAM_MANDATE_ID,    (String) null);
    m.put(PARAM_CREDITOR_ID,   (String) null);
    m.put(PARAM_SIGNATUREDATE, (String) null);
    m.put(PARAM_SEQUENCETYPE,  (String) null);
    m.put(PARAM_SEPATYPE,      (String) null);
    m.put(PARAM_TARGETDATE,    (String) null);
    return m;
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#beforeStore(java.util.Map, de.willuhn.jameica.hbci.rmi.BaseUeberweisung)
   */
  protected void beforeStore(Map params, SepaLastschrift auftrag) throws Exception
  {
    auftrag.setEndtoEndId((String)params.get(PARAM_ENDTOEND_ID));
    auftrag.setMandateId((String)params.get(PARAM_MANDATE_ID));
    auftrag.setCreditorId((String)params.get(PARAM_CREDITOR_ID));
    auftrag.setSignatureDate(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(params.get(PARAM_SIGNATUREDATE)));
    
    SepaLastSequenceType seq = null;
    String s = (String)params.get(PARAM_SEQUENCETYPE);
    if (s != null && s.length() > 0)
    {
      try
      {
        seq = SepaLastSequenceType.valueOf(s);
      }
      catch (Exception e)
      {
        throw new ApplicationException(i18n.tr("Ungültiger Sequenz-Typ: {0}",s));
      }
    }
    auftrag.setSequenceType(seq);
    
    SepaLastType type = null;
    s = (String)params.get(PARAM_SEPATYPE);
    if (s != null && s.length() > 0)
    {
      try
      {
        type = SepaLastType.valueOf(s);
      }
      catch (Exception e)
      {
        throw new ApplicationException(i18n.tr("Ungültiger Lastschrift-Art: {0}",s));
      }
    }
    
    auftrag.setType(type);
    auftrag.setTargetDate(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(params.get(PARAM_TARGETDATE)));
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#afterLoad(java.util.Map, de.willuhn.jameica.hbci.rmi.BaseUeberweisung)
   */
  protected void afterLoad(Map params, SepaLastschrift auftrag) throws Exception
  {
    params.put(PARAM_ENDTOEND_ID,   StringUtil.notNull(auftrag.getEndtoEndId()));
    params.put(PARAM_MANDATE_ID,    auftrag.getMandateId());
    params.put(PARAM_CREDITOR_ID,   auftrag.getCreditorId());
    params.put(PARAM_SIGNATUREDATE, HBCI.DATEFORMAT.format(auftrag.getSignatureDate()));
    params.put(PARAM_SEQUENCETYPE,  auftrag.getSequenceType().name());
    params.put(PARAM_SEPATYPE,      auftrag.getType() != null ? auftrag.getType().name() : StringUtil.notNull(null));
    params.put(PARAM_TARGETDATE,    auftrag.getTargetDate() != null ? HBCI.DATEFORMAT.format(auftrag.getTargetDate()) : StringUtil.notNull(null));
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#getType()
   */
  Class getType()
  {
    return SepaLastschrift.class;
  }
}
