package ext.corilead.doc;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import org.apache.log4j.Logger;
import wt.change2.WTChangeOrder2;
import wt.change2.WTChangeRequest2;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.identity.IdentityFactory;
import wt.inf.container.WTContainerRef;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;
import wt.util.LocalizableMessage;
import wt.util.WTException;
import wt.util.WTMessage;
import wt.vc.baseline.ManagedBaseline;
import wt.workflow.definer.WfDefinerHelper;
import wt.workflow.definer.WfProcessDefinition;
import wt.workflow.engine.ProcessData;
import wt.workflow.engine.WfEngineHelper;
import wt.workflow.engine.WfEngineServerHelper;
import wt.workflow.engine.WfProcess;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
 public class SetState implements RemoteAccess {

    private static Logger logger = Logger.getLogger(SetState.class);

    /**
     * 提交审阅
     *
     * @param nmcommandbean
     *            上下文环境
     * @return 执行结果
     * @throws Exception
     * @throws IOException
     */
    public static FormResult doSetState(NmCommandBean nmcommandbean) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "doSetState";
            String klass = SetState.class.getName();
            Class[] types = { NmCommandBean.class };
            Object[] values = { nmcommandbean };
            try {
                return (FormResult) RemoteMethodServer.getDefault().invoke(method, klass, null, types, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        WTPrincipal user = SessionHelper.getPrincipal();
        SessionContext.setEffectivePrincipal(SessionHelper.manager.getAdministrator());

        FormResult formresult = new FormResult();
        formresult.setNextAction(FormResultAction.NONE);
        Persistable primobject;

        try {
            primobject = (Persistable) nmcommandbean.getActionOid().getRef();

            if (primobject instanceof WTPart || primobject instanceof WTDocument
                    || primobject instanceof ManagedBaseline || primobject instanceof EPMDocument
                    || primobject instanceof WTChangeOrder2 || primobject instanceof WTChangeRequest2) {
                boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
                try {
                    String reviewPath = nmcommandbean.getTextParameter("reviewPath");
                   // if (reviewPath != null && reviewPath.trim().length() > 0) {
                        //IbaUtil.updateIbaValue("reviewPath", (WTObject) primobject, reviewPath);
                   // }
                    logger.debug("设置签审流程结束！");

                    LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) primobject,
                            State.toState("TIJIAOSHENYUE"), true);
                    logger.debug("设置状态完成！");
                    //formresult.addFeedbackMessage(getSuccessFeedbackMessage());
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(flag);
                }

            }

            formresult.setStatus(FormProcessingStatus.SUCCESS);
            formresult.setNextAction(FormResultAction.REFRESH_OPENER);
        } finally {
            SessionContext.setEffectivePrincipal(user);
        }
        return formresult;

    }

    public static FeedbackMessage getSuccessFeedbackMessage() throws WTException {

        WTMessage wtmessage = new WTMessage("ext.i401.doc.docRB", "SET_STATE_SUCCESSFUL_TITLE", (Object[]) null);
        logger.debug("wtmessage >> " + wtmessage);
        WTMessage wtmessage1 = getSuccessMessageBody();
        logger.debug("wtmessage1 >> " + wtmessage1);
        return new FeedbackMessage(FeedbackType.SUCCESS, (Locale) null, wtmessage, (ArrayList) null,
                new LocalizableMessage[] { wtmessage1 });
    }

    public static WTMessage getSuccessMessageBody() {
        return new WTMessage("ext.i401.doc.docRB", "SET_STATE_SUCCESSFUL_MSG", (Object[]) null);
    }

    /**
     * 创建流程并启动
     *
     * @param wfprocessdefinition
     *            流程定义
     * @param lifecyclemanaged
     *            PBO
     * @param data
     *            要设置进流程的变量（Map）
     * @throws Exception
     */
    public static WfProcess createProcess(WfProcessDefinition wfprocessdefinition, LifeCycleManaged lifecyclemanaged,
                                          Map data) throws Exception {
        WTContainerRef ref = null;
        try {
            Class[] parameterTypes = {};
            Object[] args = {};
            Method method = lifecyclemanaged.getClass().getMethod("getContainerReference", parameterTypes);
            WTContainerRef ref0 = (WTContainerRef) method.invoke(lifecyclemanaged, args);
            ref = WTContainerRef.newWTContainerRef(ref0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        WfProcess wfprocess = null;
        if (ref == null) {
            wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, lifecyclemanaged);
        } else {
            wfprocess = WfEngineHelper.service.createProcess(wfprocessdefinition, lifecyclemanaged, ref);
        }
        String s = constructProcessName(wfprocessdefinition, lifecyclemanaged);
        wfprocess.setName(s);
        wfprocess.setTeamTemplateId(lifecyclemanaged.getTeamTemplateId());
        ProcessData processdata = wfprocess.getContext();
        if (data != null) {
            for (Iterator item = data.keySet().iterator(); item.hasNext();) {
                String key = (String) item.next();
                processdata.setValue(key, data.get(key));
            }
        }
        WfEngineServerHelper.service.setPrimaryBusinessObject(wfprocess, (WTObject) lifecyclemanaged, false);
        WfEngineHelper.service.startProcessImmediate(wfprocess, processdata, 1);
        return wfprocess;
    }

    /**
     * 获取默认的流程名称
     * @param wfprocessdefinition	流程定义
     * @param lifecyclemanaged		PBO
     * @return
     * @throws WTException
     */
    private static String constructProcessName(WfProcessDefinition wfprocessdefinition, LifeCycleManaged lifecyclemanaged)
            throws WTException {
        TimeZone defaultZone = TimeZone.getTimeZone("Asia/Shanghai");
        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat("MMddHHmmss");
        GregorianCalendar oGregorianCalendar = new GregorianCalendar(defaultZone);
        oSimpleDateFormat.setCalendar(oGregorianCalendar);
        String timeStr = oSimpleDateFormat.format(new Date());
        String s = wfprocessdefinition.getName() +"("+ timeStr + ")_" + IdentityFactory.getDisplayIdentifier(lifecyclemanaged);
        ClassInfo classinfo = WTIntrospector.getClassInfo(wt.workflow.engine.WfExecutionObject.class);
        int i = ((Integer) classinfo.getReadPropertyDescriptor("name").getValue("UpperLimit")).intValue();
        if (!PersistenceHelper.checkStoredLength(s, i))
            s = PersistenceHelper.truncateStoredLength(s, i);
        return s;
    }

    /**
     * 通过名称获取流程模板
     * @param templateName	模板名称
     * @return
     * @throws WTException
     */
    public static WfProcessDefinition getWfProcessTemplateByName(String templateName) throws WTException{
        return WfDefinerHelper.service.getProcessDefinition(templateName);
    }
}