package com.arbiter.droid.icebreakerprot1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VenueRecyclerViewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VenueRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class VenueRecyclerViewFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private RecyclerView recyclerView;

    // @BindView(R.id.recycler_view)
    // RecyclerView recyclerView;


    // @BindView(R.id.swipe_refresh_recycler_list)
    // SwipeRefreshLayout swipeRefreshRecyclerList;

    //private SwipeRefreshLayout swipeRefreshRecyclerList;
    private VenueRecyclerViewAdapter mAdapter;

    private final ArrayList<VenueDataModel> modelList = new ArrayList<>();


    public VenueRecyclerViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VenueRecyclerViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VenueRecyclerViewFragment newInstance(String param1, String param2) {
        VenueRecyclerViewFragment fragment = new VenueRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static VenueRecyclerViewFragment newInstance() {
        VenueRecyclerViewFragment fragment = new VenueRecyclerViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_venue_recycler_view, container, false);

        // ButterKnife.bind(this);
        findViews(view);

        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        setAdapter(null);
    }

    private void findViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //swipeRefreshRecyclerList = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_recycler_list);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            //throw new RuntimeException(context.toString()
              //      + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    @Subscribe
    public void setAdapter(VenueCriteriaEvent event) {
        final String searchQuery;
        final HashMap<String, Boolean> criterias;
        if(event != null) {
            searchQuery = event.searchQuery;
            criterias = event.criterias;
            Log.v("myapp",criterias.toString());
        }
        else
        {
            searchQuery = "";
            criterias = new HashMap<>();
            criterias.put("alcohol",true);
            criterias.put("food",true);
            criterias.put("vegonly",false);
            criterias.put("drinksonly",false);
            criterias.put("dance",false);
            criterias.put("hookah",false);
            criterias.put("night",false);
        }
        getDatabaseReference().child("pubs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = children.iterator();
                String[] critNames = {"alcohol","food","vegonly","drinksonly","dance","hookah","night"};
                while(iterator.hasNext())
                {
                    DataSnapshot next = iterator.next();
                    HashMap<String,Boolean> fragCriterias = new HashMap<>();
                    for(String criteria : critNames) {
                        fragCriterias.put(criteria, Boolean.parseBoolean(next.child("tags").child(criteria).getValue().toString()));
                        if(fragCriterias.get(criteria)&&criterias.get(criteria)&&next.child("name").getValue().toString().toLowerCase().contains(searchQuery.toLowerCase())) {
                            modelList.add(new VenueDataModel(next.child("name").getValue().toString(), ""));
                            break;
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new ShimmerDisableEvent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mAdapter = new VenueRecyclerViewAdapter(getActivity(), modelList);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setAdapter(mAdapter);


        mAdapter.SetOnItemClickListener(new VenueRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, VenueDataModel model) {

                //handle item click events here
                //Toast.makeText(getActivity(), "Hey " + model.getTitle(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(view.getContext(),PubViewActivity.class);
                i.putExtra("venname",modelList.get(position).getTitle());
                startActivity(i);
            }
        });


    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
