/*
 * Copyright 2020 Bloomreach Inc. (http://www.bloomreach.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bloomreach.forge.settings.management.config.crispapi;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CrispResourceSpaceDataProvider extends SortableDataProvider<CrispResourceSpace, String> {

    private final CrispApiConfigModel crispApiConfigModel;

    public CrispResourceSpaceDataProvider(final CrispApiConfigModel crispApiConfigModel) {
        this.crispApiConfigModel = crispApiConfigModel;
    }

    @Override
    public long size() {
        return crispApiConfigModel.getObject().getCurrentCrispResourceSpaces().size();
    }

    @Override
    public IModel<CrispResourceSpace> model(CrispResourceSpace object) {
        return new Model<>(object);
    }

    @Override
    public Iterator<? extends CrispResourceSpace> iterator(long first, long count) {
        final List<CrispResourceSpace> resourceSpaceList = new LinkedList<>(
                crispApiConfigModel.getObject().getCurrentCrispResourceSpaces());

        final SortParam<String> sortParam = getSort();

        if (sortParam != null) {
            resourceSpaceList.sort((resourceSpace1, resourceSpace2) -> {
                final int direction = sortParam.isAscending() ? 1 : -1;
                switch (sortParam.getProperty()) {
                case "backendTypeName":
                    return direction * StringUtils.compareIgnoreCase(resourceSpace1.getBackendTypeName(),
                            resourceSpace2.getBackendTypeName());
                case "resoureSpaceName":
                default:
                    return direction * StringUtils.compareIgnoreCase(resourceSpace1.getResourceSpaceName(),
                            resourceSpace2.getResourceSpaceName());
                }
            });
        }

        if (first == 0L && resourceSpaceList.size() <= count) {
            return resourceSpaceList.iterator();
        }

        return resourceSpaceList.subList((int) first, (int) Math.min(first + count, resourceSpaceList.size()))
                .iterator();
    }
}
