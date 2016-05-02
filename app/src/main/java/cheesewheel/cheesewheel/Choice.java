package cheesewheel.cheesewheel;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by annie on 5/1/16.
 */


public class Choice extends Fragment{

    FragmentListener mCallback;
    String landed;
    Choice c = this;

    public interface FragmentListener{
        public void onButtonSelect(boolean b,Choice c);
    }

    public Choice(){}

    public Choice(String s){

        System.out.println(s);
        landed = s;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.choice, container, false);

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


        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonSelect(true, null);


            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onButtonSelect(false,c);
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
