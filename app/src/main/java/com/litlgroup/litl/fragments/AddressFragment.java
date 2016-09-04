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
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.AddressComponent;
import com.seatgeek.placesautocomplete.model.AddressComponentType;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

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
//
//    @BindView(R.id.etHouseNum)
//    EditText etHouseNum;

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

    @BindView(R.id.places_autocomplete)
    PlacesAutocompleteTextView placesAutocompleteTextView;

    @BindView(R.id.etCountry)
    EditText etCountry;

    Address address;

    public AddressFragment() {

    }

    public static AddressFragment newInstance(Address address, AddressValidationMode addressValidationMode) {
        AddressFragment frag = new AddressFragment();
        frag.address = address;
        frag.addressValidationMode = addressValidationMode;
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
                if(address.getApt()!= null)
                    etApt.setText(address.getApt());
//                if(address.getHouseNo() == null)
//                    etHouseNum.setText("");
//                else
//                    etHouseNum.setText(address.getHouseNo().toString());
                etStreetAddress.setText(address.getStreetAddress());
                etCity.setText(address.getCity());
                if(address.getZip() == null)
                    etZip.setText("");
                else
                    etZip.setText(address.getZip().toString());
                etState.setText(address.getState());

                if(address.getCountry() != null)
                    etCountry.setText(address.getCountry());


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

        setPlacesAutocompleteListener();
    }


    private void setPlacesAutocompleteListener()
    {
        placesAutocompleteTextView.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        placesAutocompleteTextView.getDetailsFor(place, new DetailsCallback() {
                            @Override
                            public void onSuccess(PlaceDetails placeDetails) {
                                Timber.d("place selected: ", placeDetails);

                                etStreetAddress.setText(placeDetails.name);
                                for (AddressComponent component : placeDetails.address_components) {
                                    for (AddressComponentType type : component.types) {
                                        switch (type) {
                                            case STREET_NUMBER:
                                                break;
                                            case ROUTE:
                                                break;
                                            case NEIGHBORHOOD:
                                                break;
                                            case SUBLOCALITY_LEVEL_1:
                                                break;
                                            case SUBLOCALITY:
                                                break;
                                            case LOCALITY:
                                                etCity.setText(component.long_name);
                                                break;
                                            case ADMINISTRATIVE_AREA_LEVEL_1:
                                                etState.setText(component.short_name);
                                                break;
                                            case ADMINISTRATIVE_AREA_LEVEL_2:
                                                break;
                                            case COUNTRY:
                                                etCountry.setText(component.long_name);
                                                break;
                                            case POSTAL_CODE:
                                                etZip.setText(component.long_name);
                                                break;
                                            case POLITICAL:
                                                break;
                                        }
                                    }
                                }


                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Timber.e("Error getting place details");

                            }
                        });
                    }
                }
        );
    }

    @OnClick(R.id.ibSaveAddress)
    public void startSaveAddress() {
        boolean isValidAddress = validateAddress();
        if(isValidAddress)
            saveAddress();
    }

    public enum AddressValidationMode { TASK_ADDRESS_MODE, PROFILE_ADDRESS_MODE};

    public AddressValidationMode addressValidationMode;

    private boolean validateAddress()
    {
        try {
            String streetAddress = etStreetAddress.getText().toString();
            String city = etCity.getText().toString();
            String zip = etZip.getText().toString();
            String state = etState.getText().toString();
            String country = etCountry.getText().toString();

            boolean isValid = true;

            if(addressValidationMode == AddressValidationMode.PROFILE_ADDRESS_MODE) {

                if (city == null || city.trim().isEmpty()) {
                    etCity.setError("City is required");
                    isValid = false;
                }

                if (state == null || state.trim().isEmpty()) {
                    etState.setError("State is required");
                    isValid = false;
                }

                return isValid;
            }
            else if (addressValidationMode == AddressValidationMode.TASK_ADDRESS_MODE)
            {

                if(streetAddress == null || streetAddress.trim().isEmpty())
                {
                    etStreetAddress.setError("Street Address is required");
                    isValid = false;
                }

                if (city == null || city.trim().isEmpty()) {
                    etCity.setError("City is required");
                    isValid = false;
                }

                if(zip == null || zip.trim().isEmpty())
                {
                    etZip.setError("Zip is required");
                    isValid = false;
                }

                if (state == null || state.trim().isEmpty()) {
                    etState.setError("State is required");
                    isValid = false;
                }

                return isValid;
            }

            return true;
        }
        catch (Exception ex)
        {
            Timber.e("Error validating the entered address");
            return false;
        }
    }


    private void saveAddress() {
        try {
            Address address = new Address();

            String apt = etApt.getText().toString();
            String houseNum = "";//etHouseNum.getText().toString();
            String streetAddress = etStreetAddress.getText().toString();
            String city = etCity.getText().toString();
            String zip = etZip.getText().toString();
            String state = etState.getText().toString();
            String country = etCountry.getText().toString();

            if (!apt.isEmpty())
                address.setApt(apt);

            if (!houseNum.isEmpty())
                address.setHouseNo(null);

            if (!streetAddress.isEmpty())
                address.setStreetAddress(streetAddress);

            if (!city.isEmpty())
                address.setCity(city);

            if (!zip.isEmpty()) {
                address.setZip(Integer.parseInt(zip));
            }
//                address.setZip(Integer.parseInt(zip));

            if (!state.isEmpty())
                address.setState(state);

            if (!country.isEmpty())
                address.setCountry(country);

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
