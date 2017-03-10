package com.alobha.challenger.ui.main.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alobha.challenger.GlobalConstants;
import com.alobha.challenger.R;
import com.alobha.challenger.data.api.ServerAPI;
import com.alobha.challenger.data.api.helpers.PersistentPreferences;
import com.alobha.challenger.data.api.helpers.StatusCodes;
import com.alobha.challenger.data.api.models.UserResponse;
import com.alobha.challenger.data.entities.User;
import com.alobha.challenger.ui.base.BaseFragment;
import com.alobha.challenger.ui.main.presenters.ProfilePresenter;
import com.alobha.challenger.utils.DefaultFormatter;
import com.alobha.challenger.utils.DialogFactory;
import com.alobha.challenger.utils.ImageUtil;
import com.alobha.challenger.utils.validation.EmailValidator;
import com.alobha.challenger.utils.validation.NameValidator;
import com.alobha.challenger.utils.validation.PhoneValidator;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by mrNRG on 20.06.2016.
 */
public class ProfileFragment extends BaseFragment implements ProfilePresenter.View, TextView.OnEditorActionListener {

    private OnEventListener mCallback;

    private AlertDialog profileDialog;
    private ProfilePresenter presenter;

    private NameValidator nameValidator;
    private EmailValidator emailValidator;
    private PhoneValidator phoneValidator;

    private MenuItem mItem;
    private PersistentPreferences preferences;
    private DecimalFormat distanceFormat = DefaultFormatter.distanceFormat;
    private DecimalFormat speedFormat = DefaultFormatter.speedFormat;

    private Uri mCropImageUri;

    @Bind(R.id.ivAvatar)
    ImageView ivAvatar;

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etEmail)
    EditText etEmail;

    @Bind(R.id.etPhone)
    EditText etPhone;

    @Bind(R.id.tvSpeed)
    TextView tvAvgSpeed;

    @Bind(R.id.tvDistance)
    TextView tvTotal;

    @Bind(R.id.tvTime)
    TextView tvAvgDistance;

    @Bind(R.id.tvLastRun)
    TextView tvLastRun;

    @Bind(R.id.btnChangePassword)
    TextView btnChangePassword;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();

        Bundle argumentBundle = new Bundle();
        fragment.setArguments(argumentBundle);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnEventListener) context;
        } catch (ClassCastException e) {
            Log.e(getTag(), e.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        preferences = PersistentPreferences.getInstance();

        nameValidator = new NameValidator(etName);
        emailValidator = new EmailValidator(etEmail);
        phoneValidator = new PhoneValidator(etPhone);

        etPhone.setOnEditorActionListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getString(R.string.title_profile));
        updateViewInfo();
        initChangePassBtn();
    }

    private void initChangePassBtn() {
        if(!preferences.getUserSource().equals("Email")) {
            btnChangePassword.setVisibility(View.GONE);
        } else btnChangePassword.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_edit, menu);
        mItem = menu.findItem(R.id.action_edit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (item.isChecked()) {
                    onButtonSaveClick(item);
                } else {
                    onButtonEditClick(item);
                }
                etEmail.setEnabled(item.isChecked());
                etName.setEnabled(item.isChecked());
                etPhone.setEnabled(item.isChecked());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyOptionsMenu() {
        mItem = null;
        super.onDestroyOptionsMenu();
    }

    private void onButtonEditClick(MenuItem item) {
        item.setTitle(getString(R.string.menu_save_label));
        item.setChecked(true);
        etEmail.setEnabled(true);
        etName.setEnabled(true);
        etPhone.setEnabled(true);
    }

    private void onButtonSaveClick(MenuItem item) {
        if (nameValidator.isValid() & emailValidator.isValid() & phoneValidator.isValid()) {
            item.setTitle(getString(R.string.menu_edit_label));
            item.setChecked(false);

            presenter.callProfileEdit(etEmail.getText().toString().trim(),
                    etName.getText().toString().trim(),
                    etPhone.getText().toString().trim());

            etEmail.setEnabled(false);
            etName.setEnabled(false);
            etPhone.setEnabled(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        profileDialog = DialogFactory.createProfileUpdateDialog(getActivity());
    }

    @Override
    protected void initializeLoadingDialog() {
        setLoadingDialog(DialogFactory.createLoadingDialog(getActivity(), getString(R.string.message_loading)));
    }

    @Override
    protected void initializePresenter() {
        this.presenter = new ProfilePresenter();
        this.presenter.bindView(this);
    }

    @Override
    public void showLoadingProfileUi() {
        getLoadingDialog().show();
        hideKeyboard();
    }

    @Override
    public void showErrorProfileUi(@NonNull Throwable throwable) {
        hideLoadingDialog();
        Log.d(getTag(), "error", throwable);
    }

    @Override
    public void showContentProfileUi(@NonNull UserResponse userResponse) {
        hideLoadingDialog();
        User user = userResponse.user;
        preferences.setLoggedUser(user);
        profileDialog.show();
        updateViewInfo();
        mCallback.onEvent(GlobalConstants.USER_PROFILE_UPDATED);
    }

    private void updateViewInfo() {
        String avatarURL = ServerAPI.BASE_URL + preferences.getAvatar();

        Picasso.with(getContext())
                .load(avatarURL)
                .placeholder(R.mipmap.avatar_placeholder)
                .fit().centerCrop().transform(new CropCircleTransformation())
                .into(ivAvatar);

        etName.setText(preferences.getFirstName());
        etEmail.setText(preferences.getUsername());
        etPhone.setText(preferences.getUserPhone());
        tvAvgSpeed.setText(String.format(getContext().getString(R.string.speed_wrapper), speedFormat.format(preferences.getAvgSpeed())));

        tvTotal.setText(String.format(getContext().getString(R.string.distance_wrapper), distanceFormat.format(preferences.getDistance())));
        tvAvgDistance.setText(String.format(getContext().getString(R.string.distance_wrapper), distanceFormat.format(preferences.getAvgDistance())));

        SimpleDateFormat format = DefaultFormatter.dateFormat;
        if(preferences.getLastDate()!= null) {
            tvLastRun.setText(String.valueOf(format.format(preferences.getLastDate())));
        }
    }

    @Override
    public void showInvalidProfile(int status) {
        hideLoadingDialog();
        DialogFactory.showSnackBarLong(getActivity(), StatusCodes.statusMessage(status));
    }

    @Override
    public void onDestroy() {
        hideLoadingDialog();
        setLoadingDialog(null);
        presenter.unbindView(this);
        super.onDestroy();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onButtonSaveClick(mItem);
        }
        return false;
    }

    @OnClick(R.id.ivAvatar)
    public void onAvatarClick() {
        if (CropImage.isExplicitCameraPermissionRequired(getActivity())) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        } else {
            CropImage.startPickImageActivity(getActivity());
        }
    }

    @OnClick(R.id.btnChangePassword)
    public void onButtonChangePasswordClick() {
        mCallback.onEvent(GlobalConstants.CHANGE_PASSWORD_BUTTON);
    }

    @OnClick(R.id.btnPreviousChallenges)
    public void onButtonreviousChallengesClick() {
        mCallback.onEvent(GlobalConstants.PRECIOUS_CHALLENGES_BUTTON);
    }

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(getActivity(), data);

            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            boolean requirePermissions = false;
            if (CropImage.isReadExternalStoragePermissionsRequired(getActivity(), imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                requirePermissions = true;
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                byte[] bBytes = ImageUtil.getBytesFromPath(result.getUri().getPath());
                String avatar = "";
                if (bBytes != null) {
                    avatar = Base64.encodeToString(bBytes, Base64.DEFAULT);
                }
                if (!avatar.equals("")) {
                    presenter.callChangeAvatar(avatar);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CropImage.startPickImageActivity(getActivity());
            } else {
                DialogFactory.showToastMessageLong(getActivity(), "Cancelling, required permissions are not granted");
            }
        }
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            DialogFactory.showToastMessageLong(getActivity(), "Cancelling, required permissions are not granted");
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setFixAspectRatio(true)
                .start(getActivity());
    }

}
