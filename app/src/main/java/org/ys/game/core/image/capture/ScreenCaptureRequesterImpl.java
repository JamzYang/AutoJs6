package org.ys.game.core.image.capture;

import android.app.Activity;

import org.ys.game.AbstractAutoJs;
import org.ys.game.app.OnActivityResultDelegate;
import org.ys.game.util.ForegroundServiceUtils;

public class ScreenCaptureRequesterImpl extends ScreenCaptureRequester.AbstractScreenCaptureRequester {

    AbstractAutoJs mAutoJs;

    public ScreenCaptureRequesterImpl(AbstractAutoJs autojs) {
        super();
        mAutoJs = autojs;
    }

    @Override
    public void setOnActivityResultCallback(Callback callback) {
        super.setOnActivityResultCallback((result, data) -> {
            mResult = data;
            callback.onRequestResult(result, data);
        });
    }

    @Override
    public void request() {
        ForegroundServiceUtils.requestIfNeeded(mAutoJs.getApplicationContext(), ScreenCapturerForegroundService.class);

        Activity activity = mAutoJs.getAppUtils().getCurrentActivity();

        if (activity instanceof OnActivityResultDelegate.DelegateHost) {
            ScreenCaptureRequester requester = new ActivityScreenCaptureRequester(
                    ((OnActivityResultDelegate.DelegateHost) activity).getOnActivityResultDelegateMediator(), activity);
            requester.setOnActivityResultCallback(mCallback);
            requester.request();
        } else {
            ScreenCaptureRequestActivity.request(mAutoJs.getApplicationContext(), mCallback);
        }
    }
}
