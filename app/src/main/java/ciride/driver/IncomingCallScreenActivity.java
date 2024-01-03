package ciride.driver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;

import com.general.files.AudioPlayer;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.general.files.SinchService;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;

import java.util.List;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private AudioPlayer mAudioPlayer;
    GeneralFunctions generalFunctions;
    //JSONObject userProfileJson;
    String passengerName = "";
    String passengerImage = "";
    SelectableRoundedImageView driverImageView;
    MTextView callState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.incoming);

        generalFunctions = MyApp.getInstance().getGeneralFun(this);
        if (generalFunctions.getMemberId().equals("")) {
            return;
        }
        //userProfileJson = generalFunctions.retrieveValue(Utils.USER_PROFILE_JSON);

        //JSONObject driverDetailJson = generalFunctions.getJsonObject("PassengerDetails", userProfileJson);
        // passengerImage = generalFunctions.getJsonValue("vImgName", driverDetailJson.toString());
        //passengerName = generalFunctions.getJsonValue("vName", driverDetailJson.toString());

        driverImageView = (SelectableRoundedImageView) findViewById(R.id.driverImageView);
        callState = (MTextView) findViewById(R.id.callState);
        callState.setText(generalFunctions.retrieveLangLBl("", "LBL_CALLING"));
        passengerImage = getIntent().getStringExtra("PImage");
        passengerName = getIntent().getStringExtra("Name");

        String type=getIntent().getStringExtra("type");

        if (type != null && type.equalsIgnoreCase("Passenger")) {
            if (passengerImage != null && !passengerImage.equals("")) {
                passengerImage = CommonUtilities.USER_PHOTO_PATH + getIntent().getStringExtra("Id") + "/" + passengerImage;
            }
        } else if (type != null && type.equalsIgnoreCase("Company")) {
            if (passengerImage != null && !passengerImage.equals("")) {
                passengerImage = CommonUtilities.COMPANY_PHOTO_PATH + getIntent().getStringExtra("Id") + "/" + passengerImage;

            }
        }


        driverImageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_no_pic_user));
        if (passengerImage != null && !passengerImage.equals("")) {
            Picasso.get().load(passengerImage).error(R.mipmap.ic_no_pic_user).into(driverImageView);
        }
        MButton answer = (MButton) findViewById(R.id.answerButton);
        answer.setText(generalFunctions.retrieveLangLBl("","LBL_ANSWER"));
        answer.setOnClickListener(mClickListener);
        MButton decline = (MButton) findViewById(R.id.declineButton);
        decline.setText(generalFunctions.retrieveLangLBl("","LBL_END_CALL"));
        decline.setOnClickListener(mClickListener);

        new CreateRoundedView(Color.parseColor("#d2494a"), 5, 0, 0, decline);
        new CreateRoundedView(Color.parseColor("#1a9574"), 5, 0, 0, answer);


        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    @Override
    protected void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            MTextView remoteUser = (MTextView) findViewById(R.id.remoteUser);
            // remoteUser.setText(call.getRemoteUserId());
            remoteUser.setText(passengerName != null ? passengerName : "");
        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private void answerClicked() {
        if (generalFunctions.isCallPermissionGranted(false) == false) {
            generalFunctions.isCallPermissionGranted(true);
            return;
        }

        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.answer();
            Intent intent = new Intent(this, CallScreenActivity.class);
            intent.putExtra(SinchService.CALL_ID, mCallId);
            intent.putExtra("vImage", passengerImage);
            intent.putExtra("vName", passengerName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        } else {
            finish();
        }
    }

    private void declineClicked() {
        mAudioPlayer.stopRingtone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int viewId = v.getId();

            if (viewId == R.id.answerButton) {
                if (!generalFunctions.isCallPermissionGranted(false)) {
                    generalFunctions.isCallPermissionGranted(true);
                    return;
                }
                answerClicked();
            } else if (viewId == R.id.declineButton) {
                declineClicked();
            }

        }
    };
}
