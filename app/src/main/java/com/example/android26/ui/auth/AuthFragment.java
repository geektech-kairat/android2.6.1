package com.example.android26.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android26.R;
import com.example.android26.databinding.FragmentAuthBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AuthFragment extends Fragment {
    private FragmentAuthBinding binding;
    private NavController navController;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        navController = NavHostFragment.findNavController(this);
        binding = FragmentAuthBinding.inflate(inflater, container, false);
        setmCallbacks();
        binding.btn.setOnClickListener(v -> {
            click(Objects.requireNonNull(binding.edit.getText()).toString());
        });


        return binding.getRoot();
    }

    public void click(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(requireActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void setmCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            private static final String TAG = "tag";

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.e(TAG, "onVerificationCompleted: ");
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e(TAG, "onVerificationFailed: ");
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                binding.edit.setText("");
                setTextClick("Введите смс код", "Проверить");
                binding.btn.setOnClickListener(v -> {
                    check(s, Objects.requireNonNull(binding.edit.getText()).toString());
                });
                Log.e(TAG, "onCodeSent: ");
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                binding.edit.setText("");
                Log.e(TAG, "onCodeAutoRetrievalTimeOut: ");
                setTextClick("Введите номер телефона", "Отправить");

                binding.btn.setOnClickListener(v -> {
                    click(Objects.requireNonNull(binding.edit.getText()).toString());
                });
            }
        };
    }

    public void setTextClick(String titleForEditText, String btnText) {
        binding.edit.setHint(titleForEditText);
        binding.btn.setText(btnText);
    }

    public void signIn(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    navController.navigate(R.id.navigation_home);
                } else {
                    Toast.makeText(requireContext(), " error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void check(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signIn(credential);

    }
}