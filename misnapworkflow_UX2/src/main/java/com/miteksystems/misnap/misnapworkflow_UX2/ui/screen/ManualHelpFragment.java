package com.miteksystems.misnap.misnapworkflow_UX2.ui.screen;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.miteksystems.misnap.events.TextToSpeechEvent;
import com.miteksystems.misnap.misnapworkflow_UX2.R;
import com.miteksystems.misnap.params.DocType;

import org.greenrobot.eventbus.EventBus;


public class ManualHelpFragment extends Fragment {
    private static final String KEY_DOCTYPE = "KEY_DOCTYPE";
    private static final int TTS_DELAY_MS = 2000;
    private OnFragmentInteractionListener mListener;
    private boolean mButtonPressed;
    private DocType mDocType;

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
        void onManualHelpRestartMiSnapSession();
        void onManualHelpAbortMiSnap();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlaceholderFragment.
     */
    public static ManualHelpFragment newInstance(DocType docType) {
        ManualHelpFragment fragment = new ManualHelpFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_DOCTYPE, docType);

        fragment.setArguments(args);
        return fragment;
    }

    public ManualHelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDocType = (DocType) getArguments().getSerializable(KEY_DOCTYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.manual_help_tutorial_ux2, container, false);

        TextView message1 = (TextView) rootView.findViewById(R.id.misnap_manual_help_message_1);
        TextView message2 = (TextView) rootView.findViewById(R.id.misnap_manual_help_message_2);
        TextView message3 = (TextView) rootView.findViewById(R.id.misnap_manual_help_message_3);

        message2.setText(getString(R.string.misnap_manual_help_message_2_ux2));
        if (mDocType.isCheck()) {
            message1.setText(getString(R.string.misnap_manual_help_message_1_check_ux2));
            message3.setText(getString(R.string.misnap_manual_help_message_3_check_ux2));
        }else if(mDocType.isPassport()) {
            message1.setText(getString(R.string.misnap_manual_help_message_1_passport_ux2));
            message3.setText(getString(R.string.misnap_manual_help_message_3_passport_ux2));
        }else if(mDocType.isLicense()){
            message1.setText(getString(R.string.misnap_manual_help_message_1_license_ux2));
            message3.setText(getString(R.string.misnap_manual_help_message_3_license_ux2));
        }else{
            message1.setText(getString(R.string.misnap_manual_help_message_1_document_ux2));
            message3.setText(getString(R.string.misnap_manual_help_message_3_document_ux2));
        }

        Button bContinueBtn = (Button) rootView.findViewById(R.id.manual_help_continue_btn_ux2);
        bContinueBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the control to the activity
                if (mListener != null && !mButtonPressed) {
                    mButtonPressed = true;
                    mListener.onManualHelpRestartMiSnapSession();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        StringBuilder stringBuilder = new StringBuilder();
        if(mDocType.isCheck()){
            stringBuilder.append(getString(R.string.misnap_manual_help_message_1_check_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_2_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_3_check_ux2));
        }else if(mDocType.isPassport()){
            stringBuilder.append(getString(R.string.misnap_manual_help_message_1_passport_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_2_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_3_passport_ux2));
        }else if(mDocType.isLicense()){
            stringBuilder.append(getString(R.string.misnap_manual_help_message_1_license_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_2_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_3_license_ux2));
        }else{
            stringBuilder.append(getString(R.string.misnap_manual_help_message_1_document_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_2_ux2));
            stringBuilder.append(getString(R.string.misnap_manual_help_message_3_document_ux2));
        }
        EventBus.getDefault().post(new TextToSpeechEvent(stringBuilder.toString(), TTS_DELAY_MS));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
