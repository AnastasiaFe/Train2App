package ua.nure.fedorenko.kidstim.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Completable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import ua.nure.fedorenko.kidstim.entity.Parent;
import ua.nure.fedorenko.kidstim.rest.APIServiceImpl;
import ua.nure.fedorenko.kidstim.utils.Constants;
import ua.nure.fedorenko.kidstim.utils.ImageUtils;
import ua.nure.fedorenko.kidstim.utils.Validator;
import ua.nure.fedorenko.kidstim.R;

public class RegisterActivity extends BaseActivity {

    private static final int PICK_IMAGE_FROM_GALLERY_CODE = 1;
    @BindView(R.id.emailEditText)
    EditText emailEditText;

    @BindView(R.id.passwordEditText)
    EditText passwordEditText;

    @BindView(R.id.emailLayout)
    TextInputLayout emailLayout;

    @BindView(R.id.passwordLayout)
    TextInputLayout passwordLayout;

    @BindView(R.id.nameLayout)
    TextInputLayout nameLayout;

    @BindView(R.id.surnameLayout)
    TextInputLayout surnameLayout;

    @BindView(R.id.nameEditText)
    EditText nameEditText;

    @BindView(R.id.surnameEditText)
    EditText surnameEditText;

    @BindView(R.id.uploadedImage)
    ImageView uploadedImageView;
    private APIServiceImpl apiService;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        apiService = new APIServiceImpl(this);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
    }

    @OnClick(R.id.registerButton)
    void onRegisterButtonClick() {
        clearErrors();
        if (isInputValid()) {
            Parent parent = new Parent();
            parent.setEmail(emailEditText.getText().toString());
            parent.setPassword(passwordEditText.getText().toString());
            parent.setName(nameEditText.getText().toString());
            parent.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
            parent.setSurname(surnameEditText.getText().toString());
            parent.setPhoto(parent.getEmail() + Constants.IMAGE_EXTENSION);
            Single<Parent> single = apiService.register(parent);
            SingleSubscriber<Parent> subscriber = new SingleSubscriber<Parent>() {
                @Override
                public void onSuccess(Parent value) {
                    Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.thank_for_registration), Toast.LENGTH_LONG).show();
                    uploadImage(parent);
                }

                @Override
                public void onError(Throwable error) {
                    Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.error_registration), Toast.LENGTH_LONG).show();
                }
            };
            single.subscribe(subscriber);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_FROM_GALLERY_CODE && resultCode == RESULT_OK && null != data) {
            Uri uri = data.getData();
            filePath = getRealPathFromURIPath(uri, RegisterActivity.this);
            Bitmap bitmap = ImageUtils.decodeFile(filePath);
            uploadedImageView.setImageBitmap(bitmap);
        }
    }

    private void uploadImage(Parent parent) {
        Completable completable = apiService.uploadImage(new File(filePath), parent.getEmail() + Constants.IMAGE_EXTENSION, parent.getEmail(), Constants.PARENT_ROLE);
        Completable.CompletableSubscriber subscriber = new Completable.CompletableSubscriber() {
            @Override
            public void onCompleted() {
                apiService.login(parent);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getBaseContext(), getString(R.string.error_image_upload), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSubscribe(Subscription d) {
            }
        };
        completable.subscribe(subscriber);
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @OnClick(R.id.uploadImageButton)
    void onUploadImageButtonClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture)), PICK_IMAGE_FROM_GALLERY_CODE);
    }

    private boolean isInputValid() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String name = nameEditText.getText().toString();
        String surname = surnameEditText.getText().toString();
        if (Validator.isEmailValid(email) && Validator.isPasswordValid(password) && !name.isEmpty() && !surname.isEmpty()) {
            return true;
        } else {
            if (!Validator.isEmailValid(email)) {
                emailLayout.setError(getString(R.string.error_invalid_email));
            }
            if (!Validator.isPasswordValid(password)) {
                passwordLayout.setError(getString(R.string.error_invalid_password));
            }
            if (name.isEmpty()) {
                nameLayout.setError(getString(R.string.error_invalid_name));
            }
            if (surname.isEmpty()) {
                surnameLayout.setError(getString(R.string.error_invalid_surname));
            }
            return false;
        }
    }

    private void clearErrors() {
        emailLayout.setError(null);
        passwordLayout.setError(null);
        nameLayout.setError(null);
        surnameLayout.setError(null);
    }
}
