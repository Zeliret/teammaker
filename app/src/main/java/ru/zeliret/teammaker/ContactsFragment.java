package ru.zeliret.teammaker;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ContactsFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, AbsListView.MultiChoiceModeListener {
    private ListView contactsList;
    private ContactsAdapter contactsAdapter;
    private ActionMode actionMode;

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
        contactsList.setMultiChoiceModeListener(this);
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
    public void onItemCheckedStateChanged(final ActionMode mode, final int position, final long id, final boolean checked) {
        updateActionMode();
    }

    @Override
    public boolean onCreateActionMode(final ActionMode mode, final Menu menu) {
        actionMode = mode;
        getActivity().getMenuInflater().inflate(R.menu.menu_contacts_selected, menu);
        updateActionMode();
        return true;
    }

    @Override
    public boolean onPrepareActionMode(final ActionMode mode, final Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contacts_done:
                completeChoice();
                return true;
        }

        return false;
    }

    private void completeChoice() {
        ArrayList<Player> players = new ArrayList<>();

        int size = contactsList.getCount();
        for (int i = 0; i < size; i++)
            if (contactsList.isItemChecked(i))
                players.add(Player.fromCursor((Cursor) contactsList.getItemAtPosition(i)));

        actionMode.finish();
        getHostActivity().navigate(TeamsFragment.newInstance(players));
    }

    @Override
    public void onDestroyActionMode(final ActionMode mode) {
        actionMode = null;
    }

    private void updateActionMode() {
        if (null != actionMode) {
            int checkedCount = contactsList.getCheckedItemCount();
            actionMode.setTitle(String.format(getString(R.string.title_contacts_selected),
                    checkedCount));
        }
    }

    public static class ContactsLoader extends CursorLoader {
        private static final String[] PROJECTION = {
                Contacts._ID,
                Contacts.DISPLAY_NAME_PRIMARY,
                Contacts.PHOTO_THUMBNAIL_URI
        };
        private static final String SELECTION = String.format("%s = ? and %s like ?", Contacts.DISPLAY_NAME_SOURCE, Contacts.DISPLAY_NAME);
        private static final String[] SELECTION_ARGS = new String[]{
                String.valueOf(ContactsContract.DisplayNameSources.STRUCTURED_NAME),
                "%"
        };
        private static final String ORDER = Contacts.DISPLAY_NAME_PRIMARY + " COLLATE LOCALIZED ASC";

        public ContactsLoader(final Context context) {
            super(context, Contacts.CONTENT_URI, PROJECTION, SELECTION, SELECTION_ARGS, ORDER);
        }

        public void search(final String query) {
            cancelLoad();

            SELECTION_ARGS[1] = String.format("%s%%", query);
            setSelectionArgs(SELECTION_ARGS);
            forceLoad();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contacts, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        setupSearchView(searchView);
    }


    private void setupSearchView(final SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                searchContacts(newText);

                return true;
            }
        });
    }

    private void searchContacts(final String query) {
        ContactsLoader loader = (ContactsLoader) getLoaderManager().<Cursor>getLoader(0);
        loader.search(query);
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
