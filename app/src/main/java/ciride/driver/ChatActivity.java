package ciride.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.adapter.files.ChatMessagesRecycleAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;
import com.utils.CommonUtilities;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.SelectableRoundedImageView;
import com.view.simpleratingbar.SimpleRatingBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    Context mContext;
    GeneralFunctions generalFunc;
    String isFrom = "";
    EditText input;
    MTextView userNameTxt, catTypeText;

    private ChatMessagesRecycleAdapter chatAdapter;
    private ArrayList<HashMap<String, Object>> chatList;
    private int count = 0;
    ProgressBar LoadingProgressBar;
    HashMap<String, String> data_trip_ada;
    GenerateAlertBox generateAlert;
    String userProfileJson;
    String driverImgName = "";

    MTextView tv_booking_no;
    ImageView msgbtn;
    SelectableRoundedImageView userImgView;
    LinearLayout mainArea;
    ProgressBar progressBar;
    SimpleRatingBar ratingBar;

    private FirebaseFirestore db;
    private CollectionReference dbCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_trip_chat_detail_dialog);
        mContext = ChatActivity.this;

        db = FirebaseFirestore.getInstance();

        dbCourses = db.collection("Chat");

        generalFunc = MyApp.getInstance().getGeneralFun(ChatActivity.this);
        getDetails();

        userProfileJson = generalFunc.retrieveValue(Utils.USER_PROFILE_JSON);
        driverImgName = generalFunc.getJsonValue("vImage", userProfileJson);

        data_trip_ada = new HashMap<>();
        data_trip_ada.put("iTripId", getIntent().getStringExtra("iTripId"));

        initViews();

        chatList = new ArrayList<>();
        count = 0;

        show();

    }

    public void getDetails() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getMemberTripDetails");
        parameters.put("UserType", Utils.userType);
        parameters.put("iTripId", getIntent().getStringExtra("iTripId"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
            if (isDataAvail == true) {
                mainArea.setVisibility(View.VISIBLE);

                String message = generalFunc.getJsonValue(Utils.message_str, responseString);
                userNameTxt.setText(generalFunc.getJsonValue("vName", message));
                catTypeText.setText(generalFunc.getJsonValue("vServiceName", message));
                Picasso.get().load(generalFunc.getJsonValue("vImage", message)).placeholder(R.mipmap.ic_no_pic_user).error(R.mipmap.ic_no_pic_user).into(userImgView);

                ratingBar.setRating(GeneralFunctions.parseFloatValue(0, generalFunc.getJsonValue("vAvgRating", message)));

                ((MTextView) findViewById(R.id.titleTxt)).setText("#" + generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", message)));

                ((MTextView) findViewById(R.id.chatsubtitleTxt)).setVisibility(View.VISIBLE);
                ((MTextView) findViewById(R.id.chatsubtitleTxt)).setText(generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(generalFunc.getJsonValue("tTripRequestDate", message), Utils.OriginalDateFormate, CommonUtilities.OriginalDateFormate)));

                data_trip_ada.put("iFromMemberId",generalFunc.getJsonValue("iMemberId", message));
                data_trip_ada.put("FromMemberImageName", generalFunc.getJsonValue("vImage", message));
                data_trip_ada.put("FromMemberName", generalFunc.getJsonValue("vName", message));
                data_trip_ada.put("vBookingNo",generalFunc.getJsonValue("vRideNo",message));
                data_trip_ada.put("vDate", generalFunc.getJsonValue("tTripRequestDate", message));

            }


        });
        exeWebServer.execute();
    }

    private void initViews() {
        tv_booking_no = (MTextView) findViewById(R.id.tv_booking_no);
        input = (EditText) findViewById(R.id.input);
        msgbtn = (ImageView) findViewById(R.id.msgbtn);
        userImgView = (SelectableRoundedImageView) findViewById(R.id.userImgView);
        mainArea = (LinearLayout) findViewById(R.id.mainArea);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mainArea.setVisibility(View.GONE);
        userNameTxt = (MTextView) findViewById(R.id.userNameTxt);
        catTypeText = (MTextView) findViewById(R.id.catTypeText);
        ratingBar = (SimpleRatingBar) findViewById(R.id.ratingBar);
        //catTypeText.setVisibility(View.GONE);


        ratingBar.setRating(GeneralFunctions.parseFloatValue(0, getIntent().getStringExtra("vAvgRating")));


        //((MTextView) findViewById(R.id.titleTxt)).setText("#" + vBookingNo);


    }

    public void tripCancelled(String msg) {

        if (generateAlert != null) {
            generateAlert.closeAlertBox();
        }
        generateAlert = new GenerateAlertBox(ChatActivity.this);

        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(btn_id -> {
            generateAlert.closeAlertBox();
            generalFunc.saveGoOnlineInfo();
            generalFunc.restartApp();
            // MyApp.getInstance().restartWithGetDataApp();
        });
        generateAlert.setContentMessage("", msg);
        generateAlert.setPositiveBtn(generalFunc.retrieveLangLBl("", "LBL_BTN_OK_TXT"));


        generateAlert.showAlertBox();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void show() {

        input.setHint(generalFunc.retrieveLangLBl("Enter a message", "LBL_ENTER_MSG_TXT"));

        msgbtn.setImageResource(R.drawable.ic_chat_send_disable);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    // msgbtn.setColorFilter(ContextCompat.getColor(getActContext(), R.color.lightchatbtncolor), android.graphics.PorterDuff.Mode.SRC_IN);
                    msgbtn.setImageResource(R.drawable.ic_chat_send_disable);
                } else {
                    // msgbtn.setColorFilter(null);
                    msgbtn.setImageResource(R.drawable.ic_chat_send);
                }


            }
        });

        input.setHint(generalFunc.retrieveLangLBl("Enter a message", "LBL_ENTER_MESSAGE"));

        (findViewById(R.id.backImgView)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.hideKeyboard(ChatActivity.this);
                onBackPressed();

            }
        });

        msgbtn.setOnClickListener(view -> {

            if (Utils.checkText(input) && Utils.getText(input).length() > 0) {

                HashMap<String, Object> dataMap = new HashMap<String, Object>();
                dataMap.put("eUserType", Utils.app_type);
                dataMap.put("Text", input.getText().toString().trim());
                dataMap.put("iTripId", data_trip_ada.get("iTripId"));
                dataMap.put("driverImageName", driverImgName);
                dataMap.put("passengerImageName", data_trip_ada.get("FromMemberImageName"));
                dataMap.put("driverId", generalFunc.getMemberId());
                dataMap.put("passengerId", data_trip_ada.get("iFromMemberId"));
                dataMap.put("vDate", generalFunc.getCurrentDateHourMin());
                Timestamp currentTimeStamp = Timestamp.now();
                dataMap.put("timeStamp", currentTimeStamp);
                dataMap.put("vTimeZone", generalFunc.getTimezone());
                sendTripMessageNotification(input.getText().toString().trim());
                input.setText("");

                dbCourses.document().set(dataMap)
                        .addOnSuccessListener(unused -> {})
                        .addOnFailureListener(e -> {});
            }

        });

        final RecyclerView chatCategoryRecyclerView = (RecyclerView) findViewById(R.id.chatCategoryRecyclerView);


        chatAdapter = new ChatMessagesRecycleAdapter(mContext, chatList, generalFunc, data_trip_ada);
        chatCategoryRecyclerView.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();


        Query query = dbCourses.whereEqualTo("iTripId", data_trip_ada.get("iTripId")).orderBy("timeStamp", Query.Direction.ASCENDING);

        query.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = documentChange.getDocument();
                HashMap<String, Object> dataMap = (HashMap<String, Object>) documentSnapshot.getData();
                chatList.add(dataMap);
                chatAdapter.notifyDataSetChanged();
                chatCategoryRecyclerView.smoothScrollToPosition(chatList.size());
            }
        });

        Query query1 = dbCourses.whereEqualTo("iTripId", data_trip_ada.get("iTripId"));

        query1.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                return;
            }
            for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                DocumentSnapshot documentSnapshot = documentChange.getDocument();
                HashMap<String, Object> dataMap = (HashMap<String, Object>) documentSnapshot.getData();
                if (!chatList.contains(dataMap)){
                    chatList.add(dataMap);
                    chatAdapter.notifyDataSetChanged();
                    chatCategoryRecyclerView.smoothScrollToPosition(chatList.size());
                }
            }
        });

    }


    public void sendTripMessageNotification(String message) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "SendTripMessageNotification");
        parameters.put("UserType", Utils.userType);
        parameters.put("iFromMemberId", generalFunc.getMemberId());
        parameters.put("iTripId", data_trip_ada.get("iTripId"));
        parameters.put("iToMemberId", data_trip_ada.get("iFromMemberId"));
        parameters.put("tMessage", message);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(mContext, parameters);
        exeWebServer.setLoaderConfig(mContext, false, generalFunc);
        exeWebServer.setDataResponseListener(responseString -> {
           String  setDataRes = responseString;
        });
        exeWebServer.execute();
    }

    public void setCurrentTripData(Bundle bn) {

        String iTripId = data_trip_ada != null && data_trip_ada.containsKey("iTripId") ? data_trip_ada.get("iTripId") : "";

        String iTripIdNoti=bn != null ? bn.get("iTripId").toString().trim():"";
        String iTripIdCurrent=iTripId != null ? iTripId.trim():"";
//        Log.d("CHAT_NOTI","iTripIdNoti"+iTripIdNoti);
//        Log.d("CHAT_NOTI","iTripIdCurrent"+iTripIdCurrent);
        if (Utils.checkText(iTripId) && Utils.checkText(iTripIdCurrent)&& iTripIdCurrent.equals(iTripIdNoti)) {

            Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
            intent.putExtras(bn);
            startActivity(intent);
            ChatActivity.this.finish();
        }
    }
}
