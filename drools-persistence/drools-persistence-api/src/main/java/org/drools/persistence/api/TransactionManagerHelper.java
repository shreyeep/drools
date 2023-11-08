/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.persistence.api;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TransactionManagerHelper {

    private static final String APP_UPDETEABLE_RESOURCE = "app-updateable-resource";

    public static void registerTransactionSyncInContainer(TransactionManager txm, OrderedTransactionSynchronization synchronization) {
        TransactionSynchronizationContainer container = (TransactionSynchronizationContainer)txm.getResource(TransactionSynchronizationContainer.RESOURCE_KEY);
        if (container == null) {
            container = new TransactionSynchronizationContainer();
            txm.registerTransactionSynchronization( container );
            txm.putResource(TransactionSynchronizationContainer.RESOURCE_KEY, container);
        }
        container.addTransactionSynchronization(synchronization);
    }

    @SuppressWarnings("unchecked")
    public static void addToUpdatableSet(TransactionManager txm, Transformable transformable) {
        if (transformable == null) {
            return;
        }
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            toBeUpdated = new LinkedHashSet<>();
            txm.putResource(APP_UPDETEABLE_RESOURCE, toBeUpdated);
        }
        toBeUpdated.add(transformable);
    }

    @SuppressWarnings("unchecked")
    public static void removeFromUpdatableSet(TransactionManager txm, Transformable transformable) {
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            return;
        }
        toBeUpdated.remove(transformable);
    }

    @SuppressWarnings("unchecked")
    public static Set<Transformable> getUpdateableSet(TransactionManager txm) {
        Set<Transformable> result = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (result != null) {
            SortedSet<Transformable> sorted = new TreeSet<>((o1, o2) -> {
                int compared = o1.getClass().getSimpleName().compareTo(o2.getClass().getSimpleName());
                return compared == 0 ? 1 : compared;
            });
            sorted.addAll(result);
            return sorted;
        }
        return Collections.emptySet();
    }
}
