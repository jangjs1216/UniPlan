package com.UniPlan.loginregister.Notice_B;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.UniPlan.loginregister.MainActivity;
import com.UniPlan.loginregister.Table;
import com.UniPlan.loginregister.adapters.CurriculumAdapter;
import com.UniPlan.loginregister.adapters.MultiImageAdapter;
import com.UniPlan.loginregister.curiList.Recycler_Data;
import com.UniPlan.loginregister.login.UserAccount;
import com.bumptech.glide.Glide;
import com.UniPlan.loginregister.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Post_Update extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();//????????? ?????? ????????????
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private EditText mTitle, mContents;//??????, ??????
    private String p_nickname;//???????????? ????????? ????????? //?????? ????????? ?????? ???????????? ?????? ??????
    private TextView post_photo, post_tree, post_gallery;
    private ProgressBar post_progressBar;
    private String writer_id, post_id, post_num, comment_post, like, title, content;
    private int click;
    private Timestamp timestamp;
    private ImageView post_imageView;
    private File tempFile;
    private TextView post_save, btn_back;
    private static final int FROM_CAMERA = 1;
    private static final int FROM_GALLERY = 2;
    private Table choosedTable=null;
    private String forum_sort;
    private Uri uri;
    private int commnet_num;
    private Table table;
    private String image_url,token;
    private ArrayList<String> subscriber;
    private ArrayList<Comment> comments=new ArrayList<>();
    private FirebaseStorage storage;
    private String imageFilePath;
    private Dialog addTreeDialog;
    private RecyclerView postAddTreeRV;
    private LinearLayoutManager linearLayoutManager;
    private ArrayList<Recycler_Data> arrayList;
    private CurriculumAdapter curriculumAdapter;
    private ArrayList<String > image_urllist;
    private MultiImageAdapter photoadapter;
    private ArrayList<Uri> uriList = new ArrayList<>();
    private RecyclerView photo_list;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("###", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        mTitle = findViewById(R.id.Post_write_title);//?????? , item_post.xml??? ????????? ????????????
        mContents = findViewById(R.id.Post_write_contents);

        post_save=findViewById(R.id.post_save);
        btn_back=findViewById(R.id.btn_back);
        post_photo=findViewById(R.id.post_photo);
        post_tree=findViewById(R.id.post_tree);
        post_gallery=findViewById(R.id.post_gallery);
        photo_list  =findViewById(R.id.photo_list);

        Intent intent=getIntent();
        post_id=intent.getStringExtra("post_id");
        forum_sort=intent.getStringExtra("forum_sort");

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReferenceFromUrl("gs://login-6ba8f.appspot.com/");

        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("????????? ????????? ???????????????")
                .setDeniedMessage("?????????????????????")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        if(mAuth.getCurrentUser()!=null){//UserInfo??? ?????????????????? ???????????? ???????????? ?????????

            DocumentReference docRef2 = mStore.collection(forum_sort).document(post_id);
            docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Post post = documentSnapshot.toObject(Post.class);
                    writer_id = post.getWriter_id();
                    p_nickname = post.getP_nickname();
                    like = post.getLike();
                    timestamp = post.getTimestamp();
                    comments = post.getComments();
                    commnet_num=post.getcoment_Num();
                    image_urllist=post.getImage_url();
                    Log.d("###","image_url in update : "+image_url);
                    table = post.getTable();
                    subscriber = post.getSubscriber();
                    title=post.getTitle();
                    content=post.getContents();
                    click = post.getClick();
                    token = post.getWriter_token();

                    mTitle.setText(title);
                    mContents.setText(content);

                    loadphoto();
                }
            });
        }

        post_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useGallery();
            }
        });

        post_tree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTreeDialog = new Dialog(Post_Update.this);
                addTreeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addTreeDialog.setContentView(R.layout.dialog_postaddtree);
                showDialog();
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // ????????? ?????????
        post_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavePost();
            }
        });
    }

    private void loadphoto(){
        if (image_urllist.size()>0) {

            ((MainActivity)MainActivity.maincontext).Onprogress(Post_Update.this,"????????? ?????????");

            for(String image_url : image_urllist) {
                Log.d("####", "image_url : " + image_url);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();

                Log.d("###", "?????? ?????? ?????? : " + "post_image/" + image_url + ".jpg");
                //StorageReference submitImage = storageReference.child("post_image/" + image_url + ".jpg");
                storageReference.child("post_image/" + image_url + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        uriList.add(uri);
                        photoadapter = new MultiImageAdapter(uriList, getApplicationContext());
                        photo_list.setAdapter(photoadapter);
                        photo_list.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

                        photoadapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int pos) {

                                Intent intent=new Intent(Post_Update.this,Image_zoom.class);
                                intent.putExtra("uri",uriList.get(pos));
                                startActivity(intent);
                            }
                        });

                        if(uriList.size() == image_urllist.size()) { ((MainActivity)MainActivity.maincontext).progressOFF(); }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // ??????
                    }
                });
            }
        }
    }

    private void useGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, FROM_GALLERY);
    }


    private File createImageFile() throws IOException {
        String timestamp=new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName="TEST_"+timestamp+"_";
        File storageDir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image=File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath=image.getAbsolutePath();
        return image;
    }

    public void SavePost()
    {
        Log.d("###", "SavePost??????");
        if(image_url==null && uri!=null)
        {
            Toast.makeText(Post_Update.this,"?????? ????????? ????????????",Toast.LENGTH_SHORT).show();
        }
        else {
            if (mAuth.getCurrentUser() != null) {
                Post post=new Post(writer_id, mTitle.getText().toString(), mContents.getText().toString(), p_nickname, like, timestamp, post_id, comments, commnet_num, image_urllist, forum_sort, table, subscriber, click,token);
                mStore.collection(forum_sort).document(post_id).set(post);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(data == null){   // ?????? ???????????? ???????????? ?????? ??????
            Toast.makeText(getApplicationContext(), "???????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
        }
        else{   // ???????????? ???????????? ????????? ??????
            if(data.getClipData() == null){     // ???????????? ????????? ????????? ??????
                Log.e("single choice: ", String.valueOf(data.getData()));
                Uri imageUri = data.getData();
                uriList.add(imageUri);

            }
            else{      // ???????????? ????????? ????????? ??????
                ClipData clipData = data.getClipData();

                if(clipData.getItemCount() > 10){   // ????????? ???????????? 11??? ????????? ??????
                    Toast.makeText(getApplicationContext(), "????????? 10????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                }
                else{   // ????????? ???????????? 1??? ?????? 10??? ????????? ??????

                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();  // ????????? ??????????????? uri??? ????????????.
                        try {
                            uriList.add(imageUri);  //uri??? list??? ?????????.

                        } catch (Exception e) {
                        }
                    }
                }
            }

            photoadapter = new MultiImageAdapter(uriList, getApplicationContext());
            photo_list.setAdapter(photoadapter);
            photo_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

            //?????? ??????????????? ?????????
            ((MainActivity)MainActivity.maincontext).Onprogress(Post_Update.this,"?????? ????????????");
            UploadPhoto(uriList,0);


            photoadapter.setOnItemClickListener(new MultiImageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {

                    Intent intent=new Intent(Post_Update.this,Image_zoom.class);
                    intent.putExtra("uri",uriList.get(pos));
                    startActivity(intent);
                }
            });
        }
    }

    private void UploadPhoto(ArrayList<Uri> uris,int n){

        int i=0;
        for(Uri uri:uris ) {
            Log.d("###", "Uri ???: " + uri);
            String filename = mAuth.getUid() + "_" + System.currentTimeMillis() + n;
            StorageReference ref = storageReference.child("post_image/" + filename + ".jpg");
            image_urllist.add(filename);
            image_url = filename;
            Log.d("###", filename);

            UploadTask uploadTask;
            uploadTask = ref.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    //Toast.makeText(getApplicationContext(),"????????? ??????",Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Toast.makeText(getApplicationContext(),"????????? ??????",Toast.LENGTH_LONG).show();

                }
            });
            ++i;
            if(uris.size() ==i)((MainActivity)MainActivity.maincontext).progressOFF();
        }
    }

    private int exifOrientationDegrees(int exifOrientation) {
        if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if(exifOrientation==ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap bitmap,float degree) {
        Matrix matrix=new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    PermissionListener permissionListener=new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(),"????????? ?????????",Toast.LENGTH_SHORT).show();
        }
    };

    public void showDialog() {
        addTreeDialog.show();

        docRef = mStore.collection("user").document(mAuth.getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserAccount userAccount = documentSnapshot.toObject(UserAccount.class);

                ArrayList<Table> tables = userAccount.getTables();

                postAddTreeRV = addTreeDialog.findViewById(R.id.treeListRV);
                linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                postAddTreeRV.setLayoutManager(linearLayoutManager);
                arrayList = new ArrayList<>();

                for(String tableName : userAccount.getTableNames()){
                    Recycler_Data recycler_data = new Recycler_Data(tableName);
                    arrayList.add(recycler_data);
                }
                curriculumAdapter = new CurriculumAdapter(arrayList);
                postAddTreeRV.setAdapter(curriculumAdapter);

                //?????????????????? ?????? ?????????
                curriculumAdapter.setOnItemListener(new CurriculumAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int pos, String option) {
                        if(option.equals("choice")){
                            String tableName = arrayList.get(pos).getTv_title().toString();
                            Toast.makeText(getApplicationContext(), tableName + " ?????????", Toast.LENGTH_LONG).show();

                            choosedTable = tables.get(pos);
                            addTreeDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}