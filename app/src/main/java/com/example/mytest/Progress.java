package com.example.mytest;

import android.app.ProgressDialog;
import android.content.Context;

public class Progress {
    public Progress() {
    }

    private ProgressDialog mProgressDialog;

    public void showProgressDialog(Context context) {
        closeProgressDialog();
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("");
        mProgressDialog.setMessage("잠시만 기다려주세요");
        mProgressDialog.setCancelable(true);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    public void closeProgressDialog() {
        if((mProgressDialog != null) && (mProgressDialog.isShowing())) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }
}
