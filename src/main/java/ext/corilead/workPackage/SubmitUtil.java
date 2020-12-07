package ext.corilead.workPackage;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.forms.FormResultAction;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.core.ui.resources.FeedbackType;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.wp.WorkPackage;
import ext.corilead.doc.SetState;
import org.apache.log4j.Logger;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.State;
import wt.method.RemoteMethodServer;
import wt.org.WTPrincipal;
import wt.part.WTPart;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.session.SessionServerHelper;

import java.io.IOException;

public class SubmitUtil {
    /**
     * 打包提交审阅
     *
     * @param nmcommandbean
     *            上下文环境
     * @return 执行结果
     * @throws Exception
     * @throws IOException
     */
    private static Logger logger = Logger.getLogger(SetState.class);
    public static FormResult doSetState(NmCommandBean nmcommandbean) throws Exception {
        if (!RemoteMethodServer.ServerFlag) {
            String method = "doSetState";
            String klass = SubmitUtil.class.getName();
            Class[] types = { NmCommandBean.class };
            Object[] values = { nmcommandbean };
            try {
                return (FormResult) RemoteMethodServer.getDefault().invoke(method, klass, null, types, values);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // 获取到提交用户
        WTPrincipal user = SessionHelper.getPrincipal();
        SessionContext.setEffectivePrincipal(SessionHelper.manager.getAdministrator());
        FormResult formresult = new FormResult();
        formresult.setNextAction(FormResultAction.NONE);

        try {
            // 获取到对象
            Persistable primobject = nmcommandbean.getActionOid().getWtRef().getObject();
            logger.debug("primobject.getClass()" + primobject.getClass());
            if (!(primobject instanceof LifeCycleManaged)) {
                formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
                        null, new String[] { "该对象不能执行提交审阅！" }));
            }

            String state_str = "";
            if (!(primobject instanceof LifeCycleManaged)) {
                formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
                        null, new String[] { "该对象不能执行提交审阅！" }));
            } else {
                state_str = ((LifeCycleManaged) primobject).getLifeCycleState().toString();
            }

            if (!("BIANXIE".equalsIgnoreCase(state_str))) {
                formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(), null,
                        null, new String[] { "只允许对编写状态的数据对象执行提交审阅操作！" }));
            } else if (primobject instanceof WTPart) {
                WTPart part = (WTPart) primobject;
               // WorkPackage wp = WPUtil.createPackage(part);
               // WPUtil.addPackageMembers(wp, part);

                boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
                try {
                    // 将包设置为提交审阅状态
                    //LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) wp, State.toState("TIJIAOSHENYUE"),true);
                    // 将包里的对象设置为审签中状态
                    LifeCycleHelper.service.setLifeCycleState(part,State.toState("TIJIAOSHENYUE"), true);
                    logger.debug("设置状态完成！");
                    formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(),
                            null, null, new String[] { "提交审阅成功！" }));
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(flag);
                }

                formresult.setStatus(FormProcessingStatus.SUCCESS);
                formresult.setNextAction(FormResultAction.REFRESH_OPENER);
                formresult.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);

            } else if (primobject instanceof WTDocument) {

                WTDocument doc = (WTDocument) primobject;
               // WorkPackage wp = WPUtil.createDocPackage(doc);
                //WPUtil.addPackageMembers(wp, doc);
                //String reviewPath = IbaUtil.getIBAStringValue(doc, "reviewPath");
                //IbaUtil.updateIbaValue("reviewPath", wp, reviewPath);
                boolean flag = SessionServerHelper.manager.setAccessEnforced(false);
                try {
                    // 将包设置为提交审阅状态
                   // LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) wp, State.toState("TIJIAOSHENYUE"),true);
                    // 将包里的对象设置为提交审阅状态
                    LifeCycleHelper.service.setLifeCycleState(doc,State.toState("TIJIAOSHENYUE"), true);
                    logger.debug("设置状态完成！");
                    formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(),
                            null, null, new String[] { "提交审阅成功！" }));
                } finally {
                    SessionServerHelper.manager.setAccessEnforced(flag);
                }

                formresult.setStatus(FormProcessingStatus.SUCCESS);
                formresult.setNextAction(FormResultAction.REFRESH_OPENER);
                formresult.setNextAction(FormResultAction.REFRESH_CURRENT_PAGE);

            } else {
                try {
                    LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) primobject,
                            State.toState("TIJIAOSHENYUE"), true);
                    formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.SUCCESS, SessionHelper.getLocale(),
                            null, null, new String[] { "提交审阅成功！" }));
                } catch (Exception e) {
                    e.printStackTrace();
                    formresult.addFeedbackMessage(new FeedbackMessage(FeedbackType.FAILURE, SessionHelper.getLocale(),
                            null, null, new String[] { "提交审阅失败！" + e.getMessage() }));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            SessionContext.setEffectivePrincipal(user);
        }

        return formresult;
    }
}
