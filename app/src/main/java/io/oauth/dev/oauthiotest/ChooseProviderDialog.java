package io.oauth.dev.oauthiotest;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChooseProviderDialog {
    private Dialog dialog;
    private View.OnClickListener clickListener;
    public String selected;

    private Map<String, ImageView> imgLogo = new Hashtable<>();

    public ChooseProviderDialog(Context ctx) {
        dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialog_providers);

        View.OnClickListener providerOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selected = v.getTag().toString();
                if (clickListener != null)
                    clickListener.onClick(v);
            }
        };

        imgLogo.put("facebook", (ImageView) dialog.findViewById(R.id.imageLogoFb));
        imgLogo.put("twitter", (ImageView) dialog.findViewById(R.id.imageLogoTw));
        imgLogo.put("google", (ImageView) dialog.findViewById(R.id.imageLogoGoogle));
        imgLogo.put("linkedin", (ImageView) dialog.findViewById(R.id.imageLogoLin));
        imgLogo.put("github", (ImageView) dialog.findViewById(R.id.imageLogoGh));
        ((Button) dialog.findViewById(R.id.cancelButton)).setOnClickListener(providerOnClick);

        Iterator<ImageView> it = imgLogo.values().iterator();
        while (it.hasNext()) {
            ImageView iv = (ImageView) it.next();
            iv.setOnClickListener(providerOnClick);
        }
    }

    public void setTitle(String title) {
        dialog.setTitle(title);
    }

    public void enableProviders(List<String> providers) {
        Iterator<String> it = providers.iterator();
        while (it.hasNext()) {
            imgLogo.get(it.next()).setVisibility(View.VISIBLE);
        }
    }

    public void enableProvider(String provider) {
        imgLogo.get(provider).setVisibility(View.VISIBLE);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        clickListener = listener;
    }

    public void show() {
        dialog.show();
    }
}
