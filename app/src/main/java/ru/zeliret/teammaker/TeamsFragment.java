package ru.zeliret.teammaker;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeamsFragment extends BaseFragment {
    public static final String KEY_PLAYERS = "players";
    private ListView teamsList;
    private ArrayList<Player> players;

    public TeamsFragment() {
    }

    public static TeamsFragment newInstance(final ArrayList<Player> players) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(KEY_PLAYERS, players);

        TeamsFragment fragment = new TeamsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        players = getArguments().getParcelableArrayList(KEY_PLAYERS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_teams, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        teamsList = (ListView) view.findViewById(R.id.teams);
        teamsList.setAdapter(new TeamsAdapter(view.getContext(), players));
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void setupActionBar(final ActionBar actionBar) {
        if (null != actionBar)
            actionBar.setTitle(R.string.title_teams);
    }

    @Override
    public boolean onBackPressed() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.show(getFragmentManager(), ConfirmDialog.class.getName());

        return false;
    }

    public static class ConfirmDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.confirm_exit)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            MainActivity activity = (MainActivity) getActivity();
                            activity.goBack();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
        }
    }

    private static class TeamsAdapter extends ArrayAdapter<Player> {
        private static final int[] RESOURCE_IDS = new int[]{
                R.layout.item_team_1,
                R.layout.item_team_2
        };
        private final LayoutInflater inflater;

        public TeamsAdapter(final Context context, final ArrayList<Player> items) {
            super(context, 0, items);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;
            TeamsViewHolder vh;
            if (null == view) {
                view = inflater.inflate(RESOURCE_IDS[getItemViewType(position)], parent, false);
                view.setTag(vh = new TeamsViewHolder(view));
            } else {
                vh = (TeamsViewHolder) view.getTag();
            }

            Player player = getItem(position);
            vh.nameView.setText(player.name);

            Glide.with(getContext())
                    .load(player.photoUri)
                    .into(vh.photoView);

            return view;
        }

        @Override
        public int getItemViewType(final int position) {
            return position % 2;
        }

        @Override
        public int getViewTypeCount() {
            return RESOURCE_IDS.length;
        }
    }

    private static class TeamsViewHolder {
        private CircleImageView photoView;
        private TextView nameView;

        public TeamsViewHolder(final View itemView) {
            photoView = (CircleImageView) itemView.findViewById(R.id.contact_photo);
            nameView = (TextView) itemView.findViewById(R.id.contact_name);
        }
    }
}
