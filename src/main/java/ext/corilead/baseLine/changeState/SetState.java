package ext.corilead.baseLine.changeState;

import com.ptc.core.components.forms.FormResult;

import com.wtutil.lifecycle.WTUtils;
import wt.fc.WTObject;

public class SetState {
    public static FormResult doSetState(){
        WTObject wt = null;
        boolean f = WTUtils.setState(wt,"提交签审");

        return null;
    }
}
