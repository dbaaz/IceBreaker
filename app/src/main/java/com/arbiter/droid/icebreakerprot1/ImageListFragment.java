package com.arbiter.droid.icebreakerprot1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

import static com.arbiter.droid.icebreakerprot1.Common.getDatabaseReference;
import static com.arbiter.droid.icebreakerprot1.Common.getScreenHeight;
import static com.arbiter.droid.icebreakerprot1.Common.uploadImageUrl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FlexboxLayout flexboxLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    private OnFragmentInteractionListener mListener;
    public ImageListFragment() {
        // Required empty public constructor
    }

    public static ImageListFragment newInstance() {
        ImageListFragment fragment = new ImageListFragment();
        return fragment;
    }
    void populateListFromFacebookAlbum(String album_id)
    {
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
                                GlideApp.with(getContext()).load(data.getJSONObject(i).getString("source")).placeholder(tempShimmer).into(tmp);
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
    }
    void populateList(String target_user)
    {
        final FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.MATCH_PARENT);
        getDatabaseReference().child("users").child(target_user).child("image_url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                flexboxLayout.removeAllViews();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for(DataSnapshot child:children)
                {
                    String url = child.child("url").getValue().toString();
                    ImageView tmp = new ImageView(getContext());
                    lp.setHeight(getScreenHeight()/2);
                    tmp.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    tmp.setLayoutParams(lp);
                    Shimmer shimmer = new Shimmer.ColorHighlightBuilder().build();
                    ShimmerDrawable tempShimmer = new ShimmerDrawable();
                    tempShimmer.setShimmer(shimmer);
                    GlideApp.with(getContext()).load(url).placeholder(tempShimmer).into(tmp);
                    flexboxLayout.addView(tmp);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void updateList(String url)
    {
        final FlexboxLayout.LayoutParams lp = new FlexboxLayout.LayoutParams(FlexboxLayout.LayoutParams.MATCH_PARENT, FlexboxLayout.LayoutParams.MATCH_PARENT);
        ImageView tmp = new ImageView(getContext());
        lp.setHeight(getScreenHeight()/2);
        tmp.setScaleType(ImageView.ScaleType.CENTER_CROP);
        tmp.setLayoutParams(lp);
        Shimmer shimmer = new Shimmer.ColorHighlightBuilder().build();
        ShimmerDrawable tempShimmer = new ShimmerDrawable();
        tempShimmer.setShimmer(shimmer);
        /*try {
            Picasso.get().load(compressImage(new File(url),getContext(),false)).placeholder(tempShimmer).into(tmp);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        flexboxLayout.addView(tmp);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootview = inflater.inflate(R.layout.fragment_image_list, container, false);
        flexboxLayout=rootview.findViewById(R.id.flexbox_layout);
        if(getActivity() instanceof  ImageListActivity)
        {
            ((ImageListActivity)getActivity()).setOnFragmentInteract(new ImageListActivity.FragmentInterface() {
                @Override
                public void onFragmentInteract(Bundle bundle) {
                    if(bundle.containsKey("target_user"))
                        populateList(bundle.getString("target_user"));
                    else if(bundle.containsKey("update_path"))
                        updateList(bundle.getString("update_path"));
                }
            });
        }
        else if(getActivity() instanceof ViewProfileActivity)
        {
            ((ViewProfileActivity)getActivity()).setOnFragmentInteract(new ViewProfileActivity.FragmentInterface() {
                @Override
                public void onFragmentInteract(Bundle bundle) {
                    if(bundle.containsKey("target_user"))
                        populateList(bundle.getString("target_user"));
                    else if(bundle.containsKey("update_path"))
                        updateList(bundle.getString("update_path"));
                }
            });
        }
        else if(getActivity() instanceof FacebookImageListActivity)
        {
            ((FacebookImageListActivity)getActivity()).setOnFragmentInteract(new FacebookImageListActivity.FragmentInterface() {
                @Override
                public void onFragmentInteract(Bundle bundle) {
                    if(bundle.containsKey("album_id"))
                        populateListFromFacebookAlbum(bundle.getString("album_id"));
                }
            });
        }
        return rootview;
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
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
