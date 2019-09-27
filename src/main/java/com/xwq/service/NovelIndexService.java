package com.xwq.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xwq.mapper.NovelMapper;
import com.xwq.model.Novel;
import com.xwq.util.FieldFormatUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NovelIndexService {
    private static final int PAGE_SIZE = 100;

    @Autowired
    private NovelMapper novelMapper;

    @Autowired
    private TransportClient transportClient;

    public boolean indexNovel() throws IOException {
        BulkRequestBuilder bulkBuilder = transportClient.prepareBulk();

        PageHelper.startPage(1, PAGE_SIZE);
        List<Novel> novelList = novelMapper.selectAll();
        index(novelList, bulkBuilder);

        int pages = new PageInfo<>(novelList).getPages();

        for(int i=2; i<=pages; i++) {
            PageHelper.startPage(i, PAGE_SIZE);
            novelList = novelMapper.selectAll();
            index(novelList, bulkBuilder);
        }

        BulkResponse bulkResponse = bulkBuilder.get();
        return !bulkResponse.hasFailures();
    }


    private void index(List<Novel> novelList, BulkRequestBuilder bulkBuilder) throws IOException {
        IndexRequestBuilder requestBuilder = null;
        for(Novel novel : novelList) {
            String tags = novel.getTags();
            List<String> tagList = new ArrayList<>();

            if(StringUtils.isNotBlank(tags)) {
                if(tags.contains(",")) {
                    tagList.addAll(Arrays.asList(tags.split(",")));
                } else {
                    tagList.add(tags);
                }
            }

            requestBuilder = transportClient.prepareIndex("novel", "novel")
                    .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("name", novel.getName())
                            .field("author", novel.getAuthor())
                            .field("wordNum", novel.getWordNum())
                            .field("status", novel.getStatus())
                            .field("score", novel.getScore())
                            .field("scorePersonNum", novel.getScorePersonNum())
                            .field("lastUpdateTime", FieldFormatUtil.formatLastUpdateTime(novel.getLastUpdateTime()))
                            .field("intro", FieldFormatUtil.formatIntro(novel.getIntro()))
                            .field("tags", tagList)
                            .endObject())
                    .setId(novel.getId().toString());

            bulkBuilder.add(requestBuilder);
        }
    }
}
