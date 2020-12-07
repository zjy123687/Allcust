package com.wtutil.lifecycle;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.WTObject;
import wt.lifecycle.*;
import wt.method.RemoteAccess;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.lifecycle.LifeCycleException;


public class WTUtils   implements RemoteAccess {
    private static  final Logger logger = (Logger) LoggerFactory.getLogger(WTUtils.class );
    public static boolean setState(WTObject obj, String name) {
        try {
            State state = WTUtils .name2State(name);
            // logger.debug("state = " + state.toString() + "  " +
            // state.getDisplay());
            if(state != null){
                    LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) obj, state);
                    return true;
            }
        } catch (WTInvalidParameterException e) {
            logger.error("" + e);
        } catch (LifeCycleException e) {
            logger.error("" + e);
        } catch (WTException e) {
            logger.error("" + e);
        }
             return false;
    }
    public static State name2State(String name) {
       State[] states = State.getStateSet();
        wt.lifecycle.State defaultState = wt.lifecycle.State.getStateDefault();
        for (int i = 0; i < states.length; i++) {
            State state = states[i];
            // logger.debug(name + " = ? " + state.getDisplay());
            if (state.getDisplay().equalsIgnoreCase(name))
                return state;
        }
        if (defaultState != null)
            return defaultState;
        return states[0];
    }

}
