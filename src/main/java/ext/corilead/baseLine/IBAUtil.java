package ext.corilead.baseLine;

import org.apache.log4j.Logger;
import wt.iba.definition.DefinitionLoader;
import wt.iba.definition.litedefinition.*;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.IBAValueUtility;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.service.IBAValueHelper;
import wt.iba.value.service.LoadValue;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

public class IBAUtil {
    private static Logger logger = Logger.getLogger(IBAUtil.class);

    Hashtable ibaContainer;

    private IBAUtil() {
        ibaContainer = new Hashtable();
    }

    /**
     * 初始化获取IBA软属性方法对象
     *
     * @param WT对象
     */
    public IBAUtil(IBAHolder ibaholder) {
        initializeIBAPart(ibaholder);
    }

    /**
     * 返回软属性关键字列表
     *
     * @return 返回软属性关键字列表
     */
    public String toString() {
        StringBuffer stringbuffer = new StringBuffer();
        Enumeration enumeration = ibaContainer.keys();
        try {
            while (enumeration.hasMoreElements()) {
                String s = (String) enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView) ((Object[]) ibaContainer.get(s))[1];
                stringbuffer.append(s
                        + " - "
                        + IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, SessionHelper.manager
                        .getLocale()));
                stringbuffer.append('\n');
            }
        } catch (Exception e) {
            logger.error("返回软属性关键字列表错误。", e);
        }
        return stringbuffer.toString();
    }

    /**
     * 获取软属性值
     *
     * @param 软属性关键字
     * @return 返回软属性值
     */
    public String getIBAValue(String s) {
        try {
            return getIBAValue(s, SessionHelper.manager.getLocale());
        } catch (WTException e) {
            logger.error("获取软属性值错误。", e);
        }
        return null;
    }

    /**
     * 获取软属性值
     *
     * @param s
     *            软属性关键字
     * @param locale
     *            根据本地语言获取值
     * @return 返回软属性值
     */
    public String getIBAValue(String s, Locale locale) {

        try {
            Object[] obj = (Object[]) ibaContainer.get(s);
            if (obj == null)
                return null;
            AbstractValueView abstractvalueview = (AbstractValueView) obj[1];
            // System.out.println("abstractvalueview"+abstractvalueview.getDefinition().getName()+"-----------------------");
            return IBAValueUtility.getLocalizedIBAValueDisplayString(abstractvalueview, locale);
        } catch (WTException e) {
            logger.error("获取软属性值错误。", e);
        }
        return null;
    }

    /**
     * 初始化获取IBA软属性方法对象
     *
     * @param ibaholder
     *            WT对象
     */
    private void initializeIBAPart(IBAHolder ibaholder) {
        ibaContainer = new Hashtable();
        try {

            ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null, SessionHelper.manager
                    .getLocale(), null);
            DefaultAttributeContainer defaultattributecontainer = (DefaultAttributeContainer) ibaholder
                    .getAttributeContainer();
            if (defaultattributecontainer != null) {
                AttributeDefDefaultView aattributedefdefaultview[] = defaultattributecontainer
                        .getAttributeDefinitions();
                for (int i = 0; i < aattributedefdefaultview.length; i++) {
                    AbstractValueView aabstractvalueview[] = defaultattributecontainer
                            .getAttributeValues(aattributedefdefaultview[i]);
                    if (aabstractvalueview != null) {
                        Object aobj[] = new Object[2];
                        aobj[0] = aattributedefdefaultview[i];
                        aobj[1] = aabstractvalueview[0];
                        ibaContainer.put(aattributedefdefaultview[i].getName(), ((Object) (aobj)));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("初始化获取IBA软属性方法对象错误。", e);
        }
    }

    /**
     * 更新WT对象软属性值
     *
     * @param WT对象
     * @return WT对象
     */
    public IBAHolder updateIBAPart(IBAHolder ibaholder) throws Exception {

        ibaholder = IBAValueHelper.service.refreshAttributeContainer(ibaholder, null,
                SessionHelper.manager.getLocale(), null);
        DefaultAttributeContainer defaultAttributeContainer = (DefaultAttributeContainer) (IBAValueHelper.service
                .refreshAttributeContainerWithoutConstraints(ibaholder)).getAttributeContainer();
        for (Enumeration enumeration = ibaContainer.elements(); enumeration.hasMoreElements();) {
            try {
                Object aobj[] = (Object[]) enumeration.nextElement();
                AbstractValueView abstractvalueview = (AbstractValueView) aobj[1];
                AttributeDefDefaultView attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
                if (abstractvalueview.getState() == 1) {
                    defaultAttributeContainer.deleteAttributeValues(attributedefdefaultview);
                    abstractvalueview.setState(3);
                    defaultAttributeContainer.addAttributeValue(abstractvalueview);
                }
            } catch (Exception e) {
                logger.error("更新WT对象软属性值错误。", e);
            }
        }
        defaultAttributeContainer.setConstraintParameter(new String("CSM"));
        ibaholder.setAttributeContainer(defaultAttributeContainer);
        return ibaholder;
    }

    /**
     * 设置软属性值
     *
     * @param s
     *            软属性关键字
     * @param s1
     *            软属性值
     */
    public void setIBAValue(String s, String s1) throws WTPropertyVetoException {
        AbstractValueView abstractvalueview = null;
        AttributeDefDefaultView attributedefdefaultview = null;
        Object aobj[] = (Object[]) ibaContainer.get(s);
        if (aobj != null) {
            abstractvalueview = (AbstractValueView) aobj[1];
            attributedefdefaultview = (AttributeDefDefaultView) aobj[0];
        }
        if (abstractvalueview == null)
            attributedefdefaultview = getAttributeDefinition(s);
        if (attributedefdefaultview == null) {

            return;
        }
        abstractvalueview = internalCreateValue(attributedefdefaultview, s1);
        if (abstractvalueview == null) {

            return;
        } else {
            abstractvalueview.setState(1);
            Object aobj1[] = new Object[2];
            aobj1[0] = attributedefdefaultview;
            aobj1[1] = abstractvalueview;
            ibaContainer.put(attributedefdefaultview.getName(), ((Object) (aobj1)));
            return;
        }
    }

    /**
     * 获取软属性ID
     *
     * @param s
     *            软属性关键字
     * @return 软属性对象
     */
    private AttributeDefDefaultView getAttributeDefinition(String s) {
        AttributeDefDefaultView attributedefdefaultview = null;
        try {
            attributedefdefaultview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(s);
            if (attributedefdefaultview == null) {
                AbstractAttributeDefinizerView abstractattributedefinizerview = DefinitionLoader
                        .getAttributeDefinition(s);
                if (abstractattributedefinizerview != null)
                    attributedefdefaultview = IBADefinitionHelper.service
                            .getAttributeDefDefaultView((AttributeDefNodeView) abstractattributedefinizerview);
            }
        } catch (Exception e) {
            logger.error("获取软属性ID错误。", e);
        }
        return attributedefdefaultview;
    }

    /**
     * 获取软属性ID
     *
     * @param s
     *            软属性关键字
     * @return 软属性对象
     */
    private AbstractValueView internalCreateValue(AbstractAttributeDefinizerView abstractattributedefinizerview,
                                                  String s) {
        AbstractValueView abstractvalueview = null;
        if (abstractattributedefinizerview instanceof FloatDefView)
            abstractvalueview = LoadValue.newFloatValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof StringDefView)
            abstractvalueview = LoadValue.newStringValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof IntegerDefView)
            abstractvalueview = LoadValue.newIntegerValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof RatioDefView)
            abstractvalueview = LoadValue.newRatioValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof TimestampDefView)
            abstractvalueview = LoadValue.newTimestampValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof BooleanDefView)
            abstractvalueview = LoadValue.newBooleanValue(abstractattributedefinizerview, s);
        else if (abstractattributedefinizerview instanceof URLDefView)
            abstractvalueview = LoadValue.newURLValue(abstractattributedefinizerview, s, null);
        else if (abstractattributedefinizerview instanceof ReferenceDefView)
            abstractvalueview = LoadValue.newReferenceValue(abstractattributedefinizerview, "ClassificationNode", s);
        else if (abstractattributedefinizerview instanceof UnitDefView)
            abstractvalueview = LoadValue.newUnitValue(abstractattributedefinizerview, s, null);
        return abstractvalueview;
    }
}
