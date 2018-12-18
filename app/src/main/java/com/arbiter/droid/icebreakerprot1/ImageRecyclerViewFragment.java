package com.arbiter.droid.icebreakerprot1;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_recycler_view, container, false);

        // ButterKnife.bind(this);
        findViews(view);

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
        /*
        final FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.MATCH_PARENT);
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
                            //Log.v("myapp",response.getJSONObject().getJSONArray("data").getJSONObject(0).getString("source"));
                            JSONObject jsonObject = response.getJSONObject();
                            JSONArray data = jsonObject.getJSONArray("data");
                            for(int i=0;i<data.length();i++)
                            {
                                image_url_list.add(data.getJSONObject(i).getString("source"));
                                ImageView tmp = new ImageView(getContext());
                                tmp.setTag(i);
                                lp.setHeight(getScreenHeight()/2);
                                tmp.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                tmp.setLayoutParams(lp);
                                Shimmer shimmer = new Shimmer.ColorHighlightBuilder().build();
                                ShimmerDrawable tempShimmer = new ShimmerDrawable();
                                tempShimmer.setShimmer(shimmer);
                                Picasso.get().load(data.getJSONObject(i).getString("source")).placeholder(tempShimmer).transform(new CircleTransform()).into(tmp);
                                flexboxLayout.addView(tmp);
                                tmp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Toast.makeText(v.getContext(), "Long press image to upload", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                tmp.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        ImageView temp = (ImageView)v;
                                        uploadImageUrl(image_url_list.get(Integer.parseInt(temp.getTag().toString())),getContext());
                                        return true;
                                    }
                                });
                            }
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
        */
    }
    void populateList(String target_user)
    {
        //final FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.MATCH_PARENT);
        getDatabaseReference().child("users").child(target_user).child("image_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //flexboxLayout.removeAllViews();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child:children)
                {
                    String url = child.child("url").getValue().toString();
                    modelList.add(new ImageRecyclerViewModel("","",url));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void updateList(String url)
    {
        /*final FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.MATCH_PARENT);
        ImageView tmp = new ImageView(getContext());
        lp.setHeight(getScreenHeight()/2);
        tmp.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tmp.setLayoutParams(lp);
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder().build();
        ShimmerDrawable tempShimmer = new ShimmerDrawable();
        tempShimmer.setShimmer(shimmer);
        try {
            Picasso.get().load(compressImage(new File(url),getContext(),false)).placeholder(tempShimmer).into(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        flexboxLayout.addView(tmp);*/

    }

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
                Toast.makeText(getActivity(), "Hey " + model.getTitle(), Toast.LENGTH_SHORT).show();


            }
        });


    }

}
