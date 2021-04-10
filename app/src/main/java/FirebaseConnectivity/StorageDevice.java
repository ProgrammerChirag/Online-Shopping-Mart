package FirebaseConnectivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import ModelClasses.ProductData;
import Utils.CustomProgressDialog;

public class StorageDevice {

//    Context context ;
//private static boolean uploaded = false;

    private static final String TAG = "StorageDevice";

     Uri path;

    public static void UploadImage(String account_type , String user_id , final Context context, final Uri filePath )
    {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference(account_type);


        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("uploading");
        progressDialog.show();

        StorageReference ref = storageReference.child(user_id).child(filePath.toString().substring(filePath.toString().lastIndexOf("/")+1));

        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(context.getApplicationContext() , "image uploaded" , Toast.LENGTH_LONG).show();
//               uploaded = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("error",e.getMessage()+e.getClass().getName());
//               uploaded = false;

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress
                        = (100.0
                        * taskSnapshot.getBytesTransferred()
                        / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage(
                        "Uploaded "
                                + (int)progress + "%");


            }
        });
    }

    public static void  UploadUserProfilePicture(final String user_id, final Context context, final Uri filePath){

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference("ProfileImages");

        final CustomProgressDialog customProgressDialog = new CustomProgressDialog(((Activity) (context)));

        customProgressDialog.startLoadingDialog();

        final StorageReference ref = storageReference.child(user_id).child("Profile");


        ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String path = uri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProfileImage");
                        reference = reference.child(user_id);
                        reference.setValue(path);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + "unable to upload profile image");
                        Log.d(TAG, "onFailure: " + e.getClass().getName() + " " + e.getMessage());
                    }
                });

                customProgressDialog.dismissDialog();
                Toast.makeText(context.getApplicationContext() , "Profile uploaded" , Toast.LENGTH_LONG).show();
//               customProgressDialog.dismissDialog();
//               uploaded = true;


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("error",e.getMessage()+e.getClass().getName());
                customProgressDialog.dismissDialog();
//               uploaded = false;

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("progress" , "uploading in progress");
                customProgressDialog.dismissDialog();
            }
        });

    }

    public static void UploadImageOfProperty(final ProductData productData , final Context context, Bitmap bitmap , final String dirName , final CustomProgressDialog customProgressDialog)
    {

        if (customProgressDialog != null)
        customProgressDialog.startLoadingDialog();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReference("ProductData");

        //       customProgressDialog.startLoadingDailog();
        final String name ;

        name = getAlphaNumericString();
        final StorageReference ref = storageReference.child("images").child(productData.getCategory()).child(productData.getBrand()).child(productData.getType()).child(productData.getSize()).child(productData.getColor()).child(productData.getProductName());

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        final String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        Uri file =  Uri.parse(path);

        ref.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//               customProgressDialog.dismissDialog();
                Toast.makeText(context.getApplicationContext() , "Uploaded" , Toast.LENGTH_LONG).show();
                //               customProgressDialog.dismissDialog();
//               uploaded = true;

                //uploadImageName(dirName , seller_id , name);

                Log.d(TAG, "onSuccess: uploading successful");

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Uri downloadUri = uri;

                        Log.d(TAG, "onSuccess: URI : " + downloadUri.toString());

                        uploadImageName(downloadUri.toString() , productData);

                        if (customProgressDialog != null)
                            customProgressDialog.dismissDialog();
                        else Log.d(TAG, "onSuccess: dialog is null");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailure: process failed" + e.getClass().getName() + " " + e.getMessage());

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d("error",e.getMessage()+e.getClass().getName());
                Log.d(TAG, "onFailure: error in uploading images");
//               customProgressDialog.dismissDialog();
//               uploaded = false;

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                Log.d("progress" , "uploading in progress");
            }
        });

    }

    private static void uploadImageName(final String dirName, final ProductData productData) {



        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ProductImages");
                databaseReference = databaseReference.child("images").child(productData.getCategory()).child(productData.getBrand()).child(productData.getType()).child(productData.getSize()).child(productData.getColor()).child(productData.getProductName());
                String key = databaseReference.push().getKey();

                if (key != null) {
                    databaseReference.child(key).setValue(dirName).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: "+"file name uploaded");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "onFailure: "+e.getMessage()+" " +e.getClass().getName());
                        }
                    });
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

    }

    static String getAlphaNumericString()
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

}
