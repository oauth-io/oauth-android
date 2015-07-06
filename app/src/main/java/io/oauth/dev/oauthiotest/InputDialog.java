package io.oauth.dev.oauthiotest;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InputDialog {
    private Dialog dialog;
    private View.OnClickListener clickListener;
    public String value;

    private Map<String, ImageView> imgLogo = new Hashtable<>();

    public InputDialog(Context ctx) {
        dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialog_input);

        Button btn = (Button) dialog.findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editEmail = (EditText) dialog.findViewById(R.id.editEmail);
                value = editEmail.getText().toString();
                dialog.dismiss();
                if (clickListener != null)
                    clickListener.onClick(v);
            }
        });
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        clickListener = listener;
    }

    public void show() {
        dialog.show();
    }
}
