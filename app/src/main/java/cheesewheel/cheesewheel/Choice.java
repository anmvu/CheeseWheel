package cheesewheel.cheesewheel;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by annie on 5/1/16.
 */


public class Choice extends Fragment implements ToFragment {
    Activity activity;
    public static String landed;

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.choice, container, false);
//
//    }
//
//    @Override
//    public void passToFragment(String s){
//        landed = s;
//
//    }

    private IFragmentToActivity mCallback;
    private Button btnFtoA;
    private Button btnFtoF;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);
        btnFtoA = (Button) view.findViewById(R.id.button);
        btnFtoF = (Button) view.findViewById(R.id.button2);
        btnFtoA.setOnClickListener(this);
        btnFtoF.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (IFragmentToActivity) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement IFragmentToActivity");
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    public void onRefresh() {
        Toast.makeText(getActivity(), "Fragment 1: Refresh called.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                mCallback.showToast("Hello from Fragment 1");
                break;

            case R.id.button2:
                mCallback.communicateToFragment2();
                break;
        }
    }
}

}