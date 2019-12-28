package ru.riselab.keitracker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setTitle(R.string.about);

        TextView privacyPolicyLink = (TextView) findViewById(R.id.aboutPrivacyPolicy);
        privacyPolicyLink.setMovementMethod(LinkMovementMethod.getInstance());

        TextView termsAndConditionsLink = (TextView) findViewById(R.id.aboutTermsAndConditions);
        termsAndConditionsLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
