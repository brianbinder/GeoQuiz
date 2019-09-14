package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
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
    private int mCurrentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void setQuestionText() {
        mQuestionTextView.setText(mQuestionBank[mCurrentIndex].getTextResId());
    }

    private void answerQuestion(boolean ans) {
        String toastString = getString(ans == getAnswer() ? R.string.correct_toast : R.string.incorrect_toast);
        Toast t = Toast.makeText(this, toastString, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.TOP, 0, 0);
        t.show();

    }

    private boolean getAnswer() {
        return mQuestionBank[mCurrentIndex].isAnswerTrue();
    }

    private View.OnClickListener getListener() { return getListener(true); }
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
