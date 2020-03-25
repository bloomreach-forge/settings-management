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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class CrispResourceSpaceDataProvider extends SortableDataProvider<CrispResourceSpace, String> {

    private List<CrispResourceSpace> crispResourceSpaces;

    public CrispResourceSpaceDataProvider() {
        // FIXME
        crispResourceSpaces = new ArrayList<>();
        crispResourceSpaces.add(new CrispResourceSpace("Bloomreach S&M", "brsm"));
        crispResourceSpaces.add(new CrispResourceSpace("commrecetools", "commercetools"));
        crispResourceSpaces.add(new CrispResourceSpace("Elastic Path", "elasticpath"));
    }

    @Override
    public Iterator<? extends CrispResourceSpace> iterator(long first, long count) {
        return crispResourceSpaces.iterator();
    }

    @Override
    public long size() {
        return crispResourceSpaces.size();
    }

    @Override
    public IModel<CrispResourceSpace> model(CrispResourceSpace object) {
        return new Model<>(object);
    }

}
