package ciride.driver;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.general.files.AppFunctions;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.MyApp;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class WayBillActivity extends AppCompatActivity {

    MTextView titleTxt;
    ImageView backImgView;

    public GeneralFunctions generalFunc;

    MTextView TripHTxt, driverHTxt, TripnoHTxt, rateHTxt, TimeHTxt, passengerNameHTxt, viaHTxt,
            fromHTxt, toHTxt, drivernameHTxt, driverlicenseHTxt, licensePlateHTxt, passengercapHTxt, nodata;

    MTextView userNameTxt, TripvTxt, rateVTxt, TimeVTxt, passengerNameVTxt, viaVTxt, fromVTxt,
            toVTxt, drivernameVTxt, driverlicenseVTxt, licensePlateVTxt, passengercapVTxt;


    MTextView pkgTypeHTxt, pkgTypeVTxt, pkgDetailsHTxt, pkgDetailsVTxt, reciverHTxt, reciverVTxt;

    ProgressBar loading;

    LinearLayout senderCapArea, mainarea;
    ErrorView errorView;

    LinearLayout deliveryArea, reciverNameArea;

    /*Multi Related view*/
    private View convertView;
    LinearLayout multideliveryArea;

    ScrollView scrollview;
    View colorArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_way_bill);

        generalFunc = MyApp.getInstance().getGeneralFun(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new WayBillActivity.setOnClickList());
        errorView = (ErrorView) findViewById(R.id.errorView);


        scrollview = (ScrollView) findViewById(R.id.scrollview);
        colorArea = (View) findViewById(R.id.colorArea);

        mainarea = (LinearLayout) findViewById(R.id.mainarea);
        senderCapArea = (LinearLayout) findViewById(R.id.senderCapArea);
        reciverNameArea = (LinearLayout) findViewById(R.id.reciverNameArea);
        reciverHTxt = (MTextView) findViewById(R.id.reciverHTxt);
        reciverVTxt = (MTextView) findViewById(R.id.reciverVTxt);

        loading = (ProgressBar) findViewById(R.id.loading);
        TripHTxt = (MTextView) findViewById(R.id.TripHTxt);
        driverHTxt = (MTextView) findViewById(R.id.driverHTxt);
        TripnoHTxt = (MTextView) findViewById(R.id.TripnoHTxt);
        rateHTxt = (MTextView) findViewById(R.id.rateHTxt);
        TimeHTxt = (MTextView) findViewById(R.id.TimeHTxt);
        passengerNameHTxt = (MTextView) findViewById(R.id.passengerNameHTxt);
        viaHTxt = (MTextView) findViewById(R.id.viaHTxt);
        fromHTxt = (MTextView) findViewById(R.id.fromHTxt);
        toHTxt = (MTextView) findViewById(R.id.toHTxt);
        pkgTypeHTxt = (MTextView) findViewById(R.id.pkgTypeHTxt);
        pkgTypeVTxt = (MTextView) findViewById(R.id.pkgTypeVTxt);
        pkgDetailsHTxt = (MTextView) findViewById(R.id.pkgDetailsHTxt);
        pkgDetailsVTxt = (MTextView) findViewById(R.id.pkgDetailsVTxt);
        deliveryArea = (LinearLayout) findViewById(R.id.deliveryArea);
        multideliveryArea = (LinearLayout) findViewById(R.id.multideliveryArea);

        drivernameHTxt = (MTextView) findViewById(R.id.drivernameHTxt);
        driverlicenseHTxt = (MTextView) findViewById(R.id.driverlicenseHTxt);
        licensePlateHTxt = (MTextView) findViewById(R.id.licensePlateHTxt);
        passengercapHTxt = (MTextView) findViewById(R.id.passengercapHTxt);

        nodata = (MTextView) findViewById(R.id.nodata);


        userNameTxt = (MTextView) findViewById(R.id.userNameTxt);
        TripvTxt = (MTextView) findViewById(R.id.TripvTxt);
        rateVTxt = (MTextView) findViewById(R.id.rateVTxt);
        TimeVTxt = (MTextView) findViewById(R.id.TimeVTxt);
        passengerNameVTxt = (MTextView) findViewById(R.id.passengerNameVTxt);
        viaVTxt = (MTextView) findViewById(R.id.viaVTxt);
        fromVTxt = (MTextView) findViewById(R.id.fromVTxt);
        toVTxt = (MTextView) findViewById(R.id.toVTxt);
        drivernameVTxt = (MTextView) findViewById(R.id.drivernameVTxt);
        driverlicenseVTxt = (MTextView) findViewById(R.id.driverlicenseVTxt);
        licensePlateVTxt = (MTextView) findViewById(R.id.licensePlateVTxt);
        passengercapVTxt = (MTextView) findViewById(R.id.passengercapVTxt);
        if (getIntent().hasExtra("eSystem")) {
            findViewById(R.id.passengerNameArea).setVisibility(View.GONE);
        }
        setLabel();
        getWayBillDetails();
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    public void setLabel() {

        titleTxt.setText(generalFunc.retrieveLangLBl("Way Bill", "LBL_MENU_WAY_BILL"));

        if (getIntent().hasExtra("eSystem")) {
            TripHTxt.setText(generalFunc.retrieveLangLBl("Order Details", "LBL_ORDER_DETAIL_TXT"));
            TripnoHTxt.setText(generalFunc.retrieveLangLBl("Order No", "LBL_ORDER_NO_TXT"));
            fromHTxt.setText(WordUtils.capitalize(generalFunc.retrieveLangLBl("Store Location", "LBL_STORE_LOCATION").toLowerCase()));
            toHTxt.setText(WordUtils.capitalize(generalFunc.retrieveLangLBl("Delivery Location", "LBL_DELIVERY_LOCATION_TXT").toLowerCase()));

        } else {
            TripHTxt.setText(generalFunc.retrieveLangLBl("Trip", "LBL_TRIP_DETAILS_TXT"));
            TripnoHTxt.setText(generalFunc.retrieveLangLBl("Trip", "LBL_TRIP_TXT") + "# ");
            fromHTxt.setText(generalFunc.retrieveLangLBl("From", "LBL_From"));
            toHTxt.setText(generalFunc.retrieveLangLBl("To", "LBL_To"));
        }

        ((MTextView) findViewById(R.id.deliveryHTxt)).setText(generalFunc.retrieveLangLBl("", "LBL_RIDE_SHIPMENT_DETAIL_TXT"));
        TimeHTxt.setText(generalFunc.retrieveLangLBl("Time", "LBL_TIME_TXT"));
        rateHTxt.setText(generalFunc.retrieveLangLBl("Rate", "LBL_RATE"));
        passengerNameHTxt.setText(generalFunc.retrieveLangLBl("Passenger Name", "LBL_PASSENGER_NAME_TEXT"));
        viaHTxt.setText(generalFunc.retrieveLangLBl("via", "LBL_VIA_TXT"));

        driverHTxt.setText(generalFunc.retrieveLangLBl("Driver", "LBL_DIVER"));
        drivernameHTxt.setText(generalFunc.retrieveLangLBl("Name", "LBL_NAME_TXT"));
        driverlicenseHTxt.setText(generalFunc.retrieveLangLBl("Driver Licence", "LBL_DRIVER_LICENCE") );
        licensePlateHTxt.setText(generalFunc.retrieveLangLBl("Licence Plate", "LBL_LICENCE_PLATE_TXT"));
        passengercapHTxt.setText(generalFunc.retrieveLangLBl("Passenger", "LBL_PASSENGER_TXT") + " " +
                generalFunc.retrieveLangLBl("Capacity", "LBL_CAPACITY"));


    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "LBL_ERROR_TXT", "LBL_NO_INTERNET_TXT");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(() -> getWayBillDetails());
    }

    public void getWayBillDetails() {

        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }
        final HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "displayWayBill");
        parameters.put("iDriverId", generalFunc.getMemberId());

        if (getIntent().hasExtra("eSystem")) {
            parameters.put("eSystem", Utils.eSystem_Type);
        }
        if (getIntent().hasExtra("iOrderId")) {
            parameters.put("iOrderId", getIntent().getStringExtra("iOrderId"));
        }
        if (getIntent().hasExtra("tripId")) {
            parameters.put("tripId", getIntent().getStringExtra("tripId"));
        }

        final ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(getActContext(), parameters);
        exeWebServer.setDataResponseListener(responseString -> {

            JSONObject responseStringObj=generalFunc.getJsonObject(responseString);
            if (responseStringObj != null && !responseStringObj.equals("")) {
                closeLoader();

                boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseStringObj);
                String message = generalFunc.getJsonValueStr(Utils.message_str, responseStringObj);


                if (isDataAvail) {
                    mainarea.setVisibility(View.VISIBLE);
                    findViewById(R.id.colorArea).setVisibility(View.VISIBLE);
                    setData(message);


                } else {
                    mainarea.setVisibility(View.GONE);
                   findViewById(R.id.colorArea).setVisibility(View.GONE);
                    generalFunc.showGeneralMessage("",generalFunc.retrieveLangLBl("No Record Found", message), buttonId -> onBackPressed());


                    nodata.setText(generalFunc.retrieveLangLBl("No Record Found", message));

                }


            } else {
                closeLoader();
                generateErrorView();
            }

            scrollview.setVisibility(View.VISIBLE);
            colorArea.setVisibility(View.VISIBLE);


        });
        exeWebServer.execute();
    }

    public void setData(String data) {

        if (getIntent().hasExtra("eSystem")) {
            TripvTxt.setText("#"+generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vOrderNo", data)));
//        TimeVTxt.setText(generalFunc.getJsonValue("tTripRequestDate", data));
            TimeVTxt.setText(generalFunc.getJsonValue("tOrderRequestDate", data));

            TimeVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(generalFunc.getJsonValue("tOrderRequestDate_Org", data), Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext()))));
        } else {
            TripvTxt.setText("#"+generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("vRideNo", data)));
//        TimeVTxt.setText(generalFunc.getJsonValue("tTripRequestDate", data));
            TimeVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getDateFormatedType(generalFunc.getJsonValue("tTripRequestDate", data), Utils.OriginalDateFormate, Utils.getDetailDateFormat(getActContext()))));

        }
        userNameTxt.setText(generalFunc.getJsonValue("DriverName", data));

        rateVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("Rate", data)));
        passengerNameVTxt.setText(generalFunc.getJsonValue("PassengerName", data));
        viaVTxt.setText(generalFunc.getJsonValue("ProjectName", data));

        if (!generalFunc.getJsonValue("tDaddress", data).equalsIgnoreCase("")) {
            toVTxt.setText(generalFunc.getJsonValue("tDaddress", data));
        } else {
            toVTxt.setText("--");
        }

        Log.d("eType", "setData: "+generalFunc.getJsonValue("eType", data));

        if (generalFunc.getJsonValue("eType", data).equalsIgnoreCase(Utils.eType_Multi_Delivery)
                || generalFunc.getJsonValue("eType", data).equalsIgnoreCase(Utils.CabGeneralType_Deliver) ||
                generalFunc.getJsonValue("eType", data).equalsIgnoreCase("deliver")) {
            TripHTxt.setText(generalFunc.retrieveLangLBl("", "LBL_DELIVERY"));
            TripnoHTxt.setText(generalFunc.retrieveLangLBl("Trip", "LBL_DELIVERY") + "# ");
            driverHTxt.setText(generalFunc.retrieveLangLBl("Driver", "LBL_CARRIER"));
        }


        fromVTxt.setText(generalFunc.getJsonValue("tSaddress", data));
        drivernameVTxt.setText(generalFunc.getJsonValue("DriverName", data));
        licensePlateVTxt.setText("#"+generalFunc.getJsonValue("Licence_Plate", data));
        //  driverlicenseVTxt.setText(generalFunc.getJsonValue("Driving_licence",data));
        passengercapVTxt.setText(generalFunc.convertNumberWithRTL(generalFunc.getJsonValue("PassengerCapacity", data)));

        if (getIntent().hasExtra("eSystem")) {
            senderCapArea.setVisibility(View.GONE);
            findViewById(R.id.rateArea).setVisibility(View.GONE);
        }

        String eType = generalFunc.getJsonValue("eType", data);

        if (eType.equalsIgnoreCase("Delivery") || eType.equalsIgnoreCase("Deliver")) {

            passengerNameHTxt.setText(generalFunc.retrieveLangLBl("Sender Name", "LBL_SENDER_NAME"));
            toHTxt.setText(generalFunc.retrieveLangLBl("Receiver's Location", "LBL_RECEIVER_LOCATION"));
            pkgDetailsHTxt.setText(generalFunc.retrieveLangLBl("package Details", "LBL_PACKAGE_DETAILS"));
            pkgTypeHTxt.setText(generalFunc.retrieveLangLBl("Package Type", "LBL_PACKAGE_TYPE"));
            pkgDetailsVTxt.setText(generalFunc.getJsonValue("tPackageDetails", data));
            pkgTypeVTxt.setText(generalFunc.getJsonValue("PackageName", data));
            reciverVTxt.setText(generalFunc.getJsonValue("vReceiverName", data));
            reciverHTxt.setText(generalFunc.retrieveLangLBl("Receiver Name", "LBL_RECEIVER_NAME"));
            reciverNameArea.setVisibility(View.VISIBLE);
            senderCapArea.setVisibility(View.GONE);
            deliveryArea.setVisibility(View.VISIBLE);
        } else if (eType.equalsIgnoreCase(Utils.eType_Multi_Delivery)) {
            passengerNameHTxt.setText(generalFunc.retrieveLangLBl("Sender Name", "LBL_SENDER_NAME"));

            findViewById(R.id.fromAddArea).setVisibility(View.GONE);
            findViewById(R.id.rateArea).setVisibility(View.GONE);
            findViewById(R.id.toAddArea).setVisibility(View.GONE);
            findViewById(R.id.multiDeliveryDetailArea).setVisibility(View.VISIBLE);

            senderCapArea.setVisibility(View.GONE);
            deliveryArea.setVisibility(View.GONE);
            JSONArray deliveriesArray = generalFunc.getJsonArray("Deliveries", data);

            if (deliveriesArray != null && deliveriesArray.length() > 0) {

                String LBL_To=generalFunc.retrieveLangLBl("To", "LBL_To");
                String LBL_From=generalFunc.retrieveLangLBl("From", "LBL_From");
                for (int i = 0; i < deliveriesArray.length(); i++) {

                    JSONArray deliveriesArray1 = generalFunc.getJsonArray(deliveriesArray, i);



                    if (deliveriesArray1 != null && deliveriesArray1.length() > 0) {


                        for (int j = 0; j < deliveriesArray1.length(); j++) {
                            {
                                JSONObject jobject1 = generalFunc.getJsonObject(deliveriesArray1, j);

                                String vValue=generalFunc.getJsonValueStr("vValue", jobject1);
                                String vFieldName=generalFunc.getJsonValueStr("vFieldName", jobject1);





                            }


                        }


                    }

                }

                setMultiData();
            }
        }


        if (eType.equalsIgnoreCase(Utils.CabGeneralType_UberX)) {
            toVTxt.setText("--");
            passengercapVTxt.setText("--");
            licensePlateVTxt.setText("--");
        }
    }

    private void setMultiData() {
        multideliveryArea.setVisibility(View.VISIBLE);

        if (multideliveryArea.getChildCount() > 0) {
            multideliveryArea.removeAllViewsInLayout();
        }

    }


    public Context getActContext() {
        return WayBillActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            Utils.hideKeyboard(WayBillActivity.this);
            switch (view.getId()) {
                case R.id.backImgView:
                    onBackPressed();
                    break;

            }
        }
    }
}
