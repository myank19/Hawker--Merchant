package io.goolean.tech.hawker.merchant.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.goolean.tech.hawker.merchant.Constant.StringConstant;
import io.goolean.tech.hawker.merchant.Constant.Urls;
import io.goolean.tech.hawker.merchant.Lib.FancyToast.FancyToast;
import io.goolean.tech.hawker.merchant.Lib.ImageCompression.imageCompression.ImageCompressionListener;
import io.goolean.tech.hawker.merchant.Lib.ImageCompression.imagePicker.ImagePicker;
import io.goolean.tech.hawker.merchant.Networking.VolleySingleton;
import io.goolean.tech.hawker.merchant.R;
import io.goolean.tech.hawker.merchant.Storage.SharedPrefrence_Login;

public class RequestAdvertisementActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_one, img_two, img_three, img_four, img_banner;
    private TextView tvModifyBannerImage, tv_banner;
    private Button btnStartDate, btnEndDate, bt_Camera;
    private String clcikImage = "";
    private String img="";
    private String img2="";
    private String img4="";
    private String img3="";
    private String ban="";


    private ImagePicker imagePicker;
    private String str_img1 = "", str_img2 = "", str_img3 = "", str_img4 = "", str_banner = "";
    private Button btnSubmit;
    private RequestQueue requestQueue;
    private ProgressDialog _progressDialog;
    private EditText et_title, et_description, et_address;
    CardView cvPaid, cvBanner;
    RadioGroup rgChoice;
    RadioButton rbfree, rbPaid, rbYes, rbNo;
    Date currentDate, selectedDateFrom, strFinalStartDate;
    SimpleDateFormat dateFormat, dateFormat_two;
    String finalStringDate, strStartDate = "", strEndDate = "";
    private DatePickerDialog dialogfrom = null;
    private DatePickerDialog dialogEnd = null;
    Calendar calendar;
    private int iyear, day;
    int currentmonth;
    String currentDateString, iSubHawkerNamePosition;
    protected static final int GALLERY_PICTURE = 1;
    private Intent pictureActionIntent = null;
    Bitmap bitmap;
    String selectedImagePath;
    double lat, lng;
    String text="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_advertisement);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        SharedPrefrence_Login.getDataLogin(RequestAdvertisementActivity.this);

        initId();
    }

    private void initId() {
        img_one = findViewById(R.id.img_one);
        img_one.setOnClickListener(this);
        img_two = findViewById(R.id.img_two);
        img_two.setOnClickListener(this);
        img_three = findViewById(R.id.img_three);
        img_three.setOnClickListener(this);
        img_four = findViewById(R.id.img_four);
        img_four.setOnClickListener(this);
        img_banner = findViewById(R.id.img_banner);
        img_banner.setOnClickListener(this);

        tvModifyBannerImage = findViewById(R.id.tvModifyBannerImage);
        tv_banner = findViewById(R.id.tv_banner);

        btnStartDate = findViewById(R.id.btnStartDate);
        btnEndDate = findViewById(R.id.btnEndDate);

        cvPaid = findViewById(R.id.cvPaid);
        cvBanner = findViewById(R.id.cvBanner);

        rbfree = findViewById(R.id.rbfree);
        rbfree.setOnClickListener(this);
        rbPaid = findViewById(R.id.rbPaid);
        rbPaid.setOnClickListener(this);

        rgChoice = findViewById(R.id.rgChoice);
        rbYes = findViewById(R.id.rbYes);
        rbYes.setOnClickListener(this);
        rbNo = findViewById(R.id.rbNo);
        rbNo.setOnClickListener(this);

        et_title = findViewById(R.id.et_title);
        et_description = findViewById(R.id.et_description);
        et_address = findViewById(R.id.et_address);

        btnSubmit = findViewById(R.id.btnSubmit);
        bt_Camera = findViewById(R.id.bt_Camera);
        funDateIntialize();
        bt_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runtimePermission();
            }
        });

        rgChoice.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton selectedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                text = selectedRadioButton.getText().toString();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(et_title.getText().toString())) {
                    Toast.makeText(RequestAdvertisementActivity.this, "Enter your title", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(et_description.getText().toString())) {
                    Toast.makeText(RequestAdvertisementActivity.this, "Enter your discription", Toast.LENGTH_SHORT).show();
                }/* else if (TextUtils.isEmpty(et_address.getText().toString())) {
                    Toast.makeText(RequestAdvertisementActivity.this, "Enter your Address", Toast.LENGTH_SHORT).show();
                } */ else if (TextUtils.isEmpty(strStartDate)) {
                    Toast.makeText(RequestAdvertisementActivity.this, "Select Start Date", Toast.LENGTH_SHORT).show();
                } /*else if (TextUtils.isEmpty(strEndDate)) {
                    Toast.makeText(RequestAdvertisementActivity.this, "Select End Date", Toast.LENGTH_SHORT).show();
                } */ else if (TextUtils.isEmpty(str_banner)&&rbPaid.isChecked()&&TextUtils.isEmpty(str_img1)) {
                    Toast.makeText(RequestAdvertisementActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (rbPaid.isChecked()) {
                        if(TextUtils.isEmpty(str_banner)||TextUtils.isEmpty(img))
                        {
                            Toast.makeText(RequestAdvertisementActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                        }

                        else {
//                        getLocationFromAddress(RequestAdvertisementActivity.this, et_address.getText().toString());
                            funserverrequest_Paid(SharedPrefrence_Login.getMhawker_code(), SharedPrefrence_Login.getPnumber(), et_title.getText().toString(),
                                    et_description.getText().toString(), strStartDate, img, img2, img3, img4, ban);
                        }
                    } else {

                        if(TextUtils.isEmpty(img))
                        {
                            Toast.makeText(RequestAdvertisementActivity.this, "Select Image", Toast.LENGTH_SHORT).show();
                        }
                        else {
//                        getLocationFromAddress(RequestAdvertisementActivity.this, et_address.getText().toString());
                            funserverrequest(SharedPrefrence_Login.getMhawker_code(), SharedPrefrence_Login.getPnumber(), et_title.getText().toString(),
                                    et_description.getText().toString(), strStartDate, img, img2);
                        }

                    }
                }
            }
        });
        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funStartDate(v);
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funEndDate(v);
            }
        });
    }

    //check here
    private void openBottemsheet() {
        final BottomSheetDialog dialog = new BottomSheetDialog(RequestAdvertisementActivity.this);
        dialog.setContentView(R.layout.layout_select_image);
        ImageView img_cancel_id = dialog.findViewById(R.id.img_cancel_id);
        Button btn_Gallery = dialog.findViewById(R.id.btn_Gallery);
        Button btn_Camera = dialog.findViewById(R.id.btn_Camera);

        img_cancel_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_Gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (str_img1 == "one") {
                    dialog.dismiss();
                    selectGallery("one");
                } else if (str_img2 == "two") {
                    dialog.dismiss();
                    selectGallery("two");
                } else if (str_img3 == "three") {
                    dialog.dismiss();
                    selectGallery("three");
                } else if (str_img4 == "four") {
                    dialog.dismiss();
                    selectGallery("four");
                } else if (str_banner == "banner") {
                    dialog.dismiss();
                    selectGallery("banner");
                }

            }
        });
        btn_Camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (str_img1 == "one") {
                    dialog.dismiss();
                    clickImage("one");
                } else if (str_img2 == "two") {
                    dialog.dismiss();
                    clickImage("two");
                } else if (str_img3 == "three") {
                    dialog.dismiss();
                    clickImage("three");
                } else if (str_img4 == "four") {
                    dialog.dismiss();
                    clickImage("four");
                } else if (str_banner == "banner") {
                    dialog.dismiss();
                    clickImage("banner");
                }
            }
        });
        dialog.show();
    }

    private void selectGallery(String str) {
        clcikImage = str;
        Intent pictureActionIntent = null;
        pictureActionIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pictureActionIntent, GALLERY_PICTURE);
    }

    public void clickBack(View view) {
        finish();
    }

    private void funDateIntialize() {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat_two = new SimpleDateFormat("dd/MMM/yyyy");
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        currentmonth = calendar.get(Calendar.MONTH);
        iyear = calendar.get(Calendar.YEAR);
        currentmonth = currentmonth + 1;
        currentDateString = day + "/" + currentmonth + "/" + iyear;
        finalStringDate = currentDateString;
        try {
            currentDate = dateFormat.parse(currentDateString);
            finalStringDate = dateFormat_two.format(currentDate);
            btnStartDate.setText(finalStringDate);
            btnEndDate.setText(finalStringDate);
            strStartDate = finalStringDate;
            strEndDate = finalStringDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void funStartDate(View v) {
        if (dialogfrom == null)
            dialogfrom = new DatePickerDialog(v.getContext(), new PickDate(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH) + 1);
        dialogfrom.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) + 1);
        // dialogfrom.getDatePicker().setMinDate(System.currentTimeMillis());
        dialogfrom.show();

    }

    private void funEndDate(View v) {
        if (dialogEnd == null)
            dialogEnd = new DatePickerDialog(v.getContext(), new PickEndDate(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH));
        dialogEnd.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        //   dialogEnd.getDatePicker().setMinDate(System.currentTimeMillis());
        dialogEnd.show();
    }

    @Override
    public void onClick(View view) {
        if (view == rbPaid) {
            cvPaid.setVisibility(View.VISIBLE);
            cvBanner.setVisibility(View.VISIBLE);
            tvModifyBannerImage.setVisibility(View.VISIBLE);
            rgChoice.setVisibility(View.VISIBLE);
        } else if (view == rbfree) {
            cvPaid.setVisibility(View.GONE);
            cvBanner.setVisibility(View.GONE);
            tvModifyBannerImage.setVisibility(View.GONE);
            rgChoice.setVisibility(View.GONE);
        } else if (view == img_one) {
            str_img1 = "one";
            str_img2 = "";
            str_img3 = "";
            str_img4 = "";
            bt_Camera.setVisibility(View.VISIBLE);
        } else if (view == img_two) {
            str_img1 = "";
            str_img3 = "";
            str_img4 = "";
            str_img2 = "two";
            bt_Camera.setVisibility(View.VISIBLE);
        } else if (view == img_three) {
            str_img3 = "three";
            str_img1 = "";
            str_img2 = "";
            str_img4 = "";

            bt_Camera.setVisibility(View.VISIBLE);
        } else if (view == img_four) {
            str_img4 = "four";
            str_img3 = "";
            str_img1 = "";
            str_img2 = "";
            bt_Camera.setVisibility(View.VISIBLE);
        } else if (view == img_banner) {
            str_img4 = "";
            str_img3 = "";
            str_img1 = "";
            str_img2 = "";
            str_banner = "banner";
            //Toast.makeText(getApplicationContext(),str_banner,Toast.LENGTH_LONG).show();
            selectGallery("banner");
            //bt_Camera.setVisibility(View.VISIBLE);
        }
    }

    private class PickDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;
            String dateString = dayOfMonth + "/" + monthOfYear + "/" + year;
            try {
                selectedDateFrom = dateFormat.parse(dateString);

                strFinalStartDate = selectedDateFrom;
                if (selectedDateFrom.compareTo(currentDate) >= 0) {
                    finalStringDate = dateString;
                    /*  dateString = 10/8/2019  */
                    finalStringDate = dateFormat_two.format(selectedDateFrom);
                    btnStartDate.setText(finalStringDate);
                    strStartDate = finalStringDate;
                    btnEndDate.setText("");
                    btnEndDate.setHintTextColor(Color.parseColor("#949494"));
                    btnEndDate.setHint("00/MMM/0000");
                } else {
                    FancyToast.makeText(getApplicationContext(), "You can't select Today or Previous Date.", FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class PickEndDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear = monthOfYear + 1;
            String dateString = dayOfMonth + "/" + monthOfYear + "/" + year;
            try {
                selectedDateFrom = dateFormat.parse(dateString);
                if (selectedDateFrom.compareTo(strFinalStartDate) >= 0) {
                    finalStringDate = dateString;
                    /*dateString = 10/8/2019*/
                    finalStringDate = dateFormat_two.format(selectedDateFrom);
                    btnEndDate.setText(finalStringDate);
                    strEndDate = finalStringDate;
                } else {
                    btnEndDate.setText("");
                    btnEndDate.setHintTextColor(Color.parseColor("#949494"));
                    btnEndDate.setHint("00/MMM/0000");
                    FancyToast.makeText(getApplicationContext(), "Selected date can not be smaller than current date", FancyToast.LENGTH_LONG, FancyToast.WARNING, true).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void runtimePermission() {
        Dexter.withActivity(RequestAdvertisementActivity.this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            openBottemsheet();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RequestAdvertisementActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void clickImage(String sclcikImage) {
        clcikImage = sclcikImage;
        imagePicker = new ImagePicker().chooseFromCamera(true);
        imagePicker.withActivity(this).withCompression(true).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 101) {
            if (resultCode == 0) {
                finish();
            }
        } else if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "GPS ON", Toast.LENGTH_SHORT).show();
                //location_update = new Location_Update(MainActivity.this);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (requestCode == ImagePicker.SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                imagePicker.addOnCompressListener(new ImageCompressionListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onCompressed(String filePath) {
                        Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                        if (clcikImage.equals("one")) {
                            img_one.setImageBitmap(selectedImage);
                        } else if (clcikImage.equals("two")) {
                            img_two.setImageBitmap(selectedImage);
                        } else if (clcikImage.equals("three")) {
                            img_three.setImageBitmap(selectedImage);
                        } else if (clcikImage.equals("four")) {
                            img_four.setImageBitmap(selectedImage);
                        } else if (clcikImage.equals("banner")) {
                            img_banner.setImageBitmap(selectedImage);
                            tv_banner.setVisibility(View.GONE);
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();
                        if (clcikImage.equals("one")) {
                            str_img1 = Base64.encodeToString(b, Base64.DEFAULT);
                        } else if (clcikImage.equals("two")) {
                            str_img2 = Base64.encodeToString(b, Base64.DEFAULT);
                        } else if (clcikImage.equals("three")) {
                            str_img3 = Base64.encodeToString(b, Base64.DEFAULT);
                        } else if (clcikImage.equals("four")) {
                            str_img4 = Base64.encodeToString(b, Base64.DEFAULT);
                        } else if (clcikImage.equals("banner")) {
                            str_banner = Base64.encodeToString(b, Base64.DEFAULT);
                        }
                    }
                });

                String filePath = imagePicker.getImageFilePath(data);
                if (filePath != null) {
                    Bitmap selectedImage = BitmapFactory.decodeFile(filePath);
                    img_one.setImageBitmap(selectedImage);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] b = baos.toByteArray();
                    str_img1 = Base64.encodeToString(b, Base64.DEFAULT);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {

                Uri selectedImage = data.getData();

                //String path =  selectedImage.getPath();
//                Bitmap bitmap = null;
//                String[] filePath = {MediaStore.Images.Media.DATA};
//                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                selectedImagePath = c.getString(columnIndex);
//                c.close();
                selectedImagePath = selectedImage.toString();

                //String base= encodeImage(selectedImagePath);

                //Toast.makeText(getApplicationContext(),base,Toast.LENGTH_LONG).show();


//                if (selectedImagePath != null) {
//                    Log.i("Image_Path", selectedImagePath + "");
//                     bitmap= getBitmap(selectedImagePath);  // load
//                }

                // preview image

                //bitmap = getBitmapFromLocalPath(selectedImagePath,1);

                //bitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false);


                if (clcikImage.equals("one")) {
                    Glide.with(this)
                            .load(selectedImagePath)
                            .into(img_one);
                    //img_one.setImageBitmap(bitmap);
                    bt_Camera.setVisibility(View.GONE);
                } else if (clcikImage.equals("two")) {
                    Glide.with(this)
                            .load(selectedImagePath)
                            .into(img_two);
                    //img_two.setImageBitmap(bitmap);
                    bt_Camera.setVisibility(View.GONE);
                } else if (clcikImage.equals("three")) {
                    Glide.with(this)
                            .load(selectedImagePath)
                            .into(img_three);
                    //img_three.setImageBitmap(bitmap);
                    bt_Camera.setVisibility(View.GONE);
                } else if (clcikImage.equals("four")) {
                    Glide.with(this)
                            .load(selectedImagePath)
                            .into(img_four);
                    //img_four.setImageBitmap(bitmap);
                    bt_Camera.setVisibility(View.GONE);
                } else if (clcikImage.equals("banner")) {
                    //img_banner.setImageBitmap(bitmap);
                    Glide.with(this)
                            .load(selectedImagePath)
                            .into(img_banner);
                    tv_banner.setVisibility(View.GONE);
                    bt_Camera.setVisibility(View.GONE);
                }

                Bitmap bm = null;

                //bm =  getBitmapFromLocalPath(selectedImagePath,1);

                Uri imageUri = data.getData();
                try {
                    bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                //bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
//checking
                //byte[] b = baos.toByteArray();
                //String str= BitMapToString(bm);
                if (clcikImage.equals("one")) {
                    img = BitMapToString(bm);
                    //img = resizeBase64Image(img);
                    System.out.println("hello");
                } else if (clcikImage.equals("two")) {
                    img2 = BitMapToString(bm);
                    //img2 = resizeBase64Image(img2);
                } else if (clcikImage.equals("three")) {
                    img3 = BitMapToString(bm);
                    //img3 = resizeBase64Image(img3);
                } else if (clcikImage.equals("four")) {
                    img4 = BitMapToString(bm);
                    //img4 = resizeBase64Image(img4);
                } else if (clcikImage.equals("banner")) {
                    ban = BitMapToString(bm);
                    //ban = resizeBase64Image(ban);
                }
            }
        }


    }


    private String encodeImage(String path)
    {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }


public Bitmap loadbmp(String path) throws FileNotFoundException {

    //Log.d("startedfromthebottom", file.getAbsolutePath()); //show the same path as in the activityresult above
    Bitmap bitmap = BitmapFactory.decodeFile(path); //bitmap is always null
    OutputStream os = new BufferedOutputStream(new FileOutputStream(path));
    bitmap.compress(Bitmap.CompressFormat.JPEG,100,os);
    return  bitmap;

}

    public String BitMapToString(Bitmap bitmap){

        ByteArrayOutputStream baos=new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);

        byte [] b=baos.toByteArray();

        String temp=null;

        try{

            System.gc();

            temp= Base64.encodeToString(b, Base64.DEFAULT);

        }catch(Exception e){

            e.printStackTrace();

        }catch(OutOfMemoryError e){

            baos=new  ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();

            temp=Base64.encodeToString(b, Base64.DEFAULT);

            Log.e("EWN", "Out of memory error catched");

        }

        return temp;

    }

    public static Bitmap getBitmapFromLocalPath(String path, int sampleSize) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            //  Logger.e(e.toString());
        }

        return null;
    }


    public Bitmap getBitmap(String path) {
        Bitmap bitmap = null;
        try {
            File f = new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void cancel(boolean b) {
    }


    private void funserverrequest(final String mhawker_code, final String pnumber, final String stitle, final String sdescription, final String strstartdate,
                                  final String Sstr_img1, final String Sstr_img2) {
        //Toast.makeText(getApplicationContext(), str_img1 + "", Toast.LENGTH_LONG).show();
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        _progressDialog = ProgressDialog.show(
                RequestAdvertisementActivity.this, "", "Please wait...", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        RequestAdvertisementActivity.this.cancel(true);
                        finish();
                    }
                }
        );
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_REQUEST_HAWKER_ADVERTISEMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null && response.length() > 0) {
                            _progressDialog.dismiss();
                            parseData(response);
                        } else {
                            Toast.makeText(RequestAdvertisementActivity.this, "Something Wrong!", Toast.LENGTH_SHORT).show();
                            _progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progressDialog.dismiss();
                        if (error.getClass().equals(TimeoutError.class)) {
                            Toast.makeText(RequestAdvertisementActivity.this, StringConstant.RESPONSE_FAILURE_MORE_TIME, Toast.LENGTH_LONG).show();
                        } else if (error.getClass().equals(ServerError.class)) {
                            Toast.makeText(RequestAdvertisementActivity.this, StringConstant.RESPONSE_FAILURE_NOT_RESPOND, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RequestAdvertisementActivity.this, StringConstant.RESPONSE_FAILURE_SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hawker_code", mhawker_code);
                params.put("mobile_no", pnumber);
                params.put("advertisement_title", stitle);
                params.put("detail_of_advertisement", sdescription);
                params.put("start_date", strstartdate);
//                params.put("end_date", sstrenddate);
                params.put("image_1", Sstr_img1);
                params.put("image_2", Sstr_img2);
//                params.put("latitude", String.valueOf(lat));
//                params.put("longitude", String.valueOf(lng));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(StringConstant.REPEAT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }

    String sStatus = "";

    private void parseData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            JSONObject jsoObject = obj.getJSONObject("data");
            sStatus = jsoObject.getString("status");
            if (sStatus.equals("1")) {
                String message = jsoObject.getString("message");
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish();
            } else if (sStatus.equals("0")) {
                String message = jsoObject.getString("message");
                Toast.makeText(RequestAdvertisementActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void funserverrequest_Paid(final String mhawker_code, final String pnumber, final String stitle, final String sdescription, final String strstartdate,
                                       final String Sstr_img1, final String Sstr_img2, final String Sstr_img3, final String Sstr_img4, final String banner_img) {
        requestQueue = VolleySingleton.getInstance(getApplicationContext()).getRequestQueue();
        _progressDialog = ProgressDialog.show(
                RequestAdvertisementActivity.this, "", "Please wait...", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        RequestAdvertisementActivity.this.cancel(true);
                        finish();
                    }
                }
        );
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Urls.URL_REQUEST_HAWKER_PAID_ADVERTISEMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null) {
                            _progressDialog.dismiss();
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                                String status = jsonObject1.optString("status");
                                String message = jsonObject1.optString("message");
                                String user_type = jsonObject1.optString("user_type");
                                String max_id = jsonObject1.optString("max_id");
                                if (status.equals("1")) {
                                    if (user_type.equals("First")) {
                                        Toast.makeText(RequestAdvertisementActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        startActivity(new Intent(RequestAdvertisementActivity.this, PaymentActivity.class).putExtra("max_id", max_id));
                                        et_title.setText(null);
                                        et_description.setText(null);
                                        img_banner.setImageResource(R.drawable.ic_background);
                                        img_one.setImageResource(R.drawable.ic_background);
                                        img_two.setImageResource(R.drawable.ic_background);
                                        img_three.setImageResource(R.drawable.ic_background);
                                        img_four.setImageResource(R.drawable.ic_background);
                                        tv_banner.setVisibility(View.VISIBLE);
                                        tv_banner.setText("Banner Image");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(RequestAdvertisementActivity.this, "Something Wrong!", Toast.LENGTH_SHORT).show();
                            _progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        _progressDialog.dismiss();
                        if (error.getClass().equals(TimeoutError.class)) {
                            Toast.makeText(RequestAdvertisementActivity.this, StringConstant.RESPONSE_FAILURE_MORE_TIME, Toast.LENGTH_LONG).show();
                        } else if (error.getClass().equals(ServerError.class)) {
                            Toast.makeText(RequestAdvertisementActivity.this, StringConstant.RESPONSE_FAILURE_NOT_RESPOND, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(RequestAdvertisementActivity.this, StringConstant.RESPONSE_FAILURE_SOMETHING_WENT_WRONG, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("hawker_code", mhawker_code);
                params.put("mobile_no", pnumber);
                params.put("advertisement_title", stitle);
                params.put("detail_of_advertisement", sdescription);
                params.put("start_date", strstartdate);
//                params.put("end_date", sstrenddate);
                params.put("image_1", Sstr_img1);
                params.put("image_2", Sstr_img2);
                params.put("image_3", Sstr_img3);
                params.put("image_4", Sstr_img4);
                params.put("banner_img", banner_img);
                params.put("choice", text);
//                params.put("latitude", String.valueOf(lat));
//                params.put("longitude", String.valueOf(lng));
//                params.put("address", address);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(StringConstant.REPEAT_TIME, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);
    }

    //check here
    public String resizeBase64Image(String base64image){
        byte [] encodeByte=Base64.decode(base64image.getBytes(),Base64.DEFAULT);
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inPurgeable = true;
        Bitmap image = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length,options);


        //if(image.getHeight() <= 400 && image.getWidth() <= 400){
        // return base64image;
        //}
        //image = Bitmap.createScaledBitmap(image, 400, 400, false);

        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.PNG,100, baos);

        byte [] b=baos.toByteArray();
        System.gc();
        return Base64.encodeToString(b, Base64.NO_WRAP);

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
            lat = location.getLatitude();
            lng = location.getLongitude();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }
}
