package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String CHEAT_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true";
    private boolean mAnswerIsTrue;
    private Button mShowAnswerButton;
    private TextView mAnswerTextView;
    private TextView mVersionTextView;
    private static final String EXTRA_ANSWER_SHOW ="com.example.geoquiz.answer_shown";

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        return new Intent(packageContext, CheatActivity.class).putExtra(CHEAT_ANSWER_IS_TRUE, answerIsTrue);
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOW, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mVersionTextView = findViewById(R.id.version_text_view);
        mVersionTextView.setText("API Level: " + Build.VERSION.SDK_INT);

        mAnswerIsTrue = getIntent().getBooleanExtra(CHEAT_ANSWER_IS_TRUE, false);
        mAnswerTextView = findViewById(R.id.answer_text_view);
        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mAnswerTextView.setText(mAnswerIsTrue ? R.string.true_button : R.string.false_button);
               informParent(true);
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                   int cx = mShowAnswerButton.getWidth() / 2;
                   int cy = mShowAnswerButton.getHeight() / 2;
                   float radius = mShowAnswerButton.getWidth();
                   Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                   anim.addListener(new AnimatorListenerAdapter() {
                       @Override
                       public void onAnimationEnd(Animator animation) {
                           super.onAnimationEnd(animation);
                           mShowAnswerButton.setVisibility(View.INVISIBLE);
                       }
                   });
                   anim.start();
               } else {
                   mShowAnswerButton.setVisibility(View.INVISIBLE);
               }
            }
        });
    }

    private void informParent(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOW, isAnswerShown);
        setResult(RESULT_OK, data);
    }
}
