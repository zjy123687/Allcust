<%@taglib prefix="jca" uri="http://www.ptc.com/windchill/taglib/components"%>
<%@taglib prefix="attachments" uri="http://www.ptc.com/windchill/taglib/attachments"%>
<%@include file="/netmarkets/jsp/components/beginWizard.jspf"%>
<%@include file="/netmarkets/jsp/components/includeWizBean.jspf"%>
<!--  诠释点击创建宠物后 出现的各个步骤  处理后包含一个form会提交到coommbean -->
<jca:initializeItem baseTypeName="com.acme.Pet" operation="${createBean.create}" attributePopulatorClass="com.ptc.core.components.forms.DefaultAttributePopulator"/>
<jca:wizard>
<jca:wizardStep action="petDefineItemAttributesWizStep" type="pet"/>
<jca:wizardStep action="attachments_step" type="attachments"/>
</jca:wizard>
<attachments:fileSelectionAndUploadApplet/>
<%@include file="/netmarkets/jsp/util/end.jspf"%>