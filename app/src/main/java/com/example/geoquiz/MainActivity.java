package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private int trueCount = 0;
    private int falseCount = 0;
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
    private int mQuestionAnsweredCount = 0;

    private void d(String message) {
        android.util.Log.d(TAG, message);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceSate) {
        super.onSaveInstanceState(savedInstanceSate);
        d("onSaveInstanceState");
        savedInstanceSate.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d("onCreate(Bundle) called");
        if (savedInstanceState != null) mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
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
        setQuestionText();
        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(getListener());
        mPreviousButton = findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(getListener(false));
        mQuestionTextView.setOnClickListener(getListener());
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
            ++mQuestionAnsweredCount;
            mQuestionAnswers[mCurrentIndex] = ans == getAnswer() ? 1 : 2;
//            String toastString = getString(ans == getAnswer() ? R.string.correct_toast : R.string.incorrect_toast);
//            Toast t = Toast.makeText(this, toastString, Toast.LENGTH_SHORT);
//            t.setGravity(Gravity.TOP, 0, 0);
//            t.show();
            setButtonState();
            resultToast();
        }
    }

    private void resultToast() {
        if (mQuestionAnsweredCount == mQuestionBank.length) {
            double total = 0;
            for (int result : mQuestionAnswers) {
                if (result == 1) ++total;
            }
            double result = 100 * (total / mQuestionBank.length);
            String toastString = String.format("%f%% Correct", result);
            Toast t = Toast.makeText(this, toastString, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
        }
    }

    private void setButtonState() {
        mTrueButton.setBackgroundColor(0x00000000);
        mFalseButton.setBackgroundColor(0x00000000);
        if (mQuestionAnswers[mCurrentIndex] == 0) {
            mFalseButton.setEnabled(true);
            mTrueButton.setEnabled(true);
            return;
        }
        Button colorButton = getAnswer() ? mTrueButton : mFalseButton;
        int color = mQuestionAnswers[mCurrentIndex] == 1 ? ContextCompat.getColor(this, R.color.correctAnswer) : ContextCompat.getColor(this, R.color.wrongAnswer);
        colorButton.setBackgroundColor(color);
        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
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
