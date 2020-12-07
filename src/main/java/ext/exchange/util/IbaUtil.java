package ext.exchange.util;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wt.epm.EPMDocument;
import wt.epm.attributes.EPMParameterMap;
import wt.epm.util.EPMParameterMapHelper;
import wt.epm.util.EPMSoftTypeServerUtilities;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.iba.definition.AbstractAttributeDefinition;
import wt.iba.definition.AttributeDefinitionReference;
import wt.iba.definition.BooleanDefinition;
import wt.iba.definition.FloatDefinition;
import wt.iba.definition.IntegerDefinition;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.TimestampDefinition;
import wt.iba.definition.URLDefinition;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.BooleanDefView;
import wt.iba.definition.litedefinition.FloatDefView;
import wt.iba.definition.litedefinition.IntegerDefView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.litedefinition.URLDefView;
import wt.iba.definition.service.StandardIBADefinitionService;
import wt.iba.value.BooleanValue;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.FloatValue;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAHolderReference;
import wt.iba.value.IntegerValue;
import wt.iba.value.StringValue;
import wt.iba.value.TimestampValue;
import wt.iba.value.URLValue;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.FloatValueDefaultView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueHelper;
import wt.pds.StatementSpec;
import wt.query.QueryException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.applicationcontext.implementation.DefaultServiceProvider;
import wt.type.TypeDefinitionReference;
import wt.type.Typed;
import wt.util.WTContext;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.util.WTStandardDateFormat;
import wt.util.range.Range;

import com.ptc.core.command.common.bean.entity.NewEntityCommand;
import com.ptc.core.command.common.bean.entity.PrepareEntityCommand;
import com.ptc.core.foundation.type.server.impl.SoftAttributesHelper;
import com.ptc.core.meta.common.AnalogSet;
import com.ptc.core.meta.common.AttributeIdentifier;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.ConstraintIdentifier;
import com.ptc.core.meta.common.DataSet;
import com.ptc.core.meta.common.DataTypesUtility;
import com.ptc.core.meta.common.DefinitionIdentifier;
import com.ptc.core.meta.common.DiscreteSet;
import com.ptc.core.meta.common.IdentifierFactory;
import com.ptc.core.meta.common.OperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifier;
import com.ptc.core.meta.common.TypeInstanceIdentifier;
import com.ptc.core.meta.common.WildcardSet;
import com.ptc.core.meta.container.common.AttributeContainer;
import com.ptc.core.meta.container.common.AttributeContainerSpec;
import com.ptc.core.meta.container.common.AttributeTypeSummary;
import com.ptc.core.meta.container.common.ConstraintContainer;
import com.ptc.core.meta.container.common.ConstraintData;
import com.ptc.core.meta.container.common.ConstraintException;
import com.ptc.core.meta.container.common.impl.DefaultConstraintValidator;
import com.ptc.core.meta.server.TypeIdentifierUtility;
import com.ptc.core.meta.type.common.TypeInstance;
import com.ptc.core.meta.type.mgmt.common.TypeDefinitionDefaultView;
import com.ptc.core.meta.type.mgmt.server.impl.AttributeMappingRecord;
import com.ptc.core.meta.type.mgmt.server.impl.WTTypeDefinition;
import com.ptc.core.meta.type.runtime.server.PopulatedAttributeContainerFactory;
import com.ptc.core.meta.type.server.TypeInstanceUtility;

public class IbaUtil {
    private static final Logger logger = LoggerFactory.getLogger(IbaUtil.class);

    public static void updateIbaValues(WTObject object, Properties ibaValues) {
        Enumeration enum1 = ibaValues.keys();
        while (enum1.hasMoreElements()) {
            String key = (String) enum1.nextElement();
            String value = ibaValues.getProperty(key);
            try {
                updateIbaValue(key, object, value);
            } catch (Exception e) {
                logger.error("" + e);
            }
        }
    }

    public static void updateEPMIbaValues(EPMDocument object, Properties ibaValues,ReceiveConf receiveConf ) {
        logger.debug(">>>Start to execute updateEPMIbaValues");
        Enumeration enum1 = ibaValues.keys();
        //logger.debug("enum1 = "+enum1);
        Iterator iterator = ibaValues.keySet().iterator();
        //logger.debug("iterator = "+iterator);
        // Map ibaMap = receiveConf.getEpmReceiveConf().getIbaMap();
        try {
            WTTypeDefinition typeDef = getTypeDefinition((Typed) object);
            while (enum1.hasMoreElements()) {
                String key = (String) enum1.nextElement();
                try{
                    String value = ibaValues.getProperty(key);
                    logger.debug(">>>updateEPMIbaValues key = " + key + ";value:" + value);
                    AbstractAttributeDefinition aad = getAttributeDefinition(key);
                    if(aad!=null){
                        updateIbaValue(key, object, value);
                        // SRIbaUtils.setIBAAndUpdateIBAHolder(object, key, value);
                        // logger.debug(aad.toString());
                        AttributeDefinitionReference adr = AttributeDefinitionReference
                                .newAttributeDefinitionReference(aad);
                        // logger.debug(">>>typeDef = "+typeDef);
                        // logger.debug(">>>object.getAuthoringApplication().toString() = "+object.getAuthoringApplication().toString());

                        String attributeMapping = getAttributeMapping(typeDef, aad,
                                object.getAuthoringApplication().toString());
                        // logger.debug(">>>attributeMapping = "+attributeMapping);

                        if (attributeMapping != null) {
                            QuerySpec qs = new QuerySpec(EPMParameterMap.class);
                            qs.appendWhere(new SearchCondition(EPMParameterMap.class,
                                    EPMParameterMap.IBAHOLDER_REFERENCE+".key.id", SearchCondition.EQUAL, PersistenceHelper
                                    .getObjectIdentifier(object).getId()), new int[] { 0 });
                            qs.appendAnd();
                            qs.appendWhere(new SearchCondition(EPMParameterMap.class,
                                    EPMParameterMap.PARAMETER_NAME, SearchCondition.EQUAL, attributeMapping), new int[] { 0 });
                            QueryResult epmParameterMaps = PersistenceHelper.manager.find(qs);
                            while(epmParameterMaps!=null && epmParameterMaps.hasMoreElements()){
                                // PersistenceHelper.manager.delete((EPMParameterMap)epmParameterMaps.nextElement());
                                PersistenceServerHelper.manager.remove((EPMParameterMap)epmParameterMaps.nextElement());
                            }
                            EPMParameterMap epmMap = EPMParameterMap
                                    .newEPMParameterMap(IBAHolderReference
                                                    .newIBAHolderReference((IBAHolder) object),
                                            adr, attributeMapping);
                            // PersistenceHelper.manager.save(epmMap);
                            PersistenceServerHelper.manager.insert(epmMap);
                        }
                    }
                } catch (Exception ex){
                    logger.error(">>>> updateEPMIbaValues key = "+key );
                    ex.printStackTrace();
                }
                // Iterator keys = ibaMap.keySet().iterator();
                // while(keys.hasNext()){
                // String mapKey = (String)keys.next();
                // String mapValue = (String)ibaMap.get(mapKey);
                // if(!key.equals(mapValue)){
                // continue;
                // }
                // if(mapValue.equals(mapKey)||mapValue.equals("$"+mapKey)){
                // continue;
                // }
                // AbstractAttributeDefinition aad =
                // getAttributeDefinition(mapValue);
                // logger.debug(aad.toString());
                // AttributeDefinitionReference adr =
                // AttributeDefinitionReference.newAttributeDefinitionReference(aad);
                // logger.debug(adr.toString());
                // if(adr!=null){
                // logger.debug(">>>Start to create AttributeDefinitionReference:"+mapKey);
                // if(mapKey.startsWith("$")){
                // mapKey = mapKey.substring(1);
                // }
                // EPMParameterMap epmMap =
                // EPMParameterMap.newEPMParameterMap(IBAHolderReference.newIBAHolderReference((IBAHolder)object),
                // adr , mapKey);
                // PersistenceHelper.manager.save(epmMap);
                // }
                // }
            }
        } catch (Exception e) {
            logger.error("" + e);
        }
    }

    private static String getAttributeMapping(WTTypeDefinition typeDef,
                                              AbstractAttributeDefinition add, String context) throws WTException {
        QuerySpec qs = new QuerySpec(AttributeMappingRecord.class);
        qs.appendWhere(
                new SearchCondition(AttributeMappingRecord.class,
                        AttributeMappingRecord.CONTEXT, SearchCondition.EQUAL,
                        context), new int[] {});
        qs.appendAnd();
        qs.appendWhere(
                new SearchCondition(
                        AttributeMappingRecord.class,
                        AttributeMappingRecord.ATTRIBUTE_DEFINITION + ".key.id",
                        SearchCondition.EQUAL, PersistenceHelper
                        .getObjectIdentifier(add).getId()),
                new int[] {});
        qs.appendAnd();
        qs.appendWhere(
                new SearchCondition(AttributeMappingRecord.class,
                        AttributeMappingRecord.TYPE_DEFINITION_REFERENCE + ".key.id",
                        SearchCondition.EQUAL, PersistenceHelper
                        .getObjectIdentifier(typeDef).getId()),
                new int[] {});
//		logger.debug(">>>qs = "+qs);

        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if (qr.size() > 0) {
            return ((AttributeMappingRecord) qr.nextElement()).getValue();
        }

        return null;
    }
    /**
     * 返回对象类型
     * @author Zheng Yongliang
     * @param typed
     * @return
     * @throws WTException
     */
    private static WTTypeDefinition getTypeDefinition (Typed typed) throws WTException{
        if(typed==null){
            return null;
        }
        TypeDefinitionReference tdRef = typed.getTypeDefinitionReference();
        TypeDefinitionDefaultView tdView = EPMSoftTypeServerUtilities.getTypeDefinition(tdRef);
        WTTypeDefinition typeDefinition = (WTTypeDefinition)PersistenceHelper.manager.refresh(tdView.getObjectID());

        return typeDefinition;
    }
    /**
     * Gets the attribute definition.
     *
     * @param definitionClass
     *            the definition class
     * @param attributeName
     *            the attribute name
     * @return the attribute definition
     * @throws WTException
     *             the wT exception
     */
    private static AbstractAttributeDefinition getAttributeDefinition( String attributeName) throws WTException {
        if (attributeName == null)
            return null;
        AbstractAttributeDefinition attributeDefinition = null;
        QuerySpec qs = new QuerySpec(AbstractAttributeDefinition.class);
        qs.appendWhere(new SearchCondition(AbstractAttributeDefinition.class, "name", SearchCondition.EQUAL, attributeName), new int[] {});
//        logger.debug(">>>getAttributeDefinition:"+qs);
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        if (qr.size() > 0) {
            return (AbstractAttributeDefinition) qr.nextElement();
        }
        return null;
    }

    private static AttributeDefDefaultView getAttributeDefDefaultView(
            String ibaName) throws RemoteException, WTException {
        StandardIBADefinitionService defService = new StandardIBADefinitionService();
        AttributeDefDefaultView attributeDefinition = defService
                .getAttributeDefDefaultViewByPath(ibaName);
        return attributeDefinition;
    }

    public static void updateIbaValue(String arrName, WTObject object,
                                      String newValue) throws Exception {
        AttributeDefDefaultView attributeDefinition = getAttributeDefDefaultView(arrName);
        System.out.println("arrName== " + arrName +"   "+ object +"   "+ newValue);
        if (attributeDefinition == null) {
            logger.debug("Attribute: " + arrName + " is not defined into the system. Nothing is done.");
            return;
        } else {
            Class ibaClass= null;
            Class defClass= null;
            AbstractAttributeDefinition strdef = getAttributeDefinition(arrName);
            if(strdef==null){
                logger.debug("Attribute: " + arrName + " is not defined into the system. Nothing is done.");
                return;
            }
            logger.debug(">>>>>newValue: : :" + newValue);
            //	if(newValue!=null && newValue.trim().length()>0){
            if(newValue != null){
                if (attributeDefinition instanceof StringDefView) {

                    ibaClass =  StringValue.class;
                    defClass =  StringDefinition.class;
                    QueryResult qr = queryIBAValue(ibaClass,strdef,object,arrName);
                    if (qr.hasMoreElements()) {
                        StringValue value = (StringValue) qr.nextElement();
                        value.setValue(newValue);
                        PersistenceHelper.manager.save(value);
                    } else {
                        StringValue value = StringValue.newStringValue((StringDefinition)strdef, (IBAHolder)object, newValue);
                        PersistenceHelper.manager.save(value);
                    }

                } else if (attributeDefinition instanceof FloatDefView) {
                    if("".equals(newValue)){
                        newValue="0.0";
                    }
                    ibaClass =  FloatValue.class;
                    defClass =  FloatDefinition.class;
                    QueryResult qr = queryIBAValue(ibaClass,strdef,object,arrName);
                    if (qr.hasMoreElements()) {
                        FloatValue value = (FloatValue) qr.nextElement();
                        value.setValue(Float.valueOf(newValue).floatValue());
                        PersistenceHelper.manager.save(value);
                    } else {
                        FloatValue value = FloatValue.newFloatValue((FloatDefinition)strdef, (IBAHolder)object, Float.valueOf(newValue).floatValue(),10);
                        PersistenceHelper.manager.save(value);
                    }

                } else if (attributeDefinition instanceof IntegerDefView) {
                    if("".equals(newValue)){
                        newValue="0";
                    }
                    ibaClass =  IntegerValue.class;
                    defClass =  IntegerDefinition.class;
                    QueryResult qr = queryIBAValue(ibaClass,strdef,object,arrName);
                    if (qr.hasMoreElements()) {
                        IntegerValue value = (IntegerValue) qr.nextElement();
                        value.setValue(Long.valueOf(newValue).longValue());
                        PersistenceHelper.manager.save(value);
                    } else {
                        IntegerValue value = IntegerValue.newIntegerValue((IntegerDefinition)strdef, (IBAHolder)object, Long.valueOf(newValue).longValue());
                        PersistenceHelper.manager.save(value);
                    }

                }  else if (attributeDefinition instanceof BooleanDefView) {

                    ibaClass =  BooleanValue.class;
                    defClass =  BooleanDefinition.class;
                    QueryResult qr = queryIBAValue(ibaClass,strdef,object,arrName);
                    if (qr.hasMoreElements()) {
                        BooleanValue value = (BooleanValue) qr.nextElement();
                        value.setValue(Boolean.valueOf(newValue).booleanValue());
                        PersistenceHelper.manager.save(value);
                    } else {
                        BooleanValue value = BooleanValue.newBooleanValue((BooleanDefinition)strdef, (IBAHolder)object, Boolean.valueOf(newValue).booleanValue());
                        PersistenceHelper.manager.save(value);
                    }

                } else if (attributeDefinition instanceof URLDefView) {

                    ibaClass =  URLValue.class;
                    defClass =  URLDefinition.class;
                    QueryResult qr = queryIBAValue(ibaClass,strdef,object,arrName);
                    if (qr.hasMoreElements()) {
                        URLValue value = (URLValue) qr.nextElement();
                        value.setValue(newValue);
                        PersistenceHelper.manager.save(value);
                    } else {
                        URLValue value = URLValue.newURLValue((URLDefinition)strdef, (IBAHolder)object, newValue,newValue);
                        PersistenceHelper.manager.save(value);
                    }

                } else if (attributeDefinition instanceof wt.iba.definition.litedefinition.TimestampDefView) {

                    ibaClass =  wt.iba.value.TimestampValue.class;
                    defClass =  wt.iba.definition.TimestampDefinition.class;
                    QueryResult qr = queryIBAValue(ibaClass,strdef,object,arrName);
                    if (qr.hasMoreElements()) {
                        TimestampValue value = (TimestampValue) qr.nextElement();
                        value.setValue(Timestamp.valueOf(newValue));
                        PersistenceHelper.manager.save(value);
                    } else {
                        TimestampValue value = TimestampValue.newTimestampValue((TimestampDefinition)strdef, (IBAHolder)object, Timestamp.valueOf(newValue));
                        PersistenceHelper.manager.save(value);
                    }

                }else {
                    return;
                }
            } else {
                if (attributeDefinition instanceof wt.iba.definition.litedefinition.TimestampDefView) {

                    ibaClass =  wt.iba.value.TimestampValue.class;
                    defClass =  wt.iba.definition.TimestampDefinition.class;
                    QueryResult qr = queryIBAValue(ibaClass,strdef,object,arrName);
                    if (qr.hasMoreElements()) {
                        TimestampValue value = (TimestampValue) qr.nextElement();
                        value.setValue(null);
                        PersistenceHelper.manager.save(value);
                    } else {
                        TimestampValue value = TimestampValue.newTimestampValue((TimestampDefinition)strdef, (IBAHolder)object, null);
                        PersistenceHelper.manager.save(value);
                    }
                }
                logger.debug("Attribute: " + arrName + " is value is empty. Nothing is done.");
                return;
            }
        }
    }

    public static QueryResult queryIBAValue(Class ibaClass, AbstractAttributeDefinition strdef, WTObject object, String arrName) {
        QueryResult qr = null;
        try{
            QuerySpec qs = new QuerySpec(ibaClass);
            qs.appendWhere(new SearchCondition(ibaClass,
                    "theIBAHolderReference.key", SearchCondition.EQUAL, object
                    .getPersistInfo().getObjectIdentifier()), new int[] { 0 });
            qs.appendAnd();
            qs.appendWhere(new SearchCondition(ibaClass,
                    "definitionReference.key", SearchCondition.EQUAL, strdef
                    .getPersistInfo().getObjectIdentifier()), new int[] { 0 });
//			logger.debug(">>>queryIBAValue["+arrName+"]:"+qs);
            qr = PersistenceHelper.manager.find((StatementSpec) qs);
        } catch (QueryException e){
            e.printStackTrace();
        } catch (WTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return qr;
    }

    public static void updateIbaValue2(String arrName, WTObject object,
                                       String newValue) throws Exception {
        SRIbaUtils.deleteIBAValue((IBAHolder)object, arrName);
        object = (WTObject)PersistenceHelper.manager.save(object);
        object = (WTObject)PersistenceHelper.manager.refresh(object);
        AttributeDefDefaultView attributeDefinition = getAttributeDefDefaultView(arrName);
        if (attributeDefinition == null) {
            logger.debug("Attribute: " + arrName + " is not defined into the system. Nothing is done.");
            return;
        } else {
//			Class ibaClass= null;
            AbstractAttributeDefinition attrDef = getAttributeDefinition(arrName);
            if(attrDef==null){
                logger.debug("Attribute: " + arrName + " is not defined into the system. Nothing is done.");
                return;
            }
            if (attributeDefinition instanceof StringDefView) {
//				ibaClass = StringValue.class;
                StringValue value = StringValue.newStringValue((StringDefinition)attrDef, (IBAHolder)object, newValue);
                PersistenceHelper.manager.save(value);
            } else if (attributeDefinition instanceof FloatDefView) {
//				ibaClass = FloatValue.class;
                FloatValue value = FloatValue.newFloatValue((FloatDefinition)attrDef, (IBAHolder)object, Float.valueOf(newValue).floatValue(),10);
                PersistenceHelper.manager.save(value);
            } else if (attributeDefinition instanceof IntegerDefView) {
//				ibaClass = IntegerValue.class;
                IntegerValue value = IntegerValue.newIntegerValue((IntegerDefinition)attrDef, (IBAHolder)object, Long.valueOf(newValue).longValue());
                PersistenceHelper.manager.save(value);
            }  else if (attributeDefinition instanceof BooleanDefView) {
//				ibaClass = BooleanValue.class;
                BooleanValue value = BooleanValue.newBooleanValue((BooleanDefinition)attrDef, (IBAHolder)object, Boolean.valueOf(newValue).booleanValue());
                PersistenceHelper.manager.save(value);
            } else if (attributeDefinition instanceof URLDefView) {
//				ibaClass =  URLValue.class;
                URLValue value = URLValue.newURLValue((URLDefinition)attrDef, (IBAHolder)object, newValue,newValue);
                PersistenceHelper.manager.save(value);
            } else if (attributeDefinition instanceof wt.iba.definition.litedefinition.TimestampDefView) {
//				ibaClass =  TimestampValue.class;
                TimestampValue value = TimestampValue.newTimestampValue((TimestampDefinition)attrDef, (IBAHolder)object, Timestamp.valueOf(newValue));
                PersistenceHelper.manager.save(value);
            }else {
                return;
            }
        }
//		StringDefinition strdef = getStringDefinition(arrName);
//
//		QuerySpec qs = new QuerySpec(StringValue.class);
//		qs.appendWhere(new SearchCondition(StringValue.class,
//				"theIBAHolderReference.key", SearchCondition.EQUAL, object
//						.getPersistInfo().getObjectIdentifier()), new int[] { 0 });
//		qs.appendAnd();
//		qs.appendWhere(new SearchCondition(StringValue.class,
//				"definitionReference.key", SearchCondition.EQUAL, strdef
//						.getPersistInfo().getObjectIdentifier()), new int[] { 0 });
//		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
//		if (qr.hasMoreElements()) {
//			StringValue strvalue = (StringValue) qr.nextElement();
//			strvalue.setValue(newValue);
//			PersistenceHelper.manager.save(strvalue);
//		} else {
//			StringValue sv = StringValue.newStringValue(strdef, (IBAHolder)object, newValue);
//			PersistenceHelper.manager.save(sv);
//		}
    }

    private static StringDefinition getStringDefinition(String attrName)
            throws WTException {
        StringDefinition strdfn = null;
        QuerySpec qs = new QuerySpec(StringDefinition.class);
        qs.appendWhere(new SearchCondition(StringDefinition.class,
                        StringDefinition.NAME, SearchCondition.EQUAL, attrName),
                new int[] { 0 });
        QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
        while (qr.hasMoreElements()) {
            strdfn = (StringDefinition) qr.nextElement();
        }
        return strdfn;
    }

    public static String getIBAStringValue(WTObject obj, String ibaName)
            throws WTException {

        String value = null;
        String ibaClass = "wt.iba.definition.StringDefinition";

        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);

                if (defaultattributecontainer != null) {
                    AbstractValueView avv = getIBAValueView(defaultattributecontainer,
                            ibaName, ibaClass);
                    if (avv != null) {
                        value = ((StringValueDefaultView) avv).getValue();
                    } else {
                    }
                }
            }
        } catch (RemoteException rexp) {
            logger.debug(" ** !!!!! ** ERROR Getting IBS");
            rexp.printStackTrace();
        }

        return value;

    }

    public static String getIBADateValue(WTObject obj, String ibaName)
            throws WTException {

        String value = null;
        String ibaClass = "wt.iba.definition.TimestampDefinition";

        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);

                if (defaultattributecontainer != null) {
                    AbstractValueView avv = getIBAValueView(defaultattributecontainer,
                            ibaName, ibaClass);
                    if (avv != null) {
                        Timestamp valuetmp = ((wt.iba.value.litevalue.TimestampValueDefaultView) avv).getValue();
                        value = String.valueOf(valuetmp);
                        System.out.println("Timestamp value====" + value);
                    } else {
                    }
                }
            }
        } catch (RemoteException rexp) {
            logger.debug(" ** !!!!! ** ERROR Getting IBS");
            rexp.printStackTrace();
        }

        return value;

    }


    public static Timestamp getIBATimestampValue(WTObject obj, String ibaName) throws WTException {
        Timestamp valuetmp = null;
        String ibaClass = "wt.iba.definition.TimestampDefinition";

        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);

                if (defaultattributecontainer != null) {
                    AbstractValueView avv = getIBAValueView(defaultattributecontainer, ibaName, ibaClass);
                    if (avv != null) {
                        valuetmp = ((wt.iba.value.litevalue.TimestampValueDefaultView) avv).getValue();
                    } else {
                    }
                }
            }
        } catch (RemoteException rexp) {
            logger.debug(" ** !!!!! ** ERROR Getting IBS");
            rexp.printStackTrace();
        }
        return valuetmp;
    }


    public static String getIBAFloatValue(WTObject obj, String ibaName)
            throws WTException {

        String value = null;
        String ibaClass = "wt.iba.definition.FloatDefinition";

        try {
            if (obj instanceof IBAHolder) {
                IBAHolder ibaholder = (IBAHolder) obj;
                DefaultAttributeContainer defaultattributecontainer = getContainer(ibaholder);

                if (defaultattributecontainer != null) {
                    AbstractValueView avv = getIBAValueView(defaultattributecontainer,
                            ibaName, ibaClass);

                    if (avv != null) {
                        double vauleDouble = ((FloatValueDefaultView) avv).getValue();
                        value = String.valueOf(vauleDouble);
                    } else {
                    }
                }
            }
        } catch (RemoteException rexp) {
            logger.debug(" ** !!!!! ** ERROR Getting IBS");
            rexp.printStackTrace();
        }

        return value;

    }

    private static Locale LOCALE = Locale.CHINA;

    public static DefaultAttributeContainer getContainer(IBAHolder ibaholder)
            throws WTException, RemoteException {

        ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder,
                null, LOCALE, null);
        DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
                .getAttributeContainer();

        return defaultattributecontainer;
    }

    public static AbstractValueView getIBAValueView(
            DefaultAttributeContainer dac, String ibaName, String ibaClass)
            throws WTException {

        AbstractValueView aabstractvalueview[] = null;
        AbstractValueView avv = null;

        aabstractvalueview = dac.getAttributeValues();
        for (int j = 0; j < aabstractvalueview.length; j++) {
            String thisIBAName = aabstractvalueview[j].getDefinition().getName();
            String thisIBAClass = (aabstractvalueview[j].getDefinition())
                    .getAttributeDefinitionClassName();
            if (thisIBAName.equals(ibaName) && thisIBAClass.equals(ibaClass)) {
                avv = aabstractvalueview[j];
                break;
            }
        }

        return avv;
    }

    /**
     * 设定IBA属性, 如果返回值为true, 则需要更新对象: persistable =
     * IBAValueHelper.service.updateIBAHolder(ibaHolder, null, null, null)
     *
     * @param ibaHolder
     *          设定IBA属性目标对象
     * @param ibaValues
     *          要设定的属性名和值集合
     * @return 对象是否被更改需要保存
     * @throws WTException
     */
    public static boolean setIBAValues(IBAHolder ibaHolder, Properties ibaValues)
            throws WTException {
        // 取对象的原有IBA属性
        HashMap ibaMap = new HashMap();
        Locale locale = WTContext.getContext().getLocale();
        TimeZone tzone = WTContext.getContext().getTimeZone();
        TypeInstance ti = getIBAValuesInternal(ibaHolder, null, ibaMap, false);
        // logger.debug("ibaHolder = {}", ibaHolder);
        // logger.debug("ibaValues : {}", ibaValues);
        // logger.debug("系统中存在的IBA属性 ：{}", ibaMap);
        IdentifierFactory idFactory = (IdentifierFactory) DefaultServiceProvider
                .getService(com.ptc.core.meta.common.IdentifierFactory.class, "default");

        // 整理要赋值的IBA属性
        ArrayList listIBAId = new ArrayList();
        ArrayList listIBATypeId = new ArrayList();
        ArrayList listIBAValue = new ArrayList();
        for (Enumeration en = ibaValues.keys(); en.hasMoreElements();) {
            String iName = (String) en.nextElement();
            String iVal = (String) ibaValues.get(iName);
            if (iVal == null) // null ==> 使用默认值
                continue;

            HashMap ibaInfo = (HashMap) ibaMap.get(iName);
            if (ibaInfo == null) { // 未定义的属性名称
                Persistable p = (Persistable) ibaHolder;
                String oid = PersistenceHelper.isPersistent(p) ? new ReferenceFactory()
                        .getReferenceString(p) : ibaHolder.getClass().getName() + ":NEW";
                logger.info("未定义的IBA属性名: [" + iName + "], " + oid);
                continue;
            }
            Boolean required = (Boolean) ibaInfo.get(IBA_REQUIRED);
            if (required != null && required.booleanValue() && iVal.equals(""))
                throw new WTException("属性<" + iName + ">的值不能为空!");

            AttributeIdentifier ai = (AttributeIdentifier) idFactory
                    .get((String) ibaInfo.get(IBA_IDENTIFIER));
            DefinitionIdentifier ati = ai.getDefinitionIdentifier();

            String dataType = (String) ibaInfo.get(IBA_DATATYPE);
            Object iv = convertStringToIBAValue(iVal, dataType, locale, tzone);

            listIBAId.add(ai);
            listIBAValue.add(iv);
            listIBATypeId.add(ati);
        }

        // 逐个赋值
        HashMap vmap = new HashMap();
        TypeInstanceIdentifier tii = (TypeInstanceIdentifier) ti.getIdentifier();
        // logger.debug("{}" ,tii);
        for (int i = 0; i < listIBAId.size(); i++) {
            AttributeTypeIdentifier ati = (AttributeTypeIdentifier) listIBATypeId
                    .get(i);
            AttributeIdentifier[] ais = ti.getAttributeIdentifiers(ati);
            if (ais.length > 0) {
                vmap.put(ais[0], ti.get(ais[0]));
                ti.put(ais[0], listIBAValue.get(i));
            } else {
                AttributeIdentifier ai = ati.newAttributeIdentifier(tii);
                vmap.put(ai, null);
                ti.put(ai, listIBAValue.get(i));
            }
        }

        ti.acceptDefaultContent();
        ti.purgeDefaultContent();

        // 检查约束
        if (tii.isInitialized())
            TypeInstanceUtility.populateConstraints(ti, OperationIdentifier
                    .newOperationIdentifier("STDOP|com.ptc.windchill.update"));
        else
            TypeInstanceUtility.populateConstraints(ti, OperationIdentifier
                    .newOperationIdentifier("STDOP|com.ptc.windchill.create"));
        DefaultConstraintValidator dac = DefaultConstraintValidator.getInstance();
        ConstraintContainer cc = ti.getConstraintContainer();
        if (cc != null) {
            AttributeIdentifier ais[] = ti.getAttributeIdentifiers();
            for (int i = 0; i < ais.length; i++) {
                Object ibaVal = ti.get(ais[i]);
                try {
                    dac.isValid(ti, cc, ais[i], ibaVal);
                } catch (ConstraintException ce) {
                    if ((!ce
                            .getConstraintIdentifier()
                            .getEnforcementRuleClassname()
                            .equals(
                                    "com.ptc.core.meta.container.common.impl.DiscreteSetConstraint")
                            || vmap == null || vmap.get(ais[i]) == null || (!(vmap
                            .get(ais[i]) instanceof Comparable) || ((Comparable) ti
                            .get(ais[i])).compareTo(vmap.get(ais[i])) != 0)
                            && !vmap.get(ais[i]).equals(ti.get(ais[i])))
                            && !ce
                            .getConstraintIdentifier()
                            .getEnforcementRuleClassname()
                            .equals(
                                    "com.ptc.core.meta.container.common.impl.ImmutableConstraint")) {
                        WTException wtexception = interpretConstraintViolationException(ce,
                                locale);
                        if (wtexception != null)
                            throw wtexception;
                    }
                }
            }
        }

        // 保存到目标对象
        TypeInstanceUtility.updateIBAValues(ibaHolder, ti);
        return ti.isDirty();
    }

    /**
     * 获取IBA信息的内部实现
     *
     * @param obj
     *          IBAHolder对象或typeIdentifer字串
     * @param ibaList *
     * @param ibaMap *
     * @throws WTException
     */
    public static TypeInstance getIBAValuesInternal(Object obj,
                                                    ArrayList ibaList, HashMap ibaMap, boolean returnOpts) throws WTException {
        TypeInstanceIdentifier tii = null;
        Locale locale = WTContext.getContext().getLocale();
        boolean forTypedObj = false;
        logger.debug(">>>obj:"+obj);
        // 取TypeInstanceIdentifier
        if (obj instanceof IBAHolder) { // obj是一个IBAHolder(Typed)对象
            tii = TypeIdentifierUtility.getTypeInstanceIdentifier(obj);
            logger.debug(">>>tii 11:"+tii);
            forTypedObj = true;
        } else { // obj是一个TypeIdentifier字符串, e.g. WTTYPE|wt.doc.WTDocument|...
            IdentifierFactory idFactory = (IdentifierFactory) DefaultServiceProvider
                    .getService(com.ptc.core.meta.common.IdentifierFactory.class,
                            "default");
            TypeIdentifier ti = (TypeIdentifier) idFactory.get((String) obj);
            tii = ti.newTypeInstanceIdentifier();
            logger.debug(">>>tii 22:"+tii);
        }
        logger.debug(">>>locale:"+locale);
        // 获取TypeInstance
        TypeInstance typeInstance = null;
        try {
            if (false) {
                PopulatedAttributeContainerFactory pacFactory = (PopulatedAttributeContainerFactory) DefaultServiceProvider
                        .getService(PopulatedAttributeContainerFactory.class, "virtual");
                AttributeContainer ac = pacFactory.getAttributeContainer(null,
                        (TypeIdentifier) tii.getDefinitionIdentifier());

                if (ac == null) {
                    if (obj instanceof String)
                        throw new WTException("未定义的SoftType类型: " + obj);
                    else
                        throw new WTException("未定义的SoftType类型: " + tii);
                }

                AttributeContainerSpec acSpec = new AttributeContainerSpec();
                IdentifierFactory idFact = (IdentifierFactory) DefaultServiceProvider
                        .getService(com.ptc.core.meta.common.IdentifierFactory.class,
                                "logical");
                AttributeTypeIdentifier ati1 = (AttributeTypeIdentifier) idFact.get(
                        "ALL_SOFT_SCHEMA_ATTRIBUTES", tii.getDefinitionIdentifier());
                acSpec.putEntry(ati1, true, true);
                AttributeTypeIdentifier ati2 = (AttributeTypeIdentifier) idFact.get(
                        "ALL_SOFT_ATTRIBUTES", tii.getDefinitionIdentifier());
                acSpec.putEntry(ati2, true, true);
                AttributeTypeIdentifier ati3 = (AttributeTypeIdentifier) idFact
                        .get("ALL_SOFT_CLASSIFICATION_ATTRIBUTES", tii
                                .getDefinitionIdentifier());
                acSpec.putEntry(ati3, true, true);
                if (tii.isInitialized())
                    acSpec.setNextOperation(OperationIdentifier
                            .newOperationIdentifier("STDOP|com.ptc.windchill.update"));
                else
                    acSpec.setNextOperation(OperationIdentifier
                            .newOperationIdentifier("STDOP|com.ptc.windchill.create"));
                PrepareEntityCommand peCmd = new PrepareEntityCommand();
                peCmd.setLocale(locale);
                peCmd.setFilter(acSpec);
                peCmd.setSource(tii);
                peCmd = (PrepareEntityCommand) peCmd.execute();
                typeInstance = peCmd.getResult();
                Set set = (Set) typeInstance.getSingle(ati3);
                if (set != null) {
                    for (Iterator iterator = set.iterator(); iterator.hasNext(); typeInstance
                            .purge((AttributeTypeIdentifier) iterator.next()))
                        ;
                }
                typeInstance.purge(ati1);
                typeInstance.purge(ati2);
                typeInstance.purge(ati3);
                AttributeTypeIdentifier ati[] = typeInstance
                        .getAttributeTypeIdentifiers();
                for (int j = 0; j < ati.length; j++)
                    if (ati[j].getContext() instanceof AttributeTypeIdentifier)
                        typeInstance.purge(ati[j]);
            } else {
                typeInstance = SoftAttributesHelper.getSoftSchemaTypeInstance(tii,
                        null, locale);
            }
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception,
                    "SoftAttributesHelper.getSoftSchemaTypeInstance(): "
                            + "Exception encountered when trying to create a type instance");
        } catch (UnsupportedOperationException unsupportedoperationexception) {
            throw new WTException(unsupportedoperationexception,
                    "SoftAttributesHelper.getSoftSchemaTypeInstance(): "
                            + "Exception encountered when trying to create a type instance");
        }

        // 对IBAHolder对象,填充未设定的属性
        if (forTypedObj) {
            // TypeInstanceUtility.populateMissingTypeContent(typeInstance, null);
        }

        // 逐个获取IBA属性
        AttributeIdentifier[] ais = typeInstance.getAttributeIdentifiers();
        for (int i = 0; ais != null && i < ais.length; i++) {
            DefinitionIdentifier di = ais[i].getDefinitionIdentifier();
            AttributeTypeIdentifier ati = (AttributeTypeIdentifier) di;
            AttributeTypeSummary ats = typeInstance.getAttributeTypeSummary(ati);

            String ibaIdentifier = ais[i].toExternalForm();
            String name = ati.getAttributeName();
            ati.getWithTailContext();

            String value = String.valueOf(typeInstance.get(ais[i]));
            String dataType = ats.getDataType();
            String label = ats.getLabel();
            Boolean required = ats.isRequired() ? Boolean.TRUE : null;
            Boolean editable = ats.isEditable() ? Boolean.TRUE : null;

            int min = ats.getMinStringLength();
            int max = ats.getMaxStringLength();
            Integer minStringLength = min == 0 ? null : Integer.valueOf(min);
            Integer maxStringLength = max == 0 ? null : Integer.valueOf(max);

            HashMap ibaInfo = new HashMap();
            ibaInfo.put(IBA_IDENTIFIER, ibaIdentifier);
            ibaInfo.put(IBA_NAME, name);
            ibaInfo.put(IBA_VALUE, value);
            ibaInfo.put(IBA_LABEL, label);
            ibaInfo.put(IBA_DATATYPE, dataType);
            ibaInfo.put(IBA_REQUIRED, required);
            ibaInfo.put(IBA_EDITABLE, editable);
            ibaInfo.put(IBA_STRING_LENGTH_MIN, minStringLength);
            ibaInfo.put(IBA_STRING_LENGTH_MAX, maxStringLength);

            if (returnOpts) {
                Vector options = null;
                DataSet dsVal = ats.getLegalValueSet();
                if (dsVal != null && dsVal instanceof DiscreteSet) {
                    Object[] eles = ((DiscreteSet) dsVal).getElements();
                    options = new Vector();
                    for (int j = 0; eles != null && j < eles.length; j++) {
                        options.add(String.valueOf(eles[j]));
                    }
                }
                ibaInfo.put(IBA_OPTIONS_VECTOR, options);
            }

            if (ibaList != null) {
                ibaList.add(ibaInfo);
            }
            if (ibaMap != null) {
                ibaMap.put(name, ibaInfo);
            }
        }

        return typeInstance;
    }

    /**
     * 将字符串值按指定类型转换为IBA属性值对应的对象
     *
     * @param strVal
     *          字符串值
     * @param dataType
     *          数据类型(java类型)
     * @param locale *
     * @param timezone *
     * @return Object值对象
     * @throws WTException
     */
    public static Object convertStringToIBAValue(String strVal, String dataType,
                                                 Locale locale, TimeZone timezone) throws WTException {
        Object obj = null;
        if (dataType.equals("java.lang.Long"))
            try {
                obj = Long.valueOf(strVal);
            } catch (Exception exception) {
                Object aobj1[] = { strVal };
                throw new WTException(
                        "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource",
                        "58", aobj1);
            }
        else if (dataType.equals("com.ptc.core.meta.common.FloatingPoint"))
            try {
                obj = DataTypesUtility.toFloatingPoint(strVal, locale);
            } catch (Exception exception1) {
                Object aobj3[] = { strVal };
                throw new WTException(
                        "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource",
                        "59", aobj3);
            }
        else if (dataType.equals("java.lang.Boolean"))
            obj = Boolean.valueOf(strVal);
        else if (dataType.equals("java.sql.Timestamp"))
            try {
                Date date = null;
                try {
                    date = WTStandardDateFormat.parse(strVal, 3, locale, timezone);
                } catch (ParseException parseexception) {
                    try {
                        date = WTStandardDateFormat.parse(strVal, 25, locale, timezone);
                    } catch (ParseException parseexception1) {
                        date = WTStandardDateFormat.parse(strVal, 26, locale, timezone);
                    }
                }
                obj = new Timestamp(date.getTime());
            } catch (ParseException parseexception) {
                Object aobj5[] = { strVal };
                throw new WTException(
                        "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource",
                        "60", aobj5);
            }
        else
            obj = strVal;
        return obj;
    }

    /**
     * 解释IBA约束错误, 来自: EntityTaskDelegate
     *
     * @param constraintexception *
     * @param locale *
     * @return *
     * @throws WTException
     */
    public static WTException interpretConstraintViolationException(
            ConstraintException constraintexception, Locale locale)
            throws WTException {
        AttributeIdentifier attributeidentifier = constraintexception
                .getAttributeIdentifier();
        AttributeTypeIdentifier attributetypeidentifier = (AttributeTypeIdentifier) attributeidentifier
                .getDefinitionIdentifier();
        AttributeContainerSpec attributecontainerspec = new AttributeContainerSpec();
        attributecontainerspec.putEntry(attributetypeidentifier, true, true);
        NewEntityCommand newentitycommand = new NewEntityCommand();
        try {
            (newentitycommand)
                    .setIdentifier(attributetypeidentifier.getContext());
            newentitycommand.setFilter(attributecontainerspec);
            newentitycommand.setLocale(locale);
        } catch (WTPropertyVetoException wtpropertyvetoexception) {
            throw new WTException(wtpropertyvetoexception);
        }
        newentitycommand.execute();
        TypeInstance typeinstance = newentitycommand.getResult();
        AttributeTypeSummary attributetypesummary = typeinstance
                .getAttributeTypeSummary((AttributeTypeIdentifier) attributeidentifier
                        .getDefinitionIdentifier());
        String s = attributetypesummary.getLabel();
        // Object obj = constraintexception.getAttributeContent();
        ConstraintIdentifier constraintidentifier = constraintexception
                .getConstraintIdentifier();
        String s1 = constraintidentifier.getEnforcementRuleClassname();
        ConstraintData constraintdata = constraintexception.getConstraintData();
        // String s2 = " ";
        String s3 = "com.ptc.core.HTMLtemplateutil.server.processors.processorsResource";
        String s4 = null;
        java.io.Serializable serializable = constraintdata.getEnforcementRuleData();
        ArrayList arraylist = new ArrayList();
        arraylist.add(s);
        if (s1.equals("com.ptc.core.meta.container.common.impl.RangeConstraint")) {
            if (serializable instanceof AnalogSet) {
                Range range = ((AnalogSet) serializable).getBoundingRange();
                if (range.hasLowerBound() && range.hasUpperBound()) {
                    arraylist.add(range.getLowerBoundValue());
                    arraylist.add(range.getUpperBoundValue());
                    s4 = "72";
                } else if (range.hasLowerBound()) {
                    arraylist.add(range.getLowerBoundValue());
                    s4 = "73";
                } else if (range.hasUpperBound()) {
                    arraylist.add(range.getUpperBoundValue());
                    s4 = "74";
                }
            } else {
                s4 = "75";
            }
        } else if (s1
                .equals("com.ptc.core.meta.container.common.impl.ImmutableConstraint"))
            s4 = "78";
        else if (s1
                .equals("com.ptc.core.meta.container.common.impl.DiscreteSetConstraint")) {
            if (serializable instanceof DiscreteSet) {
                Object aobj[] = ((DiscreteSet) serializable).getElements();
                String s5 = "";
                for (int j = 0; j < aobj.length; j++)
                    s5 = s5 + aobj[j].toString() + ",";

                String s7 = s5.substring(0, s5.length() - 1);
                arraylist.add(s7);
                s4 = "83";
            } else {
                s4 = "84";
            }
        } else if (s1
                .equals("com.ptc.core.meta.container.common.impl.StringLengthConstraint")) {
            if (serializable instanceof AnalogSet) {
                Range range1 = ((AnalogSet) serializable).getBoundingRange();
                if (range1.hasLowerBound() && range1.hasUpperBound()) {
                    arraylist.add(range1.getLowerBoundValue());
                    arraylist.add(range1.getUpperBoundValue());
                    s4 = "79";
                } else if (range1.hasLowerBound()) {
                    arraylist.add(range1.getLowerBoundValue());
                    s4 = "80";
                } else if (range1.hasUpperBound()) {
                    arraylist.add(range1.getUpperBoundValue());
                    s4 = "81";
                }
            } else {
                s4 = "82";
            }
        } else if (s1
                .equals("com.ptc.core.meta.container.common.impl.StringFormatConstraint")) {
            if (serializable instanceof DiscreteSet) {
                Object aobj1[] = ((DiscreteSet) serializable).getElements();
                String s6 = "";
                for (int k = 0; k < aobj1.length; k++)
                    s6 = s6 + "\"" + aobj1[k].toString() + "\" or ";

                String s8 = s6.substring(0, s6.length() - 4);
                arraylist.add(s8);
                s4 = "85";
            } else {
                s4 = "84";
            }
        } else if (s1
                .equals("com.ptc.core.meta.container.common.impl.UpperCaseConstraint"))
            s4 = "86";
        else if (s1
                .equals("com.ptc.core.meta.container.common.impl.ValueRequiredConstraint"))
            s4 = "77";
        else if (s1
                .equals("com.ptc.core.meta.container.common.impl.WildcardConstraint")) {
            if (serializable instanceof WildcardSet) {
                arraylist.add(((WildcardSet) serializable).getValue());
                int i = ((WildcardSet) serializable).getMode();
                if (i == 1) {
                    s4 = "87";
                    arraylist.add(((WildcardSet) serializable).getValue());
                } else if (i == 2) {
                    if (((WildcardSet) serializable).isNegated())
                        s4 = "89";
                    else
                        s4 = "88";
                } else if (i == 3) {
                    if (((WildcardSet) serializable).isNegated())
                        s4 = "91";
                    else
                        s4 = "90";
                } else if (i == 4)
                    if (((WildcardSet) serializable).isNegated())
                        s4 = "93";
                    else
                        s4 = "92";
            } else {
                s4 = "84";
            }
        } else {
            s4 = "84";
        }
        if (s4 != null)
            return new WTException(s3, s4, arraylist.toArray());
        else
            return null;
    }

    public static final String IBACONST_LEGAL_VALUE_SET = "LEGAL_VALUE_SET";
    public static final String IBACONST_STRING_LENGTH_SET = "STRING_LENGTH_SET";
    // private static final String IBACONST_REQUIRED = "Required";

    public static final String IBA_IDENTIFIER = "IBA_IDENTIFIER";
    public static final String IBA_NAME = "IBA_NAME";
    public static final String IBA_VALUE = "IBA_VALUE";
    public static final String IBA_LABEL = "IBA_LABEL";
    public static final String IBA_DATATYPE = "IBA_DATATYPE";
    public static final String IBA_OPTIONS_VECTOR = "IBA_OPTIONS_VECTOR";
    public static final String IBA_REQUIRED = "IBA_REQUIRED";
    public static final String IBA_EDITABLE = "IBA_EDITABLE";
    public static final String IBA_STRING_LENGTH_MIN = "IBA_STRING_LENGTH_MIN";
    public static final String IBA_STRING_LENGTH_MAX = "IBA_STRING_LENGTH_MAX";
    public static final String IBA_FROM_DEFINITION = "IBA_FROM_DEFINITION";
    public static final String IBA_UNDEFINED = "IBA_UNDEFINED";

}