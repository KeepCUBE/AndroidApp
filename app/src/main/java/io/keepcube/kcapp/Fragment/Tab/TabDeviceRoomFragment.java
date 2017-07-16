package io.keepcube.kcapp.Fragment.Tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabDeviceRoomFragment extends Fragment {


    public TabDeviceRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_fragment_device_room, container, false);
    }

}


//
//public class TabFragment extends Fragment {
//
//    private ListAdapter mAdapter;
//
//    private String mItemData = "Lorem Ipsum is simply dummy text of the printing and "
//            + "typesetting industry Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment, container, false);
//
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(
//                R.id.fragment_list_rv);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setHasFixedSize(true);
//
//        String[] listItems = mItemData.split(" ");
//
//        List<String> list = new ArrayList<String>();
//        Collections.addAll(list, listItems);
//
//        mAdapter = new ListAdapter(list);
//        recyclerView.setAdapter(mAdapter);
//
//        return view;
//    }
//}