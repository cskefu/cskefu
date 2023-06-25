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
package com.cskefu.cc.config;

import de.neuland.pug4j.spring.view.PugView;
import de.neuland.pug4j.spring.view.PugViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

public class PugCskefuViewResolver extends PugViewResolver {
    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        AbstractUrlBasedView view = super.buildView(viewName);
        if (viewName.startsWith("/resource/css")) {
            PugView pugView = (PugView) view;
            pugView.setContentType("text/css; charset=UTF-8");
        }
        return view;
    }
}
