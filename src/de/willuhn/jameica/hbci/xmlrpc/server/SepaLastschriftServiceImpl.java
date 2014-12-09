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
    m.put(XmlRpcParameter.PARAM_ENDTOEND_ID,   (String) null);
    m.put(XmlRpcParameter.PARAM_PURPOSE_CODE,  (String) null);
    m.put(XmlRpcParameter.PARAM_PMTINF_ID,     (String) null);
    m.put(XmlRpcParameter.PARAM_MANDATE_ID,    (String) null);
    m.put(XmlRpcParameter.PARAM_CREDITOR_ID,   (String) null);
    m.put(XmlRpcParameter.PARAM_SIGNATUREDATE, (String) null);
    m.put(XmlRpcParameter.PARAM_SEQUENCETYPE,  (String) null);
    m.put(XmlRpcParameter.PARAM_SEPATYPE,      (String) null);
    m.put(XmlRpcParameter.PARAM_TARGETDATE,    (String) null);
     return m;
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#beforeStore(java.util.Map, de.willuhn.jameica.hbci.rmi.BaseUeberweisung)
   */
  protected void beforeStore(Map params, SepaLastschrift auftrag) throws Exception
  {
    auftrag.setEndtoEndId((String)params.get(XmlRpcParameter.PARAM_ENDTOEND_ID));
    auftrag.setPurposeCode((String)params.get(XmlRpcParameter.PARAM_PURPOSE_CODE));
    auftrag.setPmtInfId((String)params.get(XmlRpcParameter.PARAM_PMTINF_ID));
    auftrag.setMandateId((String)params.get(XmlRpcParameter.PARAM_MANDATE_ID));
    auftrag.setCreditorId((String)params.get(XmlRpcParameter.PARAM_CREDITOR_ID));
    auftrag.setSignatureDate(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(params.get(XmlRpcParameter.PARAM_SIGNATUREDATE)));
    
    SepaLastSequenceType seq = null;
    String s = (String)params.get(XmlRpcParameter.PARAM_SEQUENCETYPE);
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
    s = (String)params.get(XmlRpcParameter.PARAM_SEPATYPE);
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
    auftrag.setTargetDate(de.willuhn.jameica.hbci.xmlrpc.util.DateUtil.parse(params.get(XmlRpcParameter.PARAM_TARGETDATE)));
  }
  
  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#afterLoad(java.util.Map, de.willuhn.jameica.hbci.rmi.BaseUeberweisung)
   */
  protected void afterLoad(Map params, SepaLastschrift auftrag) throws Exception
  {
    params.put(XmlRpcParameter.PARAM_ENDTOEND_ID,   StringUtil.notNull(auftrag.getEndtoEndId()));
    params.put(XmlRpcParameter.PARAM_PURPOSE_CODE,  StringUtil.notNull(auftrag.getPurposeCode()));
    params.put(XmlRpcParameter.PARAM_PMTINF_ID,     StringUtil.notNull(auftrag.getPmtInfId()));
    params.put(XmlRpcParameter.PARAM_MANDATE_ID,    auftrag.getMandateId());
    params.put(XmlRpcParameter.PARAM_CREDITOR_ID,   auftrag.getCreditorId());
    params.put(XmlRpcParameter.PARAM_SIGNATUREDATE, HBCI.DATEFORMAT.format(auftrag.getSignatureDate()));
    params.put(XmlRpcParameter.PARAM_SEQUENCETYPE,  auftrag.getSequenceType().name());
    params.put(XmlRpcParameter.PARAM_SEPATYPE,      auftrag.getType() != null ? auftrag.getType().name() : StringUtil.notNull(null));
    params.put(XmlRpcParameter.PARAM_TARGETDATE,    auftrag.getTargetDate() != null ? HBCI.DATEFORMAT.format(auftrag.getTargetDate()) : StringUtil.notNull(null));
  }

  /**
   * @see de.willuhn.jameica.hbci.xmlrpc.server.AbstractBaseUeberweisungServiceImpl#getType()
   */
  Class getType()
  {
    return SepaLastschrift.class;
  }
}
