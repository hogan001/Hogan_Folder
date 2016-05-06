
package com.witskies.manager.fileexplorer;

import com.witskies.w_manager.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * new file dialog
 * 
 * @author lance
 * @version ����ʱ�䣺2015-4-30 ����2:19:29
 */
public class FileTextInputDialog extends AlertDialog {
	private String mInputText;
	private String mTitle;
	private String mMsg;
	private OnFinishListener mListener;
	private Context mContext;
	private View mView;
	private EditText mFolderName;

	public interface OnFinishListener {
		// return true to accept and dismiss, false reject
		boolean onFinish(String text);
	}

	public FileTextInputDialog(Context context, String title, String msg,
			String text, OnFinishListener listener) {
		super(context);
		mTitle = title;
		mMsg = msg;
		mListener = listener;
		mInputText = text;
		mContext = context;
	}

	public String getInputText() {
		return mInputText;
	}

	protected void onCreate(Bundle savedInstanceState) {
		mView = getLayoutInflater().inflate(R.layout.textinput_dialog, null);

		setTitle(mTitle);
		setMessage(mMsg);

		mFolderName = (EditText) mView.findViewById(R.id.text);
		mFolderName.setText(mInputText);

		setView(mView);
		setButton(BUTTON_POSITIVE, mContext.getString(android.R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == BUTTON_POSITIVE) {
							mInputText = mFolderName.getText().toString();
							if (mListener.onFinish(mInputText)) {
								dismiss();
							}
						}
					}
				});
		setButton(BUTTON_NEGATIVE, mContext.getString(android.R.string.cancel),
				(DialogInterface.OnClickListener) null);

		super.onCreate(savedInstanceState);
	}
}
