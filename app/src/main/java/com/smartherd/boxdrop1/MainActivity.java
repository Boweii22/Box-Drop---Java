package com.smartherd.boxdrop1;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private ImageView resetButton;
    private BoxDropView boxDropView;
    private ImageView themeToggleIcon;
    private boolean isDarkMode;
    FrameLayout rootLayout;
    TextView textView;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Load theme based on preference
        // Load theme preference
        sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        isDarkMode = sharedPreferences.getBoolean("isDarkMode", false);

        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        rootLayout = findViewById(R.id.rootLayout);
        textView = findViewById(R.id.textView);
        themeToggleIcon = findViewById(R.id.themeToggleImage);

        // Apply initial theme
        applyTheme(isDarkMode, false);

        // Toggle theme on icon click
        themeToggleIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDarkMode = !isDarkMode;

                // Save the theme state in SharedPreferences
                editor = sharedPreferences.edit();
                editor.putBoolean("isDarkMode", isDarkMode);
                editor.apply();

                applyTheme(isDarkMode, true);
            }
        });


        boxDropView = findViewById(R.id.boxDropView);

        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> {
            boxDropView.resetBoxes(); //to reset the boxes
        });


    }

//    private void applyTheme(boolean isDarkMode) {
//        if (isDarkMode) {
//            // Set dark theme elements (black background, white text)
//            rootLayout.setBackgroundColor(Color.BLACK);
//            textView.setTextColor(Color.WHITE);
//            themeToggleIcon.setImageResource(R.drawable.sun_icon);  // Icon for dark mode
//        } else {
//            // Set light theme elements (white background, black text)
//            rootLayout.setBackgroundColor(Color.WHITE);
//            textView.setTextColor(Color.BLACK);
//            themeToggleIcon.setImageResource(R.drawable.moon_icon);   // Icon for light mode
//        }
//    }
private void applyTheme(boolean isDarkMode, boolean animate) {
    int colorFrom = ((ColorDrawable) rootLayout.getBackground()).getColor();
    int colorTo;
    int textColorFrom = textView.getCurrentTextColor();
    int textColorTo;

    if (isDarkMode) {
        colorTo = Color.BLACK; // Dark background
        textColorTo = Color.WHITE; // White text
        themeToggleIcon.setImageResource(R.drawable.moon_icon); // Dark mode icon
    } else {
        colorTo = Color.WHITE; // Light background
        textColorTo = Color.BLACK; // Black text
        themeToggleIcon.setImageResource(R.drawable.sun_icon); // Light mode icon
    }

    if (animate) {
        // Animate background color change
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // Duration of animation
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                rootLayout.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();

        // Animate text color change
        ValueAnimator textColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), textColorFrom, textColorTo);
        textColorAnimation.setDuration(500); // Duration of animation
        textColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                textView.setTextColor((int) animator.getAnimatedValue());
            }
        });
        textColorAnimation.start();

        //Animate icon color change
        ValueAnimator iconColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator());
        iconColorAnimation.setDuration(500);
        iconColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animator) {
                resetButton.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        iconColorAnimation.start();

        // Add animation for the icon fade transition
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(themeToggleIcon, "alpha", 1f, 0f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(themeToggleIcon, "alpha", 0f, 1f);
        fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeOut.setDuration(250); // Half duration for fade out
        fadeIn.setDuration(250);  // Half duration for fade in

        fadeOut.start();
        fadeOut.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) { }

            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                fadeIn.start();
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animator) { }

            @Override
            public void onAnimationRepeat(android.animation.Animator animator) { }
        });
    } else {
        // Apply theme immediately without animation
        rootLayout.setBackgroundColor(colorTo);
        textView.setTextColor(textColorTo);
    }
}
}