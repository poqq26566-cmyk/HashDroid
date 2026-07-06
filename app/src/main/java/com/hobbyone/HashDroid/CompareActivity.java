/* CompareActivity.java --
   版权所有 (C) 2010 Christophe Bouyer (Hobby One)

   此文件是 Hash Droid 的一部分。

   Hash Droid 是自由软件：您可以根据自由软件基金会发布的 GNU 通用公共许可证的条款重新分发和/或修改它；无论是许可证的第 3 版，还是（根据您的选择）任何更高版本。

   Hash Droid 的分发是希望它有用，但没有任何担保；甚至没有适销性或特定用途适用性的隐含担保。有关更多详细信息，请参阅 GNU 通用公共许可证。

   您应该已经随 Hash Droid 一起收到了 GNU 通用公共许可证的副本。如果没有，请参见 <http://www.gnu.org/licenses/>。
 */

package com.hobbyone.HashDroid;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CompareActivity extends Activity {

    private EditText mEditText1 = null;
    private EditText mEditText2 = null;
    private Button mCompareButton = null;
    private Button mClearButton1 = null;
    private Button mClearButton2 = null;
    private TextView mResultTV = null;

    /**
     * 当 Activity 首次创建时调用。
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compare);

        mEditText1 = (EditText) findViewById(R.id.edit_txt1);
        mClearButton1 = (Button) findViewById(R.id.ClearButton1);
        mEditText2 = (EditText) findViewById(R.id.edit_txt2);
        mClearButton2 = (Button) findViewById(R.id.ClearButton2);
        mCompareButton = (Button) findViewById(R.id.CompareButton);
        mResultTV = (TextView) findViewById(R.id.label_result);

        mCompareButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时执行操作
                Editable InputEdit1 = mEditText1.getText();
                String sInputText1 = InputEdit1.toString();
                Editable InputEdit2 = mEditText2.getText();
                String sInputText2 = InputEdit2.toString();
                if (sInputText1 != null && sInputText2 != null) {
                    String sText = "";
                    int IsIdentical = sInputText1
                            .compareToIgnoreCase(sInputText2);
                    if (IsIdentical == 0) {
                        sText = getString(R.string.IdenticalHashes);
                        mResultTV.setTextColor(Color.GREEN);
                    } else {
                        sText = getString(R.string.DifferentHashes);
                        mResultTV.setTextColor(Color.RED);
                    }
                    if (mResultTV != null)
                        mResultTV.setText(sText);
                }
            }
        });

        mClearButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时执行操作
                mEditText1.setText("");
                mResultTV.setText("");
            }
        });

        mClearButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时执行操作
                mEditText2.setText("");
                mResultTV.setText("");
            }
        });
    }
}
