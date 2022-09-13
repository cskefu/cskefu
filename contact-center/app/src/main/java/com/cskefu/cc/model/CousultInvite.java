/*
 * Copyright (C) 2017 优客服-多渠道客服系统
 * Modifications copyright (C) 2018-2022 Chatopera Inc, <https://www.chatopera.com>
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
package com.cskefu.cc.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "uk_consult_invite")
@org.hibernate.annotations.Proxy(lazy = false)
public class CousultInvite implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4067131780773572811L;
    private String id;
    private String impid;
    private String orgi;
    private String owner;
    private String processid;
    private String shares;
    private Date update_time;
    private String update_user;
    private String username;
    private String wfstatus;
    private boolean consult_invite_enable;    //启用邀请框
    private String consult_invite_model;
    private String consult_invite_content;
    private String consult_invite_accept;        //显示接收咨询按钮的文本
    private String consult_invite_later;        //显示稍后咨询的按钮
    private int consult_invite_delay;            //延时弹出邀请框
    private String consult_invite_bg;            //邀请框背景图片

    private boolean ctrlenter;                    //启用 CTRL+Enter发送消息

    private boolean whitelist_mode;

    private String consult_invite_position;
    private String consult_invite_color;
    private String consult_invite_right;
    private String consult_invite_left;
    private String consult_invite_bottom;
    private String consult_invite_top;
    private Date create_time;
    private String name;
    private String consult_invite_width;
    private String consult_invite_poptype;
    private String consult_invite_fontsize;
    private String consult_invite_fontstyle;
    private String consult_invite_fontcolor;
    private String consult_invite_interval;
    private String consult_invite_repeat;
    private String consult_invite_hight;
    private String snsaccountid;
    private String consult_vsitorbtn_position;
    private String consult_vsitorbtn_content;
    private String consult_vsitorbtn_right;
    private String consult_vsitorbtn_left;
    private String consult_vsitorbtn_top;
    private String consult_vsitorbtn_color;
    private String consult_vsitorbtn_model;
    private String consult_vsitorbtn_bottom;
    private String consult_invite_backimg;

    /**
     * 技能组设置
     */
    private boolean consult_skill_fixed; // 是否绑定单一技能组
    private String consult_skill_fixed_id; // 绑定单一技能组，技能组ID
    private String consult_skill_logo;        //显示技能组  logo
    private String consult_skill_title;    //显示技能组标题
    private String consult_skill_img;        //显示技能组 图片
    private String consult_skill_msg;        //显示技能组 提示信息
    private int consult_skill_numbers;        //显示最大 分组数
    private int consult_skill_maxagent;    //每个技能组最多显示多少坐席
    private String consult_skill_bottomtitle;    //显示技能组底部标题
    private boolean consult_skill_agent;    //是否显示技能组下的坐席

    private int consult_vsitorbtn_display;

    private boolean recordhis;            //记录访客的网页访问记录
    private boolean traceuser;            //实时追踪访客

    private int maxwordsnum;            //允许访客端输入的最大文本字数


    private String consult_dialog_color;
    private String consult_dialog_logo;
    private String consult_dialog_headimg;
    private String dialog_name;
    private String dialog_address;
    private String dialog_phone;
    private String dialog_mail;
    private String dialog_introduction;
    private String dialog_message;
    private String dialog_ad;

    private String lvmopentype;    //留言板弹出方式
    private boolean leavemessage;    //启用留言功能
    private boolean lvmname;
    private boolean lvmphone;
    private boolean lvmemail;
    private boolean lvmaddress;
    private boolean lvmqq;
    private boolean lvmcontent = true;
    private boolean skill;        //启用技能组功能

    private boolean onlyareaskill;    //只显示地区技能组 ， 无地区技能组是 提示消息，不显示公共技能组
    private String areaskilltipmsg;//未配置地区技能组是提示消息

    private boolean consult_info;    //启用咨询信息收集功能
    private boolean consult_info_cookies;    //启用Cookies存储用户录入的信息， 最长有效时间是 3600秒，超时需要重新填写
    private boolean consult_info_name;//填写姓名
    private boolean consult_info_email;    //填写 邮件地址
    private boolean consult_info_phone;//填写 电话号码
    private boolean consult_info_resion;    //填写咨询问题
    private String consult_info_message;    //咨询窗口显示的欢迎语

    private String agentshortcutkey;        //坐席回复快捷键
    private String usershortcutkey;        //访客快捷键

    private boolean ai;        //是否启用 AI
    private boolean aifirst;    //AI优先接入
    private boolean aisearch;    //AI允许使用 搜索引擎， 例如百度 等
    private String aimsg;        //AI欢迎信息，可以使用 HTML
    private String aisuccesstip;//AI服务连接成功的消息提示
    private String ainame;        //AI服务 昵称
    private boolean aisuggest;

    private String aiid;        //默认的机器人

    private String datadept;
    private String agent_online;


    @Id
    @Column(length = 32)
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImpid() {
        return impid;
    }

    public void setImpid(String impid) {
        this.impid = impid;
    }

    public String getOrgi() {
        return orgi;
    }

    public void setOrgi(String orgi) {
        this.orgi = orgi;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid = processid;
    }

    public String getShares() {
        return shares;
    }

    public void setShares(String shares) {
        this.shares = shares;
    }

    public Date getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }

    public String getUpdate_user() {
        return update_user;
    }

    public void setUpdate_user(String update_user) {
        this.update_user = update_user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWfstatus() {
        return wfstatus;
    }

    public void setWfstatus(String wfstatus) {
        this.wfstatus = wfstatus;
    }

    public String getConsult_invite_model() {
        return consult_invite_model;
    }

    public void setConsult_invite_model(String consult_invite_model) {
        this.consult_invite_model = consult_invite_model;
    }

    public String getConsult_invite_content() {
        return consult_invite_content;
    }

    public void setConsult_invite_content(String consult_invite_content) {
        this.consult_invite_content = consult_invite_content;
    }

    public String getConsult_invite_position() {
        return consult_invite_position;
    }

    public void setConsult_invite_position(String consult_invite_position) {
        this.consult_invite_position = consult_invite_position;
    }

    public String getConsult_invite_color() {
        return consult_invite_color;
    }

    public void setConsult_invite_color(String consult_invite_color) {
        this.consult_invite_color = consult_invite_color;
    }

    public String getConsult_invite_right() {
        return consult_invite_right;
    }

    public void setConsult_invite_right(String consult_invite_right) {
        this.consult_invite_right = consult_invite_right;
    }

    public String getConsult_invite_left() {
        return consult_invite_left;
    }

    public void setConsult_invite_left(String consult_invite_left) {
        this.consult_invite_left = consult_invite_left;
    }

    public String getConsult_invite_bottom() {
        return consult_invite_bottom;
    }

    public void setConsult_invite_bottom(String consult_invite_bottom) {
        this.consult_invite_bottom = consult_invite_bottom;
    }

    public String getConsult_invite_top() {
        return consult_invite_top;
    }

    public void setConsult_invite_top(String consult_invite_top) {
        this.consult_invite_top = consult_invite_top;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConsult_invite_width() {
        return consult_invite_width;
    }

    public void setConsult_invite_width(String consult_invite_width) {
        this.consult_invite_width = consult_invite_width;
    }

    public String getConsult_invite_poptype() {
        return consult_invite_poptype;
    }

    public void setConsult_invite_poptype(String consult_invite_poptype) {
        this.consult_invite_poptype = consult_invite_poptype;
    }

    public String getConsult_invite_fontsize() {
        return consult_invite_fontsize;
    }

    public void setConsult_invite_fontsize(String consult_invite_fontsize) {
        this.consult_invite_fontsize = consult_invite_fontsize;
    }

    public String getConsult_invite_fontstyle() {
        return consult_invite_fontstyle;
    }

    public void setConsult_invite_fontstyle(String consult_invite_fontstyle) {
        this.consult_invite_fontstyle = consult_invite_fontstyle;
    }

    public String getConsult_invite_fontcolor() {
        return consult_invite_fontcolor;
    }

    public void setConsult_invite_fontcolor(String consult_invite_fontcolor) {
        this.consult_invite_fontcolor = consult_invite_fontcolor;
    }

    public String getConsult_invite_interval() {
        return consult_invite_interval;
    }

    public void setConsult_invite_interval(String consult_invite_interval) {
        this.consult_invite_interval = consult_invite_interval;
    }

    public String getConsult_invite_repeat() {
        return consult_invite_repeat;
    }

    public void setConsult_invite_repeat(String consult_invite_repeat) {
        this.consult_invite_repeat = consult_invite_repeat;
    }

    public String getConsult_invite_hight() {
        return consult_invite_hight;
    }

    public void setConsult_invite_hight(String consult_invite_hight) {
        this.consult_invite_hight = consult_invite_hight;
    }

    public String getSnsaccountid() {
        return snsaccountid;
    }

    public void setSnsaccountid(String snsaccountid) {
        this.snsaccountid = snsaccountid;
    }

    public String getConsult_vsitorbtn_position() {
        return consult_vsitorbtn_position;
    }

    public void setConsult_vsitorbtn_position(String consult_vsitorbtn_position) {
        this.consult_vsitorbtn_position = consult_vsitorbtn_position;
    }

    public String getConsult_vsitorbtn_content() {
        return consult_vsitorbtn_content;
    }

    public void setConsult_vsitorbtn_content(String consult_vsitorbtn_content) {
        this.consult_vsitorbtn_content = consult_vsitorbtn_content;
    }

    public String getConsult_vsitorbtn_right() {
        return consult_vsitorbtn_right;
    }

    public void setConsult_vsitorbtn_right(String consult_vsitorbtn_right) {
        this.consult_vsitorbtn_right = consult_vsitorbtn_right;
    }

    public String getConsult_vsitorbtn_left() {
        return consult_vsitorbtn_left;
    }

    public void setConsult_vsitorbtn_left(String consult_vsitorbtn_left) {
        this.consult_vsitorbtn_left = consult_vsitorbtn_left;
    }

    public String getConsult_vsitorbtn_top() {
        return consult_vsitorbtn_top;
    }

    public void setConsult_vsitorbtn_top(String consult_vsitorbtn_top) {
        this.consult_vsitorbtn_top = consult_vsitorbtn_top;
    }

    public String getConsult_vsitorbtn_color() {
        return consult_vsitorbtn_color;
    }

    public void setConsult_vsitorbtn_color(String consult_vsitorbtn_color) {
        this.consult_vsitorbtn_color = consult_vsitorbtn_color;
    }

    public String getConsult_vsitorbtn_model() {
        return consult_vsitorbtn_model;
    }

    public void setConsult_vsitorbtn_model(String consult_vsitorbtn_model) {
        this.consult_vsitorbtn_model = consult_vsitorbtn_model;
    }

    public String getConsult_vsitorbtn_bottom() {
        return consult_vsitorbtn_bottom;
    }

    public void setConsult_vsitorbtn_bottom(String consult_vsitorbtn_bottom) {
        this.consult_vsitorbtn_bottom = consult_vsitorbtn_bottom;
    }

    public String getConsult_invite_backimg() {
        return consult_invite_backimg;
    }

    public void setConsult_invite_backimg(String consult_invite_backimg) {
        this.consult_invite_backimg = consult_invite_backimg;
    }

    public String getDatadept() {
        return datadept;
    }

    public void setDatadept(String datadept) {
        this.datadept = datadept;
    }

    public String getAgent_online() {
        return agent_online;
    }

    public void setAgent_online(String agent_online) {
        this.agent_online = agent_online;
    }

    public String getConsult_dialog_color() {
        return consult_dialog_color;
    }

    public void setConsult_dialog_color(String consult_dialog_color) {
        this.consult_dialog_color = consult_dialog_color;
    }

    public String getConsult_dialog_logo() {
        return consult_dialog_logo;
    }

    public void setConsult_dialog_logo(String consult_dialog_logo) {
        this.consult_dialog_logo = consult_dialog_logo;
    }

    public String getConsult_dialog_headimg() {
        return consult_dialog_headimg;
    }

    public void setConsult_dialog_headimg(String consult_dialog_headimg) {
        this.consult_dialog_headimg = consult_dialog_headimg;
    }

    public int getConsult_vsitorbtn_display() {
        return consult_vsitorbtn_display;
    }

    public void setConsult_vsitorbtn_display(int consult_vsitorbtn_display) {
        this.consult_vsitorbtn_display = consult_vsitorbtn_display;
    }

    public String getDialog_name() {
        return dialog_name;
    }

    public void setDialog_name(String dialog_name) {
        this.dialog_name = dialog_name;
    }

    public String getDialog_address() {
        return dialog_address;
    }

    public void setDialog_address(String dialog_address) {
        this.dialog_address = dialog_address;
    }

    public String getDialog_phone() {
        return dialog_phone;
    }

    public void setDialog_phone(String dialog_phone) {
        this.dialog_phone = dialog_phone;
    }

    public String getDialog_mail() {
        return dialog_mail;
    }

    public void setDialog_mail(String dialog_mail) {
        this.dialog_mail = dialog_mail;
    }

    public String getDialog_introduction() {
        return dialog_introduction;
    }

    public void setDialog_introduction(String dialog_introduction) {
        this.dialog_introduction = dialog_introduction;
    }

    public String getDialog_message() {
        return dialog_message;
    }

    public void setDialog_message(String dialog_message) {
        this.dialog_message = dialog_message;
    }

    public String getDialog_ad() {
        return dialog_ad;
    }

    public void setDialog_ad(String dialog_ad) {
        this.dialog_ad = dialog_ad;
    }

    public boolean isConsult_invite_enable() {
        return consult_invite_enable;
    }

    public void setConsult_invite_enable(boolean consult_invite_enable) {
        this.consult_invite_enable = consult_invite_enable;
    }

    public String getConsult_invite_accept() {
        return consult_invite_accept;
    }

    public void setConsult_invite_accept(String consult_invite_accept) {
        this.consult_invite_accept = consult_invite_accept;
    }

    public String getConsult_invite_later() {
        return consult_invite_later;
    }

    public void setConsult_invite_later(String consult_invite_later) {
        this.consult_invite_later = consult_invite_later;
    }

    public int getConsult_invite_delay() {
        return consult_invite_delay;
    }

    public void setConsult_invite_delay(int consult_invite_delay) {
        this.consult_invite_delay = consult_invite_delay;
    }

    public String getConsult_invite_bg() {
        return consult_invite_bg;
    }

    public void setConsult_invite_bg(String consult_invite_bg) {
        this.consult_invite_bg = consult_invite_bg;
    }

    public boolean isLeavemessage() {
        return leavemessage;
    }

    public void setLeavemessage(boolean leavemessage) {
        this.leavemessage = leavemessage;
    }

    public boolean isLvmname() {
        return lvmname;
    }

    public void setLvmname(boolean lvmname) {
        this.lvmname = lvmname;
    }

    public boolean isLvmphone() {
        return lvmphone;
    }

    public void setLvmphone(boolean lvmphone) {
        this.lvmphone = lvmphone;
    }

    public boolean isLvmemail() {
        return lvmemail;
    }

    public void setLvmemail(boolean lvmemail) {
        this.lvmemail = lvmemail;
    }

    public boolean isLvmaddress() {
        return lvmaddress;
    }

    public void setLvmaddress(boolean lvmaddress) {
        this.lvmaddress = lvmaddress;
    }

    public boolean isLvmcontent() {
        return lvmcontent;
    }

    public void setLvmcontent(boolean lvmcontent) {
        this.lvmcontent = lvmcontent;
    }

    public String getLvmopentype() {
        return lvmopentype;
    }

    public void setLvmopentype(String lvmopentype) {
        this.lvmopentype = lvmopentype;
    }

    public boolean isSkill() {
        return skill;
    }

    public void setSkill(boolean skill) {
        this.skill = skill;
    }

    public boolean isLvmqq() {
        return lvmqq;
    }

    public void setLvmqq(boolean lvmqq) {
        this.lvmqq = lvmqq;
    }

    public String getConsult_skill_logo() {
        return consult_skill_logo;
    }

    public void setConsult_skill_logo(String consult_skill_logo) {
        this.consult_skill_logo = consult_skill_logo;
    }

    public String getConsult_skill_title() {
        return consult_skill_title;
    }

    public void setConsult_skill_title(String consult_skill_title) {
        this.consult_skill_title = consult_skill_title;
    }

    public String getConsult_skill_img() {
        return consult_skill_img;
    }

    public void setConsult_skill_img(String consult_skill_img) {
        this.consult_skill_img = consult_skill_img;
    }

    public String getConsult_skill_msg() {
        return consult_skill_msg;
    }

    public void setConsult_skill_msg(String consult_skill_msg) {
        this.consult_skill_msg = consult_skill_msg;
    }

    public int getConsult_skill_numbers() {
        return consult_skill_numbers;
    }

    public void setConsult_skill_numbers(int consult_skill_numbers) {
        this.consult_skill_numbers = consult_skill_numbers;
    }

    public int getConsult_skill_maxagent() {
        return consult_skill_maxagent;
    }

    public void setConsult_skill_maxagent(int consult_skill_maxagent) {
        this.consult_skill_maxagent = consult_skill_maxagent;
    }

    public String getConsult_skill_bottomtitle() {
        return consult_skill_bottomtitle;
    }

    public void setConsult_skill_bottomtitle(String consult_skill_bottomtitle) {
        this.consult_skill_bottomtitle = consult_skill_bottomtitle;
    }

    public boolean isAi() {
        return ai;
    }

    public void setAi(boolean ai) {
        this.ai = ai;
    }

    public boolean isAifirst() {
        return aifirst;
    }

    public void setAifirst(boolean aifirst) {
        this.aifirst = aifirst;
    }

    public boolean isAisearch() {
        return aisearch;
    }

    public void setAisearch(boolean aisearch) {
        this.aisearch = aisearch;
    }

    public String getAimsg() {
        return aimsg;
    }

    public void setAimsg(String aimsg) {
        this.aimsg = aimsg;
    }

    public String getAisuccesstip() {
        return aisuccesstip;
    }

    public void setAisuccesstip(String aisuccesstip) {
        this.aisuccesstip = aisuccesstip;
    }

    public String getAiname() {
        return ainame;
    }

    public void setAiname(String ainame) {
        this.ainame = ainame;
    }

    public boolean isConsult_info() {
        return consult_info;
    }

    public void setConsult_info(boolean consult_info) {
        this.consult_info = consult_info;
    }

    public boolean isConsult_info_name() {
        return consult_info_name;
    }

    public void setConsult_info_name(boolean consult_info_name) {
        this.consult_info_name = consult_info_name;
    }

    public boolean isConsult_info_email() {
        return consult_info_email;
    }

    public void setConsult_info_email(boolean consult_info_email) {
        this.consult_info_email = consult_info_email;
    }

    public boolean isConsult_info_phone() {
        return consult_info_phone;
    }

    public void setConsult_info_phone(boolean consult_info_phone) {
        this.consult_info_phone = consult_info_phone;
    }

    public boolean isConsult_info_resion() {
        return consult_info_resion;
    }

    public void setConsult_info_resion(boolean consult_info_resion) {
        this.consult_info_resion = consult_info_resion;
    }

    public String getConsult_info_message() {
        return consult_info_message;
    }

    public void setConsult_info_message(String consult_info_message) {
        this.consult_info_message = consult_info_message;
    }

    public boolean isConsult_info_cookies() {
        return consult_info_cookies;
    }

    public void setConsult_info_cookies(boolean consult_info_cookies) {
        this.consult_info_cookies = consult_info_cookies;
    }

    public boolean isRecordhis() {
        return recordhis;
    }

    public void setRecordhis(boolean recordhis) {
        this.recordhis = recordhis;
    }

    public boolean isTraceuser() {
        return traceuser;
    }

    public void setTraceuser(boolean traceuser) {
        this.traceuser = traceuser;
    }

    public boolean isConsult_skill_agent() {
        return consult_skill_agent;
    }

    public void setConsult_skill_agent(boolean consult_skill_agent) {
        this.consult_skill_agent = consult_skill_agent;
    }

    public boolean isOnlyareaskill() {
        return onlyareaskill;
    }

    public void setOnlyareaskill(boolean onlyareaskill) {
        this.onlyareaskill = onlyareaskill;
    }

    public String getAreaskilltipmsg() {
        return areaskilltipmsg;
    }

    public void setAreaskilltipmsg(String areaskilltipmsg) {
        this.areaskilltipmsg = areaskilltipmsg;
    }

    public String getAiid() {
        return aiid;
    }

    public void setAiid(String aiid) {
        this.aiid = aiid;
    }

    public int getMaxwordsnum() {
        return maxwordsnum;
    }

    public void setMaxwordsnum(int maxwordsnum) {
        this.maxwordsnum = maxwordsnum;
    }

    public String getAgentshortcutkey() {
        return agentshortcutkey;
    }

    public void setAgentshortcutkey(String agentshortcutkey) {
        this.agentshortcutkey = agentshortcutkey;
    }

    public String getUsershortcutkey() {
        return usershortcutkey;
    }

    public void setUsershortcutkey(String usershortcutkey) {
        this.usershortcutkey = usershortcutkey;
    }

    public boolean isCtrlenter() {
        return ctrlenter;
    }

    public void setCtrlenter(boolean ctrlenter) {
        this.ctrlenter = ctrlenter;
    }

    public boolean isConsult_skill_fixed() {
        return consult_skill_fixed;
    }

    public void setConsult_skill_fixed(boolean consult_skill_fixed) {
        this.consult_skill_fixed = consult_skill_fixed;
    }

    public String getConsult_skill_fixed_id() {
        return consult_skill_fixed_id;
    }

    public void setConsult_skill_fixed_id(String consult_skill_fixed_id) {
        this.consult_skill_fixed_id = consult_skill_fixed_id;
    }

    public boolean isAisuggest() {
        return aisuggest;
    }

    public void setAisuggest(boolean aisuggest) {
        this.aisuggest = aisuggest;
    }

    public boolean isWhitelist_mode() {
        return whitelist_mode;
    }

    public void setWhitelist_mode(boolean whitelist_mode) {
        this.whitelist_mode = whitelist_mode;
    }
}
