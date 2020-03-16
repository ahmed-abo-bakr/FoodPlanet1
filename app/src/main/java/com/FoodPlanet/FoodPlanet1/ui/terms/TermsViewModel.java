package com.FoodPlanet.FoodPlanet1.ui.terms;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TermsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TermsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is share fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}