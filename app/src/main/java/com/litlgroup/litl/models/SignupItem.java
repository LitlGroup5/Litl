package com.litlgroup.litl.models;

/**
 * Created by monusurana on 9/4/16.
 */
public class SignupItem {
    public static final int DEFAULT_COLOR = -1;

    int imageResourceId;
    String mainText;
    String descriptionText;
    int textColor;

    private SignupItem(SignupItemBuilder signupItem) {
        this.imageResourceId = signupItem.imageResourceId;
        this.mainText = signupItem.mainText;
        this.descriptionText = signupItem.descriptionText;
        this.textColor = signupItem.textColor;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }

    public String getMainText() {
        return mainText;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public int getTextColor() {
        return textColor;
    }

    public static class SignupItemBuilder {
        int imageResourceId;
        String mainText;
        String descriptionText;
        int textColor;

        public SignupItemBuilder(int imageResourceId, String mainText) {
            this.imageResourceId = imageResourceId;
            this.mainText = mainText;
            this.textColor = DEFAULT_COLOR;
        }

        public SignupItemBuilder descriptionText(String descriptionText) {
            this.descriptionText = descriptionText;
            return this;
        }

        public SignupItemBuilder textColor(int textColor) {
            this.textColor = textColor;
            return this;
        }

        public SignupItem build() {
            return new SignupItem(this);
        }
    }
}
