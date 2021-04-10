package AdminPanel;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.selflearn.rpsstationary.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import AdminPanel.Adapter.ImageRecyclerViewAdapter;
import ModelClasses.ProductData;
import Utils.CustomDialogMaker;
import Utils.CustomProgressDialog;
import Utils.CustomProgressDialogSpinKit;

public class EditProductImages extends AppCompatActivity {

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
    List<String> images;
    ProductData productData;
    private static final String TAG = "EditProductImages";
    String ProductName , Category , Type , Size , Color , QuantityInOnePack , Brand , MRP , SellingPrice , Discount , OrderQuantity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_product_images);


        productData = (ProductData) getIntent().getSerializableExtra("productData");
        images = getIntent().getStringArrayListExtra("images");



        if (productData != null && images != null) {


            bitmapList = new ArrayList<>();
            customProgressDialog = new CustomProgressDialog(EditProductImages.this);
            setData(images);
            findID();
            getPermission();
            setListeners();


        }

    }

    private void setData(List<String> images) {
        recyclerView = findViewById(R.id.recycler_view_images);

        recyclerView.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
        recyclerView.setHasFixedSize(true);

        for (final String str : images) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try{

                        FutureTarget<Bitmap> submit = Glide.with(EditProductImages.this)
                                .asBitmap()
                                .load(str)
                                .thumbnail(0.1f)
                                .override(Target.SIZE_ORIGINAL , Target.SIZE_ORIGINAL) //------getting image in circle
                                .submit();

                        bitmapList.add(submit.get());

                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            Thread thread = new Thread(runnable);
            thread.start();

        }

        Log.d(TAG, "setData: " + bitmapList.size());
        recyclerView.setAdapter(new ImageRecyclerViewAdapter(EditProductImages.this , bitmapList));

    }

    private void findID() {

        camera = findViewById(R.id.click_from_camera);
        gallery = findViewById(R.id.choose_from_galary);
        next = findViewById(R.id.next_btn);
        recyclerView = findViewById(R.id.recycler_view_images);

    }

    private void getPermission() {


        Log.d(TAG, "getPermission: called");

        if (!(ContextCompat.checkSelfPermission(EditProductImages.this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(EditProductImages.this , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_GRANTED) &&
                !(ContextCompat.checkSelfPermission(EditProductImages.this , Manifest.permission.READ_EXTERNAL_STORAGE)
                        ==PackageManager.PERMISSION_GRANTED)
        ){
            ActivityCompat.requestPermissions(EditProductImages.this, new String[] {Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
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
        recyclerView.setAdapter(new ImageRecyclerViewAdapter(EditProductImages.this , bitmapList));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (bitmapList.size() < 1) {
                        new CustomDialogMaker(EditProductImages.this).createAndShowDialogWarning("please upload at least 1 image");
                    } else {
                        CustomProgressDialogSpinKit customProgressDialogSpinKit = new CustomProgressDialogSpinKit(EditProductImages.this);
                        customProgressDialogSpinKit.startLoadingDialog();

                        constraintLayout = findViewById(R.id.constraint_layout3);
                        constraintLayout.setAlpha(0.4f);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductImages");
                        databaseReference = databaseReference.child("images").child(productData.getCategory()).child(productData.getBrand()).child(productData.getType())
                                .child(productData.getSize()).child(productData.getColor()).child(productData.getProductName());
                        databaseReference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: " + "data deleted success");
                                new UploadTask(productData, bitmapList, EditProductImages.this).execute();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: " +e.getClass().getName() + " " + e.getMessage());
                            }
                        });

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
                recyclerView.setAdapter(new ImageRecyclerViewAdapter(EditProductImages.this , bitmapList));
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
            recyclerView.setAdapter(new ImageRecyclerViewAdapter(EditProductImages.this , bitmapList));
            Log.d(TAG, "onActivityResult: " + bitmapList.size());
        }else
        {
            Log.d(TAG, "onActivityResult: error while loading image ");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}


