package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.myapplication.CheatActivity.EXTRA_ANSWER_SHOWN;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String EXTRA_ANSWER_IS_TRUE = "ANSWER_IS_TRUE";
    private static final int CHEAT_REQ_CODE = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private Button mPrevButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;
    private TextView mCheatTimesTextView;
    private int correctNumber = 0;
    private boolean isCheater;
    private int cheatCount = 3;

    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_australia, true),
        new Question(R.string.question_oceans, true),
        new Question(R.string.question_mideast, false),
        new Question(R.string.question_africa, false),
        new Question(R.string.question_americas, true),
        new Question(R.string.question_asia, true)
    };

    private int totalNumber = mQuestionBank.length;

    private int mCurrentIndex = 0;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "this is onCreate()");

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text);
        mCheatTimesTextView = (TextView) findViewById(R.id.cheat_time_textview);
        mTrueButton = (Button) findViewById(R.id.true_button);
        mFalseButton = (Button) findViewById(R.id.false_button);
        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);
        mCheatButton = (Button) findViewById(R.id.cheat_button);

        updateQuestion();

        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue()), CHEAT_REQ_CODE);
            }
        });

        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mToast = Toast.makeText(QuizActivity.this, R.string.correct_toast, Toast.LENGTH_SHORT);
//                mToast.setGravity(Gravity.TOP, 0, 50);
//
//                mToast.show();
                checkAnswer(true);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mToast = Toast.makeText(QuizActivity.this, R.string.incorrect_toast, Toast.LENGTH_SHORT);
//                mToast.setGravity(Gravity.TOP, 0, 50);
//
//                mToast.show();
                checkAnswer(false);
            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                isCheater = false;
                updateQuestion();
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentIndex >= 1) {
                    mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                } else {
                    mCurrentIndex = mQuestionBank.length - 1;
                }

                updateQuestion();
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        updateCheatText();
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (isCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                correctNumber++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(QuizActivity.this, messageResId, Toast.LENGTH_SHORT).show();

        if (mCurrentIndex == mQuestionBank.length - 1) {
            checkCorrectness();
        }
    }

    private void checkCorrectness() {
        Toast.makeText(QuizActivity.this, correctNumber * 1.0 / totalNumber + "", Toast.LENGTH_SHORT).show();
    }

    public static Intent newIntent(Context context, boolean isAnswerTrue) {
        Intent intent = new Intent(context, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, isAnswerTrue);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == CHEAT_REQ_CODE) {
            if (data == null) {
                return;
            }
            isCheater = CheatActivity.wasAnswerShown(data);
            if (isCheater && cheatCount > 0) {
                cheatCount--;
                updateCheatText();
            }
        }
    }

    private void updateCheatText() {
        String cheatText = String.format("%d times left", cheatCount);
        mCheatTimesTextView.setText(cheatText);

        if (cheatCount <= 0) {
            mCheatButton.setClickable(false);
            mCheatButton.setEnabled(false);
        }
    }
}