package com.litlgroup.litl.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.litlgroup.litl.R;
import com.litlgroup.litl.models.Address;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by Hari on 8/27/2016.
 */
public class AddressFragment
        extends android.support.v4.app.DialogFragment

{

    @BindView(R.id.etApt)
    EditText etApt;

    @BindView(R.id.etHouseNum)
    EditText etHouseNum;

    @BindView(R.id.etStreetAddress)
    EditText etStreetAddress;

    @BindView(R.id.etCity)
    EditText etCity;

    @BindView(R.id.etZip)
    EditText etZip;

    @BindView(R.id.etState)
    EditText etState;

    @BindView(R.id.ibSaveAddress)
    ImageButton ibSaveAddress;

    Address address;

    public AddressFragment() {

    }

    public static AddressFragment newInstance(Address address) {
        AddressFragment frag = new AddressFragment();
        frag.address = address;
        return frag;
    }


    private Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addres,
                container, false);
        unbinder = ButterKnife.bind(this, view);

        if (address != null) {
            try {
                etApt.setText(address.getApt());
                if(address.getHouseNo() == null)
                    etHouseNum.setText("");
                else
                    etHouseNum.setText(address.getHouseNo().toString());
                etStreetAddress.setText(address.getStreetAddress());
                etCity.setText(address.getCity());
                if(address.getZip() == null)
                    etZip.setText("");
                else
                    etZip.setText(address.getZip().toString());
                etState.setText(address.getState());


            } catch (Exception ex) {
                Timber.e("error parsing address information");
                ex.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {


    }

    @OnClick(R.id.ibSaveAddress)
    public void startSaveAddress() {
        saveAddress();
    }


    private void saveAddress() {
        try {
            Address address = new Address();

            String apt = etApt.getText().toString();
            String houseNum = etHouseNum.getText().toString();
            String streetAddress = etStreetAddress.getText().toString();
            String city = etCity.getText().toString();
            String zip = etZip.getText().toString();
            String state = etState.getText().toString();

            if (!apt.isEmpty())
                address.setApt(apt);

            if (!houseNum.isEmpty())
                address.setHouseNo(Integer.parseInt(houseNum));

            if (!streetAddress.isEmpty())
                address.setStreetAddress(streetAddress);

            if (!city.isEmpty())
                address.setCity(city);

            if (!zip.isEmpty())
                address.setZip(Integer.parseInt(zip));

            if (!state.isEmpty())
                address.setState(state);

            AddressFragmentListener listener;

            if (getTargetFragment() == null)
                listener = (AddressFragmentListener) getActivity();
            else
                listener = (AddressFragmentListener) getTargetFragment();

            listener.onFinishAddressFragment(address);

            dismiss();
        } catch (Exception ex) {
            Timber.e("Error when saving address information");
            ex.printStackTrace();
        }
    }



    public interface AddressFragmentListener
    {
        void onFinishAddressFragment(Address address);
    }


}
