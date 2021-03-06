/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.arm.dag;

import com.microsoft.azure.arm.model.Creatable;
import com.microsoft.azure.arm.model.implementation.CreatableUpdatableImpl;
import com.microsoft.azure.arm.model.implementation.CreateUpdateTask;
import org.junit.Assert;
import rx.Observable;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link IPizza}
 */
class PizzaImpl
        extends CreatableUpdatableImpl<IPizza, PizzaInner, PizzaImpl>
        implements IPizza {
    final List<Creatable<IPizza>> delayedPizzas;
    boolean prepareCalled = false;

    public PizzaImpl(String name) {
        super(name, name, new PizzaInner());
        delayedPizzas = new ArrayList<>();
    }

    /**
     * a pizza specified via this wither will be added immediately as dependency.
     *
     * @param pizza the pizza
     * @return the next stage of pizza
     */
    @Override
    public PizzaImpl withInstantPizza(Creatable<IPizza> pizza) {
        this.addDependency(pizza);
        return this;
    }

    /**
     * a pizza specified via this wither will not be added immediately as a dependency, will be added only
     * inside beforeGroupCreateOrUpdate {@link CreateUpdateTask.ResourceCreatorUpdater#beforeGroupCreateOrUpdate()}
     *
     * @param pizza the pizza
     * @return the next stage of pizza
     */
    @Override
    public PizzaImpl withDelayedPizza(Creatable<IPizza> pizza) {
        this.delayedPizzas.add(pizza);
        return this;
    }

    @Override
    public void beforeGroupCreateOrUpdate() {
        Assert.assertFalse("PizzaImpl::beforeGroupCreateOrUpdate() should not be called multiple times", this.prepareCalled);
        prepareCalled = true;
        int oldCount = this.taskGroup().getNode(this.key()).dependencyKeys().size();
        for(Creatable<IPizza> pizza : this.delayedPizzas) {
            this.addDependency(pizza);
        }
        int newCount = this.taskGroup().getNode(this.key()).dependencyKeys().size();
        System.out.println("Pizza(" + this.name() + ")::beforeGroupCreateOrUpdate() 'delayedSize':" + this.delayedPizzas.size()
                + " 'dependency count [old, new]': [" + oldCount + "," + newCount + "]");
    }

    @Override
    public Observable<IPizza> createResourceAsync() {
        System.out.println("Pizza(" + this.name() + ")::createResourceAsync()");
        return Observable.just(this)
                .delay(250, TimeUnit.MILLISECONDS)
                .map(new Func1<PizzaImpl, IPizza>() {
                    @Override
                    public IPizza call(PizzaImpl pizza) {
                        return pizza;
                    }
                });
    }

    @Override
    public boolean isInCreateMode() {
        return true;
    }

    @Override
    protected Observable<PizzaInner> getInnerAsync() {
        return Observable.just(this.inner());
    }
}
