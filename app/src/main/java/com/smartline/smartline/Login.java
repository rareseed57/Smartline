package com.smartline.smartline;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.*;

import static android.app.Activity.RESULT_OK;

public class Login extends Fragment {
    private static final String TAG = "Login";
    List<AuthUI.IdpConfig> providers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.login, container, false);
        if(!((MainActivity) getActivity()).getLogged())
        {
            loginScreen();
        }
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK)
            {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                ((MainActivity) getActivity()).setUser(user);
                ((MainActivity) getActivity()).setLogged(true);
                ((((MainActivity) getActivity()).refUsers)).child((user.getEmail()).replace("@","H").replace(".","P")).setValue(user.getDisplayName());
                Toast.makeText(getContext(), "Successfully logged in. \n Account: "+ (user.getEmail()), Toast.LENGTH_SHORT).show();
                ((MainActivity) getActivity()).setViewPager(((MainActivity) getActivity()).idHome);
            }
            else
                {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    if (response == null)
                    {
                        Toast.makeText(getContext(), "User requested disconnection.", Toast.LENGTH_SHORT).show();
                        loginScreen();
                    }
                    else if(response.getError().getErrorCode()== ErrorCodes.NO_NETWORK)
                    {
                        Toast.makeText(getContext(), "Login failed. Check your internet connection and retry " + response.getError().getErrorCode(), Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).setLogged(false);
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Login failed. Error code: " + response.getError().getErrorCode(), Toast.LENGTH_SHORT).show();
                        ((MainActivity) getActivity()).setLogged(false);
                    }

                }
        }
    }

    public void loginScreen()
    {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_bkg)// Set logo drawable
                        .setTheme(R.style.AppTheme)//set theme
                        .setIsSmartLockEnabled(true)
                        .build(),
                1);
    }
}