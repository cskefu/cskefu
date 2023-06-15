package com.cskefu.cc.persistence.impl;

import com.cskefu.cc.model.Tag;
import com.cskefu.cc.persistence.interfaces.DataExchangeInterface;
import com.cskefu.cc.persistence.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("tagdata")
public class TagDataExchangeImpl implements DataExchangeInterface {
    @Autowired
    private TagRepository tagRes;

    public String getDataById(String id) {
        Tag tag = tagRes.findById(id);
        return tag != null ? tag.getTag() : id;
    }

    @Override
    public List<Serializable> getListDataById(String id, String creater) {
        return null;
    }

    public void process(Object data) {

    }
}
