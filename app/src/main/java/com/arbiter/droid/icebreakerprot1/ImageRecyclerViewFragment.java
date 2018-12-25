package com.arbiter.droid.icebreakerprot1;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.arbiter.droid.icebreakerprot1.Common.getCurrentUser;
import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;
import static com.arbiter.droid.icebreakerprot1.Common.getStorageReference;
import static com.arbiter.droid.icebreakerprot1.Common.uploadImageUrl;

/**
 * A simple {@link Fragment} subclass.
 */


public class ImageRecyclerViewFragment extends Fragment {


    private RecyclerView recyclerView;

    // @BindView(R.id.recycler_view)
    // RecyclerView recyclerView;


    private ImageRecyclerViewAdapter mAdapter;

    private ArrayList<ImageRecyclerViewModel> modelList = new ArrayList<>();


    public ImageRecyclerViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_image_recycler_view, container, false);

        findViews(view);
        if(getActivity() instanceof  ImageListActivity)
        {
            ((ImageListActivity)getActivity()).setOnFragmentInteract(new ImageListActivity.FragmentInterface() {
                @Override
                public void onFragmentInteract(Bundle bundle) {
                    if(bundle.containsKey("target_user")) {
                        populateList(bundle.getString("target_user"));
                    }
                    else if(bundle.containsKey("album_id")) {
                        populateListFromFacebookAlbum(bundle.getString("album_id"));
                    }
                }
            });
        }
        else if(getActivity() instanceof ViewProfileActivity)
        {
            ((ViewProfileActivity)getActivity()).setOnFragmentInteract(new ViewProfileActivity.FragmentInterface() {
                @Override
                public void onFragmentInteract(Bundle bundle) {
                    if(bundle.containsKey("target_user")) {
                        populateList(bundle.getString("target_user"));

                    }
                }
            });
        }
        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAdapter();
    }


    private void findViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }
    void populateListFromFacebookAlbum(String album_id)
    {
        final ArrayList<String> image_url_list = new ArrayList<>();
        GraphRequest gr = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/"+album_id+"/photos",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response)
                    {
                        try
                        {
                            JSONObject jsonObject = response.getJSONObject();
                            JSONArray data = jsonObject.getJSONArray("data");
                            for(int i=0;i<data.length();i++)
                            {
                                image_url_list.add(data.getJSONObject(i).getString("source"));
                                modelList.add(new ImageRecyclerViewModel(data.getJSONObject(i).getString("source")));
                            }
                            mAdapter.updateList(modelList);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle b = new Bundle();
        b.putString("fields","source");
        gr.setParameters(b);
        gr.executeAsync();
    }
    void populateList(String target_user)
    {
        getDatabaseReference().child("users").child(target_user).child("image_url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                modelList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child:children)
                {
                    String url = child.child("url").getValue().toString();
                    modelList.add(new ImageRecyclerViewModel(url));
                }
                mAdapter.updateList(modelList);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /*void updateList(String url)
    {
        modelList.add(new ImageRecyclerViewModel(url));
        mAdapter.updateList(modelList);
    }*/

    private void setAdapter() {


        //modelList.add(new ImageRecyclerViewModel("Android", "Hello " + " Android"));


        mAdapter = new ImageRecyclerViewAdapter(getActivity(), modelList);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setAdapter(mAdapter);


        mAdapter.SetOnItemClickListener(new ImageRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ImageRecyclerViewModel model) {

                //handle item click events here
            }

            @Override
            public void onDeleteClick(View view, int position, ImageRecyclerViewModel model) {
                final String url = model.getUrl();
                modelList.remove(position);
                mAdapter.updateList(modelList);
                getDatabaseReference().child("users").child(getCurrentUser()).child("image_url").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if(snapshot.child("url").getValue().toString().equals(url)) {
                                String fileName = snapshot.child("filename").getValue().toString();
                                getDatabaseReference().child("users").child(getCurrentUser()).child("image_url").child(snapshot.getKey()).removeValue();
                                if(!snapshot.child("filename").getValue().toString().equals("facebook"))
                                    getStorageReference().child("usr_img").child(getCurrentUser()).child(fileName).delete();
                            }
                        }
                        ;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onUploadClick(View view, int position, ImageRecyclerViewModel model) {
                uploadImageUrl(model.getUrl(),getContext());
            }
        });


    }

}
