package cheesewheel.cheesewheel;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Choice extends Fragment{

    FragmentListener mCallback;
    String landed;
    Choice c = this;
    boolean isOnline;

    public interface FragmentListener {
        public void onButtonSelect(boolean b, Choice c);
    }

    public Choice(){}

    public Choice(String s, String online){
        landed = s;
        if(online.length() <= 0) isOnline = false;
        else isOnline = true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.choice,container,false);


        if(landed == null){
            System.out.println("It's null!");
        }
        else {
            System.out.println("bundle: " + landed);
            String prompt = landed;
            prompt += "?";
            TextView text = (TextView) view.findViewById(R.id.landed_on);
            text.setText(prompt);
        }

        final Button yes = (Button)view.findViewById(R.id.yes);
        final Button no = (Button)view.findViewById(R.id.No);

        if (!isOnline) {
            yes.setVisibility(View.GONE);
            no.setGravity(Gravity.CENTER);
        } else {
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onButtonSelect(true, null);
                }
            });
        }

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonSelect(false, c);
            }
        });


        return view;
    }



    @Override
    public void setArguments(Bundle b){

    }

    public void onAttach(Context context){
        super.onAttach(context);

        try{
            mCallback = (FragmentListener) context;

        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement FragmentListener");
        }
    }
}