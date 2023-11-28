package com.Nunbody.domain.Mail.domain;

import com.Nunbody.domain.Mail.controller.MailController;
import lombok.Setter;

import java.util.ArrayList;
@Setter
public class MailList {
    private String host;
    public ArrayList<Mail> data;


    public void addData(Mail mail) {
        this.data.add(mail);
    }
}
