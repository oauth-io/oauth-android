package io.oauth.dev.oauthiotest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;

import io.oauth.OAuth;
import io.oauth.OAuthCallback;
import io.oauth.OAuthData;
import io.oauth.OAuthUser;
import io.oauth.OAuthUserCallback;
import io.oauth.OAuthUsers;

public class InfosFragment extends Fragment implements OAuthCallback {

    private View rootView;
    private MainActivity activity;
    private OAuth oauth;
    private OAuthUsers users;

    public InfosFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = ((MainActivity) getActivity());
        oauth = activity.oauth;
        users = activity.users;
        activity.SetLogged(true);

        rootView = inflater.inflate(R.layout.fragment_infos, container, false);

        TextView txtFullname = (TextView) rootView.findViewById(R.id.textFullname);
        TextView txtEmail = (TextView) rootView.findViewById(R.id.textEmail);

        final OAuthUser id = users.getIdentity();
        txtFullname.setText(id.data.get("firstname") + " " + id.data.get("lastname"));
        txtEmail.setText(id.data.get("email"));

        Iterator prov = id.providers.iterator();
        ImageView synclogo;
        while (prov.hasNext()) {
            synclogo = (ImageView) rootView.findViewWithTag((String) prov.next());
            if (synclogo != null)
                synclogo.setVisibility(View.VISIBLE);
        }

        ListView listview = (ListView) rootView.findViewById(R.id.listView);
        ArrayList<String> list = new ArrayList<String>();
        Iterator it = id.data.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key != "id")
                list.add(key + ": " + id.data.get(key));
        }
        ArrayAdapter adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        Button syncBtn = (Button) rootView.findViewById(R.id.syncBtn);
        syncBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChooseProviderDialog dlg = new ChooseProviderDialog(activity);
                dlg.setTitle("Sync with a provider");
                if (id.providers.indexOf("facebook") == -1) dlg.enableProvider("facebook");
                if (id.providers.indexOf("twitter") == -1) dlg.enableProvider("twitter");
                if (id.providers.indexOf("google") == -1) dlg.enableProvider("google");
                if (id.providers.indexOf("linkedin") == -1) dlg.enableProvider("linkedin");
                if (id.providers.indexOf("github") == -1) dlg.enableProvider("github");
                dlg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( ! dlg.selected.equals(""))
                            oauth.popup(dlg.selected, InfosFragment.this);
                    }
                });
                dlg.show();
            }
        });

        Button remBtn = (Button) rootView.findViewById(R.id.remBtn);
        remBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ChooseProviderDialog dlg = new ChooseProviderDialog(activity);
                dlg.setTitle("Remove a linked provider");
                dlg.enableProviders(id.providers);
                dlg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( ! dlg.selected.equals("")) {
                            id.removeProvider(dlg.selected, new OAuthUserCallback() {
                                @Override
                                public void onFinished() {
                                    ImageView logo = (ImageView) rootView.findViewWithTag(dlg.selected);
                                    logo.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError(String message) {
                                    activity.displayError(message);
                                }
                            });
                        }
                    }
                });
                dlg.show();
            }
        });

        ImageView avatar = (ImageView) rootView.findViewById(R.id.imageProfile);
        ImageDownloader imgdl = new ImageDownloader(avatar, id.data.get("avatar"));

        return rootView;
    }

    @Override
    public void onFinished(final OAuthData data) {
        if (data.status.equals("error"))
            activity.displayError(data.error);
        else
            users.getIdentity().addProvider(data, new OAuthUserCallback() {
                @Override
                public void onFinished() {
                    ImageView logo = (ImageView) rootView.findViewWithTag(data.provider);
                    logo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError(String message) {
                    activity.displayError(message);
                }
            });
    }
}
