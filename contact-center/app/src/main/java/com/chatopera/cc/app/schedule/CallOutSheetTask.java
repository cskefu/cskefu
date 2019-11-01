/*
 * Copyright (C) 2018 Chatopera Inc, <https://www.chatopera.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chatopera.cc.app.schedule;

import com.chatopera.cc.app.basic.MainContext;
import com.chatopera.cc.util.mobile.MobileAddress;
import com.chatopera.cc.util.mobile.MobileNumberUtils;
import com.chatopera.cc.app.persistence.es.ContactsRepository;
import com.chatopera.cc.app.persistence.repository.CallOutDialplanRepository;
import com.chatopera.cc.app.persistence.repository.CallOutTargetRepository;
import com.chatopera.cc.app.model.CallOutDialplan;
import com.chatopera.cc.app.model.CallOutTarget;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.SAXHelper;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 外呼系统表格任务处理
 * 使用Apache POI SAX mode处理大文件
 * http://svn.apache.org/repos/asf/poi/trunk/src/examples/src/org/apache/poi/xssf/eventusermodel/examples/FromHowTo.java
 */
@Component
public class CallOutSheetTask {
    private final static Logger logger = LoggerFactory.getLogger(CallOutSheetTask.class);

    @Autowired
    private CallOutTargetRepository callOutTargetRes;

    @Autowired
    private CallOutDialplanRepository callOutDialplanRes;

    @Autowired
    private ContactsRepository contactsRes;

    /**
     * Do the workloads
     *
     * @return
     */
    @Async("callOutTaskExecutor")
    public void run(final String dialplanId,
                    final String orgi,
                    final String organId,
                    final MultipartFile file) {
        logger.info("[callout sheet] process file to create targets {}", dialplanId);
        try (OPCPackage pkg = OPCPackage.open(file.getInputStream())) {
            XSSFReader r = new XSSFReader(pkg);
            SharedStringsTable sst = r.getSharedStringsTable();

            XMLReader parser = fetchSheetParser(sst, dialplanId, orgi, organId);

            // Just process the first sheet
            try (InputStream sheet = r.getSheetsData().next()) {
                logger.info("[callout sheet] start to process sheet");
                InputSource sheetSource = new InputSource(sheet);
                parser.parse(sheetSource);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (OpenXML4JException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private XMLReader fetchSheetParser(final SharedStringsTable sst, final String dialplan, final String orgi, final String organId) throws ParserConfigurationException, SAXException {
        XMLReader parser = SAXHelper.newXMLReader();
        ContentHandler handler = new SheetHandler(sst, dialplan, orgi, organId);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private class SheetHandler extends DefaultHandler {
        private final SharedStringsTable sst;
        private final String dialplanId;
        private final String orgi;
        private final String organId;
        private String lastContents;
        private boolean nextIsString;
        private boolean inlineStr;
        private boolean isIgnoreCurrentTransaction;
        private int targetnum;
        private final LruCache<Integer, String> lruCache = new LruCache<>(50);

        private class LruCache<A, B> extends LinkedHashMap<A, B> {
            private final int maxEntries;

            public LruCache(final int maxEntries) {
                super(maxEntries + 1, 1.0f, true);
                this.maxEntries = maxEntries;
            }

            @Override
            protected boolean removeEldestEntry(final Map.Entry<A, B> eldest) {
                return super.size() > maxEntries;
            }
        }

        private SheetHandler(final SharedStringsTable sst, final String dialplanId, final String orgi, final String organId) {
            this.sst = sst;
            this.dialplanId = dialplanId;
            this.orgi = orgi;
            this.organId = organId;
        }

        @Override
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            // c => cell
            if (name.equals("c")) {
                // Print the cell reference
//                logger.info("[callout sheet] cell ref {}", attributes.getValue("r"));
                // 增加限制，只处理第一列，忽略其它列
                if (!attributes.getValue("r").startsWith("A")) {
                    isIgnoreCurrentTransaction = true;
                    lastContents = "";
                    return;
                }
                isIgnoreCurrentTransaction = false;
                // Figure out if the value is an index in the SST
                String cellType = attributes.getValue("t");
                nextIsString = cellType != null && cellType.equals("s");
                inlineStr = cellType != null && cellType.equals("inlineStr");
            }
            // Clear contents cache
            lastContents = "";
        }

        /**
         * 阶段性的更新目标客户数，让前端感知状态
         */
        private void dumpTargetNumToDb(){
            if(targetnum % 500 == 0){
                CallOutDialplan dp = callOutDialplanRes.findOne(dialplanId);
                dp.setTargetnum(targetnum);
                callOutDialplanRes.save(dp);
            }
        }

        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            // Process the last contents as required.
            // Do now, as characters() may be called more than once
            if (isIgnoreCurrentTransaction)
                return;

            if (nextIsString) {
                Integer idx = Integer.valueOf(lastContents);
                lastContents = lruCache.get(idx);
                if (lastContents == null && !lruCache.containsKey(idx)) {
                    lastContents = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                    lruCache.put(idx, lastContents);
                }
                nextIsString = false;
            }

            // v => contents of a cell
            // Output after we've seen the string contents
            if (name.equals("v") || (inlineStr && name.equals("c"))) {
//                logger.info("[callout sheet] last contents {}", lastContents);
                // #TODO 当前 elasticsearch repo不支持 existsBy的方法，导致这里检索量很大
                String phone = StringUtils.trim(lastContents);
                if (StringUtils.isNumeric(phone)) {
                    if (StringUtils.length(phone) == 11) {
                        if (contactsRes.countByDatastatusIsFalseAndPhoneAndOrgi(phone, orgi) == 0) {
                            // 不是已有联系人
                            CallOutTarget ct = new CallOutTarget();
                            MobileAddress ma = MobileNumberUtils.getAddress(phone);
                            ct.setCalls(0);
                            ct.setCountry(ma.getCountry());
                            ct.setProvince(ma.getProvince());
                            ct.setCity(ma.getCity());
                            ct.setPhone(phone);
                            ct.setDialplan(dialplanId);
                            ct.setInvalid(false);
                            ct.setOrgi(orgi);
                            ct.setOrganid(organId);
                            callOutTargetRes.save(ct);
                            // 目标客户总数递增
                            targetnum += 1;
                            dumpTargetNumToDb();
//                            logger.info("[callout sheet] create a new target phone [{}], target size {}", phone, targetnum);
                        }
                    }
                }
            }
        }

        @Override
        public void endDocument(){
            logger.info("[callout sheet] 目标客户电话号处理完毕.");
            CallOutDialplan dp = callOutDialplanRes.findOne(dialplanId);
            dp.setTargetnum(targetnum);
            dp.setStatus(MainContext.CallOutDialplanStatusEnum.STOPPED.toString());
            callOutDialplanRes.save(dp);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException { // NOSONAR
            lastContents += new String(ch, start, length);
        }
    }
}
