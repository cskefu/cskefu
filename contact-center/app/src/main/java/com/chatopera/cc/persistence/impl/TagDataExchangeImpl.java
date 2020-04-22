package com.chatopera.cc.persistence.impl;

import com.chatopera.cc.model.Tag;
import com.chatopera.cc.persistence.interfaces.DataExchangeInterface;
import com.chatopera.cc.persistence.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service("tagdata")
@RequiredArgsConstructor
public class TagDataExchangeImpl implements DataExchangeInterface {
    @NonNull
    private final TagRepository tagRes;

    public String getDataByIdAndOrgi(String id, String orgi) {
        Tag tag = tagRes.findByOrgiAndId(orgi, id);
        return tag != null ? tag.getTag() : id;
    }

    @Override
    public List<Serializable> getListDataByIdAndOrgi(String id, String creater, String orgi) {
        return null;
    }

    public void process(Object data, String orgi) {

    }
}
