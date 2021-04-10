package AdminPanel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selflearn.rpsstationary.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import AdminPanel.Adapter.ImageRecyclerViewAdapter;
import FirebaseConnectivity.StorageDevice;
import ModelClasses.ProductData;
import Utils.CustomDialogMaker;
import Utils.CustomProgressDialog;
import Utils.CustomProgressDialogSpinKit;

public class UploadProductImages extends AppCompatActivity {

    private static final int PICK_IMAGE = 100;
    private static final int REQUEST_IMAGE_CAPTURE =  102;
    private static boolean PERMISSION_GRANTED = false;
    RecyclerView recyclerView;
    ImageView camera , gallery;
    Bitmap bitmap;
    Button next;
    List<Bitmap> bitmapList ;
    private int requestCode = 101;
    CustomProgressDialog customProgressDialog;
    ConstraintLayout constraintLayout;

    ProductData productData;

    String ProductName , Category , Type , Size , Color , QuantityInOnePack , Brand , MRP , SellingPrice , Discount , OrderQuantity;

    private static final String TAG = "UploadProductImages";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_images);


        Intent intent = getIntent();

        ProductName = intent.getStringExtra("ProductName");
        Category = intent.getStringExtra("Category");
        Type = intent.getStringExtra("Type");
        Size = intent.getStringExtra("Size");
        Color = intent.getStringExtra("Color");
        QuantityInOnePack = intent.getStringExtra("QuantityInOnePack");
        Brand = intent.getStringExtra("Brand");
        MRP = intent.getStringExtra("MRP");
        SellingPrice = intent.getStringExtra("SellingPrice");
        Discount = intent.getStringExtra("Discount");
        OrderQuantity = intent.getStringExtra("OrderQuantity");

        productData = new ProductData(
                ProductName , Category , Type , Size
                , Color , QuantityInOnePack , Brand , MRP , SellingPrice , Discount , OrderQuantity
        );


        if (productData != null) {

            bitmapList = new ArrayList<>();
            customProgressDialog = new CustomProgressDialog(UploadProductImages.this);
            findID();
            getPermission();
            setListeners();
        }
    }

    private void findID() {

        camera = findViewById(R.id.click_from_camera);
        gallery = findViewById(R.id.choose_from_galary);
        next = findViewById(R.id.next_btn);
        recyclerView = findViewById(R.id.recycler_view_images);

    }

    private void getPermission() {


        Log.d(TAG, "getPermission: called");

        if (!(ContextCompat.checkSelfPermission(UploadProductImages.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(UploadProductImages.this , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(UploadProductImages.this , Manifest.permission.READ_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_GRANTED)
        ){
            ActivityCompat.requestPermissions(UploadProductImages.this, new String[] {Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
        }else {
            PERMISSION_GRANTED = true;
        }
    }

    private void setListeners() {

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // opening camera
                if (PERMISSION_GRANTED)
                {
                    openCameraAndGetImage();
                }else {
                    getPermission();
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // opening gallery;
                openGalleryAndGetImage();

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this , RecyclerView.HORIZONTAL , false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new ImageRecyclerViewAdapter(UploadProductImages.this , bitmapList));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (bitmapList.size() < 1) {
                        new CustomDialogMaker(UploadProductImages.this).createAndShowDialogWarning("please upload at least 4 image");
                    } else {
                        CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(UploadProductImages.this);
                        customProgressDialogSpinKit.startLoadingDialog();

                        constraintLayout = findViewById(R.id.constraint_layout3);
                        constraintLayout.setAlpha(0.4f);

                        new UploadTask(productData, bitmapList, UploadProductImages.this).execute();
                    }
                }catch (Exception e)
                {
                    Log.d(TAG, "onClick: " +e.getClass().getName() + " " +e.getMessage());
                    Toast.makeText(getApplicationContext() , e.getMessage() + " " + e.getClass().getName()  , Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }


    private void openCameraAndGetImage() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    private void openGalleryAndGetImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == this.requestCode)
        {
            if (grantResults.length > 0)
            {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        PERMISSION_GRANTED = false;
                        return;
                    }
                }
                PERMISSION_GRANTED = true;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK)
        {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
            }
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                bitmapList.add(bitmap);
                recyclerView.setAdapter(new ImageRecyclerViewAdapter(UploadProductImages.this , bitmapList));
                Log.d(TAG, "onActivityResult: " + bitmapList.size());
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            if (data != null) {
                bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            }
            bitmapList.add(bitmap);
            recyclerView.setAdapter(new ImageRecyclerViewAdapter(UploadProductImages.this , bitmapList));
            Log.d(TAG, "onActivityResult: " + bitmapList.size());
        }else
        {
            Log.d(TAG, "onActivityResult: error while loading image ");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}

class UploadTask extends AsyncTask<Context , Integer , Long>{

    CustomProgressDialog customProgressDialog;
    private static final String TAG = "UploadTask";
    Context context;
    List<Bitmap> bitmapList;
    private ProductData productData;

    public UploadTask( ProductData productData , List<Bitmap> bitmapList , Context context)
    {
        this.context = context;
        this.productData = productData;
        this.bitmapList = bitmapList;
    }

    @Override
    protected Long doInBackground(Context... contexts) {


        Log.d(TAG, "onClick: starting dialog");
        if (customProgressDialog == null)
            Log.d(TAG, "onClick: dialog is getting null");


        String dirName;
        Date today = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        dirName = format.format(today);

//                    if (customProgressDialog != null){
//                    customProgressDialog.dismissDialog();}

        //customProgressDialog.startLoadingDialog();

        for(int i=0 ; i <bitmapList.size() ; i++)
        {
            //customProgressDialog = new CustomProgressDialog((Activity) context);

            StorageDevice.UploadImageOfProperty(productData,
                    context,
                    bitmapList.get(i),
                    dirName , customProgressDialog
            );
        }

//        productData = new ProductData();
//        productData.setCategory("student");
//        productData.setProductName("bottle");

        uploadProductData(productData , dirName , bitmapList.size() );

        //customProgressDialog.dismissDialog();

        Log.d(TAG, "onClick: "+bitmapList.size());



        return null;
    }

    private void uploadProductData(ProductData productData, final String dirName , final int size) {

        //returnToFormUI(dirName , bitmapList.size());

        if (productData != null)
        {
            Log.d(TAG, "uploadProductData: way to upload data of product.");
            //Toast.makeText(context.getApplicationContext() , "uploading is about to complete" , Toast.LENGTH_LONG).show();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductData");

            databaseReference = databaseReference.child(productData.getCategory()).child(productData.getBrand()).child(productData.getType()).child(productData.getSize()).child(productData.getColor()).child(productData.getProductName());

            databaseReference.setValue(productData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    returnToFormUI(dirName , bitmapList.size());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: "+e.getMessage()+" " +e.getClass().getName());
                    Toast.makeText(context.getApplicationContext() , "we are getting some technical issues we will fix it soon" , Toast.LENGTH_LONG).show();

                    returnToFormUI(dirName , bitmapList.size());
                }
            });
        }
    }

    private void returnToFormUI(String dirName, int size) {

        ((Activity)(context)).finish();
        Intent intent = new Intent(context , AdminDashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        context.startActivity(intent);

    }
}
