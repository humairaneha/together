package com.apptask.together.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IdeaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IdeaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Idea fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}