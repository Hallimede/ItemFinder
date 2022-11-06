package com.example.t.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.t.MainActivity;
import com.example.t.R;
import com.example.t.model.LoginRes;
import com.example.t.model.User;
import com.example.t.network.ServiceCreator;
import com.example.t.network.UserService;
import com.example.t.util.MyConstants;
import com.example.t.view.animator.ColoredToast;
import com.google.gson.Gson;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private static final String TAG = LoginFragment.class.getSimpleName();

    private View view;

    private User mUser;
    private String lockPassword;
    private StringBuilder input;

    private UserService userService = (UserService) ServiceCreator.INSTANCE.create(UserService.class);

    private TextView txtPhone;
    private TextView txtPassword;
    private TextView txtName;
    private TextView txtTitle;
    private View viewName;
    private Button btnLogin;
    private TextView btnRegister;

    private Toast mToast;
    private ColoredToast mCToast;

    private Handler handler = new Handler();

    private Executor executor = new Executor() {
        @Override
        public void execute(Runnable command) {
            handler.post(command);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //判断加载哪个页面
        String userJson = getActivity().getSharedPreferences(MyConstants.SP_NAME, getActivity().MODE_PRIVATE).getString(MyConstants.USER, "");
        Gson gson = new Gson();
        mUser = gson.fromJson(userJson, User.class);

        lockPassword = getActivity().getSharedPreferences(MyConstants.SP_NAME, getActivity().MODE_PRIVATE).getString(MyConstants.LOCK_PASSWORD, "");

        if (mUser != null) {
            // 有缓存
            if (lockPassword == null || lockPassword.equals("")) {
                // 没锁
                enterMainPage(mUser);
                view = inflater.inflate(R.layout.blank_page, container, false);
            } else {
                // 有锁
                view = inflater.inflate(R.layout.lock_page, container, false);
                initLock(mUser);
            }
        } else {
            //没缓存
            view = inflater.inflate(R.layout.log_in, container, false);
            initLogin();
        }
        return view;
    }


    private void initLogin() {
        btnLogin = view.findViewById(R.id.login_btn);
        btnRegister = view.findViewById(R.id.reg_btn);
        txtPhone = view.findViewById(R.id.login_phone);
        txtPassword = view.findViewById(R.id.login_password);
        txtName = view.findViewById(R.id.login_name);
        txtTitle = view.findViewById(R.id.login_title);
        viewName = view.findViewById(R.id.login_name_view);
        viewName.setVisibility(View.GONE);

        txtPhone.setOnClickListener(v -> {
            typePhone();
        });
        txtPassword.setOnClickListener(v -> {
            typePassword();
        });
        txtName.setOnClickListener(v -> {
            typeName();
        });
        btnLogin.setOnClickListener(v -> {
            if (viewName.getVisibility() == View.VISIBLE) {
                register();
            } else {
                login();
            }
        });
        btnRegister.setOnClickListener(v -> {
            if (viewName.getVisibility() == View.VISIBLE) {
                viewName.setVisibility(View.GONE);
                btnLogin.setText("登陆");
                btnRegister.setText("注册");
                txtTitle.setText("登陆");
            } else {
                viewName.setVisibility(View.VISIBLE);
                btnLogin.setText("注册");
                btnRegister.setText("登陆");
                txtTitle.setText("注册");
            }
        });
    }

    private void initLock(User user) {

        Button biometricLoginButton = view.findViewById(R.id.fingerprint_btn);
        BiometricManager biometricManager = BiometricManager.from(getActivity());
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d(TAG, "应用可以进行生物识别技术进行身份验证");
                biometricLoginButton.setOnClickListener(view -> showBiometricPrompt());
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                biometricLoginButton.setOnClickListener(view -> showTip("该设备上没有搭载可用的生物特征功能"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                biometricLoginButton.setOnClickListener(view -> showTip("生物识别功能当前不可用"));
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                biometricLoginButton.setOnClickListener(view -> showTip("用户没有录入生物识别数据"));
                break;
        }

        input = new StringBuilder();

        view.findViewById(R.id.digit_0).setOnClickListener(v -> {
            input.append("0");
            checkInput(user);
        });
        view.findViewById(R.id.digit_1).setOnClickListener(v -> {
            input.append("1");
            checkInput(user);
        });
        view.findViewById(R.id.digit_2).setOnClickListener(v -> {
            input.append("2");
            checkInput(user);
        });
        view.findViewById(R.id.digit_3).setOnClickListener(v -> {
            input.append("3");
            checkInput(user);
        });
        view.findViewById(R.id.digit_4).setOnClickListener(v -> {
            input.append("4");
            checkInput(user);
        });
        view.findViewById(R.id.digit_5).setOnClickListener(v -> {
            input.append("5");
            checkInput(user);
        });
        view.findViewById(R.id.digit_6).setOnClickListener(v -> {
            input.append("6");
            checkInput(user);
        });
        view.findViewById(R.id.digit_7).setOnClickListener(v -> {
            input.append("7");
            checkInput(user);
        });
        view.findViewById(R.id.digit_8).setOnClickListener(v -> {
            input.append("8");
            checkInput(user);
        });
        view.findViewById(R.id.digit_9).setOnClickListener(v -> {
            input.append("9");
            checkInput(user);
        });
    }

    private void checkInput(User user) {
        showPoint(input.length());
        if (input.toString().equals(lockPassword)) {
            enterMainPage(user);
            input.delete(0, input.length());
        } else if (input.length() == 3) {
            Log.d(TAG, "color 1 =" + ContextCompat.getColor(getActivity(), R.color.font_white));
            Log.d(TAG, "color 2 =" + ContextCompat.getColor(getActivity(), R.color.warning_red));
            ColoredToast.showToast(getActivity(), "安全码错误，请重新输入", false,
                    ContextCompat.getColor(getActivity(), R.color.white), getResources().getColor(R.color.warning_red));
            input.delete(0, input.length());
            showPoint(0);
        }
    }

    private void showPoint(int no) {
        switch (no) {
            case 1:
                view.findViewById(R.id.lock_1).setBackground(getActivity().getDrawable(R.drawable.theme_point));
                view.findViewById(R.id.lock_2).setBackground(getActivity().getDrawable(R.drawable.white_point));
                view.findViewById(R.id.lock_3).setBackground(getActivity().getDrawable(R.drawable.white_point));
                break;
            case 2:
                view.findViewById(R.id.lock_1).setBackground(getActivity().getDrawable(R.drawable.theme_point));
                view.findViewById(R.id.lock_2).setBackground(getActivity().getDrawable(R.drawable.theme_point));
                view.findViewById(R.id.lock_3).setBackground(getActivity().getDrawable(R.drawable.white_point));
                break;
            case 3:
                view.findViewById(R.id.lock_1).setBackground(getActivity().getDrawable(R.drawable.theme_point));
                view.findViewById(R.id.lock_2).setBackground(getActivity().getDrawable(R.drawable.theme_point));
                view.findViewById(R.id.lock_3).setBackground(getActivity().getDrawable(R.drawable.theme_point));
                break;
            default:
                view.findViewById(R.id.lock_1).setBackground(getActivity().getDrawable(R.drawable.white_point));
                view.findViewById(R.id.lock_2).setBackground(getActivity().getDrawable(R.drawable.white_point));
                view.findViewById(R.id.lock_3).setBackground(getActivity().getDrawable(R.drawable.white_point));
                break;

        }
    }

    private void typePhone() {
        txtPhone.setText("");
    }

    private void typePassword() {
        txtPassword.setText("");
    }

    private void typeName() {
        txtName.setText("");
    }

    public void register() {
        String phoneNumber = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        String name = txtName.getText().toString();
        User user = new User(phoneNumber, password, name);
        Log.d(TAG, "Register: " + user.toString());

        Call<User> call = userService.register(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                User res = response.body();
                Log.d(TAG, "Register successfully!" + res.toString());
                enterMainPage(res);
                setDefaultUser(res);
            }

            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Log.d(TAG, "Register 请求失败信息: " + t.getMessage());
            }
        });
    }

    public void login() {
        String phoneNumber = txtPhone.getText().toString();
        String password = txtPassword.getText().toString();
        User user = new User(phoneNumber, password);
        Log.d(TAG, "Login: " + user.toString());

        Call<LoginRes> call = userService.login(user);
        call.enqueue(new Callback<LoginRes>() {
            @Override
            public void onResponse(@NonNull Call<LoginRes> call, @NonNull Response<LoginRes> response) {
                LoginRes res = response.body();
                if (res.getCode() == 200) {
                    Log.d(TAG, "Login successfully! " + res.getUser().toString());
                    enterMainPage(res.getUser());
                    setDefaultUser(res.getUser());
                } else {
                    Log.d(TAG, "Login failed!" + res.getUser().toString());
//                    showTip("登陆错误，请重新输入");
                    ColoredToast.showToast(getActivity(), "密码错误，请重新输入", false,
                            getResources().getColor(R.color.white), getResources().getColor(R.color.warning_red));
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginRes> call, Throwable t) {
                Log.d(TAG, "Login 请求失败信息: " + t.getMessage());
            }
        });
    }

    private void enterMainPage(User user) {
        Intent intent = new Intent(getActivity(), MainPageActivity.class);
        intent.putExtra("user", (Parcelable) user);
        startActivity(intent);
        getActivity().finish();
    }

    private void setDefaultUser(User user) {
        // 存进本地
        Gson gson = new Gson();
        String json = gson.toJson(user);
        SharedPreferences.Editor editor = ((MainActivity) getActivity()).mSharedPreferences.edit();
        if (!TextUtils.isEmpty(json)) {
            editor.putString(MyConstants.USER, json);
            Log.d(TAG, "add user to sp");
        }
        editor.apply();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showTip(final String str) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    //生物认证的setting
    private void showBiometricPrompt() {
        BiometricPrompt.PromptInfo promptInfo =
                new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("指纹") //设置大标题
                        .setSubtitle("使用指纹解锁") // 设置标题下的提示
                        .setNegativeButtonText("取消") //设置取消按钮
                        .build();

        //需要提供的参数callback
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            //各种异常的回调
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                showTip("认证错误" + errString);
            }

            //认证成功的回调
            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                BiometricPrompt.CryptoObject authenticatedCryptoObject =
                        result.getCryptoObject();
                // User has verified the signature, cipher, or message
                // authentication code (MAC) associated with the crypto object,
                // so you can use it in your app's crypto-driven workflows.
//                showTip("Authentication success");
                enterMainPage(mUser);
            }

            //认证失败的回调
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                showTip("认证失败");
            }
        });

        // 显示认证对话框
        biometricPrompt.authenticate(promptInfo);
    }
}
