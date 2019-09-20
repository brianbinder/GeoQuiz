package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String ANSWERS_INDEX = "answers";
    private static final String CHEAT_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };
    private int[] mQuestionAnswers = new int[]{0, 0, 0, 0, 0, 0};
    private int mCurrentIndex = 0;

    private void d(String message) {
        android.util.Log.d(TAG, message);
    }

    private void d(String message, Throwable tr) {
        android.util.Log.d(TAG, message, tr);
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceSate) {
        super.onSaveInstanceState(savedInstanceSate);
        d("onSaveInstanceState");
        savedInstanceSate.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceSate.putIntArray(ANSWERS_INDEX, mQuestionAnswers);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data != null && CheatActivity.wasAnswerShown(data)) {
                mQuestionAnswers[mCurrentIndex] = 3;
                setButtonState();
            }


        }
    }

    @Override
    public void onStart() {
        super.onStart();
        d("onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        d("onResume() called");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d("onCreate(Bundle) called");
        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mQuestionAnswers = savedInstanceState.getIntArray(ANSWERS_INDEX);
        }
        setContentView(R.layout.activity_main);

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerQuestion(true);
            }
        });
        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                answerQuestion(false);
            }
        });

        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(getListener());

        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(getListener());
        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(getListener(false));

        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(CheatActivity.newIntent(MainActivity.this, getAnswer()), REQUEST_CODE_CHEAT);
            }
        });
        setQuestionText();
    }


    @Override
    public void onPause() {
        super.onPause();
        d("onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        d("onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        d("onDestroy() called");
    }

    private void setQuestionText() {
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
        setButtonState();
    }

    private void answerQuestion(boolean ans) {
        if (mQuestionAnswers[mCurrentIndex] == 0) {
            mQuestionAnswers[mCurrentIndex] = ans == getAnswer() ? 1 : 2;
            setButtonState();
        }
    }

    private void resultToast() {
        for (int result : mQuestionAnswers) {
            if (result == 0) return;
        }

        double total = 0;
        double correct = 0;
        for (int result : mQuestionAnswers) {
            if (result == 1) ++correct;
            if (result != 3) ++total;
        }
        String toastString = "You cheated on every question!";
        if (total != 0) {
            double result = 100 * (correct / total);
            toastString = String.format("%f%% Correct", result);
        }
        Toast t = Toast.makeText(this, toastString, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP, 0, 0);
        t.show();
    }

    private void setButtonState() {
        mTrueButton.setBackgroundColor(0x00000000);
        mFalseButton.setBackgroundColor(0x00000000);
        if (mQuestionAnswers[mCurrentIndex] == 0) {
            mFalseButton.setEnabled(true);
            mTrueButton.setEnabled(true);
            mCheatButton.setEnabled(true);
            return;
        }
        Button colorButton = getAnswer() ? mTrueButton : mFalseButton;
        colorButton.setBackgroundColor(answerColor());
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        mCheatButton.setEnabled(false);
        resultToast();
    }

    private int answerColor() {
        switch (mQuestionAnswers[mCurrentIndex]) {
            case 1:
                return ContextCompat.getColor(this, R.color.correctAnswer);
            case 2:
                return ContextCompat.getColor(this, R.color.wrongAnswer);
            case 3:
                return ContextCompat.getColor(this, R.color.cheatAnswer);

        }
        return 0x00000000;
    }

    private boolean getAnswer() {
        return mQuestionBank[mCurrentIndex].isAnswerTrue();
    }

    private View.OnClickListener getListener() {
        return getListener(true);
    }

    private View.OnClickListener getListener(final boolean next) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeQuestion(next);
                setQuestionText();
            }
        };
    }

    private void changeQuestion(boolean next) {
        mCurrentIndex = (mCurrentIndex + (next ? 1 : -1)) % mQuestionBank.length;
        if (mCurrentIndex < 0) mCurrentIndex = mQuestionBank.length - 1;
    }
}
