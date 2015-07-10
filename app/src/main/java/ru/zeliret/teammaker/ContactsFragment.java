package ru.zeliret.teammaker;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    private ListView contactsList;
    private ContactsAdapter contactsAdapter;

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactsList = (ListView) view.findViewById(R.id.contacts);
        contactsList.setAdapter(contactsAdapter = new ContactsAdapter(view.getContext()));
        contactsList.setOnItemClickListener(this);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void setupActionBar(final ActionBar actionBar) {
        if (null != actionBar)
            actionBar.setTitle(R.string.title_contacts);
    }

    @Override
    public void onStart() {
        super.onStart();

        updateActionBar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
        return new ContactsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor c) {
        contactsAdapter.changeCursor(c);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> loader) {
        contactsAdapter.changeCursor(null);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contacts_done:
                completeChoice();
                return true;
        }

        return false;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_contacts, menu);
    }

    private void completeChoice() {
        int checkedCount = contactsList.getCheckedItemCount();
        if (checkedCount > 2) { // TODO: Temp team count = 2
            SparseBooleanArray checked = contactsList.getCheckedItemPositions();

            ArrayList<Player> players = new ArrayList<>();
            for (int i = 0; i < checked.size(); i++)
                if (checked.valueAt(i))
                    players.add(Player.fromCursor((Cursor) contactsList.getItemAtPosition(checked.keyAt(i))));

            getHostActivity().navigate(TeamsFragment.newInstance(players));
        } else {
            Snackbar.make(contactsList, R.string.error_not_enough_players, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void updateActionBar() {
        ActionBar actionBar = getActionBar();
        if (null != actionBar) {
            int checkedCount = contactsList.getCheckedItemCount();
            if (checkedCount > 0)
                actionBar.setSubtitle(String.format(getString(R.string.title_contacts_selected), checkedCount));
            else
                actionBar.setSubtitle(null);
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        updateActionBar();
    }

    public static class ContactsLoader extends CursorLoader {
        private static final String[] PROJECTION = {
                Contacts._ID,
                Contacts.DISPLAY_NAME_PRIMARY,
                Contacts.PHOTO_THUMBNAIL_URI
        };
        private static final String SELECTION = String.format("%s = ?", Contacts.DISPLAY_NAME_SOURCE);
        private static final String[] SELECTION_ARGS = new String[]{
                String.valueOf(ContactsContract.DisplayNameSources.STRUCTURED_NAME)
        };
        private static final String ORDER = Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC";

        public ContactsLoader(final Context context) {
            super(context, Contacts.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, ORDER);
        }
    }

    private static class ContactsAdapter extends CursorAdapter {
        public ContactsAdapter(final Context context) {
            super(context, null, 0);
        }

        @Override
        public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacts, parent, false);
            view.setTag(new ContactsViewHolder(view));
            return view;
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor) {
            Player player = Player.fromCursor(cursor);
            if (null != player) {
                ContactsViewHolder vh = (ContactsViewHolder) view.getTag();
                vh.nameView.setText(player.name);
                Glide.with(view.getContext())
                        .load(player.photoUri)
                        .into(vh.photoView);
            }
        }

        @Override
        public long getItemId(final int position) {
            Cursor c = (Cursor) getItem(position);
            return null != c ? c.getInt(c.getColumnIndex(Contacts._ID)) : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return true;
        }
    }

    private static class ContactsViewHolder {
        private CircleImageView photoView;
        private TextView nameView;

        public ContactsViewHolder(final View itemView) {
            photoView = (CircleImageView) itemView.findViewById(R.id.contact_photo);
            nameView = (TextView) itemView.findViewById(R.id.contact_name);
        }
    }
}
