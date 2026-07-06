/* FileActivity.java --
   版权所有 (C) 2010 Christophe Bouyer (Hobby One)

   此文件是 Hash Droid 的一部分。

   Hash Droid 是自由软件：您可以根据自由软件基金会发布的 GNU 通用公共许可证的条款重新分发和/或修改它；无论是许可证的第 3 版，还是（根据您的选择）任何更高版本。

   Hash Droid 的分发是希望它有用，但没有任何担保；甚至没有适销性或特定用途适用性的隐含担保。有关更多详细信息，请参阅 GNU 通用公共许可证。

   您应该已经随 Hash Droid 一起收到了 GNU 通用公共许可证的副本。如果没有，请参见 <http://www.gnu.org/licenses/>。
 */

package com.hobbyone.HashDroid;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.OpenableColumns;
import android.text.ClipboardManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileActivity extends Activity implements Runnable {
    private Button mSelectFileButton = null;
    private CheckBox mCheckBox = null;
    private Button mGenerateButton = null;
    private Button mCopyButton = null;
    private Spinner mSpinner = null;
    private TextView mResultTV = null;
    private String msFileSize = "";
    private String msHash = "";
    private String[] mFunctions;
    private ClipboardManager mClipboard = null;
    private final int SELECT_FILE_REQUEST = 0;
    private HashFunctionOperator mHashOpe = null;
    private ProgressDialog mProgressDialog = null;
    private int miItePos = -1;
    private Uri mSelectedFileUri = null;

    /**
     * 当 Activity 首次创建时调用。
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file);

        mSelectFileButton = (Button) findViewById(R.id.SelectFileButton);
        mGenerateButton = (Button) findViewById(R.id.GenerateButton);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mResultTV = (TextView) findViewById(R.id.label_result);
        mCopyButton = (Button) findViewById(R.id.CopyButton);
        mClipboard = (ClipboardManager) getSystemService("clipboard");
        mFunctions = getResources().getStringArray(R.array.Algo_Array);
        mCheckBox = (CheckBox) findViewById(R.id.UpperCaseCB);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Algo_Array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(5); // 默认选择 MD5

        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                                       View selectedItemView, int position, long id) {
                // 在此处编写您的代码
                // 隐藏复制按钮
                if (!msHash.equals(""))
                    mCopyButton.setVisibility(View.INVISIBLE);
                // 清空结果文本视图
                if (mResultTV != null)
                    mResultTV.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 在此处编写您的代码
            }
        });

        mSelectFileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent openExplorerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    if (null != openExplorerIntent) {
                        openExplorerIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        openExplorerIntent.setType("*/*");
                        startActivityForResult(Intent.createChooser(openExplorerIntent, "选择文件"), SELECT_FILE_REQUEST);
                    }
                } catch (ActivityNotFoundException e) {
                }
            }
        });

        mGenerateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时执行操作
                if (null != mSelectedFileUri) {
                    miItePos = mSpinner.getSelectedItemPosition();
                    File fileToHash = new File(mSelectedFileUri.getPath());
                    if (fileToHash != null)
                        ComputeAndDisplayHash();
                    else {
                        String sWrongFile = getString(R.string.wrong_file);
                        if (mResultTV != null)
                            mResultTV.setText(sWrongFile);
                        if (mCopyButton != null)
                            mCopyButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        mCopyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时执行操作
                if (mClipboard != null) {
                    mClipboard.setText(msHash);
                    String sCopied = getString(R.string.copied);
                    Toast.makeText(FileActivity.this, sCopied,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCheckBox.setChecked(false); // 默认小写
        mCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击时执行操作
                if (!msHash.equals("")) {
                    // 哈希值已经计算过，只需将其转换为小写或大写
                    String OldHash = msHash;
                    if (mCheckBox.isChecked()) {
                        msHash = OldHash.toUpperCase();
                    } else {
                        msHash = OldHash.toLowerCase();
                    }
                    if (mResultTV != null) {
                        String sResult = mResultTV.getText().toString();
                        sResult = sResult.replaceAll(OldHash, msHash);
                        mResultTV.setText(sResult);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_FILE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                mSelectedFileUri = data.getData(); // 包含文件位置的 Uri
                if (null != mSelectedFileUri) {
                    Cursor cursor = getContentResolver().query(mSelectedFileUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        String ret = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        mSelectFileButton.setText(ret);
                    }
                }
            }
        }
    }

    private void ComputeAndDisplayHash() {
        if (mHashOpe == null)
            mHashOpe = new HashFunctionOperator();
        String sAlgo = "";
        if (miItePos == 0)
            sAlgo = "Adler-32";
        else if (miItePos == 1)
            sAlgo = "CRC-32";
        else if (miItePos == 2)
            sAlgo = "Haval";
        else if (miItePos == 3)
            sAlgo = "md2";
        else if (miItePos == 4)
            sAlgo = "md4";
        else if (miItePos == 5)
            sAlgo = "md5";
        else if (miItePos == 6)
            sAlgo = "ripemd-128";
        else if (miItePos == 7)
            sAlgo = "ripemd-160";
        else if (miItePos == 8)
            sAlgo = "sha-1";
        else if (miItePos == 9)
            sAlgo = "sha-256";
        else if (miItePos == 10)
            sAlgo = "sha-384";
        else if (miItePos == 11)
            sAlgo = "sha-512";
        else if (miItePos == 12)
            sAlgo = "tiger";
        else if (miItePos == 13)
            sAlgo = "whirlpool";
        mHashOpe.SetAlgorithm(sAlgo);

        String sCalculating = getString(R.string.Calculating);
        mProgressDialog = ProgressDialog.show(FileActivity.this, "",
                sCalculating, true);

        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    // 线程启动时调用
    public void run() {
        msHash = "";
        msFileSize = "";

        if (null != mSelectedFileUri) {
            if (mHashOpe != null) {
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(mSelectedFileUri);

                } catch (FileNotFoundException e1) {
                }
                if (null != inputStream) {
                    msHash = mHashOpe.FileToHash(inputStream);
                }
            }

            Cursor cursor = getContentResolver().query(mSelectedFileUri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                msFileSize = FileSizeDisplay(cursor.getLong(sizeIndex), false);
            }
        }
        handler.sendEmptyMessage(0);
    }

    private String FileSizeDisplay(long lbytes, boolean bSI) {
        int unit = bSI ? 1000 : 1024;
        if (lbytes < unit)
            return lbytes + " B";
        int exp = (int) (Math.log(lbytes) / Math.log(unit));
        String pre = (bSI ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
                + (bSI ? "" : "i");
        return String.format("%.2f %sB", lbytes / Math.pow(unit, exp), pre);
    }

    // 计算完成后调用此方法
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 隐藏进度对话框
            if (mProgressDialog != null)
                mProgressDialog.dismiss();
            if (null != mSelectedFileUri) {
                File fileToHash = new File(mSelectedFileUri.getPath());
                if (fileToHash != null) {
                    Resources res = getResources();
                    String fileName = "";
                    Cursor cursor = getContentResolver().query(mSelectedFileUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                    String sFileNameTitle = String
                            .format(res.getString(R.string.FileName),
                                    fileName);
                    String sFileSizeTitle = String.format(
                            res.getString(R.string.FileSize), msFileSize);
                    String sFileHashTitle = "";
                    if (!msHash.equals("")) {
                        if (mCheckBox != null) {
                            if (mCheckBox.isChecked()) {
                                msHash = msHash.toUpperCase();
                            } else {
                                msHash = msHash.toLowerCase();
                            }
                        }
                        String Function = "";
                        if (miItePos >= 0)
                            Function = mFunctions[miItePos];
                        sFileHashTitle = String.format(
                                res.getString(R.string.Hash), Function, msHash);
                        // 显示复制按钮
                        if (mCopyButton != null)
                            mCopyButton.setVisibility(View.VISIBLE);
                    } else {
                        sFileHashTitle = String.format(
                                res.getString(R.string.unable_to_calculate),
                                fileToHash.getName());
                        // 隐藏复制按钮
                        if (mCopyButton != null)
                            mCopyButton.setVisibility(View.INVISIBLE);
                    }

                    if (mResultTV != null)
                        mResultTV.setText(sFileNameTitle + sFileSizeTitle + sFileHashTitle);
                }
            }
        }
    };
}
