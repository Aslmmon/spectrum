package com.spectrum.services.models.drawer;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Abins Shaji on 16/02/18.
 */

public class FaqModel {
    @SerializedName("status")
    private String status;
    @SerializedName("faq")
    private List<Faq_data>faq_data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Faq_data> getFaq_data() {
        return faq_data;
    }

    public void setFaq_data(List<Faq_data> faq_data) {
        this.faq_data = faq_data;
    }

    public class Faq_data
    {
        @SerializedName("question")
        private String question;
        @SerializedName("answer")
        private String answer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }

}
