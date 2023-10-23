/*
 * Copyright (C) 2023 Beijing Huaxia Chunsong Technology Co., Ltd.
 * <https://www.chatopera.com>, Licensed under the Chunsong Public
 * License, Version 1.0  (the "License"), https://docs.cskefu.com/licenses/v1.html
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cskefu.cc.model;

import org.json.JSONObject;

import java.io.Serializable;

public class ExecuteResult implements Serializable {
    public final static int RC_SUCC = 0;
    public final static int RC_ERR1 = 1;
    public final static int RC_ERR2 = 2;
    public final static int RC_ERR3 = 3;
    public final static int RC_ERR4 = 4;
    public final static int RC_ERR5 = 5;
    public final static int RC_ERR6 = 6;
    public final static int RC_ERR7 = 7;
    public final static int RC_ERR8 = 8;
    public final static int RC_ERR9 = 9;

    private int rc; // 0 for success, errors other
    private String error;
    private String msg;
    private JSONObject data;


    public ExecuteResult() {

    }

    public ExecuteResult(final int rc, final String msg) {
        this.rc = rc;
        this.msg = msg;
    }

    public ExecuteResult(final int rc, final String msg, final String error) {
        this.rc = rc;
        this.msg = msg;
        this.error = error;
    }

    public ExecuteResult(final int rc,
                         final String msg,
                         final String error,
                         final JSONObject data) {
        this.rc = rc;
        this.msg = msg;
        this.error = error;
        this.data = data;
    }

    public int getRc() {
        return rc;
    }

    public void setRc(int rc) {
        this.rc = rc;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
